package com.firecode.hadooptest.flink.table_sql_api;

import java.io.File;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.types.Row;

import com.firecode.hadooptest.flink.domain.User;
/**
 * 使用SQL API进行批处理（离线计算）
 * @author JIANG
 */
public class DataSetTableApiTest {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		
		BatchTableEnvironment tableEnv = BatchTableEnvironment.create(env);
		// 读取CSV文件
		CsvReader readCsvFile = env.readCsvFile(System.getProperty("user.dir")+File.separator+"data"+File.separator+"word_count.csv")	
								   .lineDelimiter("\n")  //行分割
				                   .fieldDelimiter(",")  //属性分割
				                   .ignoreFirstLine();   //忽略第一行数据
		// 设置映射实体类型的方式加载数据
		DataSource<User> dataSource = readCsvFile.pojoType(User.class, "name","age","job");
		// 基于数据源建立Table
		Table table = tableEnv.fromDataSet(dataSource);
		// 注册表
		tableEnv.registerTable("test_user", table);
		// 查询数据并创建Table
		Table resultTable = tableEnv.sqlQuery("select * from test_user where age > 30");
		// 将Table数据转换为DataSet
		DataSet<Row> dataSet = tableEnv.toDataSet(resultTable,Row.class);
		// 打印数据
		dataSet.print();
	}
}
