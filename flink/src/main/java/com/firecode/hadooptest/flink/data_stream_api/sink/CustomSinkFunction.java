package com.firecode.hadooptest.flink.data_stream_api.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomSourceFunction;

/**
 * 流处理自定义Sink（数据输出）
 * @author JIANG
 */
public class CustomSinkFunction {
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 添加数据源
		DataStreamSource<Long> dataSource = env.addSource(new CustomSourceFunction());
		// 添加自定义数据输出
		dataSource.addSink(new CustomRichSinkFunction());
		env.execute("CustomSink");
	}
	
	/**
	 * 自定义数据输出
	 * @author JIANG
	 */
	private static class CustomRichSinkFunction extends RichSinkFunction<Long> {
		
		private static final long serialVersionUID = 1L;

		@Override
		@SuppressWarnings("rawtypes")
		public void invoke(Long value, Context context) throws Exception {
			System.err.println(value);
		}

		@Override
		public void open(Configuration parameters) throws Exception {
			super.open(parameters);
		}

		@Override
		public void close() throws Exception {
			super.close();
		}
	}

}
