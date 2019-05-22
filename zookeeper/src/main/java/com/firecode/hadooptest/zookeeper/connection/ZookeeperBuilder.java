package com.firecode.hadooptest.zookeeper.connection;

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.ZooKeeper;

/**
 * Zookeeper连接建立者
 * 
 * @author JIANG
 */
public class ZookeeperBuilder {

	/**
	 * Zookeeper连接地址
	 */
	private String connectStr = "127.0.0.1:2181";
	/**
	 * Zookeeper连接超时时间
	 */
	private int connectTimeout = 5000;

	/**
	 * 建立Zookeeper连接
	 * @return
	 * @throws IOException
	 */
	public ZooKeeper builder() throws IOException {
		Thread currentThread = Thread.currentThread();
		ZooKeeper zk = new ZooKeeper(connectStr, connectTimeout, (event) -> {
			// zookeeper连接好以后
			if (event.getState() == Event.KeeperState.SyncConnected) {
				// 放开等待，执行下面的逻辑
				LockSupport.unpark(currentThread);
			}
		});
		// 使当前线程处于等待
		LockSupport.park();
		return zk;
	}

	public ZookeeperBuilder connectStr(String connectStr) {
		this.connectStr = connectStr;
		return this;
	}

	public ZookeeperBuilder timeout(int connectTimeOut) {
		this.connectTimeout = connectTimeOut;
		return this;
	}
}
