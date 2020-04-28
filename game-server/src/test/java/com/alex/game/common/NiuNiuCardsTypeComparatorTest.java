package com.alex.game.common;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.alex.game.games.common.niuniu.NiuNiuCard;
import com.alex.game.games.common.niuniu.NiuNiuCards;
import com.alex.game.games.common.niuniu.NiuNiuCardsTypeComparator;

/**
 * NiuNiuCardsTypeComparatorTest
 * 
 * @author Alex
 * @date 2017年5月27日 下午3:53:39
 */
public class NiuNiuCardsTypeComparatorTest {

	@Test
	public void testComp() {
		// 37QQ9
		NiuNiuCards myCards = new NiuNiuCards(Arrays.asList(NiuNiuCard.FANG_KUAI_SAN, NiuNiuCard.HONG_TAO_QI, NiuNiuCard.HEI_TAO_Q, NiuNiuCard.MEI_HUA_Q, NiuNiuCard.HONG_TAO_JIU));
		// 10JK45
		NiuNiuCards otherCards = new NiuNiuCards(Arrays.asList(NiuNiuCard.HONG_TAO_SHI, NiuNiuCard.HONG_TAO_J, NiuNiuCard.FANG_KUAI_K, NiuNiuCard.HONG_TAO_SI, NiuNiuCard.MEI_HUA_WU));
		
		Assert.assertTrue(!NiuNiuCardsTypeComparator.compare(myCards, otherCards));
		// 3QQQQ
		myCards = new NiuNiuCards(Arrays.asList(NiuNiuCard.HEI_TAO_K, NiuNiuCard.HONG_TAO_Q, NiuNiuCard.HEI_TAO_Q, NiuNiuCard.MEI_HUA_Q, NiuNiuCard.FANG_KUAI_Q));
		// 4444K
		otherCards = new NiuNiuCards(Arrays.asList(NiuNiuCard.HEI_TAO_SI, NiuNiuCard.HONG_TAO_SI, NiuNiuCard.MEI_HUA_SI, NiuNiuCard.FANG_KUAI_SI, NiuNiuCard.HONG_TAO_K));
		
		Assert.assertTrue(NiuNiuCardsTypeComparator.compare(myCards, otherCards));
		
	}
}
