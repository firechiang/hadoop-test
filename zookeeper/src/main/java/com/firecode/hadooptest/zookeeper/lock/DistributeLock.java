package com.firecode.hadooptest.zookeeper.lock;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.firecode.hadooptest.zookeeper.connection.ZookeeperFactory;

/**
 * 分布式锁
 * @author JIANG
 */
public class DistributeLock implements Watcher {
	
	private static final String GROUP_PATH = "/disLocks";
	private static final String SUB_PATH = "/disLocks/sub";
	
	/**
	 * 获取到锁之后的回调函数
	 */
	private Consumer<?> consumer;
	/**
	 * 当前锁的路径
	 */
	private String selfPath;
	/**
	 * 排在前面的那把锁的路径
	 */
	private String waitPath;
	
	private ZooKeeper zk;
	
	public DistributeLock(Consumer<?> consumer){
		this.consumer = consumer;
		this.zk = ZookeeperFactory.getInstance().create();
		try {
			//如果锁的父节点不存在，就创建一个持久的父节点
			if(zk.exists(GROUP_PATH, false) == null){
				this.zk.create(GROUP_PATH, GROUP_PATH.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			//获取锁
			this.getLock();
		} catch (KeeperException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取锁
	 */
	public boolean getLock() throws KeeperException, InterruptedException {
		/**
		 * 创建节点
		 * 参数1：路径  
		 * 参数2：数据  
		 * 参数 3：权限，完全开放权限
		 * 参数4：临时节点(绑定Session)，连接断开后自动删除，后面加 SEQUENTIAL说明重名没有关系，因为它是往后加序列号的写
		 */
		selfPath = zk.create(SUB_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.err.println("创建锁路径："+selfPath+"，线程ID："+Thread.currentThread().getId());
		if(checkMinPath()){
			return true;
		}
		return false;
	}
	
	public boolean checkMinPath() throws KeeperException, InterruptedException{
		List<String> subNodes = zk.getChildren(GROUP_PATH, false);
		Collections.sort(subNodes);
		int index = subNodes.indexOf(selfPath.substring(GROUP_PATH.length()+1));
		switch(index){
			case -1:{
				System.err.println("本节点不存在！");
				return false;
			}
			case 0:{
				//获取到锁
				if(null != this.consumer){
					//回调
					this.consumer.accept(null);
				}
				this.unlock();
				return true;
			}
			default:{
				//排在前面的那把锁路径
				this.waitPath = GROUP_PATH + "/"+subNodes.get(index -1);
				try{
					zk.getData(waitPath, this, new Stat());
					return false;
				}catch(Exception e){
					//如果排在前面的那把锁路径没有了
					if(zk.exists(waitPath, false) == null){
						return checkMinPath();
					}else{
						throw e;
					}
				}

			}
		}
		
	}
	
	/**
	 * 释放锁
	 */
	public void unlock(){
		try{
			if(zk.exists(this.selfPath, false) == null){
				System.err.println("节点不存在！");
				return;
			}
			zk.delete(this.selfPath, -1);
			zk.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听锁列表的变化
	 */
	@Override
	public void process(WatchedEvent event) {
		//如果排在前面的那把锁路径被移除了
		if(event.getType() == Event.EventType.NodeDeleted && event.getPath().equals(this.waitPath)){
			try{
				this.checkMinPath();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
