package com.firecode.hadooptest.flink.data_set_api.transformation;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.GroupReduceOperator;

/**
 * 取前N条数据简单使用
 * @author JAING
 */
public class FirstN_use {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		DataSource<String> fromElements = env.fromElements("1","2","3","4","5");
		// 取前3条数据
		GroupReduceOperator<String, String> first = fromElements.first(3);
		first.print();
	}
}
