package com.alex.game.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import com.alex.game.dbdata.dom.PlayerRankDom;

public class FixSizedPriorityQueue  {
	private PriorityQueue<PlayerRankDom> queue;
	private HashMap<Long,PlayerRankDom> map;
	private int maxSize; // 堆的最大容量

	public FixSizedPriorityQueue(int maxSize) {
		if (maxSize <= 0)
			throw new IllegalArgumentException();
		this.maxSize = maxSize;
		this.queue = new PriorityQueue<PlayerRankDom>(maxSize);
		map=new HashMap<Long,PlayerRankDom>();
	}

	public boolean add(PlayerRankDom e) {
		boolean flag=false;
		if (queue.size() < maxSize) { 
			// 未达到最大容量，先检查是否已经添加
			if(map.containsKey(e.getPlayerId())) {
				queue.remove(map.get(e.getPlayerId()));
			}
			queue.add(e);
			map.put(e.getPlayerId(), e);
			flag=true;
		} else { // 队列已满
			PlayerRankDom peek = queue.peek();
			if (e.compareTo(peek) < 0) { // 将新元素与当前堆顶元素比较，保留较小的元素
				PlayerRankDom re=queue.poll();
				map.remove(re.getPlayerId());
				queue.add(e);
				map.put(e.getPlayerId(), e);
				flag=true;
			}
		}
		return flag;
	}

	public List<PlayerRankDom> sortedList() {
		List<PlayerRankDom> list = new ArrayList<PlayerRankDom>(queue);
		Collections.sort(list); // PriorityQueue本身的遍历是无序的，最终需要对队列中的元素进行排序
		return list;
	}
}
