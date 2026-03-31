-- ==========================================================
-- Quadra (高学历相亲交友) 教学样板项目 DDL 脚本
-- 服务域：身份域 (quadra_user)
-- 数据库：`quadra_user`
-- 描述：包含用户账号、基础信息、个人设置、防骚扰提问及黑名单。
-- 特性：融合 DDD 聚合根设计、MySQL 8 JSON 虚拟列索引、乐观锁、Outbox 模式。
-- ==========================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS `quadra_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quadra_user`;

-- ==========================================================
-- 聚合根：User (包含 User, Profile, Setting)
-- 描述：强一致性生命周期，注册时同步创建
-- ==========================================================

-- 2. 用户账号表 (聚合根核心)
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法全局唯一ID',
  `mobile` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `hx_user` varchar(100) DEFAULT NULL COMMENT '环信IM用户名',
  `hx_password` varchar(100) DEFAULT NULL COMMENT '环信IM密码',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '账号状态：0-禁用，1-正常',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mobile_deleted` (`mobile`, `deleted`) COMMENT '手机号防重索引（结合软删除）',
  UNIQUE KEY `uk_hx_user` (`hx_user`) COMMENT '环信账号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账号表（聚合根）';

-- 3. 用户资料表 (实体，依附于 User 聚合根)
-- 亮点：使用 JSON 存储 tags，并使用 MySQL 8 虚拟列提取 education 进行索引
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile` (
  `id` bigint NOT NULL COMMENT '主键，与 user 表 id 保持一致 (1:1共享主键)',
  `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `city` varchar(50) DEFAULT NULL COMMENT '常驻城市',
  `income` varchar(50) DEFAULT NULL COMMENT '收入区间 (如 20K-30K)',
  `profession` varchar(50) DEFAULT NULL COMMENT '所属行业',
  `marriage` tinyint DEFAULT '0' COMMENT '婚姻状态：0-未婚，1-离异，2-丧偶',
  `cover_pic` varchar(500) DEFAULT NULL COMMENT '主页封面图URL',
  `tags` json DEFAULT NULL COMMENT '用户个性标签及扩展信息 (JSON对象格式，如 {"education":"本科", "tags":["阳光", "运动"]})',
  `education` varchar(20) GENERATED ALWAYS AS (json_unquote(json_extract(`tags`,'$.education'))) STORED COMMENT '学历(虚拟列，从tags JSON对象中提取)',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_city` (`city`),
  KEY `idx_education` (`education`) COMMENT '基于虚拟列的学历索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资料表（实体）';

-- 4. 用户设置表 (实体，依附于 User 聚合根)
DROP TABLE IF EXISTS `user_setting`;
CREATE TABLE `user_setting` (
  `id` bigint NOT NULL COMMENT '主键，与 user 表 id 保持一致 (1:1共享主键)',
  `like_notification` tinyint NOT NULL DEFAULT '1' COMMENT '点赞通知：0-关闭，1-开启',
  `comment_notification` tinyint NOT NULL DEFAULT '1' COMMENT '评论通知：0-关闭，1-开启',
  `system_notification` tinyint NOT NULL DEFAULT '1' COMMENT '系统公告通知：0-关闭，1-开启',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户偏好设置表（实体）';

-- ==========================================================
-- 独立聚合根：防骚扰与黑名单
-- ==========================================================

-- 5. 陌生人提问表 (独立聚合根)
DROP TABLE IF EXISTS `stranger_question`;
CREATE TABLE `stranger_question` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '归属用户ID',
  `question_text` varchar(500) NOT NULL COMMENT '问题内容',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号，越小越靠前',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='陌生人提问表';

-- 6. 用户黑名单表 (独立聚合根)
-- 亮点：高频读取业务，需结合 Redis 缓存 (user:blacklist:{user_id}) 使用
DROP TABLE IF EXISTS `user_blacklist`;
CREATE TABLE `user_blacklist` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '操作人(拉黑者)用户ID',
  `target_user_id` bigint NOT NULL COMMENT '被拉黑用户ID',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_user_id`, `deleted`) COMMENT '防止重复拉黑',
  KEY `idx_target_user_id` (`target_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户黑名单表';

-- ==========================================================
-- 最终一致性保障：Outbox 发件箱表
-- 描述：微服务架构下保证本地事务与 MQ 消息投递的最终一致性
-- ==========================================================

-- 7. 领域事件发件箱表
DROP TABLE IF EXISTS `outbox_event`;
CREATE TABLE `outbox_event` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `aggregate_type` varchar(50) NOT NULL COMMENT '聚合根类型 (如 User)',
  `aggregate_id` bigint NOT NULL COMMENT '聚合根ID',
  `event_type` varchar(100) NOT NULL COMMENT '事件类型 (如 UserRegisteredEvent)',
  `payload` json NOT NULL COMMENT '事件载荷数据 (JSON格式)',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '投递状态：0-待投递，1-投递成功，2-投递失败',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间，结合 status=2 使用，避免疯狂扫描',
  `error_msg` text DEFAULT NULL COMMENT '最后一次失败原因',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_next_retry` (`status`, `next_retry_time`) COMMENT '定时任务扫描未投递或需重试消息的索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领域事件发件箱表';
