/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.player.manager;
import java.util.Collection;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.PlayerProto.PlayerPB;
import com.alex.game.player.PlayerProto.ResBankGoldChangeMsg;
import com.alex.game.player.PlayerProto.ResGoldChangeMsg;
import com.alex.game.player.PlayerProto.ResPlayerInfo;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * 玩家消息管理
 *
 * @author Alex
 * @date 2016/8/5 16:04
 */
@Singleton
public class PlayerMsgMgr {

    @Inject
    private PlayerMgr playerMgr;
  
    /**
     * 向玩家发送消息
     * @param player
     * @param protocol
     * @param byteString
     */
    public void sendMsg(Player player,int protocol,ByteString byteString) {
		CommonMessage.Builder commonMessage =CommonMessage.newBuilder();
		commonMessage.setId(protocol);
		commonMessage.setContent(byteString);
	    writeMsg(player, commonMessage.build());
	}
    /**
     * 向玩家发送消息
     *
     * @param player
     * @param msg
     */
    public void writeMsg(Player player, CommonMessage msg) {
        if (player != null) {
            writeMsg(player.channel, msg);
        }
    }

    /**
     * 向玩家发送消息
     *
     * @param playerId
     * @param msg
     */
    public void writeMsg(long playerId, CommonMessage msg) {
        writeMsg(playerMgr.getPlayer(playerId), msg);
    }

    /**
     * 向指定channel发送消息
     *
     * @param channel
     * @param msg
     */
    public void writeMsg(Channel channel, CommonMessage msg) {
        if (channel != null && channel.isActive()) {
        	ByteBuf result = Unpooled.buffer();
            result.writeBytes(msg.toByteArray());
        	BinaryWebSocketFrame frame=new BinaryWebSocketFrame(result);
        	channel.writeAndFlush(frame);
        }
    }
    /**
     * 向玩家发送消息
     *
     * @param playerId
     * @param msg
     */
    public void writeMsg(long playerId, int protocol,ByteString byteString) {
    	sendMsg(playerMgr.getPlayer(playerId), protocol,byteString);
    }
    /**
     * 全服发送消息,全服的发送先对消息编码再发送,避免在每个玩家channel中重复编码
     *
     * @param msg
     */
    public void writeWorld(CommonMessage msg) {
    	// 默认大小256字节
    	/*ByteBuf msgBuf = Unpooled.wrappedBuffer(new byte[256]);
    	msgBuf.setIndex(0, 0);
    	ResMsgEncoder.encode(msg, msgBuf);*/
    	/*for (Player player : playerMgr.onLinePlayers().values()) {
    		writeMsg(player.channel, msgBuf);
    	}*/
    	
    	for (Player player : playerMgr.onLinePlayers().values()) {
        	writeMsg(player.channel, msg);
        }
    }

    /**
     * 向多个玩家发送消息
     *
     * @param playerIds
     * @param msg
     */
    public void write(Collection<Long> playerIds, CommonMessage msg) {
        playerIds.forEach((playerId) -> writeMsg(playerId, msg));
    }

    /**
     * 发送玩家信息消息
     *
     * @param player
     */
    public void sendPlayerInfoMsg(Player player) {
    	ResPlayerInfo.Builder msg = ResPlayerInfo.newBuilder();
    	PlayerPB.Builder playerInfo =getPlayerInfo(player);
		msg.setPlayer(playerInfo);
		CommonMessage.Builder commonMessage =CommonMessage.newBuilder();
		commonMessage.setId(MsgHandlerFactory.getProtocol("player.ResPlayerInfo"));
		commonMessage.setContent(msg.build().toByteString());
		writeMsg(player, commonMessage.build());
    }
  
    public PlayerPB.Builder getPlayerInfo(PlayerDom player) {
    	PlayerPB.Builder playerInfo = PlayerPB.newBuilder();
		playerInfo.setBankGold(player.bankGold());
		playerInfo.setExp(player.getExp());
		playerInfo.setGold(player.gold());
		playerInfo.setIcon(player.getIcon());
		playerInfo.setId(player.getId());
		playerInfo.setLevel(player.getLevel());
	
		playerInfo.setLoginIp(player.getLoginIp());
		playerInfo.setNickName(player.getNickName());
		if(player.getPhone()!=null) {
			playerInfo.setPhone(player.getPhone());
		}
		playerInfo.setSex(player.getSex());
		playerInfo.setTourist(player.isTourist());
		playerInfo.setUserName(player.getUserName());
		playerInfo.setVipLevel(player.getVipLevel());
		playerInfo.setAgent(player.isAgent());
		//playerInfo.setKey(player.getLoginKey());
		playerInfo.setPackageId(player.getPackageId());
		playerInfo.setRegisterTime(player.getRegisterTime().getTime()/1000);
		playerInfo.setPlayerType(player.getPlayerType());
		if(player.getBankPwd()==null) {
			playerInfo.setInitBankPwd(false);
		}else {
			playerInfo.setInitBankPwd(true);
		}
		if(player.getIsModfiyNickname()!=null) {
			playerInfo.setIsModfiyNickname(player.getIsModfiyNickname());
		}
		
		return playerInfo;
    }
    public PlayerPB.Builder getPlayerInfo(Player player) {
    	PlayerPB.Builder playerInfo = PlayerPB.newBuilder();
		playerInfo.setBankGold(player.bankGold());
		playerInfo.setExp(player.getExp());
		playerInfo.setGold(player.gold());
		playerInfo.setIcon(player.getIcon());
		playerInfo.setId(player.getId());
		playerInfo.setLevel(player.getLevel());
	
		playerInfo.setLoginIp(player.getLoginIp());
		playerInfo.setNickName(player.getNickName());
		if(player.getPhone()!=null) {
			playerInfo.setPhone(player.getPhone());
		}
		playerInfo.setSex(player.getSex());
		playerInfo.setTourist(player.isTourist());
		playerInfo.setUserName(player.getUserName());
		playerInfo.setVipLevel(player.getVipLevel());
		playerInfo.setAgent(player.isAgent());
		//playerInfo.setKey(player.getLoginKey());
		playerInfo.setPackageId(player.getPackageId());
		playerInfo.setRegisterTime(player.getRegisterTime().getTime()/1000);
		playerInfo.setPlayerType(player.getPlayerType());
		if(player.getBankPwd()==null) {
			playerInfo.setInitBankPwd(false);
		}else {
			playerInfo.setInitBankPwd(true);
		}
		if(player.getIsModfiyNickname()!=null) {
			playerInfo.setIsModfiyNickname(player.getIsModfiyNickname());
		}
		return playerInfo;
    }
	

    /**
     * 玩家金币改变消息
     *
     * @param player
     */
    public void sendGoldChangeMsg(Player player) {
    	ResGoldChangeMsg.Builder msg = ResGoldChangeMsg.newBuilder();
    	msg.setGold(player.gold());
    	sendMsg(player, MsgHandlerFactory.getProtocol("player.ResGoldChangeMsg"), msg.build().toByteString());
    }
    
    /**
     * 玩家金币改变消息
     *
     * @param playerId
     */
    public void sendGoldChangeMsg(long playerId) {
    	ResGoldChangeMsg.Builder msg = ResGoldChangeMsg.newBuilder();
    	Player player = playerMgr.getPlayer(playerId);
    	if (player != null) {
    		msg.setGold(player.gold());
    		sendMsg(player, MsgHandlerFactory.getProtocol("player.ResGoldChangeMsg"), msg.build().toByteString());
		}
    }
    
    /**
     * 玩家银行金币改变消息
     *
     * @param player
     */
    public void sendBankGoldChangeMsg(Player player) {
    	ResBankGoldChangeMsg.Builder msg = ResBankGoldChangeMsg.newBuilder();
    	msg.setBankGold(player.bankGold());
    	sendMsg(player, MsgHandlerFactory.getProtocol("player.ResBankGoldChangeMsg"), msg.build().toByteString());
    }
    
    /**
     * 玩家经验改变消息
     *
     * @param player
     */
//    public void sendExpChangeMsg(Player player) {
//    	ResExpChangeMsg msg = new ResExpChangeMsg();
//    	msg.setVal(player.getExp());
//    	writeMsg(player, msg);
//    }
    
    /**
     * 玩家等级改变消息
     *
     * @param player
     */
//    public void sendLevelChangeMsg(Player player) {
//    	ResLevelChangeMsg msg = new ResLevelChangeMsg();
//    	msg.setVal(player.getLevel());
//    	writeMsg(player, msg);
//    }

    /**
     * 玩家vip等级改变消息
     * @param player
     */
//    public void sendVIPChangeMsg(Player player) {
//    	ResVIPChangeMsg msg = new ResVIPChangeMsg();
//    	msg.setVal(player.getVipLevel());
//    	writeMsg(player, msg);
//		
//	}
    /**
     * 发送错误消息
     * 
     * @param player
     * @param args
     */
//    public void sendErrorMsg(Player player, int msgId, String... args) {
//		ResErrorMsg msg = new ResErrorMsg();
//		msg.setMsgId(msgId);
//		Collections.addAll(msg.getArgs(), args);
//		writeMsg(player, msg);
//	}
    
    /**
     * 发送警告消息
     * 
     * @param player
     * @param args
     */
//    public void sendWarnMsg(Player player, int msgId, String... args) {
//    	ResErrorMsg msg = new ResErrorMsg();
//    	msg.setMsgId(msgId);
//    	Collections.addAll(msg.getArgs(), args);
//    	writeMsg(player, msg);
//    }
//    
    /**
     * 发送提示消息
     * 
     * @param player
     * @param args
     */
//    public void sendTipMsg(Player player, int msgId, String... args) {
//    	ResTipMsg msg = new ResTipMsg();
//    	msg.setMsgId(msgId);
//    	Collections.addAll(msg.getArgs(), args);
//    	writeMsg(player, msg);
//    }
    
    /**
     * 发送右下角提示消息
     * 
     * @param player
     * @param args
     */
//    public void sendShowMsg(Player player, int msgId, String... args) {
//    	ResShowMsg msg = new ResShowMsg();
//    	msg.setMsgId(msgId);
//    	Collections.addAll(msg.getArgs(), args);
//    	writeMsg(player, msg);
//    }

    /**
     * 发送玩家充值金额改变
     * @param player
     */
//	public void sendRechargeMoney(Player player) {
//		ResRechargeMoneyChangeMsg msg = new ResRechargeMoneyChangeMsg();
//    	msg.setVal(player.getRechargeMoney().get());
//    	writeMsg(player, msg);
//	}

	/**
	 * 发送玩家充值成功提示
	 * @param player
	 */
//	public void sendRechargeSuccessTips(Player player,List<RechargeTips> money) {
//		ResRechargeSuccessTipsMsg msg=new ResRechargeSuccessTipsMsg();
//		msg.setTips(money);
//		writeMsg(player, msg);
//	}

}
