package com.firecode.hadooptest.flink.data_stream_api.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomRichParallelSourceFunction;

/**
 * 自定义数据源（注意：继承 RichParallelSourceFunction 类的数据源是并行处理的，而且还可自定义open和clode逻辑）
 * 输出 0-999 的数据到 Flink
 */
public class CustomRichParallelSourceFunctionTest {
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 添加数据源
		DataStreamSource<Long> dataSource = env.addSource(new CustomRichParallelSourceFunction());
		// 设置并行度
		DataStreamSource<Long> setParallelism = dataSource.setParallelism(2);
		// 打印数据
		setParallelism.print();
		env.execute("CustomRichParallelSourceFunctionTest");
	}
}
