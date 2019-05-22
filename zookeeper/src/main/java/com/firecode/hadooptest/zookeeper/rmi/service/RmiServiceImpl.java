package com.firecode.hadooptest.zookeeper.rmi.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * rmi接口实现
 * 
 * @author JIANG
 */
public class RmiServiceImpl extends UnicastRemoteObject implements RmiService{

	private static final long serialVersionUID = -3578305680610938648L;


	public RmiServiceImpl() throws RemoteException {
		super();
	}
	
	@Override
	public String getName(String name) throws RemoteException {
		
		return String.format("我是名字：%s", name);
	}

}
