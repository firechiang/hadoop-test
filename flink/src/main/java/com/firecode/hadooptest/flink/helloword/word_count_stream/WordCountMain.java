package com.firecode.hadooptest.flink.helloword.word_count_stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
/**
 * 数据流（实时流）的方式统计单词出现次数
 * @author JIANG
 */
public class WordCountMain {
	
	
	public static void main(String[] args) throws Exception {
		final ParameterTool params = ParameterTool.fromArgs(args);
		// 设置执行环境（上下文执行环境）
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 设置全局参数
		env.getConfig().setGlobalJobParameters(params);
		// 连接7070端口服务去拉取流数据（注意：测试的话可以先使用：nc -lk 7070 启动服务（yum -y install nmap-ncat.x86_64），然后发送数据即可）
		DataStreamSource<String> streamDataSet = env.socketTextStream("192.168.83.143", 7070);
		// 以单条数据为Key，单词和次数为Value
		SingleOutputStreamOperator<Tuple2<String, Integer>> flatMap = streamDataSet.flatMap(new Tokenizer());
		// 按照单条数据分组（0就是按照Tuple2<String, Integer> Key分组）
		KeyedStream<Tuple2<String, Integer>, Tuple> keyBy = flatMap.keyBy(0);
		// 每5秒统计一次
		WindowedStream<Tuple2<String, Integer>, Tuple, TimeWindow> timeWindow = keyBy.timeWindow(Time.seconds(5));
		// 求和（1就是按照Tuple2<String, Integer> Value求和）
		SingleOutputStreamOperator<Tuple2<String, Integer>> sum = timeWindow.sum(1);
		// 打印数据
		DataStreamSink<Tuple2<String, Integer>> print = sum.print();
		// 设置并发数，默认3（注意：这个设置是针对当前算子的，不是全局的（我们这里是在 print 上设置的并发数，所以只在 print 算子上起作用））
		print.setParallelism(1);
		// 数据写入磁盘
		//sum.writeAsCsv("");
		env.execute("WordCountMain");
	}
	
	
	/**
	 * 单条数据分割写入单词出现次数
	 * @author JIANG
	 */
	private static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
		private static final long serialVersionUID = 1L;

		@Override
		public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
			// 分割单条数据
			String[] tokens = value.toLowerCase().split(",");
			for (String token : tokens) {
				if (token.length() > 0) {
					out.collect(new Tuple2<>(token, 1));
				}
			}
		}
	}

}
