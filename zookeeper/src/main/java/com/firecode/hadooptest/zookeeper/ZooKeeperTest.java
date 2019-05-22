package com.firecode.hadooptest.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.firecode.hadooptest.zookeeper.connection.ZookeeperFactory;

/**
 * Hello world!
 *
 */
public class ZooKeeperTest {
	
	ZooKeeper zk;
	
	@Before
	public void connect(){
		zk = ZookeeperFactory.getInstance().create();
	}
	
	
	/**
	 * 创建临时的节点(注意：临时的节点和Session绑定，连接关闭节点自动删除)
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	@Test
	public void testCreate1() throws KeeperException, InterruptedException{
		/**
		 * Ids.OPEN_ACL_UNSAFE 完全开放权限
		 * CreateMode.PERSISTENT 临时的节点
		 */
		String res = zk.create("/zk001", "zk001-a".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.err.println(res);
	}
	
	/**
	 * 创建持久化的节点
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	@Test
	public void testCreate2() throws KeeperException, InterruptedException{
		/**
		 * Ids.OPEN_ACL_UNSAFE 完全开放权限
		 * CreateMode.PERSISTENT 持久化的节点
		 */
		String res = zk.create("/zk001", "zk001-a".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.err.println(res);
	}
	
	/**
	 * 获取节点数据
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	@Test
	public void getData() throws KeeperException, InterruptedException{
		byte[] data = zk.getData("/zk001", false, null);
		System.err.println(new String(data));
	}
	
	/**
	 * 获取节点是否存在
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	@Test
	public void exists() throws KeeperException, InterruptedException{
		Stat exists = zk.exists("/zk001", false);
		System.err.println(exists);
	}
	
	/**
	 * 删除节点
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	@Test
	public void testDelete() throws KeeperException, InterruptedException{
		zk.delete("/zk001", -1);
	}
	
	@After
	public void close() throws InterruptedException{
		zk.close();
	}
}
