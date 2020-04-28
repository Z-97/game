package com.alex.game.common.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 多线程下的随机数生成不推荐全局Random的实例，而是使用java 7的ThreadLocalRandom具体原因（来至jdk的说明文档）如下：
 * 具体原因（来至jdk的说明文档）如下：
 * 1.Instances of java.util.Random are threadsafe. However, the concurrent use of the same java.util.Random instance across threads 
 * 	may encounter contention and consequent poor performance. Consider instead using ThreadLocalRandom in multithreaded designs.
 * 2.A random number generator isolated to the current thread. Like the global Random generator used by the Math class, 
 * 	a ThreadLocalRandom is initialized with an internally generated seed that may not otherwise be modified. When applicable, 
 * 	use of ThreadLocalRandom rather than shared Random objects in concurrent programs will typically encounter much less overhead and contention. 
 * 	Use of ThreadLocalRandom is particularly appropriate when multiple tasks (for example, each a ForkJoinTask) use random numbers in parallel in thread pools.
 * jdk说明地址：
 * http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadLocalRandom.html
 * http://docs.oracle.com/javase/7/docs/api/java/util/Random.html
 * @author Alex
 * @date 2017年4月18日 下午3:00:40
 * 
 */
public class RandomUtil {

	private static Random random = new SecureRandom(("" + System.currentTimeMillis()).getBytes());
	
	private RandomUtil() {}

	/**
	 * 取值范围：[0,max)
	 * @param max
	 * @return [0,max)
	 */
	public static int randomWell(int max) {
		return new Random(mix32(System.nanoTime())).nextInt(max);
	}
	
    private static int mix32(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        return (int)(((z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L) >>> 32);
    }
	
	/**
	 * 取值范围：[0,max)
	 * @param max
	 * @return [0,max)
	 */
	public static int random(int max) {
		return random.nextInt(max);
	}

	/**
	 * 取值范围：[min,max]
	 * @param min
	 * @param max
	 * @return [min,max]
	 */
	public static int random(int min, int max) {
		if (max - min <= 0) {
			return min;
		}
		return min + random.nextInt(max - min + 1);
	}

	/**
	 * 计算可能性 example：55%的概率，RandomUtils.probable(55,100)
	 * @param probablility		可能系数
	 * @param gailv				概率数
	 * @return
	 */
	public static boolean probable(int probablility, int gailv) {
		return probablility > random.nextInt(gailv);
	}

	/**
	 * 从list中随机指定数目的元素
	 * @param list			随机的list
	 * @param num			随机的数目
	 * @return
	 */
	public static <T> List<T> randomList(List<T> list, int num) {
		if (list == null) {
			throw new IllegalArgumentException("随机list 不能为null");
		}

		if (list.size() == 0 || num <= 0) {
			return new ArrayList<>();
		}

		// 数目不能大于list的大小
		num = num > list.size() ? list.size() : num;

		Map<Integer, T> res = new HashMap<>();
		for (;;) {
			int index = random(list.size());
			if (res.containsKey(index)) {
				continue;
			}

			res.put(index, list.get(index));

			if (res.size() == num) {
				break;
			}
		}

		return new ArrayList<>(res.values());
	}

	/**
	 * 从list随机出一个元素
	 * @param list
	 * @return
	 */
	public static <T> T randomList(List<T> list) {
		
		return randomList(list, 1).get(0);
	}
	
	/**
	 * 随机选取数组元素
	 * @param array
	 * @param num
	 * @return
	 */
	public static <T> List<T> randomArray(T[] array, int num) {
		if (array == null) {
			throw new IllegalArgumentException("随机array 不能为null");
		}

		return randomList(Arrays.asList(array), num);
	}

	/**
	 * 随机boolean
	 * @return
	 */
	public static boolean randomBool() {
		return random.nextBoolean();
	}
	
}
