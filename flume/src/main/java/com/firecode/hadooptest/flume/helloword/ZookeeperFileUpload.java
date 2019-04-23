package com.firecode.hadooptest.flume.helloword;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 上传Flume配置文件到Zookeeper
 * @author JIANG
 */
public class ZookeeperFileUpload {
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		Thread currentThread = Thread.currentThread();
		ZooKeeper zk = new ZooKeeper("192.168.83.1:2181", 3000, (WatchedEvent e)->{
			if(KeeperState.SyncConnected.equals(e.getState())){
				LockSupport.unpark(currentThread);
			}
		});
		LockSupport.park();
		FileInputStream fs = new FileInputStream(new File("D:\\avro-conf.properties"));
		byte[] bytes = new byte[fs.available()];
		fs.read(bytes);
		fs.close();
		String rootPath = "/flume";
		if(zk.exists(rootPath, true) == null) {
		    zk.create(rootPath,"a".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT); 
		}
		String path = rootPath+"/a1";
		if(zk.exists(path, true) == null) {
		    zk.create(path, bytes, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		Stat s = new Stat();
		byte[] data = zk.getData(path, false, s);
		System.out.println(new String(data));
		System.err.println("我是数据版本号："+s.getVersion());
	}
}
