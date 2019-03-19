package com.firecode.hadooptest.hdfs.helloworld;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 本地（Windows）开发测试
 * @author JIANG
 *
 */
public class LocalTest {
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration(true);
		FileSystem fileSystem = FileSystem.get(conf);
		//创建目录
		fileSystem.create(new Path("/usr/test/"));
	}

}
