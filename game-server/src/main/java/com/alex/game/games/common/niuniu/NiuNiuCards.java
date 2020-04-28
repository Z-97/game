package com.alex.game.games.common.niuniu;

import java.util.List;

import com.alex.game.common.tuple.Pair;

/**
 * NiuNiuCards
 * 
 * @author Alex
 * @date 2017年4月26日 下午9:26:12
 */
public class NiuNiuCards {
	
	// 玩家的牌数据
	public final List<NiuNiuCard> cards;
	// 玩家的牌最优牌(3+2)组合
	public final List<NiuNiuCard> bestCards;
	// 当前牌型
	public final NiuNiuCardsType cardsType;
	// 最大的牌
	private final NiuNiuCard maxCard;
	
	public NiuNiuCards(List<NiuNiuCard> cards) {
		this.cards = cards;
		Pair<NiuNiuCardsType, List<NiuNiuCard>> cardsTypeData = NiuNiuCardsTypeGetter.getCardsTypeData(cards);
		this.cardsType = cardsTypeData.v1;
		this.bestCards = cardsTypeData.v2;
		this.maxCard = NiuNiuCardsTypeGetter.getMaxCard(cards);
	}
	
	/**
	 * 获取用于比较的牌
	 * 
	 * @return
	 */
	public NiuNiuCard compareCard() {
		if (cardsType == NiuNiuCardsType.SI_ZHA) {// 炸弹牛比较的是炸弹
			if (cards.get(0) == cards.get(1)) {
				return cards.get(0);
			}else {
				return cards.get(1);
			}
		} else {
			return maxCard;
		}
	}
	
}
