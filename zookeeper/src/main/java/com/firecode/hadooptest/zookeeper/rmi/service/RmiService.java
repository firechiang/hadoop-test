package com.firecode.hadooptest.zookeeper.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * rmi接口
 * @author JIANG
 */
public interface RmiService extends Remote {
	
	public String getName(String name) throws RemoteException;

}
