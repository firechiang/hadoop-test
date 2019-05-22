package com.firecode.hadooptest.zookeeper.rmi.service;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.ZooKeeper;

import com.firecode.hadooptest.zookeeper.connection.ZookeeperFactory;


/**
 * Rmi服务的消费者
 * 
 * @author JIANG
 */
public class RmiServiceConsumer {
	
	private static final List<String> urlList = new ArrayList<String>();
	
	public RmiServiceConsumer() throws InterruptedException{
		ZooKeeper zk = ZookeeperFactory.getInstance().create();
		if(null != zk){
			try {
				wathNode(zk);
			} catch (KeeperException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 监听Rmi服务地址的变化
	 * 每次Rmi服务地址有变化，先清空原有的服务地址，再添加新的服务地址
	 * @param zk
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void wathNode(ZooKeeper zk) throws KeeperException, InterruptedException {
		//清空原有的服务地址
		urlList.clear();
		List<String> children = zk.getChildren(RmiServiceProvider.ZK_REGISTRY_PATH, event -> {
			//如果子节点有变化
			if(event.getType() == Event.EventType.NodeChildrenChanged){
				try {
					//重新调用一次自己
					wathNode(zk);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		if(null != children) {
			for(String node:children) {
				byte[] data = zk.getData(String.join("/",RmiServiceProvider.ZK_REGISTRY_PATH,node), false, null);
				String url = new String(data);
				System.err.println("新增Rmi服务地址："+url);
				urlList.add(url);
			}
		}
	}
	
	
	/**
	 * 获取Rmi服务
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Remote> T getRmiService() {
		T service = null;
		int size = urlList.size();
		if(size > 0){
			String url;
			if(size == 1){
				url = urlList.get(0);
			}else{
				//随机取一个
				url = urlList.get(ThreadLocalRandom.current().nextInt(size));
			}
			try {
				service = (T)Naming.lookup(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}  //获取服务
		}
		return service;
	}

}
