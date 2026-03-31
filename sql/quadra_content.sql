-- ==========================================================
-- Quadra (高学历相亲交友) 教学样板项目 DDL 脚本
-- 服务域：内容分发域 (quadra_content)
-- 数据库：`quadra_content`
-- 描述：图文动态和短视频的发布，以及基于推拉结合架构 (Push/Pull) 的 Feed 流。
-- 特性：多媒体 JSON 结构化、Feed 架构设计。
-- ==========================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS `quadra_content` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quadra_content`;

-- ==========================================================
-- 生产端：动态与视频聚合根
-- ==========================================================

-- 2. 图文动态表 (聚合根)
-- 亮点：摒弃冗余的图片关联表，采用 JSON 存储 medias 数组 (包含 url, cover, width, height)
DROP TABLE IF EXISTS `movement`;
CREATE TABLE `movement` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '发布者用户ID',
  `text_content` varchar(1000) DEFAULT NULL COMMENT '动态文字内容',
  `medias` json DEFAULT NULL COMMENT '多媒体资源数组 (如: [{"url":"cdn..","type":"IMAGE","width":800,"height":600}])',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '发布时经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '发布时纬度',
  `location_name` varchar(100) DEFAULT NULL COMMENT '发布时位置名称',
  `state` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态：0-未审核，1-通过，2-驳回',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '冗余点赞数 (实际精确数据在 interaction 服务)',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '冗余评论数',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`) COMMENT '用于全局最新动态查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图文动态表';

-- 3. 短视频表 (聚合根)
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '发布者用户ID',
  `text_content` varchar(500) DEFAULT NULL COMMENT '视频描述',
  `video_url` varchar(500) NOT NULL COMMENT '视频CDN链接',
  `cover_url` varchar(500) NOT NULL COMMENT '视频封面CDN链接',
  `duration` int DEFAULT '0' COMMENT '视频时长(秒)',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '冗余点赞数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '冗余评论数',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短视频表';

-- ==========================================================
-- 分发端：Feed 架构双层模型 (教学亮点：Push vs Pull)
-- ==========================================================

-- 4. 动态收件箱表 (Push 模式 / Inbox)
-- 描述：当普通用户发动态时，异步写入其所有粉丝的 Inbox 表，实现“读极快”的读扩散。
DROP TABLE IF EXISTS `movement_inbox`;
CREATE TABLE `movement_inbox` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `owner_id` bigint NOT NULL COMMENT '信箱主人ID (粉丝ID)',
  `author_id` bigint NOT NULL COMMENT '发布者ID (博主ID)',
  `movement_id` bigint NOT NULL COMMENT '动态内容ID',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投递时间 (排序依据)',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_owner_movement` (`owner_id`, `movement_id`, `deleted`) COMMENT '防止重复投递',
  KEY `idx_owner_created` (`owner_id`, `created_at` DESC) COMMENT '用户查询自己的时间线专属索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态收件箱表(推模式)';

-- 7. 领域事件发件箱表 (quadra_content 服务私有)
DROP TABLE IF EXISTS `outbox_event`;
CREATE TABLE `outbox_event` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `aggregate_type` varchar(50) NOT NULL COMMENT '聚合根类型',
  `aggregate_id` bigint NOT NULL COMMENT '聚合根ID',
  `event_type` varchar(100) NOT NULL COMMENT '事件类型',
  `payload` json NOT NULL COMMENT '事件载荷数据',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '投递状态',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间，结合 status=2 使用',
  `error_msg` text DEFAULT NULL COMMENT '最后一次失败原因',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_next_retry` (`status`, `next_retry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领域事件发件箱表';