package com.alex.game.plaza.bank.manager;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.alex.game.common.util.MD5Util;
import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dbdic.dic.ItemDic;
import com.alex.game.dbdic.dic.SysConfigDic;
import com.alex.game.dblog.GiftAction;
import com.alex.game.dblog.LogAction;
import com.alex.game.dblog.bank.DepositeWithdrawGoldLog;
import com.alex.game.dblog.bank.TransferGoldLog;
import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.recharge.RechargeLog;
import com.alex.game.event.manager.EventMgr;
import com.alex.game.event.struct.bank.DepositeWithdrawGoldEvent;
import com.alex.game.event.struct.bank.RechargeSuccessEvent;
import com.alex.game.event.struct.bank.TransferAccounts;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.player.struct.Player;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BankMgr {

	private static final Logger LOG = LoggerFactory.getLogger(BankMgr.class);
	// 银行密码格式，6位数字与字母
	public static final String BANK_PWD_PATTERN ="^[A-Za-z0-9]{6}$";
	@Inject
	private BankMsgMgr bankMsgMgr;
	@Inject
	private PlayerMgr playerMgr;
	
	@Inject
	private EventMgr eventMgr;
	
	@Inject
	private ItemDic itemDic;
	@Inject
	private SysConfigDic sysCfgDic;
	/**
	 * 设置银行初始密码
	 * @param player
	 * @param bankPwd
	 */
	public void initBankPwd(Player player, String bankPwd) {
		
		if(player.getBankPwd()!=null||!bankPwd.matches(BANK_PWD_PATTERN)) {
			bankMsgMgr.sendResSetBankPwd(player,1);
			return;
		}
		player.setBankPwd(MD5Util.encodeWithTail(bankPwd));
		playerMgr.updatePlayerAsync(player);
		bankMsgMgr.sendResSetBankPwd(player, 0);
		LOG.info("玩家[{}][{}]修改银行初始密码成功", player.getUserName(), player.getId());
	}

	/**
	 * 重置银行密码
	 * @param player
	 * @param oldBankPwd
	 * @param newBankPwd
	 * @param code
	 */
	public void resetBankPwd(Player player, String newPwd, String code) {
		if (newPwd == null || !newPwd.matches(BANK_PWD_PATTERN)) {
			bankMsgMgr.sendResResetBankPwd(player, 1);
			LOG.warn("玩家[{}][{}]输入的新的银行密码格式不合法", player.getNickName(), player.getId());
			return;
		}

		player.setBankPwd(MD5Util.encodeWithTail(newPwd));
		playerMgr.updatePlayerAsync(player);
		bankMsgMgr.sendResResetBankPwd(player, 0);
		LOG.info("玩家[{}][{}]修改银行密码成功", player.getUserName(), player.getId());
		
	}

	/**
	 * 银行存钱
	 * @param player
	 * @param gold
	 */
	public void saveBankGold(Player player, long gold) {
		if (gold <= 0) {
			bankMsgMgr.sendResSaveGold(player, 2);
			return;
		}
		if (player.gold() < gold) {
			bankMsgMgr.sendResSaveGold(player, 1);
			LOG.warn("玩家[{}][{}]存入保险箱的金币大于身上的金币", player.getNickName(), player.getId());
			return;
		}
		long beforeGold = player.gold();
		long beforeBankGold = player.bankGold();

		playerMgr.addBankGold(player, gold, LogAction.DEPOSITE_GOLD);
		playerMgr.addGold(player, -gold, LogAction.DEPOSITE_GOLD);
		// 存款日志
		DbLogService.log(
				new DepositeWithdrawGoldLog(player, 0, beforeGold, beforeBankGold, player.gold(), player.bankGold()));
		LOG.info("玩家[{}][{}]存入金币[{}]到银行", player.getNickName(), player.getId(), gold);
		// 触发存取金币事件
		eventMgr.post(new DepositeWithdrawGoldEvent(player));
		bankMsgMgr.sendResSaveGold(player, 0);
	}

	/**
	 * 银行取钱
	 * @param player
	 * @param bankPwd
	 * @param gold
	 */
	public void reqGetBankGold(Player player, long gold) {
//		if (bankPwd == null || !player.getBankPwd().equals(MD5Util.encodeWithTail(bankPwd))) {
//			bankMsgMgr.sendResGetBankGold(player, 1);
//			LOG.warn("玩家[{}][{}]取钱时输入的银行密码错误", player.getNickName(), player.getId());
//			return;
//		}
		if (gold <= 0) {
			return;
		}
		if (player.bankGold() < gold) {
			bankMsgMgr.sendResGetBankGold(player, 2);
			LOG.warn("玩家[{}][{}]取钱的金币大于保险箱里的金币", player.getNickName(), player.getId());
			return;
		}
		long beforeGold = player.gold();
		long beforeBankGold = player.bankGold();

		playerMgr.addBankGold(player, -gold, LogAction.WITHDRAW_GOLD);
		// 数据在玩家线程更新成功后才记录日志
		playerMgr.addGold(player, gold, LogAction.WITHDRAW_GOLD);
		
		bankMsgMgr.sendResGetBankGold(player, 0);
		// 取款日志
		DbLogService.log(
				new DepositeWithdrawGoldLog(player, 1, beforeGold, beforeBankGold, player.gold(), player.bankGold()));
		LOG.info("玩家[{}][{}]从银行提取金币[{}]", player.getNickName(), player.getId(), gold);
		// 触发存取金币事件
		eventMgr.post(new DepositeWithdrawGoldEvent(player));
	}

	/**
	 * 赠送礼物
	 * @param player
	 * @param toPlayerId
	 * @param itemId
	 * @param itemNum
	 */
	public void sendGift(Player player, long toPlayerId, int itemId, int itemNum) {
		PlayerDom targetPlayerDom = playerMgr.selectById(toPlayerId);
		if (targetPlayerDom == null) {
			bankMsgMgr.sendResGiftPresent(player, 2);
			LOG.warn("[{}][{}]赠送礼物的玩家[{}]不存在", player.getNickName(), player.getId(), toPlayerId);
			return;
		}
		if(itemDic.get(itemId)==null) {
			bankMsgMgr.sendResGiftPresent(player, 3);
			LOG.warn("物品id[{}]不存在", itemId);
			return ;
		}
		
		if(itemNum<=0) {
			bankMsgMgr.sendResGiftPresent(player, 4);
			LOG.warn("物品数量[{}]不正确", itemNum);
			return;
		}
		int  itemNumLimit = sysCfgDic.getIntVal(1) ;
		if(itemNum>itemNumLimit) {
			bankMsgMgr.sendResGiftPresent(player, 5);
			return;
		}
		long gold=itemDic.get(itemId).getPrice()*itemNum;
		if (player.gold() < gold) {
			bankMsgMgr.sendResGiftPresent(player, 1);
			LOG.warn("玩家[{}][{}]转账的金币大于保险箱里的金币", player.getNickName(), player.getId());
			return;
		}
		
		long beforeGold = player.gold();
		long beforeBankGold = player.bankGold();

		playerMgr.addGold(player, -gold, LogAction.TRANSFER_GOLD);
		playerMgr.addGold(targetPlayerDom, gold, LogAction.TRANSFER_GOLD);
		
		playerMgr.addAddGiftRecord(player, targetPlayerDom.getNickName(), GiftAction.GIVE, itemId, itemNum, gold);
		playerMgr.addAddGiftRecord(targetPlayerDom, player.getNickName(), GiftAction.GET, itemId, itemNum, gold);
		targetPlayerDom.setTransferGoldTips(true);
		// 给转账提示给收款人
		bankMsgMgr.sendTransferGoldTips(targetPlayerDom.getId(),player.getNickName(),itemId,itemNum);
		// 手动保存一次数据,后台可以查到数据
		playerMgr.updatePlayerAsync(targetPlayerDom);
		// 发送转账成功消息(客户端有逻辑处理)
		bankMsgMgr.sendResGiftPresent(player, 0);
		
		eventMgr.post(new TransferAccounts(player));
		eventMgr.post(new RechargeSuccessEvent(targetPlayerDom.getId(), gold,1));
		// 数据在玩家线程更新成功后才记录日志
		DbLogService
		.log(new RechargeLog(targetPlayerDom, player.getId(),gold,1,0));
		DbLogService
				.log(new TransferGoldLog(player, toPlayerId, beforeGold, beforeBankGold, player.gold(), player.bankGold()));

		LOG.info("玩家[{}][{}]给[{}][{}]转账[{}]", player.getNickName(), player.getId(), targetPlayerDom.getNickName(),
				targetPlayerDom.getId(), gold);
	}

	public void enterBank(Player player, String bankPwd) {
//		if(player.isEnterBank) {
//			bankMsgMgr.sendResEnterBank(player, 4);
//			return;
//		}
		if(player.getBankPwd()==null) {
			bankMsgMgr.sendResEnterBank(player, 3);
			return;
		}
		
		if(bankPwd==null||bankPwd.equals("")||bankPwd.length()>64) {
			bankMsgMgr.sendResEnterBank(player, 1);
			return;
		}
		
		if(!player.getBankPwd().equals(MD5Util.encodeWithTail(bankPwd))) {
			bankMsgMgr.sendResEnterBank(player, 2);
			return;
		}
		player.isEnterBank=true;
		bankMsgMgr.sendResEnterBank(player, 0);
	}
	
	
}
