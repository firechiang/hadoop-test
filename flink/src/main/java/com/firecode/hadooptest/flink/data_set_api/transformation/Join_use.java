package com.firecode.hadooptest.flink.data_set_api.transformation;

import java.util.Arrays;

import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.JoinOperator.DefaultJoin;
import org.apache.flink.api.java.operators.JoinOperator.EquiJoin;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * join连接两个数据集的函数简单使用
 * 
 * @author JIANG
 */
public class Join_use {

	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		DataSource<Tuple2<Integer, String>> dataSource1 = env.fromCollection(Arrays.asList(new Tuple2<>(1, "111"), new Tuple2<>(2, "222"), new Tuple2<>(4, "333")));
		DataSource<Tuple2<Integer, String>> dataSource2 = env.fromCollection(Arrays.asList(new Tuple2<>(1, "第一"), new Tuple2<>(2, "第二"), new Tuple2<>(3, "第三")));
		// 连接dataSource2，where(0)是指左边用哪个属性做连接，equalTo(0)是指右边用哪个属性做连接；0都是取的Tuple2<>的Key
		DefaultJoin<Tuple2<Integer, String>, Tuple2<Integer, String>> equalTo = dataSource1.join(dataSource2).where(0).equalTo(0);
		// 处理连接后的结果集数据		
		EquiJoin<Tuple2<Integer, String>, Tuple2<Integer, String>, Collector<Tuple2<Integer, String>>> with = equalTo.with(new PointWeighter());
		with.print();
	}

	public static class PointWeighter implements FlatJoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Collector<Tuple2<Integer, String>>> {
		private static final long serialVersionUID = 1L;
		@Override
		public void join(Tuple2<Integer, String> first, Tuple2<Integer, String> second,
				Collector<Collector<Tuple2<Integer, String>>> out) throws Exception {
			System.err.println("左边："+first+"，右边："+second);
		}
	}
}
