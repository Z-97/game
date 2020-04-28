/*
 Navicat Premium Data Transfer

 Source Server         : 内网
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : 172.16.10.120
 Source Database       : game_data

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : utf-8

 Date: 09/02/2017 16:02:54 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `ip_limit`
-- ----------------------------
DROP TABLE IF EXISTS `ip_limit`;
CREATE TABLE `ip_limit` (
  `ip` varchar(255) NOT NULL,
  `register_times` int(11) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `player`
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '玩家id',
  `user_name` varchar(32) NOT NULL COMMENT '用户名',
  `nick_name` varchar(32) NOT NULL COMMENT '玩家昵称',
  `pwd` varchar(128) NOT NULL COMMENT '密码',
  `channel_id` varchar(32) DEFAULT '0' COMMENT '渠道id',
  `package_id` varchar(32) DEFAULT '0' COMMENT '包id',
  `register_ip` varchar(128) DEFAULT '' COMMENT '注册ip,支持ipv6(长度128)',
  `register_device` int(11) DEFAULT NULL COMMENT '注册设备(0:Android,1:iphone)',
  `register_device_model` varchar(128) DEFAULT NULL COMMENT '注册设备型号',
  `register_time` datetime DEFAULT NULL COMMENT '注册时间',
  `register_mac` varchar(128) DEFAULT NULL COMMENT '注册机器码',
  `login_key` varchar(128) DEFAULT NULL COMMENT '登陆key',
  `login_ip` varchar(128) DEFAULT NULL COMMENT '登录ip,支持ipv6(长度128)',
  `login_device` int(11) DEFAULT NULL COMMENT '0安卓1iphone',
  `login_device_model` varchar(128) DEFAULT NULL COMMENT '登录手机机型',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `login_mac` varchar(128) DEFAULT NULL COMMENT '登录机器码',
  `login_count` int(11) DEFAULT '0' COMMENT '登录次数',
  `logout_time` datetime DEFAULT NULL COMMENT '退出时间',
  `tourist` tinyint(1) DEFAULT '0' COMMENT '是否为游客',
  `tourist_mac` varchar(128) DEFAULT NULL,
  `player_type` int(11) DEFAULT '0' COMMENT '玩家类型(0:普通玩家,1:代理,2:线上推广员,3:线下推广员)',
  `agent_recharge_money` bigint(20) DEFAULT '0' COMMENT '代理充值总额',
  `agent_exchange_money` bigint(20) DEFAULT '0' COMMENT '代理提现总额',
  `sex` int(11) DEFAULT '0' COMMENT '	',
  `icon` int(11) DEFAULT '1' COMMENT '用户图标id',
  `phone` varchar(12) DEFAULT NULL COMMENT '绑定手机号',
  `binding_phone_time` datetime DEFAULT NULL COMMENT '绑定手机时间',
  `alipay` varchar(128) DEFAULT NULL COMMENT '支付宝账号',
  `alipay_name` varchar(18) DEFAULT NULL,
  `bank_card` varchar(32) DEFAULT NULL COMMENT '银行卡号',
  `bank_user_name` varchar(32) DEFAULT NULL COMMENT '开户名',
  `bank_name` varchar(128) DEFAULT NULL COMMENT '开户行名称',
  `control` varchar(2048) DEFAULT NULL,
  `gold` bigint(20) DEFAULT '0' COMMENT '金币',
  `tax` bigint(20) DEFAULT '0' COMMENT '税收',
  `win_gold` bigint(20) DEFAULT '0' COMMENT '游戏赢金币总数',
  `lose_gold` bigint(20) DEFAULT '0' COMMENT '游戏输金币总额 ',
  `recharge_money` bigint(20) DEFAULT '0' COMMENT '充值人民币金额',
  `exchange_money` bigint(20) DEFAULT '0' COMMENT '兑换人民币金额(提现金额)',
  `transfer_in_gold` bigint(20) DEFAULT '0' COMMENT '合计转入金币',
  `transfer_out_gold` bigint(20) DEFAULT '0' COMMENT '合计转出金币',
  `exp` bigint(20) DEFAULT '0' COMMENT '经验',
  `level` int(11) DEFAULT '0' COMMENT '玩家等级',
  `online` tinyint(1) DEFAULT '0' COMMENT '是否在线',
  `vip_level` int(11) DEFAULT '0' COMMENT '会员等级',
  `locked` tinyint(1) DEFAULT '0' COMMENT '是否锁住',
  `bank_pwd` varchar(64) DEFAULT 'DD963B3E2936E866133BB07C018C8843' COMMENT '银行密码',
  `bank_gold` bigint(20) DEFAULT '0' COMMENT '银行金币',
  `update_gold_time` datetime DEFAULT NULL COMMENT '玩家行为产生对金币变化更新时间，用于活跃玩家统计',
  `game_time` bigint(20) DEFAULT '0' COMMENT '游戏时间(毫秒)',
  `online_time` bigint(20) DEFAULT '0' COMMENT '在线时长(毫秒)',
  `buyu_skin` int(11) DEFAULT '1' COMMENT '捕鱼炮台皮肤',
  `buyu_hit_gold` bigint(20) DEFAULT '0' COMMENT '打死鱼获得的合计金币',
  `buyu_gold_daily` bigint(20) DEFAULT '0' COMMENT '捕鱼每日赚取金币',
  `buyu_game_time_daily` bigint(20) DEFAULT '0' COMMENT '捕鱼每日游戏时间',
  `orders` mediumtext COMMENT '订单',
  `emails` mediumtext COMMENT '消息',
  `agent_transfer_refund_orders` mediumtext COMMENT '代理转账退款的订单',
  `bank_records` mediumtext COMMENT '银行记录',
  `exchange_records` mediumtext COMMENT '兑换人民币记录',
  `email_tips` tinyint(1) DEFAULT '0' COMMENT '新邮件提示',
  `transfer_gold_tips` tinyint(1) DEFAULT '0' COMMENT '转账提示',
  `customer_tips` tinyint(1) DEFAULT '0' COMMENT '客服反馈提示',
  `update_time` datetime DEFAULT NULL COMMENT '更新数据时间',
  `is_ip_limit` tinyint(1) DEFAULT NULL COMMENT '同一ip注册数量超过5',
  `extension_code` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_index` (`user_name`) USING BTREE,
  UNIQUE KEY `phone_index` (`phone`) USING BTREE,
  UNIQUE KEY `tourist_mac_index` (`tourist_mac`) USING BTREE,
  KEY `login_time_index` (`login_time`) USING BTREE,
  KEY `register_time_index` (`register_time`) USING BTREE,
  KEY `login_time_package_id_index` (`login_time`,`package_id`) USING BTREE,
  KEY `tourist_index` (`tourist`) USING BTREE,
  KEY `register_device_index` (`register_device`) USING BTREE,
  KEY `register_ip_index` (`register_ip`) USING BTREE,
  KEY `login_ip_index` (`login_ip`) USING BTREE,
  KEY `alipay_index` (`alipay`)
) ENGINE=InnoDB AUTO_INCREMENT=380774 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `rank`
-- ----------------------------
DROP TABLE IF EXISTS `rank`;
CREATE TABLE `rank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `level_rank` text,
  `gold_rank` text,
  `game_time_rank` text,
  `rank_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `sys_email`
-- ----------------------------
DROP TABLE IF EXISTS `sys_email`;
CREATE TABLE `sys_email` (
  `id` bigint(20) NOT NULL,
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `content` varchar(100) DEFAULT NULL COMMENT '内容',
  `sender_name` varchar(20) DEFAULT NULL COMMENT '发送者名称',
  `sender_id` bigint(20) DEFAULT NULL,
  `sender_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
