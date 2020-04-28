package com.alex.game.games.common.niuniu;

import com.alex.game.games.common.CardSuit;

/**
 * 1-13：方块，14-26：梅花，27-39：红桃，40-52：黑桃
 * 
 * @author Alex
 * @date 2017年4月26日 下午9:01:46
 */
public enum NiuNiuCard {
	/*
	 * 方块 A - K,1-13
	 */
	FANG_KUAI_A(1, 1, 1, CardSuit.FANG_KUAI),
	FANG_KUAI_ER(2, 2, 2, CardSuit.FANG_KUAI),
	FANG_KUAI_SAN(3, 3, 3, CardSuit.FANG_KUAI),
	FANG_KUAI_SI(4, 4, 4, CardSuit.FANG_KUAI),
	FANG_KUAI_WU(5, 5, 5, CardSuit.FANG_KUAI),
	FANG_KUAI_LIU(6, 6, 6, CardSuit.FANG_KUAI),
	FANG_KUAI_QI(7, 7, 7, CardSuit.FANG_KUAI),
	FANG_KUAI_BA(8, 8, 8, CardSuit.FANG_KUAI),
	FANG_KUAI_JIU(9, 9, 9, CardSuit.FANG_KUAI),
	FANG_KUAI_SHI(10, 10, 10, CardSuit.FANG_KUAI),
	FANG_KUAI_J(11, 10, 11, CardSuit.FANG_KUAI),
	FANG_KUAI_Q(12, 10, 12, CardSuit.FANG_KUAI),
	FANG_KUAI_K(13, 10, 13, CardSuit.FANG_KUAI),
	
	/*
	 * 梅花 A - K,14-26
	 */
	MEI_HUA_A(14, 1, 1, CardSuit.MEI_HUA),
	MEI_HUA_ER(15, 2, 2, CardSuit.MEI_HUA),
	MEI_HUA_SAN(16, 3, 3, CardSuit.MEI_HUA),
	MEI_HUA_SI(17, 4, 4, CardSuit.MEI_HUA),
	MEI_HUA_WU(18, 5, 5, CardSuit.MEI_HUA),
	MEI_HUA_LIU(19, 6, 6, CardSuit.MEI_HUA),
	MEI_HUA_QI(20, 7, 7, CardSuit.MEI_HUA),
	MEI_HUA_BA(21, 8, 8, CardSuit.MEI_HUA),
	MEI_HUA_JIU(22, 9, 9, CardSuit.MEI_HUA),
	MEI_HUA_SHI(23, 10, 10, CardSuit.MEI_HUA),
	MEI_HUA_J(24, 10, 11, CardSuit.MEI_HUA),
	MEI_HUA_Q(25, 10, 12, CardSuit.MEI_HUA),
	MEI_HUA_K(26, 10, 13, CardSuit.MEI_HUA),
	
	/*
	 * 红桃 A - K，27-39
	 */
	HONG_TAO_A(27, 1, 1, CardSuit.HONG_TAO),
	HONG_TAO_ER(28, 2, 2, CardSuit.HONG_TAO),
	HONG_TAO_SAN(29, 3, 3, CardSuit.HONG_TAO),
	HONG_TAO_SI(30, 4, 4, CardSuit.HONG_TAO),
	HONG_TAO_WU(31, 5, 5, CardSuit.HONG_TAO),
	HONG_TAO_LIU(32, 6, 6, CardSuit.HONG_TAO),
	HONG_TAO_QI(33, 7, 7, CardSuit.HONG_TAO),
	HONG_TAO_BA(34, 8, 8, CardSuit.HONG_TAO),
	HONG_TAO_JIU(35, 9, 9, CardSuit.HONG_TAO),
	HONG_TAO_SHI(36, 10, 10, CardSuit.HONG_TAO),
	HONG_TAO_J(37, 10, 11, CardSuit.HONG_TAO),
	HONG_TAO_Q(38, 10, 12, CardSuit.HONG_TAO),
	HONG_TAO_K(39, 10, 13, CardSuit.HONG_TAO),
	
	/*
	 * 黑桃 A - K,40-52
	 */
	HEI_TAO_A(40, 1, 1, CardSuit.HEI_TAO),
	HEI_TAO_ER(41, 2, 2, CardSuit.HEI_TAO),
	HEI_TAO_SAN(42, 3, 3, CardSuit.HEI_TAO),
	HEI_TAO_SI(43, 4, 4, CardSuit.HEI_TAO),
	HEI_TAO_WU(44, 5, 5, CardSuit.HEI_TAO),
	HEI_TAO_LIU(45, 6, 6, CardSuit.HEI_TAO),
	HEI_TAO_QI(46, 7, 7, CardSuit.HEI_TAO),
	HEI_TAO_BA(47, 8, 8, CardSuit.HEI_TAO),
	HEI_TAO_JIU(48, 9, 9, CardSuit.HEI_TAO),
	HEI_TAO_SHI(49, 10, 10, CardSuit.HEI_TAO),
	HEI_TAO_J(50, 10, 11, CardSuit.HEI_TAO),
	HEI_TAO_Q(51, 10, 12, CardSuit.HEI_TAO),
	HEI_TAO_K(52, 10, 13, CardSuit.HEI_TAO),
	;
	
	public final int id;
	// 牌的数字(10、J、Q、K都是10)
	public final int num;
	// 编号(1-13)
	public final int no;
	// 牌的花色 
	public final CardSuit suit;
	
	private NiuNiuCard(int id, int num, int no, CardSuit suit) {
		this.id = id;
		this.num = num;
		this.no = no;
		this.suit = suit;
	}


	/**
	 * 根据牌的id获取牌
	 * 
	 * @param id
	 * @return
	 */
	public static NiuNiuCard getCard(int id) {
		switch (id) {
			case 1:return FANG_KUAI_A;
			case 2:return FANG_KUAI_ER;
			case 3:return FANG_KUAI_SAN;
			case 4:return FANG_KUAI_SI;
			case 5:return FANG_KUAI_WU;
			case 6:return FANG_KUAI_LIU;
			case 7:return FANG_KUAI_QI;
			case 8:return FANG_KUAI_BA;
			case 9:return FANG_KUAI_JIU;
			case 10:return FANG_KUAI_SHI;
			case 11:return FANG_KUAI_J;
			case 12:return FANG_KUAI_Q;
			case 13:return FANG_KUAI_K;
			case 14:return MEI_HUA_A;
			case 15:return MEI_HUA_ER;
			case 16:return MEI_HUA_SAN;
			case 17:return MEI_HUA_SI;
			case 18:return MEI_HUA_WU;
			case 19:return MEI_HUA_LIU;
			case 20:return MEI_HUA_QI;
			case 21:return MEI_HUA_BA;
			case 22:return MEI_HUA_JIU;
			case 23:return MEI_HUA_SHI;
			case 24:return MEI_HUA_J;
			case 25:return MEI_HUA_Q;
			case 26:return MEI_HUA_K;
			case 27:return HONG_TAO_A;
			case 28:return HONG_TAO_ER;
			case 29:return HONG_TAO_SAN;
			case 30:return HONG_TAO_SI;
			case 31:return HONG_TAO_WU;
			case 32:return HONG_TAO_LIU;
			case 33:return HONG_TAO_QI;
			case 34:return HONG_TAO_BA;
			case 35:return HONG_TAO_JIU;
			case 36:return HONG_TAO_SHI;
			case 37:return HONG_TAO_J;
			case 38:return HONG_TAO_Q;
			case 39:return HONG_TAO_K;
			case 40:return HEI_TAO_A;
			case 41:return HEI_TAO_ER;
			case 42:return HEI_TAO_SAN;
			case 43:return HEI_TAO_SI;
			case 44:return HEI_TAO_WU;
			case 45:return HEI_TAO_LIU;
			case 46:return HEI_TAO_QI;
			case 47:return HEI_TAO_BA;
			case 48:return HEI_TAO_JIU;
			case 49:return HEI_TAO_SHI;
			case 50:return HEI_TAO_J;
			case 51:return HEI_TAO_Q;
			case 52:return HEI_TAO_K;
			default:return null;
		}
	}
	
}
