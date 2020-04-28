package com.alex.game.httpservice.util;

/**
 * 充值类型0普通充值1代理充值2管理员充值
 * 
 * @author yougo 2017年5月22日
 */
public enum RechargeType {

	COOMMON(0, "普通充值"), AGENT(1, "代理充值"), ADMIN(2, "管理员充值"), AGENT_REFUND(3, "代理充值退款");
	public final int type;
	public final String desc;

	/**
	 * @param type
	 * @param desc
	 */
	private RechargeType(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}
}
