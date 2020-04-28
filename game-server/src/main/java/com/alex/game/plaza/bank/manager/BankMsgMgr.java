package com.alex.game.plaza.bank.manager;
import com.alex.game.bank.BankProto.ResEnterBank;
import com.alex.game.bank.BankProto.ResGetBankGold;
import com.alex.game.bank.BankProto.ResGiftPresent;
import com.alex.game.bank.BankProto.ResResetBankPwd;
import com.alex.game.bank.BankProto.ResSaveGold;
import com.alex.game.bank.BankProto.ResSetBankPwd;
import com.alex.game.bank.BankProto.ResTransferGold;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BankMsgMgr {
	

	@Inject
	private PlayerMsgMgr msgMgr;

	/**
	 * 发送设置初始银行开密码结果
	 * @param player
	 * @param res 1已经设计初始密码
	 */
	public void sendResSetBankPwd(Player player, int res) {
		ResSetBankPwd.Builder resSetBankPwd=ResSetBankPwd.newBuilder();
		resSetBankPwd.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("bank.ResSetBankPwd"), resSetBankPwd.build().toByteString());
	}

	public void sendResResetBankPwd(Player player, int res) {
		ResResetBankPwd.Builder resSetBankPwd=ResResetBankPwd.newBuilder();
		resSetBankPwd.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("bank.ResResetBankPwd"), resSetBankPwd.build().toByteString());
	}

	/**
	 * 存金币
	 * @param player
	 * @param  res  0成功，1金币不足
	 */
	public void sendResSaveGold(Player player, int res) {
		ResSaveGold.Builder rescreateGobangRoom =ResSaveGold.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("bank.ResSaveGold"), rescreateGobangRoom.build().toByteString());
	}

	/**
	 * 取金币返回
	 * @param player
	 * @param res
	 */
	public void sendResGetBankGold(Player player, int res) {
		ResGetBankGold.Builder resSetBankPwd=ResGetBankGold.newBuilder();
		resSetBankPwd.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("bank.ResGetBankGold"), resSetBankPwd.build().toByteString());
		
	}

	/**
	 * 送礼物结果
	 * @param player
	 * @param res
	 */
	public void sendResGiftPresent(Player player, int res) {
		ResGiftPresent.Builder resSetBankPwd=ResGiftPresent.newBuilder();
		resSetBankPwd.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("bank.ResGiftPresent"), resSetBankPwd.build().toByteString());
	}

	/**
	 * 
	 * @param toPlayerId收款人id
	 * @param sendName转款人名字
	 * @param itemId物品id
	 * @param itemNum物品数量
	 */
	public void sendTransferGoldTips(long toPlayerId,String sendName,int itemId,int itemNum) {
		ResTransferGold.Builder resTransferGold=ResTransferGold.newBuilder();
		resTransferGold.setItemId(itemId);
		resTransferGold.setItemNum(itemNum);
		resTransferGold.setSendName(sendName);
		msgMgr.writeMsg(toPlayerId, MsgHandlerFactory.getProtocol("bank.ResTransferGold"), resTransferGold.build().toByteString());
		
	}

	public void sendResEnterBank(Player player, int res) {
		ResEnterBank.Builder resSetBankPwd=ResEnterBank.newBuilder();
		resSetBankPwd.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("bank.ResEnterBank"), resSetBankPwd.build().toByteString());
	}

	

	
	
}
