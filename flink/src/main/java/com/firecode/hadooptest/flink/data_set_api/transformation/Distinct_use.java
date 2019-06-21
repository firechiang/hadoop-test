package com.firecode.hadooptest.flink.data_set_api.transformation;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.DistinctOperator;

/**
 * distinct去重函数简单使用
 * @author JAING
 */
public class Distinct_use {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		DataSource<Integer> dataSource = env.fromElements(1,2,3,4,5,6,2,4,5);
		// 去重
		DistinctOperator<Integer> distinct = dataSource.distinct();
		// 去重指定位置
		dataSource.distinct(0);
		distinct.print();
	}
}
