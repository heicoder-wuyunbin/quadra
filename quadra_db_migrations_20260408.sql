-- ==========================================================
-- Quadra 数据库增量修复脚本（2026-04-08）
-- 目标：修复已发现的结构问题，并提供“兼容旧代码”的可选补丁。
-- 适用：MySQL 8.4 / InnoDB / utf8mb4（不考虑其它数据库兼容）
--
-- 使用方式（建议）：
--   1) 先在测试库执行并验证
--   2) 线上执行请走变更窗口，并评估锁表时间（尤其是大表）
-- ==========================================================

/* ----------------------------------------------------------
 * 1) 修复：后台登录日志写入失败（Unknown column 'ip'）
 *
 * 根因：
 *   表结构（quadra_system.sys_login_log）字段为 ip_address，
 *   但代码 insert 使用了 ip。
 *
 * 最佳实践推荐（MySQL 8.4）：
 *   优先修代码：把 insert 字段 ip 改为 ip_address（不建议为了代码错误改表结构）。
 *
 * 下面提供“数据库兼容旧代码”的可选补丁（不推荐长期保留）：
 *   - 新增废弃字段 ip
 *   - 通过触发器把 ip 同步到 ip_address
 * 注意：
 *   - 触发器会增加写入开销；建议仅作为临时过渡方案。
 * ---------------------------------------------------------- */

USE `quadra_system`;

-- 可选：仅当你暂时无法立刻发布后端修复时启用（建议发布代码修复后移除触发器与废弃字段）
ALTER TABLE `sys_login_log`
  ADD COLUMN IF NOT EXISTS `ip` varchar(50) DEFAULT NULL COMMENT '兼容旧字段（废弃）：请改用 ip_address';

DROP TRIGGER IF EXISTS `trg_sys_login_log_bi`;
DROP TRIGGER IF EXISTS `trg_sys_login_log_bu`;

DELIMITER $$
CREATE TRIGGER `trg_sys_login_log_bi`
BEFORE INSERT ON `sys_login_log`
FOR EACH ROW
BEGIN
  IF NEW.`ip_address` IS NULL AND NEW.`ip` IS NOT NULL THEN
    SET NEW.`ip_address` = NEW.`ip`;
  END IF;
END$$

CREATE TRIGGER `trg_sys_login_log_bu`
BEFORE UPDATE ON `sys_login_log`
FOR EACH ROW
BEGIN
  IF NEW.`ip_address` IS NULL AND NEW.`ip` IS NOT NULL THEN
    SET NEW.`ip_address` = NEW.`ip`;
  END IF;
END$$
DELIMITER ;


/* ----------------------------------------------------------
 * 2) 修复/改进：quadra_interaction.interaction 表的“点赞唯一性”约束
 *
 * 现状问题（严重）：
 *   interaction 表同时存 LIKE 与 COMMENT，
 *   但当前唯一索引 uk_like_limit = (user_id, target_id, target_type, deleted)
 *   会导致“同一用户对同一目标只能写一条记录”，从而把评论也限制死。
 *
 * 最佳实践做法（MySQL 8）：
 *   使用“生成列 + UNIQUE”实现仅对 LIKE 做唯一约束：
 *   - LIKE 且 deleted=0 时生成唯一键
 *   - 其他行为生成 NULL（UNIQUE 允许多个 NULL）
 * ---------------------------------------------------------- */

USE `quadra_interaction`;

-- 先删除旧唯一索引（如果存在）
ALTER TABLE `interaction`
  DROP INDEX `uk_like_limit`;

-- 增加生成列（用于 LIKE 的唯一约束）
ALTER TABLE `interaction`
  ADD COLUMN `like_unique_key` varchar(200)
    GENERATED ALWAYS AS (
      CASE
        WHEN `action_type` = 'LIKE' AND `deleted` = 0 THEN
          CONCAT(CAST(`user_id` AS CHAR), '_', `target_type`, '_', CAST(`target_id` AS CHAR))
        ELSE NULL
      END
    ) STORED
    COMMENT '仅用于 LIKE 的唯一约束（COMMENT 等行为为 NULL）',
  ADD UNIQUE KEY `uk_like_limit` (`like_unique_key`);
