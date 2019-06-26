package com.firecode.hadooptest.flink.data_stream_api.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomSourceFunction;

/**
 * 自定义数据源简单使用（注意：实现 SourceFunction 接口的数据源是不能并行处理的）
 * 输出 0-999 的数据到 Flink
 */
public class CustomSourceFunctionTest {
	
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 添加数据源
		DataStreamSource<Long> dataSource = env.addSource(new CustomSourceFunction());
		// 打印数据
		dataSource.print();
		env.execute("CustomSourceFunctionTest");
	}
}
