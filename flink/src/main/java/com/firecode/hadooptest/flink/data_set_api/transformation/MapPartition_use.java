package com.firecode.hadooptest.flink.data_set_api.transformation;

import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapPartitionOperator;
import org.apache.flink.util.Collector;

/**
 * mapPartition函数（转换分区数据）简单使用
 * 转换分区里面的数据（就是一个分区调用一次）
 * @author JAING
 */
public class MapPartition_use {
	
	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		DataSource<String> dataSource = env.fromElements("1","2","3","4","5");
		// 设置2并行度（如果没有自定义分区，那么设置2个并行度，会自动有2个分区）
		DataSource<String> setParallelism = dataSource.setParallelism(2);
		MapPartitionOperator<String, String> mapPartition = setParallelism.mapPartition(new MapPartition());
		mapPartition.print();
	}
	
	public static class MapPartition implements MapPartitionFunction<String,String> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void mapPartition(Iterable<String> values, Collector<String> out) throws Exception {
			StringBuilder sb = new StringBuilder();
			for(String s:values){
				sb.append(s);
			}
			out.collect(sb.toString());
		}
	}

}
