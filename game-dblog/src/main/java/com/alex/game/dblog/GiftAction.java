package com.alex.game.dblog;

public enum GiftAction {

	GIVE(1, "赠送"),
	GET(2, "收取"),
	;//标识id
		public final int type;
		//描述
		public final String desc;
		
		private GiftAction(int type, String desc) {
			this.type = type;
			this.desc = desc;
		}

	    @Override
	    public String toString() {
	        return "" + type;
	    }
}
