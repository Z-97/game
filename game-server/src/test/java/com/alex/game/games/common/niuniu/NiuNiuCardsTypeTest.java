package com.alex.game.games.common.niuniu;

import java.util.Arrays;

import org.junit.Test;

/**
 * 
 * 
 * @author Alex
 * @date 2017年5月9日 下午5:37:00
 */
public class NiuNiuCardsTypeTest {

	@Test
	public void testCardsType() {
		NiuNiuCards cards = new NiuNiuCards(Arrays.asList(NiuNiuCard.HEI_TAO_A, 
				NiuNiuCard.HEI_TAO_Q, NiuNiuCard.HEI_TAO_SHI, NiuNiuCard.MEI_HUA_SHI, NiuNiuCard.HEI_TAO_J));
		System.out.println(cards.cardsType);
	}
}
