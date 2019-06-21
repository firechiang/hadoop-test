package com.firecode.hadooptest.flink.data_set_api.transformation;

import java.util.Arrays;

import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.GroupReduceOperator;
import org.apache.flink.api.java.operators.SortedGrouping;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * 分组后再排序简单使用
 * @author JAING
 */
public class GroupSort_use {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载数据   
		DataSource<Tuple2<Integer, String>> dataSource = env.fromCollection(Arrays.asList(new Tuple2<>(1,"aaa"),new Tuple2<>(2,"sdfsdfsd"),new Tuple2<>(3,"sdfsdfs"),new Tuple2<>(4,"dfsdfa")));
		// 按照 Tuple2<String, Integer> 的Key分组
		UnsortedGrouping<Tuple2<Integer, String>> groupBy = dataSource.groupBy(0);
		// 分组后排序
		SortedGrouping<Tuple2<Integer, String>> sortGroup = groupBy.sortGroup(1, Order.DESCENDING);
		// 取前1个
		GroupReduceOperator<Tuple2<Integer, String>, Tuple2<Integer, String>> first = sortGroup.first(1);
		first.print();
	}
}
