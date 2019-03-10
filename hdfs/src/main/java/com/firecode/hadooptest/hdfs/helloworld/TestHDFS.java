package com.firecode.hadooptest.hdfs.helloworld;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

/**
 * 文件更多操作请看 org.apache.hadoop.fs.FileSystem 源码
 * @author JIANG
 *
 */
public class TestHDFS implements Closeable {
	
	private FileSystem fs;
	private Configuration conf;
	/**
	 * Windows开发须做如下准备且完成后电脑重启
	 * 1，配置环境变量 HADOOP_USER_NAME，值配的是远程HDFS配置文件（hadoop-env.sh）里面配的那个操作用户名
	 * 2，下载Hadoop并配置环境变量 HADOOP_HOME（别忘了将bin目录加入Path），并下载hadoop.dll和winutils.exe放到bin目录。下载地址（官方给的）：https://github.com/steveloughran/winutils
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 */
	public void connection() throws IOException, URISyntaxException, InterruptedException {
		
		this.conf = new Configuration(false);
		/*
		  *  直接使用HDFS配置文件连接
		 * conf.addResource("/opt/huabingood/pseudoDistributeHadoop/hadoop-2.6.0-cdh5.10.0/etc/hadoop/core-site.xml");
         * conf.addResource("/opt/huabingood/pseudoDistributeHadoop/hadoop-2.6.0-cdh5.10.0/etc/hadoop/hdfs-site.xml");
        */
		conf.set("fs.defaultFS", "hdfs://mycluster");
		conf.set("dfs.nameservices","mycluster");
		conf.set("dfs.ha.namenodes.mycluster","myNameNode1,myNameNode2");
		conf.set("dfs.namenode.rpc-address.mycluster.myNameNode1","172.20.10.7:8020");
		conf.set("dfs.namenode.rpc-address.mycluster.myNameNode2","172.20.10.8:8020");
		//hdfs文件系统客户端实现，如果没配将导致java.io.IOException: No FileSystem for scheme: hdfs
		conf.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem");
		conf.set("dfs.client.failover.proxy.provider.mycluster","org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
		this.fs = FileSystem.get(conf);
	}
	
	/**
	 * 文件目录是否存在
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean exists(Path path) throws IOException {
		
		return this.fs.exists(path);
	}
	
	/**
	 * 创建文件或目录
	 * @param path
	 * @throws IOException
	 */
	public void mkdir(Path path) throws IOException {
		
		if(exists(path)) {
			/**
			 * true 递归删除所有子目录
			 */
			fs.delete(path,true);
		}
		fs.create(path);
	}
	
	/**
	 * 上传
	 * @throws IOException
	 */
	public void upload() throws IOException {
		Path path = new Path("/tools/jdk-8u171-linux-x64.tar.gz");
		FSDataOutputStream out = fs.create(path);
		InputStream in = new BufferedInputStream(new FileInputStream(new File("D:\\test-file\\jdk-8u171-linux-x64.tar.gz")));
		IOUtils.copyBytes(in, out,conf ,true);
	}
	/**
	 * 下载
	 * @throws IOException
	 */
	public void download() throws IOException {
		Path path = new Path("/tools/jdk-8u171-linux-x64.tar.gz");
		FSDataInputStream in = fs.open(path);
		//in.seek(1212);  偏移量，从文件的哪个位置开始读
		OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("D:\\test-file\\jdk-8u171-linux-x64.tar.gz-下载")));
		IOUtils.copyBytes(in, out,conf ,true);
	}
	
	/**
	 * 获取文件块的信息
	 * @throws IOException 
	 */
	public void blockInfo() throws IOException {
		Path path = new Path("/tools/jdk-8u171-linux-x64.tar.gz");
		//获取文件或目录简要信息
		FileStatus file = fs.getFileStatus(path);
		BlockLocation[] fileBlockLocations = fs.getFileBlockLocations(file, 0, file.getLen());
		for (int i = 0; i < fileBlockLocations.length; i++) {
			BlockLocation blockLocation = fileBlockLocations[i];
			System.err.println(blockLocation);
		}
	}
	
	
	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
		TestHDFS hdfs = new TestHDFS();
		hdfs.connection();
		p(hdfs.exists(new Path("/tools")));
		hdfs.mkdir(new Path("/testtest"));
		//hdfs.upload();
		//hdfs.download();
		hdfs.blockInfo();
		hdfs.close();
	}


	@Override
	public void close() throws IOException {
		this.fs.close();
	}
	
	private static void p(Object o) {
		System.err.println(o);
	}
	

}
