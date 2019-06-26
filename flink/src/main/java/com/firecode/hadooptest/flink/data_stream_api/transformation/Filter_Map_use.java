package com.firecode.hadooptest.flink.data_stream_api.transformation;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomRichParallelSourceFunction;

/**
 * Filter过滤函数 和 Map转换函数简单使用
 * @author JAING
 */
public class Filter_Map_use {
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 添加数据源
		DataStreamSource<Long> dataSource = env.addSource(new CustomRichParallelSourceFunction());
		// 设置并行度
		DataStreamSource<Long> setParallelism = dataSource.setParallelism(1);
		// 过滤基数数据
		SingleOutputStreamOperator<Long> filter = setParallelism.filter(c -> c % 2 == 0);
		// 转换数据
		SingleOutputStreamOperator<Long> map = filter.map(c -> c + 2);
		// 打印数据
		map.print();
		env.execute("Filter_Map_use");
	}

}
