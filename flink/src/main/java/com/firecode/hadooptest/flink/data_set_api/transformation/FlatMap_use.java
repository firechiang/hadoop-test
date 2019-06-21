package com.firecode.hadooptest.flink.data_set_api.transformation;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;

import com.firecode.hadooptest.flink.LineSplitter;

/**
 * flatMap函数简单使用
 * @author JIANG
 */
public class FlatMap_use {
	
	public static void main(String[] args) throws Exception {
		// 获取DataSet执行上下文
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// 加载数据
		DataSource<String> dataSource = env.fromElements("Who's there?","I think I hear them. Stand, ho! Who's there?");
		// 转换数据
		FlatMapOperator<String, Tuple2<String, Integer>> flatMap = dataSource.flatMap(new LineSplitter(" "));
		// 按照 Tuple2<String, Integer> 的Key分组
		UnsortedGrouping<Tuple2<String, Integer>> groupBy = flatMap.groupBy(0);
		// 按照 Tuple2<String, Integer> 的Value求和
		AggregateOperator<Tuple2<String, Integer>> sum = groupBy.sum(1);
		// 打印数据
		sum.print();
	}
}
