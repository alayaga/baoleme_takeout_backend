CREATE DATABASE IF NOT EXISTS `baoleme_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `baoleme_db`;

-- 用户账号表
CREATE TABLE `user_account` (
  `username`   VARCHAR(64)  NOT NULL,
  `password`   VARCHAR(100) NOT NULL,
  `role`       VARCHAR(20)  NOT NULL DEFAULT 'client' COMMENT 'client | merchant',
  `profile_id` VARCHAR(64)  DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户画像表
CREATE TABLE `user_profile` (
  `id`                   VARCHAR(64)  NOT NULL,
  `name`                 VARCHAR(50)  NOT NULL,
  `tags`                 VARCHAR(255) DEFAULT '[]',
  `history_orders_count` INT          DEFAULT 0,
  `favorite_category`    VARCHAR(50)  DEFAULT '热销推荐',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜品分类表
CREATE TABLE `category` (
  `id`   VARCHAR(64) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `icon` VARCHAR(50) DEFAULT 'Utensils',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜品表
CREATE TABLE `dish` (
  `id`          VARCHAR(64)    NOT NULL,
  `name`        VARCHAR(100)   NOT NULL,
  `price`       DECIMAL(10,2)  NOT NULL,
  `description` TEXT,
  `image`       VARCHAR(500)   DEFAULT '',
  `category`    VARCHAR(50)    NOT NULL,
  `sales`       INT            DEFAULT 0,
  `stock`       INT            DEFAULT 99,
  `status`      VARCHAR(20)    DEFAULT 'active',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单主表
CREATE TABLE `orders` (
  `id`          VARCHAR(64)   NOT NULL,
  `user_id`     VARCHAR(64)   NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  `status`      VARCHAR(30)   NOT NULL DEFAULT 'pending',
  `created_at`  VARCHAR(50)   NOT NULL,
  `address`     VARCHAR(255)  NOT NULL,
  `phone`       VARCHAR(32)   NOT NULL,
  `note`        VARCHAR(255)  DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单明细表
CREATE TABLE `order_item` (
  `id`        BIGINT       AUTO_INCREMENT,
  `order_id`  VARCHAR(64)  NOT NULL,
  `dish_id`   VARCHAR(64)  NOT NULL,
  `dish_name` VARCHAR(100) NOT NULL,
  `price`     DECIMAL(10,2) NOT NULL,
  `quantity`  INT          NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 客服会话表
CREATE TABLE `chat_session` (
  `id`           VARCHAR(64) NOT NULL,
  `user_id`      VARCHAR(64) NOT NULL,
  `user_email`   VARCHAR(100) DEFAULT '',
  `mode`         VARCHAR(20)  DEFAULT 'bot',
  `status`       VARCHAR(20)  DEFAULT 'active',
  `last_updated` VARCHAR(50)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_userid` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 聊天消息表
CREATE TABLE `chat_message` (
  `id`         VARCHAR(64) NOT NULL,
  `session_id` VARCHAR(64) NOT NULL,
  `role`       VARCHAR(20) NOT NULL,
  `content`    TEXT        NOT NULL,
  `timestamp`  VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========== 预置测试数据 ==========

-- 用户画像
INSERT INTO `user_profile` VALUES ('u1', '王小明 (加班狂人)', '["熬夜加班","偏爱重口味","快捷简餐"]', 24, '川湘名菜');
INSERT INTO `user_profile` VALUES ('admin_1', '管理员', '[]', 0, '热销推荐');

-- 用户账号（密码明文存储，课设演示用）
INSERT INTO `user_account` VALUES ('xiaoming', '123456', 'client', 'u1');
INSERT INTO `user_account` VALUES ('admin', 'admin', 'merchant', 'admin_1');

-- 分类
INSERT INTO `category` VALUES ('1','热销推荐','Flame');
INSERT INTO `category` VALUES ('2','川湘名菜','FlameKindling');
INSERT INTO `category` VALUES ('3','快餐简餐','Utensils');
INSERT INTO `category` VALUES ('4','甜品饮品','CupSoda');
INSERT INTO `category` VALUES ('5','健康轻食','Salad');

-- 菜品
INSERT INTO `dish` VALUES ('d1','招牌黄焖鸡米饭',22.00,'精选嫩滑鸡腿肉，搭配香菇、青椒与秘制酱汁，微火慢炖，汤浓肉香。','https://images.unsplash.com/photo-1596797038530-2c107229654b?w=600','热销推荐',1250,99,'active');
INSERT INTO `dish` VALUES ('d2','川味麻辣香锅',38.00,'多种新鲜蔬菜与肉类自由搭配，大火爆炒，麻辣鲜香，一锅满足。','https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600','川湘名菜',890,99,'active');
INSERT INTO `dish` VALUES ('d3','经典牛肉拉面',18.00,'手工拉制面条，搭配慢炖牛肉与浓郁骨汤，撒上香菜与葱花。','https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=600','快餐简餐',2100,99,'active');
INSERT INTO `dish` VALUES ('d4','鱼香肉丝盖饭',20.00,'经典川菜鱼香肉丝，酸甜微辣，搭配软糯米饭，下饭神器。','https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=600','川湘名菜',760,99,'active');
INSERT INTO `dish` VALUES ('d5','日式照烧鸡腿饭',25.00,'烤制多汁鸡腿，淋上特制照烧酱汁，配以温泉蛋与时蔬。','https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600','快餐简餐',650,99,'active');
INSERT INTO `dish` VALUES ('d6','凯撒沙拉配烟熏三文鱼',35.00,'新鲜罗马生菜，搭配烟熏三文鱼、帕玛森芝士与自制凯撒酱。','https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600','健康轻食',420,99,'active');
INSERT INTO `dish` VALUES ('d7','手工黑糖珍珠奶茶',15.00,'大杯全脂奶牛乳香醇厚，黑糖珍珠咀嚼感十足，甜蜜解压。','https://images.unsplash.com/photo-1558857563-b371033873b8?w=600','甜品饮品',3200,99,'active');
INSERT INTO `dish` VALUES ('d8','芒果椰汁西米露',16.00,'新鲜芒果果肉搭配Q弹西米与香浓椰汁，清爽解腻的饭后甜品。','https://images.unsplash.com/photo-1546039907-7b3a4711b02c?w=600','甜品饮品',1800,99,'active');
