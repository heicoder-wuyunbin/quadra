-- ==========================================================
-- Quadra (高学历相亲交友) 教学样板项目 DDL 脚本
-- 服务域：推荐域 (quadra_recommend) - AI 融合版
-- 数据库：`quadra_recommend`
-- 描述：基于“行为层 -> 特征层 -> 结果层”的三层驱动架构，融合本地 AI 模型。
-- ==========================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS `quadra_recommend` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quadra_recommend`;

-- ==========================================================
-- 第一层：行为层 (燃料)
-- ==========================================================

-- 2. 用户行为日志表 (核心聚合根)
-- 描述：取代原本无结构的日志表，精准记录 view/like/skip/dislike 等结构化行为，作为推荐系统燃料。
DROP TABLE IF EXISTS `user_action_log`;
CREATE TABLE `user_action_log` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '行为产生者用户ID',
  `target_id` bigint NOT NULL COMMENT '目标ID (可能是另一用户ID，也可能是动态ID)',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型：USER, MOVEMENT, VIDEO',
  `action_type` varchar(20) NOT NULL COMMENT '动作类型：VIEW-浏览, LIKE-喜欢, SKIP-跳过, DISLIKE-不感兴趣',
  `weight` decimal(5,2) NOT NULL DEFAULT '1.00' COMMENT '动作权重分数 (如：LIKE=5, VIEW=1, DISLIKE=-5)',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '动作发生时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_action_time` (`user_id`, `action_type`, `created_at` DESC) COMMENT '用于离线统计某用户近期的特定行为'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户结构化行为日志表(行为层)';

-- ==========================================================
-- 第二层：特征层 (画像与AI)
-- ==========================================================

-- 3. 用户特征画像表 (聚合根)
-- 描述：包含统计标签和供本地大模型计算的 Embedding 向量。
DROP TABLE IF EXISTS `user_feature`;
CREATE TABLE `user_feature` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `active_score` int NOT NULL DEFAULT '0' COMMENT '活跃度得分',
  `tags_summary` json DEFAULT NULL COMMENT '统计聚合后的偏好标签分布 (如 {"科幻":0.8, "运动":0.5})',
  `ai_embedding` json DEFAULT NULL COMMENT 'AI大模型生成的稠密向量 (浮点数组 JSON)',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '画像最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户特征画像表(特征层)';

-- 4. 内容特征画像表 (聚合根)
DROP TABLE IF EXISTS `content_feature`;
CREATE TABLE `content_feature` (
  `id` bigint NOT NULL COMMENT '主键',
  `target_id` bigint NOT NULL COMMENT '目标内容ID',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型：MOVEMENT, VIDEO',
  `heat_score` int NOT NULL DEFAULT '0' COMMENT '内容热度总分 (基于曝光、点赞、评论换算)',
  `tags_summary` json DEFAULT NULL COMMENT '内容标签特征',
  `ai_embedding` json DEFAULT NULL COMMENT '内容文本/图片对应的AI稠密向量',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_target` (`target_id`, `target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容特征画像表(特征层)';

-- ==========================================================
-- 第三层：结果层 (输出)
-- ==========================================================

-- 5. 缘分推荐用户结果表
-- 描述：推荐引擎 (规则 + AI向量计算) 定时生成的缓存结果，供前端首页"探探"模块拉取
DROP TABLE IF EXISTS `recommend_user`;
CREATE TABLE `recommend_user` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '接受推荐的用户ID',
  `recommend_target_id` bigint NOT NULL COMMENT '被推荐出来的候选人ID',
  `score` decimal(6,2) NOT NULL COMMENT '推荐总匹配度得分 (规则分 + AI相似度)',
  `recommend_date` date NOT NULL COMMENT '推荐所属日期 (按天刷新)',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算生成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target_date` (`user_id`, `recommend_target_id`, `recommend_date`),
  KEY `idx_user_score` (`user_id`, `recommend_date`, `score` DESC) COMMENT '用于前端按分数倒序拉取今日推荐'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缘分推荐结果表(结果层)';

-- 6. 内容推荐结果表
-- 描述：首页"推荐"时间线的计算结果
DROP TABLE IF EXISTS `recommend_content`;
CREATE TABLE `recommend_content` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '接受推荐的用户ID',
  `target_id` bigint NOT NULL COMMENT '推荐的内容ID',
  `target_type` varchar(20) NOT NULL COMMENT '类型：MOVEMENT, VIDEO',
  `score` decimal(6,2) NOT NULL COMMENT '内容推荐得分',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算生成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
  KEY `idx_user_score` (`user_id`, `score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容推荐结果表 (结果层)';

-- ==========================================================
-- 基础设施：领域事件发件箱
-- ==========================================================

-- 7. 领域事件发件箱表 (quadra_recommend 服务私有)
DROP TABLE IF EXISTS `outbox_event`;
CREATE TABLE `outbox_event` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `event_id` varchar(64) NOT NULL COMMENT '事件 ID (UUID)',
  `event_type` varchar(100) NOT NULL COMMENT '事件类型 (如 UserActionLoggedEvent)',
  `aggregate_type` varchar(50) NOT NULL COMMENT '聚合根类型',
  `aggregate_id` bigint NOT NULL COMMENT '聚合根 ID',
  `payload` json NOT NULL COMMENT '事件载荷数据',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '投递状态：0-待处理，1-处理中，2-已完成，3-失败',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
  `error_msg` text DEFAULT NULL COMMENT '最后一次失败原因',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_id` (`event_id`),
  KEY `idx_status_next_retry` (`status`, `next_retry_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领域事件发件箱表';