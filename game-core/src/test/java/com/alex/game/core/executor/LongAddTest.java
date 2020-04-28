package com.alex.game.core.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import org.junit.Test;

public class LongAddTest {

	@Test
	public void testLongAdder() {
		Executor executor = Executors.newFixedThreadPool(8);
		final LongAdder val = new LongAdder();
		
		long now = System.currentTimeMillis();
		for (int i = 0; i <= 10000000; i++) {
			executor.execute(() -> {
				val.increment();
				if (val.longValue() == 1000000) {
					System.out.println("LongAdder:" + (System.currentTimeMillis() - now));
				}
			});
			
		}
	}
	
	@Test
	public void testAtomiLong() {
		Executor executor = Executors.newFixedThreadPool(8);
		final AtomicLong val = new AtomicLong(0);
		
		long now = System.currentTimeMillis();
		for (int i = 0; i <= 10000000; i++) {
			executor.execute(() -> {
				if (val.incrementAndGet() == 1000000) {
					System.out.println("AtomiLong:" + (System.currentTimeMillis() - now));
				}
			});
			
		}
	}
	

}
