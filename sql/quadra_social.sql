-- ==========================================================
-- Quadra (高学历相亲交友) 教学样板项目 DDL 脚本
-- 服务域：社交关系域 (quadra_social)
-- 数据库：`quadra_social`
-- 描述：包含用户关注、双向好友、探探类喜欢及访客记录。
-- 特性：无物理外键，纯业务主键关联，高并发表设计。
-- ==========================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS `quadra_social` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quadra_social`;

-- 2. 双向好友关系表 (聚合根)
-- 亮点：双向关系，添加好友时需插入两条记录（A->B 和 B->A）
DROP TABLE IF EXISTS `friendship`;
CREATE TABLE `friendship` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `friend_id` bigint NOT NULL COMMENT '好友用户ID',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间(成为好友时间)',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`, `deleted`) COMMENT '防止重复添加好友',
  KEY `idx_friend_id` (`friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='双向好友关系表';

-- 3. 单向关注关系表 (聚合根)
-- 描述：用于关注大V或视频创作者，高频读取，需同步至 Redis Set
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '操作人(粉丝)用户ID',
  `target_user_id` bigint NOT NULL COMMENT '被关注人(博主)用户ID',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未取消关注，1-已取消关注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_user_id`, `deleted`) COMMENT '防止重复关注',
  KEY `idx_target_user_id` (`target_user_id`) COMMENT '查询某人的粉丝列表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单向关注关系表';

-- 4. 探探类互相喜欢记录表 (聚合根)
-- 描述：左滑(不喜欢)/右滑(喜欢)的行为记录，匹配是基于这些行为计算出来的结果。
DROP TABLE IF EXISTS `user_match_like`;
CREATE TABLE `user_match_like` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '操作人用户ID',
  `target_user_id` bigint NOT NULL COMMENT '被评价人用户ID',
  `action_type` varchar(20) NOT NULL COMMENT '行为类型：LIKE-喜欢(右滑), DISLIKE-不喜欢(左滑)',
  `match_time` datetime DEFAULT NULL COMMENT '互相匹配成功的时间 (不为空代表互相喜欢)',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_user_id`, `deleted`) COMMENT '限制同一用户对同一目标只能操作一次',
  KEY `idx_target_user_id` (`target_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='探探类滑动行为记录表';

-- 5. 主页访客记录表 (聚合根)
-- 描述：记录谁访问了我的主页
DROP TABLE IF EXISTS `user_visitor`;
CREATE TABLE `user_visitor` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '被访问主页的用户ID',
  `visitor_id` bigint NOT NULL COMMENT '访客用户ID',
  `visit_date` date NOT NULL COMMENT '访问日期 (用于聚合统计每天的访问量)',
  `visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '具体访问时间',
  `source` varchar(50) DEFAULT NULL COMMENT '访问来源 (如 首页推荐, 附近的人, 动态圈)',
  `score` decimal(5,2) DEFAULT '0.00' COMMENT '访客缘分得分 (基于推荐系统的计算结果)',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `visit_date`) COMMENT '查询某人某天的访客',
  KEY `idx_visitor_id` (`visitor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主页访客记录表';

-- ==========================================================
-- 异步位置轨迹表 (可选落盘)
-- 描述：核心“附近的人”查询由 Redis GEO (GeoHash) 承担，
-- 此表仅通过 MQ 异步保存用户的最后已知位置，用作数据分析或重建 Redis 缓存。
-- ==========================================================

-- 6. 用户位置轨迹表
DROP TABLE IF EXISTS `user_location_track`;
CREATE TABLE `user_location_track` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `longitude` decimal(10,7) NOT NULL COMMENT '经度',
  `latitude` decimal(10,7) NOT NULL COMMENT '纬度',
  `address_name` varchar(200) DEFAULT NULL COMMENT '位置文本描述',
  `location_point` point NOT NULL COMMENT 'MySQL 8 空间类型 Point，用于备用空间查询',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上报时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`, `deleted`) COMMENT '每个用户仅保留最后一条位置',
  SPATIAL KEY `sp_idx_location` (`location_point`) COMMENT '空间索引 (备用)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户位置异步轨迹表';

-- 7. 领域事件发件箱表 (quadra_social 服务私有)
DROP TABLE IF EXISTS `outbox_event`;
CREATE TABLE `outbox_event` (
  `id` bigint NOT NULL COMMENT '主键，雪花算法',
  `aggregate_type` varchar(50) NOT NULL COMMENT '聚合根类型 (如 Friendship)',
  `aggregate_id` bigint NOT NULL COMMENT '聚合根ID',
  `event_type` varchar(100) NOT NULL COMMENT '事件类型',
  `payload` json NOT NULL COMMENT '事件载荷数据',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '投递状态：0-待投递，1-投递成功，2-投递失败',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间，结合 status=2 使用',
  `error_msg` text DEFAULT NULL COMMENT '最后一次失败原因',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_next_retry` (`status`, `next_retry_time`) COMMENT '定时任务扫描索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领域事件发件箱表';