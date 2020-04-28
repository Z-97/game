package com.alex.game.dbdata.dom;
/**
 * 银行记录
 * @author yejuhua
 *
 */
public class BankRecord {

	// 时间
		private long time;
		// 类型
		private String type;
		// 金额
		private long money;
		// 操作前
		private long beforeMoney;
		// 操作后
		private long afterMoney;

		public BankRecord() {
			super();
		}

		public BankRecord(long time, String type, long money, long beforeMoney, long afterMoney) {
			this.time = time;
			this.type = type;
			this.money = money;
			this.beforeMoney = beforeMoney;
			this.afterMoney = afterMoney;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public long getMoney() {
			return money;
		}

		public void setMoney(long money) {
			this.money = money;
		}

		public long getBeforeMoney() {
			return beforeMoney;
		}

		public void setBeforeMoney(long beforeMoney) {
			this.beforeMoney = beforeMoney;
		}

		public long getAfterMoney() {
			return afterMoney;
		}

		public void setAfterMoney(long afterMoney) {
			this.afterMoney = afterMoney;
		}
}
