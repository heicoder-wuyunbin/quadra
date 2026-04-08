-- ==========================================================
-- Quadra (高学历相亲交友) 教学样板项目 DDL 脚本
-- 服务域：系统与数据域 (quadra_system)
-- 数据库：`quadra_system`
-- 描述：包含后台管理员账号、角色权限，以及宏观业务数据分析统计。
-- 特性：标准的 RBAC (Role-Based Access Control) 权限模型设计。
-- ==========================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS `quadra_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quadra_system`;

-- ==========================================================
-- 后台权限管理 (标准 RBAC5 模型)
-- ==========================================================

-- 2. 管理员表 (聚合根)
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `username` varchar(50) NOT NULL COMMENT '登录用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username_deleted` (`username`, `deleted`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统管理员表';

-- 插入默认管理员账号（用户名：admin，密码：123456）
INSERT INTO `sys_admin` (`id`, `username`, `password`, `real_name`, `status`, `version`, `deleted`, `created_at`, `updated_at`)
VALUES 
  (1, 'admin', '$2a$10$ttAXvgQ8MlxCgZk0Eg1u3.4JJKhgoiwkBr0LwQlKVI.iLjRqDRUEu', '超级管理员', 1, 0, 0, NOW(), NOW());

-- 3. 角色表 (聚合根)
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL COMMENT '主键',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码 (如 ADMIN, AUDITOR, OPERATOR)',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称 (如 超级管理员, 审核员, 运营)',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code_deleted` (`role_code`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- 4. 菜单/权限资源表 (实体)
-- 亮点：树形结构设计，统一管理前端路由菜单和后端接口权限(按钮级)
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL COMMENT '主键',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID，0表示顶级节点',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单或权限名称 (如 用户管理, 删除用户)',
  `menu_type` tinyint NOT NULL COMMENT '资源类型：1-目录，2-菜单，3-按钮(接口权限)',
  `permission_code` varchar(100) DEFAULT NULL COMMENT '权限标识代码 (如 user:delete，仅 menu_type=3 时有效)',
  `path` varchar(200) DEFAULT NULL COMMENT '前端路由地址 (仅目录/菜单有效)',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '同级排序号，越小越靠前',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-隐藏/禁用，1-显示/正常',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单与权限资源表';

-- 4.1 管理员-角色关联表 (无状态关系表)
DROP TABLE IF EXISTS `sys_admin_role`;
CREATE TABLE `sys_admin_role` (
  `id` bigint NOT NULL COMMENT '主键',
  `admin_id` bigint NOT NULL COMMENT '管理员ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员与角色关联表';

-- 4.2 角色-菜单权限关联表 (无状态关系表)
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单/权限ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色与菜单权限关联表';

-- ==========================================================
-- 宏观数据分析与日志
-- ==========================================================

-- 5. 每日核心数据统计表 (聚合根)
-- 描述：由定时任务在凌晨统计前一日各微服务的数据并汇总于此，供后台 Dashboard 展示。
DROP TABLE IF EXISTS `sys_data_analysis`;
CREATE TABLE `sys_data_analysis` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `record_date` date NOT NULL COMMENT '统计归属日期',
  `num_registered` int NOT NULL DEFAULT '0' COMMENT '新注册用户数 (来自 quadra_user)',
  `num_active` int NOT NULL DEFAULT '0' COMMENT '日活跃用户数 DAU (基于 quadra_recommend 的 action_log 计算)',
  `num_movement` int NOT NULL DEFAULT '0' COMMENT '新增动态数 (来自 quadra_content)',
  `num_matched` int NOT NULL DEFAULT '0' COMMENT '互相喜欢匹配成功对数 (来自 quadra_social)',
  `num_retention_1d` int NOT NULL DEFAULT '0' COMMENT '次日留存用户数',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '统计生成时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_date` (`record_date`) COMMENT '保证每天只有一条汇总数据'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日核心数据统计表';

-- 6. 管理员操作日志表
DROP TABLE IF EXISTS `sys_operate_log`;
CREATE TABLE `sys_operate_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `admin_id` bigint NOT NULL COMMENT '操作人管理员ID',
  `admin_name` varchar(50) DEFAULT NULL COMMENT '冗余管理员姓名',
  `module` varchar(50) NOT NULL COMMENT '操作模块 (如 内容审核, 用户封禁)',
  `action` varchar(50) NOT NULL COMMENT '具体动作 (如 封禁, 驳回, 删除)',
  `target_id` bigint DEFAULT NULL COMMENT '被操作的目标业务ID (如被封禁的用户ID)',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '操作人IP',
  `user_agent` varchar(255) DEFAULT NULL COMMENT 'User-Agent',
  `request_params` json DEFAULT NULL COMMENT '请求参数',
  `response_status` int DEFAULT NULL COMMENT '状态码',
  `execute_time` int DEFAULT NULL COMMENT '耗时(ms)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_time` (`admin_id`, `created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台管理员操作审计日志表';

-- 6.1 管理后台登录日志表
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `admin_id` bigint DEFAULT NULL COMMENT '管理员ID',
  `admin_name` varchar(50) DEFAULT NULL COMMENT '管理员姓名',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '登录IP',
  `location` varchar(100) DEFAULT NULL COMMENT '登录地点',
  `user_agent` varchar(255) DEFAULT NULL COMMENT 'User-Agent',
  `status` varchar(20) NOT NULL COMMENT '状态: SUCCESS, FAILED',
  `reason` varchar(255) DEFAULT NULL COMMENT '失败原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_time` (`admin_id`, `created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台管理员登录日志表';

-- 6.2 系统错误日志表
DROP TABLE IF EXISTS `sys_error_log`;
CREATE TABLE `sys_error_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `level` varchar(20) NOT NULL COMMENT '错误级别: ERROR, WARN, INFO',
  `service` varchar(50) NOT NULL COMMENT '服务名',
  `message` text NOT NULL COMMENT '错误消息',
  `stack_trace` text DEFAULT NULL COMMENT '错误堆栈',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID/管理员ID',
  `request_id` varchar(64) DEFAULT NULL COMMENT '请求ID/TraceID',
  `url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `params` json DEFAULT NULL COMMENT '请求参数',
  `handled` tinyint NOT NULL DEFAULT '0' COMMENT '是否已处理: 0-否, 1-是',
  `handled_by` varchar(50) DEFAULT NULL COMMENT '处理人姓名',
  `handled_at` datetime DEFAULT NULL COMMENT '处理时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_service_level` (`service`, `level`),
  KEY `idx_created_at` (`created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统错误日志表';

-- 6.3 管理后台接口访问日志（用于排查 401/404/耗时等问题，可扩展到全链路）
DROP TABLE IF EXISTS `sys_request_log`;
CREATE TABLE `sys_request_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `service` varchar(50) NOT NULL DEFAULT 'quadra-system' COMMENT '来源服务（如 quadra-system / quadra-gateway）',
  `trace_id` varchar(64) DEFAULT NULL COMMENT '链路ID（requestId/traceId）',
  `admin_id` bigint DEFAULT NULL COMMENT '管理员ID（若已鉴权通过）',
  `method` varchar(10) NOT NULL COMMENT 'HTTP 方法',
  `path` varchar(255) NOT NULL COMMENT '请求路径（不含域名）',
  `query_string` text DEFAULT NULL COMMENT 'QueryString',
  `status_code` int NOT NULL COMMENT 'HTTP 状态码',
  `duration_ms` int NOT NULL COMMENT '耗时（毫秒）',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '客户端IP（优先 X-Forwarded-For）',
  `user_agent` varchar(255) DEFAULT NULL COMMENT 'User-Agent',
  `request_headers` json DEFAULT NULL COMMENT '请求头（已脱敏）',
  `request_body` mediumtext DEFAULT NULL COMMENT '请求体（限长、可配置）',
  `response_body` mediumtext DEFAULT NULL COMMENT '响应体（限长、可配置）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_admin_time2` (`admin_id`, `created_at` DESC),
  KEY `idx_status_time` (`status_code`, `created_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理后台接口访问日志';

-- 7. 领域事件发件箱表 (quadra_system 服务私有)
DROP TABLE IF EXISTS `outbox_event`;
CREATE TABLE `outbox_event` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `aggregate_type` varchar(50) NOT NULL COMMENT '聚合根类型',
  `aggregate_id` bigint NOT NULL COMMENT '聚合根ID',
  `event_type` varchar(100) NOT NULL COMMENT '事件类型 (如 AdminCreatedEvent)',
  `payload` json NOT NULL COMMENT '事件载荷数据',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '投递状态',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
  `error_msg` text DEFAULT NULL COMMENT '最后一次失败原因',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_next_retry` (`status`, `next_retry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领域事件发件箱表';
