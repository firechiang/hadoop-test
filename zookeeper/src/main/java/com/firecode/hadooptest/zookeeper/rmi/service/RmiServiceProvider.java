package com.firecode.hadooptest.zookeeper.rmi.service;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.firecode.hadooptest.zookeeper.connection.ZookeeperFactory;

/**
 * Rmi服务生产者
 * @author JIANG
 */
public class RmiServiceProvider {
	
	public static final String ZK_REGISTRY_PATH = "/registry";//注意这个节点要先创建，或者会报错
	public static final String ZK_PROVIDER_PATH = String.join("/",ZK_REGISTRY_PATH,"provider");
	
	
	/**
	 * 注册 Rmi 服务
	 */
	public void putlish(Remote remote,String host,int port){
		String url = this.putslihService(remote, host, port);
		if(url != null){
			ZooKeeper zookeeper = ZookeeperFactory.getInstance().create();
			if(null != zookeeper){
				this.createNode(zookeeper, url);
			}
		}
	}
	
	/**
	 * 创建zookeeper node 节点
	 * @param zk
	 * @param url
	 */
	private void createNode(ZooKeeper zk,String url) {
		byte[] data = url.getBytes();
		try {
			Stat exists = zk.exists(ZK_REGISTRY_PATH, false);
			//如果父节点不存在就创建
			if(null == exists){
				zk.create(ZK_REGISTRY_PATH, ZK_REGISTRY_PATH.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			/**
			 * 创建节点
			 * 1，路径
			 * 2，数据
			 * 3，权限，此为最大权限
			 * 4.节点的类型《类型分4中  1短暂的，就是和zk断开连接后删除当前节点，2 持久化的     说明：后面加 SEQUENTIAL<如下面> 的说明重名没有关系，因为他是往后加序列号的写》
			 */
			String res = zk.create(ZK_PROVIDER_PATH, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			System.err.println("Rmi服务注册成功，服务地址："+url+"，注册地址："+res);
		} catch (KeeperException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * 创建 rmi 服务
	 * @param remote
	 * @param host
	 * @param port
	 * @return
	 */
	private String putslihService(Remote remote,String host,int port){
		String url = null;
		try{
			url = String.format("rmi://%s:%d/%s",host,port,remote.getClass().getName());
			LocateRegistry.createRegistry(port);
			Naming.rebind(url, remote);
		}catch(Exception e){
			e.printStackTrace();
		}
		return url;
	}

}