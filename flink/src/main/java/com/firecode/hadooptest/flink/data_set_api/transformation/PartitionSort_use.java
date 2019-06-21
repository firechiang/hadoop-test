package com.firecode.hadooptest.flink.data_set_api.transformation;

import java.util.Arrays;

import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.SortPartitionOperator;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * 分区数据排序简单使用
 * @author JAING
 */
public class PartitionSort_use {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载数据   
		DataSource<Tuple2<String, Integer>> dataSource = env.fromCollection(Arrays.asList(new Tuple2<>("aaa",1),new Tuple2<>("sdfsdfsd",2),new Tuple2<>("sdfsdfs",1),new Tuple2<>("dfsdfa",1)));
		DataSource<Tuple2<String, Integer>> setParallelism = dataSource.setParallelism(2);
		// 分区数据排序
		SortPartitionOperator<Tuple2<String, Integer>> sortPartition = setParallelism.sortPartition(1, Order.ASCENDING);
		sortPartition.print();
	}
}
