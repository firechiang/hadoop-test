package com.firecode.hadooptest.zookeeper.rmi;

import java.rmi.RemoteException;

import com.firecode.hadooptest.zookeeper.rmi.service.RmiService;
import com.firecode.hadooptest.zookeeper.rmi.service.RmiServiceImpl;
import com.firecode.hadooptest.zookeeper.rmi.service.RmiServiceProvider;

/**
 * Rmi服务器(可使用修改端口的方法启动多个)
 * @author JIANG
 *
 */
public class RmiServer {
	
	public static void main(String[] args) throws RemoteException, InterruptedException {
		String host = "localhost";
		int port = 11232;
		RmiServiceProvider sp = new RmiServiceProvider();
		RmiService hs  = new RmiServiceImpl();
		//创建rmi服务并且并且创建zookeeper node 节点
		sp.putlish(hs, host, port);
		Thread.sleep(Integer.MAX_VALUE);
	}

}
