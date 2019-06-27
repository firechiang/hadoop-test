package com.firecode.hadooptest.flink.data_stream_api.transformation;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomParalleSourceFunction;
import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomRichParallelSourceFunction;

/**
 * Union合并多个流数据函数的简单使用
 * @author JAING
 */
public class Union_use {
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		/**
		 * 设置Flink处理事件时间规则
		 * TimeCharacteristic.ProcessingTime（处理时间）Flink开始计算的时间，比如每隔5秒计算一次，就是开始计算的那个时间（注意：如果是计算时间段数据这个可能会不准，因为先产生的数据可能后到达Flink）
		 * TimeCharacteristic.EventTime（事件时间）数据产生的时间（单条数据里面应该有个属性是描述数据的产生时间），在数据到达Flink之前就有了，比如日志数据，就是日志产生的那个时刻
		 * TimeCharacteristic.IngestionTime（摄取时间）数据进入Flink的时间
		 */
		env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
		StreamExecutionEnvironment setParallelism = env.setParallelism(1);
		// 添加数据源1
		DataStreamSource<Long> dataSource1 = setParallelism.addSource(new CustomRichParallelSourceFunction());
		// 添加数据源2
		DataStreamSource<Long> addSource2 = setParallelism.addSource(new CustomParalleSourceFunction());
		// 合并数据源
		DataStream<Long> union = dataSource1.union(addSource2);
		union.print().setParallelism(1);
		env.execute("Union_use");
	}

}
