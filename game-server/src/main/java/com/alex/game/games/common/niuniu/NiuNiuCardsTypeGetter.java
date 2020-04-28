package com.alex.game.games.common.niuniu;

import java.util.ArrayList;
import java.util.List;

import com.alex.game.common.tuple.Pair;
import com.google.common.collect.TreeMultiset;

/**
 * 牛牛牌型获取工具类
 * 
 * @author Alex
 * @date 2017年4月26日 下午9:44:49
 */
public class NiuNiuCardsTypeGetter {

	private NiuNiuCardsTypeGetter() {
	}

	/**
	 * 获取牌型的同时将提示牌型计算出来
	 * 
	 * @param cards
	 * @return 牌型和最优牌组合(需要的目标最优牌组合，如果是牛，前3张是整数，后2张是点数)
	 */
	public static Pair<NiuNiuCardsType, List<NiuNiuCard>> getCardsTypeData(List<NiuNiuCard> cards) {
		if (isWuXiao(cards)) {
			return new Pair<>(NiuNiuCardsType.WU_XIAO, cards);
		} else if (isWuHua(cards)) {
			return new Pair<>(NiuNiuCardsType.WU_HUA, cards);
		} else if (isSiZha(cards)) {
			return new Pair<>(NiuNiuCardsType.SI_ZHA, cards);
		} else if (isSiHua(cards)) {
			return new Pair<>(NiuNiuCardsType.SI_HUA, cards);
		} else {
			return getNiuNiuType(cards);
		}
	}

	/**
	 * 是否是五小,五小：5张牌都小于5,并且全部加起来小于或等于10
	 * 
	 * @param cards
	 */
	private static boolean isWuXiao(List<NiuNiuCard> cards) {
		int totalNum = 0;
		for (NiuNiuCard card : cards) {
			if (card.no >= NiuNiuCard.HEI_TAO_WU.no) {
				return false;
			}

			totalNum += card.num;
		}

		return totalNum <= 10;
	}

	/**
	 * 是否是五花,五花：5张牌全为花（如Q，J，J，Q，K）
	 * 
	 * @param cards
	 * @return
	 */
	private static boolean isWuHua(List<NiuNiuCard> cards) {
		for (NiuNiuCard card : cards) {
			if (card.no < NiuNiuCard.HEI_TAO_J.no) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 是否是四花,四花：5张牌中一张为10，另外4张为花[指J、Q、K]（如10，J，J，Q，K）
	 * 
	 * @param cards
	 * @return
	 */
	private static boolean isSiHua(List<NiuNiuCard> cards) {
		// 10牌的个数
		int cardTenNum = 0;

		for (NiuNiuCard card : cards) {
			if (card.no == NiuNiuCard.HEI_TAO_SHI.no && ++cardTenNum > 1) {
				return false;
			} else if (card.no < NiuNiuCard.HEI_TAO_SHI.no) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 是否四炸,四炸：5张牌中有4张一样的牌，此时无需有牛。若庄家闲家都是四炸牌型，则比较4张一样的牌的大小。
	 * 
	 * @param cards
	 * @return
	 */
	private static boolean isSiZha(List<NiuNiuCard> cards) {
		TreeMultiset<Integer> cardNos = TreeMultiset.create();
		for (NiuNiuCard card : cards) {
			cardNos.add(card.no);
		}

		int firstCardNoCount = cardNos.firstEntry().getCount();
		return cardNos.elementSet().size() == 2 && (firstCardNoCount == 1 || firstCardNoCount == 4);
	}

	/**
	 * 只获取牛牛类型
	 * 
	 * @param cards
	 * @return
	 */
	private static Pair<NiuNiuCardsType, List<NiuNiuCard>> getNiuNiuType(List<NiuNiuCard> cards) {
		// 牌数字的合计
		int totalNum = 0;
		for (NiuNiuCard card : cards) {
			totalNum += card.num;
		}

		for (int i = 0; i < cards.size(); i++) {
			for (int j = i + 1; j < cards.size(); j++) {
				for (int k = j + 1; k < cards.size(); k++) {
					NiuNiuCard firstCard = cards.get(i);
					NiuNiuCard secondCard = cards.get(j);
					NiuNiuCard thirdCard = cards.get(k);
					int sumNum = firstCard.num + secondCard.num + thirdCard.num;
					
					if (sumNum % 10 == 0) {
						// 提示最优牌(前3张是整数，后2张是点数)
						List<NiuNiuCard> bestCards = new ArrayList<>();
						bestCards.add(firstCard);
						bestCards.add(secondCard);
						bestCards.add(thirdCard);
						
						for (NiuNiuCard card : cards) {
							if (card != firstCard && card != secondCard && card != thirdCard) {
								bestCards.add(card);
							}
						}

						int niu = totalNum % 10;
						return new Pair<>(NiuNiuCardsType.geCardsType(niu == 0 ? 10 : niu), bestCards);
					}
				}
			}
		}

		return new Pair<>(NiuNiuCardsType.MEI_NIU, cards);
	}

	/**
	 * 获取最大的牌
	 * 
	 * @param cards
	 * @return
	 */
	public static NiuNiuCard getMaxCard(List<NiuNiuCard> cards) {
		NiuNiuCard maxCard = null;
		for (NiuNiuCard card : cards) {
			if (maxCard == null || card.no > maxCard.no 
					|| (card.no == maxCard.no && card.suit.ordinal() > maxCard.suit.ordinal())) {
				maxCard = card;
			}
		}
		
		return maxCard;
	}
}
