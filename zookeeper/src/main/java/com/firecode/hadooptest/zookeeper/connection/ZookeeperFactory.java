package com.firecode.hadooptest.zookeeper.connection;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;

/**
 * Zookeeper连接生产者
 * 
 * @author JIANG
 */
public class ZookeeperFactory {

	private ZookeeperBuilder ZookeeperBuilder = new ZookeeperBuilder().connectStr("127.0.0.1:2181").timeout(5000);

	/**
	 * 创建Zookeeper连接
	 */
	public ZooKeeper create() {
		ZooKeeper zk;
		try {
			zk = ZookeeperBuilder.builder();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return zk;
	}
	
	/**
	 * 获取Zookeeper连接生产者
	 * @return
	 */
	public static ZookeeperFactory getInstance() {
		
		return SingleStand.INSTANCE;
	}
	
	private static class SingleStand {
		
		private static final ZookeeperFactory INSTANCE = new ZookeeperFactory();
	}
}
