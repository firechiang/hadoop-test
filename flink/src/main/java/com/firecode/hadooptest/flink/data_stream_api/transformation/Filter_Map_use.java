package com.firecode.hadooptest.flink.data_stream_api.transformation;

import org.apache.flink.streaming.api.TimeCharacteristic;
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
		/**
		 * 设置Flink处理事件时间规则
		 * TimeCharacteristic.ProcessingTime（处理时间）Flink开始计算的时间，比如每隔5秒计算一次，就是开始计算的那个时间（注意：如果是计算时间段数据这个可能会不准，因为先产生的数据可能后到达Flink）
		 * TimeCharacteristic.EventTime（事件时间）数据产生的时间（单条数据里面应该有个属性是描述数据的产生时间），在数据到达Flink之前就有了，比如日志数据，就是日志产生的那个时刻
		 * TimeCharacteristic.IngestionTime（摄取时间）数据进入Flink的时间
		 */
		env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
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
