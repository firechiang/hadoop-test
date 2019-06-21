package com.firecode.hadooptest.flink.data_set_api.transformation;

import java.util.Arrays;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.CrossOperator.DefaultCross;
import org.apache.flink.api.java.operators.DataSource;

/**
 * cross 函数简单使用
 * 左边的每一条数据，和右边的所有数据做关联
 * @author JIANG
 */
public class Cross_use {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		DataSource<Integer> dataSource1 = env.fromCollection(Arrays.asList(1,2,3,4,5));
		DataSource<String> dataSource2 = env.fromCollection(Arrays.asList("第一","第二","第三"));
		// 左边的每一条数据，和右边的所有数据做关联
		DefaultCross<Integer, String> cross = dataSource1.cross(dataSource2);
		cross.print();
	}
}
