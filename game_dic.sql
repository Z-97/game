/*
 Navicat Premium Data Transfer

 Source Server         : 120
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : 172.16.10.120
 Source Database       : game_dic

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : utf-8

 Date: 12/05/2017 13:33:54 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `area_config`
-- ----------------------------
DROP TABLE IF EXISTS `area_config`;
CREATE TABLE `area_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `platform_id` int(11) NOT NULL DEFAULT '1' COMMENT '平台ID',
  `area_name` varchar(32) NOT NULL DEFAULT '' COMMENT '地区名',
  `area_code` varchar(32) NOT NULL DEFAULT '' COMMENT '地区码',
  `app_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'APP名',
  `website` varchar(256) DEFAULT NULL COMMENT '网址',
  `maintenance` tinyint(1) NOT NULL DEFAULT '0' COMMENT '游戏维护',
  `pay_url` varchar(256) DEFAULT '' COMMENT '支付网址',
  `exchange_url` varchar(256) DEFAULT '' COMMENT '兑换网址',
  `review` tinyint(1) NOT NULL DEFAULT '0' COMMENT '审核模式',
  `alipay` tinyint(1) NOT NULL DEFAULT '1' COMMENT '支付宝支付',
  `weixin` tinyint(1) NOT NULL DEFAULT '1' COMMENT '微信支付',
  `agent` tinyint(1) NOT NULL DEFAULT '0' COMMENT '代理支付',
  `announcement_id` int(11) DEFAULT NULL COMMENT '公告ID',
  `disabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否禁用',
  `shared` tinyint(1) NOT NULL DEFAULT '0' COMMENT '共享配置',
  `qq_pay` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'QQ支付',
  `union_pay` tinyint(1) NOT NULL DEFAULT '1' COMMENT '银联支付',
  `jd_pay` tinyint(1) NOT NULL DEFAULT '1' COMMENT '京东支付',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `black_list`
-- ----------------------------
DROP TABLE IF EXISTS `black_list`;
CREATE TABLE `black_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int(11) DEFAULT NULL COMMENT '类型(0:mac,1:ip)',
  `val` varchar(256) DEFAULT NULL COMMENT '值',
  `endTime` datetime DEFAULT NULL COMMENT '结束时间',
  `desc` varchar(1024) DEFAULT NULL COMMENT '描述',
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `brnn_room`
-- ----------------------------
DROP TABLE IF EXISTS `brnn_room`;
CREATE TABLE `brnn_room` (
  `id` int(11) NOT NULL COMMENT '房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `lower` int(11) DEFAULT NULL COMMENT '进入下限(金币)',
  `killRate` int(11) DEFAULT NULL COMMENT '系统通杀率百分比',
  `killRateOffset` int(11) DEFAULT NULL COMMENT '系统通杀率百分比偏移',
  `killMinCardsType` int(11) DEFAULT NULL COMMENT '系统通杀最小牌型',
  `killMinCardsRate` int(11) DEFAULT NULL COMMENT '系统通杀最小牌型百分比',
  `switchCardsType` int(11) DEFAULT NULL COMMENT '闲家最大下注需要换牌的最小牌型',
  `switchCardsRate` int(11) DEFAULT NULL COMMENT '闲家最大下注需要换牌的百分比',
  `robotNum` int(11) DEFAULT NULL COMMENT '机器人人数',
  `robotLowerGold` int(11) DEFAULT NULL COMMENT '机器人携带金币下限',
  `robotUpperGold` int(11) DEFAULT NULL COMMENT '机器人携带金币上限',
  `bankerRobotNum` int(11) DEFAULT NULL COMMENT '上庄机器人人数',
  `bankerRobotBirthRate` int(11) DEFAULT NULL COMMENT '上庄机器人生成百分比',
  `bankerRobotRate` int(11) DEFAULT NULL COMMENT '上庄机器人申请庄家百分比',
  `bankerRobotLowerGold` int(11) DEFAULT NULL COMMENT '上庄机器人携带金币下限',
  `bankerRobotUpperGold` int(11) DEFAULT NULL COMMENT '上庄机器人携带金币上限',
  `robotLowerRound` int(11) DEFAULT NULL COMMENT '机器人下限局数',
  `robotUpperRound` int(11) DEFAULT NULL COMMENT '机器人上限局数',
  `robotMinGold` int(11) DEFAULT NULL COMMENT '机器人最小金币',
  `robotBetLowerRate` int(11) DEFAULT NULL COMMENT '下注下限百分比',
  `robotBetUpperRate` int(11) DEFAULT NULL COMMENT '下注上限百分比',
  `playerBankerSwitchCardsType` int(11) DEFAULT NULL COMMENT '玩家做庄通杀时需要换牌的最小牌型',
  `playerBankerSwitchCardsRate` int(11) DEFAULT NULL COMMENT '玩家做庄通杀时需要换牌的百分比',
  `playerBankerLoseRate` int(11) DEFAULT NULL COMMENT '玩家做庄通赔的百分比',
  `table` int(11) DEFAULT NULL COMMENT '桌子',
  `tablePlayerNum` int(11) DEFAULT NULL COMMENT '桌子最大人数',
  `bankerGold` int(11) DEFAULT NULL COMMENT '玩家做庄金币',
  `bankerRound` int(11) DEFAULT NULL COMMENT '庄家局数',
  `sysBankerGold` int(11) DEFAULT NULL COMMENT '系统坐庄金币',
  `taxRate` int(11) DEFAULT NULL COMMENT '税率百分比',
  `ableBetChips` varchar(255) DEFAULT NULL COMMENT '下注筹码(金币)规则',
  `multiples` varchar(255) DEFAULT NULL COMMENT '倍数规则(没牛到牛九-牛牛-四花-四炸-五花-五小)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `brnn_time`
-- ----------------------------
DROP TABLE IF EXISTS `brnn_time`;
CREATE TABLE `brnn_time` (
  `id` int(11) NOT NULL COMMENT 'id',
  `time` int(11) DEFAULT NULL COMMENT '时间/s',
  `desc` varchar(128) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `buyu_curve`
-- ----------------------------
DROP TABLE IF EXISTS `buyu_curve`;
CREATE TABLE `buyu_curve` (
  `id` int(11) NOT NULL COMMENT '路径id',
  `angle` int(11) DEFAULT NULL COMMENT '夹角',
  `ratio` int(11) DEFAULT NULL COMMENT '缩放比例',
  `data` varchar(2048) DEFAULT NULL COMMENT '路径配置数据',
  `desc` varchar(1024) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `buyu_fish`
-- ----------------------------
DROP TABLE IF EXISTS `buyu_fish`;
CREATE TABLE `buyu_fish` (
  `id` int(11) NOT NULL COMMENT '鱼id',
  `bbx_id` int(11) DEFAULT NULL COMMENT '包围盒id',
  `lock_leve` int(11) DEFAULT NULL COMMENT '锁定等级',
  `type` int(11) DEFAULT NULL COMMENT '鱼类型(1:普通鱼,2:同类炸弹,3:范围炸弹)',
  `bombRange` int(11) DEFAULT NULL COMMENT '鱼类型为3范围炸弹鱼的范围',
  `fishs` varchar(64) DEFAULT NULL COMMENT '同类炸弹鱼种类',
  `min_rate` int(11) DEFAULT NULL COMMENT '最小倍率',
  `max_rate` int(11) DEFAULT NULL COMMENT '最大倍率',
  `death_strategy` int(11) DEFAULT NULL COMMENT '鱼死亡后的刷鱼策略id',
  `notice_type` int(11) DEFAULT NULL COMMENT '公告类型(0:无公告,1:全服公告,2:本房间公告)',
  `notice_content` varchar(1024) DEFAULT NULL COMMENT '公告内容',
  `desc` varchar(1024) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `buyu_room`
-- ----------------------------
DROP TABLE IF EXISTS `buyu_room`;
CREATE TABLE `buyu_room` (
  `id` int(11) NOT NULL COMMENT '房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `type` int(11) DEFAULT NULL COMMENT '0:普通房间,1:免费房',
  `freeGold` int(11) DEFAULT NULL COMMENT '免费房的免费金币',
  `freeTime` int(11) DEFAULT NULL COMMENT '免费房的时间(秒)',
  `scences` varchar(128) DEFAULT NULL COMMENT '鱼的场景',
  `controlParmLower` int(11) DEFAULT NULL COMMENT '房间整体控制参数',
  `controlParmUpper` int(11) DEFAULT NULL COMMENT '房间整体控制参数',
  `killRate` int(11) DEFAULT NULL COMMENT '必杀率(百分比分子)',
  `fishControl` varchar(2048) DEFAULT NULL COMMENT '房间鱼控制',
  `roomNoticeLower` int(11) DEFAULT NULL COMMENT '打死鱼房间公告金币下限',
  `serverNoticeLower` int(11) DEFAULT NULL COMMENT '打死鱼全服公告金币下限',
  `lower` int(11) DEFAULT NULL COMMENT '进入下限(金币)',
  `table` int(11) DEFAULT NULL COMMENT '桌子数',
  `chair` int(11) DEFAULT NULL COMMENT '每桌椅子数',
  `batteryScores` varchar(256) DEFAULT NULL COMMENT '炮台分数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `buyu_scence`
-- ----------------------------
DROP TABLE IF EXISTS `buyu_scence`;
CREATE TABLE `buyu_scence` (
  `id` int(11) NOT NULL COMMENT '场景id',
  `strategys` varchar(128) DEFAULT NULL COMMENT '刷鱼策略',
  `lifeTime` int(11) DEFAULT NULL COMMENT '场景存在时间（秒）',
  `desc` varchar(512) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `buyu_strategy`
-- ----------------------------
DROP TABLE IF EXISTS `buyu_strategy`;
CREATE TABLE `buyu_strategy` (
  `id` int(11) NOT NULL COMMENT '策略id',
  `type` int(11) DEFAULT NULL COMMENT '类型(1:普通,2:鱼阵,3:点,4:线,5:圆,6:鱼排,7:死后出圈鱼)',
  `data` varchar(1024) DEFAULT NULL COMMENT '配置数据(json格式)',
  `desc` varchar(512) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `client_switch_display_control`
-- ----------------------------
DROP TABLE IF EXISTS `client_switch_display_control`;
CREATE TABLE `client_switch_display_control` (
  `id` int(11) NOT NULL COMMENT 'id',
  `val` varchar(128) DEFAULT NULL COMMENT '名称',
  `desc` varchar(256) DEFAULT NULL COMMENT '值',
  `platform_id` int(11) NOT NULL DEFAULT '1' COMMENT '平台ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ddz_room`
-- ----------------------------
DROP TABLE IF EXISTS `ddz_room`;
CREATE TABLE `ddz_room` (
  `id` int(11) NOT NULL COMMENT '房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `base` int(11) DEFAULT NULL COMMENT '底分',
  `lower` int(11) DEFAULT NULL COMMENT '进入下限',
  `table` int(11) DEFAULT NULL COMMENT '桌子数',
  `chair` int(11) DEFAULT NULL COMMENT '每桌椅子数',
  `taxRate` int(11) DEFAULT NULL COMMENT '税率百分比',
  `robotLowerGold` int(11) DEFAULT NULL COMMENT '机器人金币下限',
  `robotUpperGold` int(11) DEFAULT NULL COMMENT '机器人金币上限',
  `badCardRate` int(11) DEFAULT NULL COMMENT '机器人烂牌百分比',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ddz_time`
-- ----------------------------
DROP TABLE IF EXISTS `ddz_time`;
CREATE TABLE `ddz_time` (
  `id` int(11) NOT NULL COMMENT 'id',
  `time` int(11) DEFAULT NULL COMMENT '时间/s',
  `desc` varchar(128) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `game`
-- ----------------------------
DROP TABLE IF EXISTS `game`;
CREATE TABLE `game` (
  `id` int(11) NOT NULL COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '游戏名称',
  `desc` varchar(255) DEFAULT NULL COMMENT '描述',
  `state` int(11) DEFAULT NULL COMMENT '状态(0关闭1开启2暂未开放)',
  `seq` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `game_switch_function_control`
-- ----------------------------
DROP TABLE IF EXISTS `game_switch_function_control`;
CREATE TABLE `game_switch_function_control` (
  `id` int(11) NOT NULL COMMENT 'id',
  `val` varchar(128) DEFAULT NULL COMMENT '名称',
  `desc` varchar(256) DEFAULT NULL COMMENT '值',
  `platform_id` int(11) NOT NULL DEFAULT '1' COMMENT '平台ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `hhdz_room`
-- ----------------------------
DROP TABLE IF EXISTS `hhdz_room`;
CREATE TABLE `hhdz_room` (
  `id` int(11) NOT NULL COMMENT '房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `lower` int(11) DEFAULT NULL COMMENT '进入下限(金币)',
  `killRate` int(11) DEFAULT NULL COMMENT '系统通杀率百分比',
  `killRateOffset` int(11) DEFAULT NULL COMMENT '系统通杀率百分比偏移',
  `robotNum` int(11) DEFAULT NULL COMMENT '机器人人数',
  `robotLowerGold` int(11) DEFAULT NULL COMMENT '机器人携带金币下限',
  `robotUpperGold` int(11) DEFAULT NULL COMMENT '机器人携带金币上限',
  `robotLowerRound` int(11) DEFAULT NULL COMMENT '机器人下限局数',
  `robotUpperRound` int(11) DEFAULT NULL COMMENT '机器人上限局数',
  `robotMinGold` int(11) DEFAULT NULL COMMENT '机器人最小金币',
  `robotBetLowerRate` int(11) DEFAULT NULL COMMENT '下注下限百分比',
  `robotBetUpperRate` int(11) DEFAULT NULL COMMENT '下注上限百分比',
  `table` int(11) DEFAULT NULL COMMENT '桌子',
  `tablePlayerNum` int(11) DEFAULT NULL COMMENT '桌子最大人数',
  `taxRate` int(11) DEFAULT NULL COMMENT '税率百分比',
  `ableBetChips` varchar(255) DEFAULT NULL COMMENT '下注筹码(金币)规则',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `hhdz_time`
-- ----------------------------
DROP TABLE IF EXISTS `hhdz_time`;
CREATE TABLE `hhdz_time` (
  `id` int(11) NOT NULL COMMENT 'id',
  `time` int(11) DEFAULT NULL COMMENT '时间/s',
  `desc` varchar(128) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ip_limit_config`
-- ----------------------------
DROP TABLE IF EXISTS `ip_limit_config`;
CREATE TABLE `ip_limit_config` (
  `id` int(11) NOT NULL COMMENT 'id',
  `startTime` int(11) DEFAULT NULL COMMENT 'IP限制的开始时间（小时）',
  `endTime` int(11) DEFAULT NULL COMMENT 'IP限制的结束时间（小时）',
  `val` int(11) DEFAULT NULL COMMENT '限制注册的数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `jdnn_room`
-- ----------------------------
DROP TABLE IF EXISTS `jdnn_room`;
CREATE TABLE `jdnn_room` (
  `id` int(11) NOT NULL COMMENT '房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `lower` int(11) DEFAULT NULL COMMENT '进入下限(金币)',
  `table` int(11) DEFAULT NULL COMMENT '桌子',
  `tablePlayerNum` int(11) DEFAULT NULL COMMENT '桌子最大人数',
  `taxRate` int(11) DEFAULT NULL COMMENT '税率百分比',
  `ableBetScore` varchar(255) DEFAULT NULL COMMENT '叫分倍数(金币)规则',
  `ableBetBanker` varchar(255) DEFAULT NULL COMMENT '抢庄倍数(金币)规则',
  `multiples` varchar(255) DEFAULT NULL COMMENT '倍数规则(没牛到牛九-牛牛-四花-四炸-五花-五小)',
  `base` int(11) DEFAULT NULL COMMENT '底分',
  `robotNum` int(11) DEFAULT NULL COMMENT '机器人人数',
  `robotLowerGold` int(11) DEFAULT NULL COMMENT '机器人携带金币下限',
  `robotUpperGold` int(11) DEFAULT NULL COMMENT '机器人携带金币上限',
  `robotUpperRound` int(11) DEFAULT NULL COMMENT '机器人上限局数',
  `robotLowerRound` int(11) DEFAULT NULL COMMENT '机器人下限局数',
  `matchRobotLower` int(11) DEFAULT NULL COMMENT '房间人数小于N时匹配机器人',
  `replaceRobotRate` int(11) DEFAULT NULL COMMENT '当有机器人退出房间后，几率新机器人补充进来的。',
  `robotBetBankerRate` varchar(255) DEFAULT NULL COMMENT '机器人抢庄几率',
  `robotCallSroceRateByPlayer` varchar(255) DEFAULT NULL COMMENT '机器人叫分几率(玩家当庄)',
  `robotCallSroceRateByRobot` varchar(255) DEFAULT NULL COMMENT '机器人叫分几率(机器人当庄)',
  `RobotFreeKillRate` int(11) DEFAULT NULL COMMENT '减损通杀：玩家庄时，当庄家牌为最大且为牛牛及以上牌型时，一定几率重新发牌直到最大牌型小于牛牛，庄家仍然拿最大牌，其他玩家随机拿牌',
  `RobotFreeBankerKillRate` int(11) DEFAULT NULL COMMENT '减少AI通赔：AI庄时，AI庄最小时，一定几率在最大和最小之间（不包含最大最小牌），随机选取一副',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `jdnn_time`
-- ----------------------------
DROP TABLE IF EXISTS `jdnn_time`;
CREATE TABLE `jdnn_time` (
  `id` int(11) NOT NULL COMMENT 'id',
  `time` int(11) DEFAULT NULL COMMENT '时间/s',
  `desc` varchar(128) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `level`
-- ----------------------------
DROP TABLE IF EXISTS `level`;
CREATE TABLE `level` (
  `id` int(11) NOT NULL COMMENT 'id',
  `experience` int(11) DEFAULT NULL COMMENT '所需经验',
  `title` varchar(32) DEFAULT NULL COMMENT '头衔',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '显示内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `package_switch_control`
-- ----------------------------
DROP TABLE IF EXISTS `package_switch_control`;
CREATE TABLE `package_switch_control` (
  `package_id` varchar(32) NOT NULL COMMENT '渠道id()',
  `show_alipay_bing` int(11) DEFAULT NULL COMMENT '支付宝绑定(0关闭1开启)',
  `show_pmd` int(11) DEFAULT NULL COMMENT '跑马灯(0关闭1开启)',
  `show_exchange` int(11) DEFAULT NULL COMMENT '是否显示兑换(0关闭1开启)',
  `show_buyu_fcsm` int(11) DEFAULT NULL COMMENT '是否显示捕鱼的分场说明(0关闭1开启)',
  `enter_small_game` int(11) DEFAULT NULL COMMENT '是否能够进入小游戏(0关闭1开启)',
  `show_help` int(11) DEFAULT NULL COMMENT '是否显示客服按钮(0关闭1开启)',
  `show_email` int(11) DEFAULT NULL COMMENT '是否显示消息按钮(0关闭1开启)',
  `show_vip` int(11) DEFAULT NULL COMMENT '是否显示vip(0关闭1开启)',
  `show_gg_bindguide` int(11) DEFAULT NULL COMMENT '是否显示公告与绑定引导(0关闭1开启)',
  `switch_recharge` int(11) DEFAULT NULL COMMENT '是否切换到非审核的充值状态(0关闭1开启)\n',
  `show_wechat_recharge` int(11) DEFAULT NULL COMMENT '是否显示充值微信tab按钮(0关闭1开启)\n',
  `show_agent_recharge` int(11) DEFAULT NULL COMMENT '是否显示代理充值(0关闭1开启)',
  `show_agent_zs` int(11) DEFAULT NULL COMMENT ' 是否可以显示代理招商(0关闭1开启)\n',
  `show_apply_agent` int(11) DEFAULT NULL COMMENT '是否可以申请代理(0关闭1开启)',
  `show_alipay_recharge` int(11) DEFAULT NULL COMMENT '是否显示支付宝充值(0关闭1开启)',
  `show_alipay_exchange` int(11) DEFAULT NULL COMMENT '是否显示支付宝兑换(0关闭1开启)',
  `show_agent_exchange` int(11) DEFAULT NULL COMMENT '是否显示代理兑现(0关闭1开启)',
  `show_bank_transfer` int(11) DEFAULT NULL COMMENT '是否显示银行转账(0关闭1开启)',
  `show_qqpay_recharge` int(11) DEFAULT NULL COMMENT '是否显示qq支付(0关闭1开启)',
  `show_unionpay_recharge` int(11) DEFAULT NULL COMMENT '是否显示银联支付(0关闭1开启)',
  `show_jdpay_recharge` int(11) NOT NULL DEFAULT '0' COMMENT '是否显示京东支付',
  `recharge_strategy` int(11) DEFAULT '1',
  PRIMARY KEY (`package_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `player_black_list`
-- ----------------------------
DROP TABLE IF EXISTS `player_black_list`;
CREATE TABLE `player_black_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `playerId` bigint(20) DEFAULT NULL COMMENT '玩家id ',
  `gameNo` int(11) DEFAULT NULL COMMENT '游戏id',
  `inCome` bigint(20) DEFAULT NULL COMMENT '营收',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `qznn_room`
-- ----------------------------
DROP TABLE IF EXISTS `qznn_room`;
CREATE TABLE `qznn_room` (
  `id` int(11) NOT NULL COMMENT '房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `lower` int(11) DEFAULT NULL COMMENT '进入下限(金币)',
  `table` int(11) DEFAULT NULL COMMENT '桌子',
  `tablePlayerNum` int(11) DEFAULT NULL COMMENT '桌子最大人数',
  `taxRate` int(11) DEFAULT NULL COMMENT '税率百分比',
  `ableBetScore` varchar(255) DEFAULT NULL COMMENT '叫分倍数(金币)规则',
  `ableBetBanker` varchar(255) DEFAULT NULL COMMENT '抢庄倍数(金币)规则',
  `multiples` varchar(255) DEFAULT NULL COMMENT '倍数规则(没牛到牛九-牛牛-四花-四炸-五花-五小)',
  `base` int(11) DEFAULT NULL COMMENT '底分',
  `robotNum` int(11) DEFAULT NULL COMMENT '机器人人数',
  `robotLowerGold` int(11) DEFAULT NULL COMMENT '机器人携带金币下限',
  `robotUpperGold` int(11) DEFAULT NULL COMMENT '机器人携带金币上限',
  `robotUpperRound` int(11) DEFAULT NULL COMMENT '机器人上限局数',
  `robotLowerRound` int(11) DEFAULT NULL COMMENT '机器人下限局数',
  `matchRobotLower` int(11) DEFAULT NULL COMMENT '房间人数小于N时匹配机器人',
  `replaceRobotRate` int(11) DEFAULT NULL COMMENT '当有机器人退出房间后，几率新机器人补充进来的。',
  `robotBetBankerRate` varchar(255) DEFAULT NULL COMMENT '机器人抢庄几率',
  `robotCallSroceRateByPlayer` varchar(255) DEFAULT NULL COMMENT '机器人叫分几率(玩家当庄)',
  `robotCallSroceRateByRobot` varchar(255) DEFAULT NULL COMMENT '机器人叫分几率(机器人当庄)',
  `RobotFreeKillRate` int(11) DEFAULT NULL COMMENT '减损通杀：玩家庄时，当庄家牌为最大且为牛牛及以上牌型时，一定几率重新发牌直到最大牌型小于牛牛，庄家仍然拿最大牌，其他玩家随机拿牌',
  `RobotFreeBankerKillRate` int(11) DEFAULT NULL COMMENT '减少AI通赔：AI庄时，AI庄最小时，一定几率在最大和最小之间（不包含最大最小牌），随机选取一副',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `qznn_time`
-- ----------------------------
DROP TABLE IF EXISTS `qznn_time`;
CREATE TABLE `qznn_time` (
  `id` int(11) NOT NULL COMMENT 'id',
  `time` int(11) DEFAULT NULL COMMENT '时间/s',
  `desc` varchar(128) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `sensitive_word`
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word` (
  `id` int(11) NOT NULL COMMENT 'id',
  `word` varchar(128) DEFAULT NULL COMMENT '敏感词',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `shuiguoji_icon`
-- ----------------------------
DROP TABLE IF EXISTS `shuiguoji_icon`;
CREATE TABLE `shuiguoji_icon` (
  `id` int(11) NOT NULL COMMENT '图标id',
  `name` varchar(255) DEFAULT NULL COMMENT '图标名称',
  `desc` varchar(255) DEFAULT NULL COMMENT '图标描述',
  `type` tinyint(4) DEFAULT NULL COMMENT '类型(0:人物,1:武器,2:其他)',
  `weight` int(11) DEFAULT NULL COMMENT '权重(合计权重1w)',
  `effect1` varchar(255) DEFAULT NULL COMMENT '单独特效id',
  `effect2` varchar(255) DEFAULT NULL COMMENT '全体特效id',
  `music1` varchar(255) DEFAULT NULL COMMENT '单独音效',
  `music2` varchar(255) DEFAULT NULL COMMENT '全体音效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `shuiguoji_icon_reward`
-- ----------------------------
DROP TABLE IF EXISTS `shuiguoji_icon_reward`;
CREATE TABLE `shuiguoji_icon_reward` (
  `id` int(11) NOT NULL COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT '奖励名称',
  `threeLine` int(11) DEFAULT NULL COMMENT '3连线倍数',
  `fourLine` int(11) DEFAULT NULL COMMENT '4连线倍数',
  `fiveLine` int(11) DEFAULT NULL COMMENT '5连线倍数',
  `all` int(11) DEFAULT NULL COMMENT '全盘奖倍数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `shuiguoji_line_coord`
-- ----------------------------
DROP TABLE IF EXISTS `shuiguoji_line_coord`;
CREATE TABLE `shuiguoji_line_coord` (
  `id` int(11) NOT NULL COMMENT '线条（id）',
  `line` int(11) DEFAULT NULL COMMENT '线条序号',
  `row` int(11) DEFAULT NULL COMMENT '行',
  `column` int(11) DEFAULT NULL COMMENT '列',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `shuiguoji_room`
-- ----------------------------
DROP TABLE IF EXISTS `shuiguoji_room`;
CREATE TABLE `shuiguoji_room` (
  `id` int(11) NOT NULL COMMENT '房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `lower` int(11) DEFAULT NULL COMMENT '进入下限',
  `table` int(11) DEFAULT NULL COMMENT '桌子数',
  `ableBetChips` varchar(256) DEFAULT NULL COMMENT '下注筹码(金币)规则',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `sys_config`
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` int(11) NOT NULL COMMENT 'id',
  `type` int(11) DEFAULT NULL COMMENT '0:普通类型,1:开关类型',
  `val` varchar(128) DEFAULT NULL COMMENT '名称',
  `desc` varchar(256) DEFAULT NULL COMMENT '值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `vip`
-- ----------------------------
DROP TABLE IF EXISTS `vip`;
CREATE TABLE `vip` (
  `id` int(11) NOT NULL COMMENT 'id',
  `batterySkin` int(11) DEFAULT NULL COMMENT '炮台皮肤',
  `recharge` int(11) DEFAULT NULL COMMENT '充值',
  `icon` int(11) DEFAULT NULL COMMENT '图标',
  `desc` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `white_list`
-- ----------------------------
DROP TABLE IF EXISTS `white_list`;
CREATE TABLE `white_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `playerId` bigint(20) DEFAULT NULL COMMENT '玩家id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `zjh_room`
-- ----------------------------
DROP TABLE IF EXISTS `zjh_room`;
CREATE TABLE `zjh_room` (
  `id` int(11) NOT NULL COMMENT '房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `lower` int(11) DEFAULT NULL COMMENT '进入下限金币',
  `table` int(11) DEFAULT NULL COMMENT '桌子数',
  `base` int(11) DEFAULT NULL COMMENT '底注',
  `controlCardRate` int(11) DEFAULT NULL COMMENT '控制发大牌概率百分比',
  `taxRate` int(11) DEFAULT NULL COMMENT '税率百分比',
  `top` int(11) DEFAULT NULL COMMENT '封顶，桌面最多',
  `maxRound` int(11) DEFAULT NULL COMMENT '最大轮数',
  `chips` varchar(255) DEFAULT NULL COMMENT '可用下注筹码',
  `robotLowerNum` int(11) DEFAULT NULL COMMENT '机器人携带金币下限',
  `robotUpperNum` int(11) DEFAULT NULL COMMENT '机器人携带金币上限',
  `robotLowerGold` int(11) DEFAULT NULL COMMENT '机器人携带金币下限',
  `robotUpperGold` int(11) DEFAULT NULL COMMENT '机器人携带金币上限',
  `robotLowerRound` int(11) DEFAULT NULL COMMENT '机器人下限局数',
  `robotUpperRound` int(11) DEFAULT NULL COMMENT '机器人上限局数',
  `robotMaxCardRate` int(11) DEFAULT NULL COMMENT '机器人最大牌百分比',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `zjh_time`
-- ----------------------------
DROP TABLE IF EXISTS `zjh_time`;
CREATE TABLE `zjh_time` (
  `id` int(11) NOT NULL COMMENT 'id',
  `time` int(11) DEFAULT NULL COMMENT '时间/s',
  `desc` varchar(128) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
