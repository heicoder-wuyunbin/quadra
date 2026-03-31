-- ==========================================================
-- Quadra (高学历相亲交友) 教学样板项目 DDL 脚本
-- 服务域：互动域 (quadra_interaction)
-- 数据库：`quadra_interaction`
-- 描述：彻底无状态化的点赞、评论等高频操作。
-- 特性：通过统一的 interaction 行为模型，保护核心内容库。
-- ==========================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS `quadra_interaction` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quadra_interaction`;

-- 2. 统一互动行为表 (核心聚合根)
-- 亮点：抽象点赞(LIKE)和评论(COMMENT)为统一行为模型，可轻易扩展 收藏(FAVORITE)、转发(SHARE) 等
DROP TABLE IF EXISTS `interaction`;
CREATE TABLE `interaction` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '发起互动(点赞/评论)的用户ID',
  `target_id` bigint NOT NULL COMMENT '目标业务ID (如 movement_id 或 video_id)',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型：MOVEMENT-动态, VIDEO-视频',
  `action_type` varchar(20) NOT NULL COMMENT '互动类型：LIKE-点赞, COMMENT-评论',
  `content` varchar(500) DEFAULT NULL COMMENT '互动内容 (评论时必填，点赞为空)',
  `reply_to_id` bigint DEFAULT NULL COMMENT '回复的评论ID (若是二级评论)',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-已取消(如取消点赞/删除评论)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '互动时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_like_limit` (`user_id`, `target_id`, `target_type`, `deleted`) COMMENT '防止对同一目标重复点赞 (依赖代码判断 action_type=LIKE 时才插入此表)',
  KEY `idx_target_action` (`target_id`, `target_type`, `action_type`, `created_at` DESC) COMMENT '用于按时间倒序查询某个动态的评论列表/点赞列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一互动行为表';

-- 3. 领域事件发件箱表 (quadra_interaction 服务私有)
-- 描述：当发生点赞或评论时，将事件写入 outbox，由 MQ 通知 quadra_content 更新冗余的计数器
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