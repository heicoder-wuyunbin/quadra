USE `quadra_system`;

ALTER TABLE `sys_operate_log`
  ADD COLUMN `user_agent` varchar(255) DEFAULT NULL COMMENT 'User-Agent',
  ADD COLUMN `request_params` json DEFAULT NULL COMMENT '请求参数',
  ADD COLUMN `response_status` int DEFAULT NULL COMMENT '状态码',
  ADD COLUMN `execute_time` int DEFAULT NULL COMMENT '耗时(ms)';

CREATE TABLE IF NOT EXISTS `sys_login_log` (
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

CREATE TABLE IF NOT EXISTS `sys_error_log` (
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