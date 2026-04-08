# Quadra 数据库脚本体检（2026-04-08）

你上传的各域 DDL（system/user/content/social/interaction/recommend）整体风格比较统一：InnoDB + utf8mb4 + 软删 + 乐观锁 + created_at/updated_at，作为教学样板是合格的。下面按“会导致线上问题/影响扩展性/可读性一致性”的优先级给结论与建议。

## A. 本次登录 500 的根因与修复建议（必须）
**根因**：`quadra_system.sys_login_log` 表字段是 **`ip_address`**，但后端写入 SQL 使用了 **`ip`**，导致：

> Unknown column 'ip' in 'field list'

**最佳实践推荐**：改代码（Mapper/SQL）把 `ip` 改为 `ip_address`，并统一整个系统都用 `ip_address`（你脚本里 `sys_operate_log`、`sys_request_log` 也都是 `ip_address`）。

**数据库侧临时兜底**：如果你暂时无法发布后端修复，可以在库里新增废弃字段 `ip` 并用触发器同步到 `ip_address`（有性能与维护成本，建议仅短期使用）。对应 SQL 我已经放在：

- `quadra_db_migrations_20260408.sql` 的第 1 部分

## B. 结构级问题：interaction 表的唯一索引会“误伤评论”（必须）
在 `quadra_interaction.interaction` 里你用一张表承载 `LIKE` 与 `COMMENT`，但当前唯一索引：

`uk_like_limit (user_id, target_id, target_type, deleted)`

会导致 **同一用户对同一内容最多只能插入一条互动记录**，从而让评论功能无法支持多条评论（或出现奇怪的重复键冲突）。

**最佳实践修复**：用“生成列 + UNIQUE”把唯一性只约束到 `LIKE`（MySQL 8 下可行，且不需要改业务表结构太多）。对应 SQL 我已经放在：

- `quadra_db_migrations_20260408.sql` 的第 2 部分

## C. 其它改进建议（可选，但推荐统一）
### 1) 表名 `user`
虽然你用了反引号 `` `user` ``，MySQL 能跑，但在运维/工具/跨库迁移时更容易踩坑（与系统库、保留字、审计规则冲突）。建议长期演进为 `users` 或 `app_user`。

### 2) 软删字段类型
目前用 `tinyint` 存 `deleted`，可用；但建议统一约束语义：
- `deleted`：0/1
- `status`：业务状态（0/1/2...）
并在所有“唯一性约束”里保持一致（你大部分表已经做了 `(..., deleted)` 的联合唯一，挺好）。

### 3) JSON 字段与索引
你在 `user_profile` 用了 `GENERATED ALWAYS AS ... STORED` + 索引，这是很好的 MySQL 8 实践。建议对其他高频筛选字段也按需“提取为生成列再建索引”，避免在 JSON 上直接全表扫。

### 4) 日志大字段（request_body/response_body）
`sys_request_log` 用 `mediumtext` 合理，但要配合：
- 应用侧限长（比如截断到 4KB/16KB）
- 脱敏（token/密码/手机号）
否则很容易把日志库写爆或引起慢 SQL（你注释里已经提到“限长、可配置”，建议落实到代码）。

### 5) 外键策略
你的脚本整体是“微服务分库、无物理外键”的取向（social 脚本里也明确写了无外键），这在生产里通常更稳（降低耦合与锁风险）。如果你想更强一致性，可以在**单库内**对少量强一致表加 FK，但需要评估写入与迁移成本。

## D. 我还需要你确认的两点（避免我做错假设）
1) 目标数据库是 MySQL 8.x 吗？（例如 8.0.30+ / 8.0.34+）
2) 你希望生产环境是否**绝对不加外键**（与你脚本注释一致），还是允许对核心表加少量 FK？

---

## E. 已确认的落地策略（你已回复）
- 数据库：**MySQL 8.4**（不考虑兼容其它数据库）
- 约束策略：**不加外键**（保持微服务分库、应用层保证一致性）
