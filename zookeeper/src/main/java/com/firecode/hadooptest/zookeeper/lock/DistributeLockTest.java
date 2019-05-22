package com.firecode.hadooptest.zookeeper.lock;

import java.util.concurrent.CountDownLatch;

/**
 * 测试分布式锁
 * 
 * @author JIANG
 */
public class DistributeLockTest {
	
	private static final int THREAD_NUM = 10;
	public static final CountDownLatch latch = new 	CountDownLatch(THREAD_NUM);
	
	public static void main(String[] args) throws InterruptedException {
	    for(int i=0;i<THREAD_NUM;i++) {
	    	new Thread(()-> {
	    		long threadId = Thread.currentThread().getId();
				new DistributeLock (arg->{
					System.err.println(threadId);
					latch.countDown();
				});
	    	}).start();
	    }
		latch.await();
		System.err.println("所有任务执行完毕！");
	}

}
