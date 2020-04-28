package com.alex.game.games.common.niuniu;

/**
 * 牌的类型
 * 
 * @author Alex
 * @date 2017年4月26日 下午9:21:43
 */
public enum NiuNiuCardsType {

	MEI_NIU(0),			 	//没牛
	NIU_1(1),               //牛1
	NIU_2(2),               //牛2
	NIU_3(3),               //牛3
	NIU_4(4),               //牛4
	NIU_5(5),               //牛5
	NIU_6(6),               //牛6
	NIU_7(7),               //牛7
	NIU_8(8),               //牛8
	NIU_9(9),               //牛9
	NIU_NIU(10),             //牛牛
	SI_HUA(11),              //四花
	SI_ZHA(12),              //四炸
	WU_HUA(13),              //五花
	WU_XIAO(14);             //五小
	
	public final int niu;

	private NiuNiuCardsType(int niu) {
		this.niu = niu;
	}
	
	/**
	 * 获取牌型
	 * @param niu
	 * @return
	 */
	public static NiuNiuCardsType geCardsType(int niu) {
		switch (niu) {
			case 0: return MEI_NIU;
			case 1: return NIU_1;
			case 2: return NIU_2;
			case 3: return NIU_3;
			case 4: return NIU_4;
			case 5: return NIU_5;
			case 6: return NIU_6;
			case 7: return NIU_7;
			case 8: return NIU_8;
			case 9: return NIU_9;
			case 10: return NIU_NIU;
			case 11: return SI_HUA;
			case 12: return SI_ZHA;
			case 13: return WU_HUA;
			case 14: return WU_XIAO;
			default: return null;
		}
	}
}
