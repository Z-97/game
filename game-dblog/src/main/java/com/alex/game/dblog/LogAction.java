package com.alex.game.dblog;

/**
 * 由系统和在该系统所做的操作构成
 * 1-100大厅，101-200捕鱼,201-300百人牛牛,301-400炸金花
 * @author Alex
 * @date 2017年4月30日 下午5:47:39
 */
public enum LogAction {
	/*
	 * 大厅
	 */
	BINDING_PHONE(1, "绑定手机"),
	DEPOSITE_GOLD(2, "存入"),//存金币
	WITHDRAW_GOLD(3, "取出"),//提取金币
	TRANSFER_GOLD(4, "转账"),//转账
	REGISTER(5, "注册"),
	RECHEARGE(6, "充值"),
	BANKEND_ADD_GOLD(7, "后台加金币"),
	BANKEND_ADD_BANK_GOLD(8, "系统"),//后台加银行金币
	EXCHANGE_CASH_SERVICE_REFOUND(9, "退款"),//提现失败
	AGENT_RECHEARGE(10, "买入"),//给代理加银行金币
	EXCHANGE_CASH(11, "兑换"),//兑换现金
	CALL_EXCHANGE_CASH_SERVICE_REFOUND(12, "退款"),//调用兑换接口失败退款
	EXCHANGE_CASH_MANUAL_REFOUND(13, "兑换现金失败手动退款"),//兑换现金失败手动退款
	AGENT_EXCHANGE_CASH(14, "兑换"),//代理兑换现金
	AGENT_REFOUND(15, "退款"),//代理退款给平台
	TRANSFER_REFUND_GOLD(16, "转账退款"),//代理转账退款给代理
	EXCHAN_GECASH_FROM_AGENT(17, "代理回收"),//从代理兑换现金
	BANKEND_ADD_PROMOTER_GOLD(7, "后台给推广员加金币"),
	

	
	/*
	 * 骰子
	 */
	DICE_BALANCE(201, "骰子结算"),
	DICE_BET(202, "骰子下注"),
	DICE_REDPACKAGE(203, "骰子豹子红包"),
	
	/*
	 * 红黑大战
	 */
	HHDZ_BALANCE(801, "红黑大战结算"),
	HHDZ_BET(802, "红黑大战下注"),
	;
	
	//标识id
	public final int id;
	//描述
	public final String desc;
	
	private LogAction(int id, String desc) {
		this.id = id;
		this.desc = desc;
	}

    @Override
    public String toString() {
        return "" + id;
    }
}
