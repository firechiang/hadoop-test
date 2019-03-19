package com.firecode.hadooptest.hdfs.helloworld;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 本地（Windows）开发测试
 * @author JIANG
 *
 */
public class LocalTest {
	
	private FileSystem fs;
	private Configuration conf;
	
	public void connection() throws IOException, URISyntaxException, InterruptedException {
		this.conf = new Configuration(false);
		conf.set("fs.defaultFS", "hdfs://localhost:9820");
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
	
	
	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
		LocalTest test = new LocalTest();
		test.connection();
		System.err.println("目录是否存在："+test.exists(new Path("/tools")));
		//创建文件
		test.mkdir(new Path("/usr/test/"));
	}

}
