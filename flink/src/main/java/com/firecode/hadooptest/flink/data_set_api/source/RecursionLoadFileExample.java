package com.firecode.hadooptest.flink.data_set_api.source;

import java.io.File;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;
import org.junit.Test;
/**
 * 递归加载文件夹下所有文件数据
 * @author JIANG
 */
public class RecursionLoadFileExample  {
	
	@Test
	public void test() throws Exception {
		// 获取DataSet执行上下文
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载数据
		DataSource<String> dataSource = env.readTextFile(System.getProperty("user.dir")+File.separator+"data"+File.separator);
		Configuration parameters = new Configuration();
		// 递归读取文件夹下所有文件数据
		parameters.setBoolean("recursive.file.enumeration", true);
		dataSource = dataSource.withParameters(parameters);
		dataSource.print();
	}
}
