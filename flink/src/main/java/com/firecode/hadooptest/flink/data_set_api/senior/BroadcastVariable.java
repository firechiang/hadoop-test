package com.firecode.hadooptest.flink.data_set_api.senior;

import java.util.Collection;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.configuration.Configuration;

/**
 * Broadcast共享数据简单使用
 * 
 * @author JIANG
 */
public class BroadcastVariable {

	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		DataSource<Integer> toBroadcast = env.fromElements(1, 2, 3);
		DataSource<String> data = env.fromElements("a", "b");
		MapOperator<String, String> map = data.map(new RichMapFunction<String, String>() {
			private static final long serialVersionUID = 1L;
			/**
			 * Map开始之前调用
			 */
			@Override
			public void open(Configuration parameters) throws Exception {
                // 获取共享数据
				Collection<Integer> broadcastSet = getRuntimeContext().getBroadcastVariable("broadcastSetName");
				System.err.println(broadcastSet);
			}

			@Override
			public String map(String value) throws Exception {
				return value;
			}
		});
		// 将数据源 toBroadcast 共享到Map算子（共享数据应该是在Map算子之前执行）
		MapOperator<String, String> withBroadcastSet = map.withBroadcastSet(toBroadcast, "broadcastSetName");
		withBroadcastSet.print();
	}

}
