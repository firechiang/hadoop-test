package com.firecode.hadooptest.zookeeper.rmi;

import java.rmi.RemoteException;

import com.firecode.hadooptest.zookeeper.rmi.service.RmiService;
import com.firecode.hadooptest.zookeeper.rmi.service.RmiServiceConsumer;

public class RmiClient {
	
	public static void main(String[] args) throws InterruptedException, RemoteException {
		RmiServiceConsumer sc = new RmiServiceConsumer();
		while(true){
			RmiService service = sc.getRmiService();
			if(null != service){
				System.out.println(service.getName("毛毛"));
			}else{
				System.err.println("没有可用服务，建议调整RmiServer端口，再启动添加服务");
			}
		    Thread.sleep(3000);
		}
	}

}
