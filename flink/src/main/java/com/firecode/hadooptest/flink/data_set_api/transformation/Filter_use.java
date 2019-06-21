package com.firecode.hadooptest.flink.data_set_api.transformation;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FilterOperator;

/**
 * filter过滤函数简单使用
 * @author JAING
 */
public class Filter_use {
	
	public static void main(String[] args) throws Exception {
		// 加载上下文执行环境
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载数据
		DataSource<String> dataSource = env.fromElements("1","2","3","5","6","7","asdasdjisada");
		// 过滤数据
		FilterOperator<String> filter = dataSource.filter((value)->{
			
			return !value.equals("asdasdjisada");
		});
		filter.print();
	}

}
