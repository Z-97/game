package com.alex.game.games.buyu.manager;

/**
 * BuyuMgr测试
 * 
 * @author Alex
 * @date 2017年3月31日 下午2:00:35
 */
public class BuyuMgrTest {/*

	private static final int TEST_NUM = 1000000;
	private static final int TEST_FISH_MAX_MULTIPLE = 100;

	*//**
	 * 个人控制
	 *//*
	@Test
	public void testControl() {
		// 误差集合
		List<Integer> vals = new ArrayList<>();

		System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t", "鱼倍数", "打中次数", "个人系数", "房间系数", "鱼系数",
				"合计系数", "理论死亡次数", "算法死亡次数", "误差次数"));
		for (int fishMultiple = 2; fishMultiple <= TEST_FISH_MAX_MULTIPLE; fishMultiple++) {
			// 个人控制系数
			int playerParam = RandomUtil.random(-1000, 1000);
			// 房间控制系数
			int roomParamLower = -40;
			int roomParamUpper = -60;
			// 鱼系数
			int fishParam = RandomUtil.random(-1000, 1000);
			// 实际死亡次数
			int dieNum = 0;
			for (int i = 0; i < TEST_NUM; i++) {
				if (BuyuMgr.control(fishMultiple, playerParam, roomParamLower, roomParamUpper, fishParam, 0)) {
					dieNum++;
				}
			}

			int totalParam = playerParam + roomParam + fishParam;
			// 理论打死次数
			int theoryDieNum = Math.round(((float) 1000 + totalParam) / (1000 * fishMultiple) * TEST_NUM);
			theoryDieNum = theoryDieNum < 0 ? 0 : theoryDieNum;
			System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t", fishMultiple, TEST_NUM,
					playerParam, roomParam, fishParam, totalParam, theoryDieNum, dieNum, dieNum - theoryDieNum));
			vals.add(dieNum - theoryDieNum);
		}

		Pair<Double, Double> resData = computeData(vals);
		System.out.println("平均误差:" + resData.v1 + "\t误差的方差:" + resData.v2);
	}

	*//**
	 * 只遍历数组一次求方差，利用公式DX^2=EX^2-(EX)^2
	 * 
	 * @param vals
	 * @return vals的平均值和方差
	 *//*
	private static Pair<Double, Double> computeData(List<Integer> vals) {
		double variance = 0;// 方差
		double avg = 0;// 平均数
		int i, len = vals.size();
		double sum = 0, sum2 = 0;
		for (i = 0; i < len; i++) {
			sum += vals.get(i);
		}

		avg = sum / len;

		for (i = 0; i < len; i++) {
			sum2 += (vals.get(i) - avg) * (vals.get(i) - avg);
		}

		variance = sum2 / len;
		return new Pair<>(avg, Math.sqrt(variance));
	}

	
	@Test
	public void testControl2() {
		long players = 1000000;
		long playerGold = 600;
		
//		long totalIn = players * playerGold;
		long totalOut = 0;
		for (long i = 0; i < players * playerGold; i++) {
			int fishMultiple = ThreadLocalRandom.current().nextInt(2, 200);
			if (BuyuMgr.control(fishMultiple, -20, 0, 0, 0)) {
				totalOut += fishMultiple;
			}
		}
		
		System.out.println(totalOut);
	}
*/}
