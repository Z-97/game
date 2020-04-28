package com.alex.game.games.common.niuniu;

/**
 * 牛牛牌型比较工具类
 * 
 * @author Alex
 * @date 2017年4月26日 下午9:23:45
 */
public class NiuNiuCardsTypeComparator {

	private NiuNiuCardsTypeComparator() {}
	
	/**
	 * 比较大小,先比较牌型，牌型一样比较最大的牌的数字，数字一样比较花色(黑桃最大)
	 * 
	 * @param myCards
	 * @param otherCards
	 * @return				true:我大  false：庄家大
	 */
	public static boolean compare(NiuNiuCards myCards, NiuNiuCards otherCards) {
		if (myCards.cardsType.niu > otherCards.cardsType.niu) {
			return true;
		} else if (myCards.cardsType.niu < otherCards.cardsType.niu) {
			return false;
		} else {
			NiuNiuCard myCompareCard = myCards.compareCard();
			NiuNiuCard otherCompareCard = otherCards.compareCard();
			if (myCompareCard.no > otherCompareCard.no) {
				return true;
			} else if (myCompareCard.no < otherCompareCard.no) {
				return false;
			} else {
				return myCompareCard.suit.ordinal() > otherCompareCard.suit.ordinal();
			}
		}
	}
	
}
