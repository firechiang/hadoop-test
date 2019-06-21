package com.firecode.hadooptest.flink.data_set_api.source;

import java.io.File;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple3;
import org.junit.Test;

import com.firecode.hadooptest.flink.domain.User;
/**
 * 加载csv文件数据
 * @author JIANG
 */
public class LoadCsvFileExample  {
	
	@Test
	public void test() throws Exception {
		// 获取DataSet执行上下文
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载csv文件数据
		CsvReader readCsvFile = env.readCsvFile(System.getProperty("user.dir")+File.separator+"data"+File.separator+"word_count.csv")
				                   .lineDelimiter("\n")  //行分割
				                   .fieldDelimiter(",")  //属性分割
		                           .ignoreFirstLine();   //忽略第一行数据
		// 设置映射实体类型的方式加载数据
		DataSource<User> dataSource1 = readCsvFile.pojoType(User.class, "name","age","job");
		// 设置Tuple类型的方式加载数据
		DataSource<Tuple3<String, Integer, String>> dataSource2 = readCsvFile.types(String.class, Integer.class, String.class);
		dataSource1.print();
		dataSource2.print();
	}
}
