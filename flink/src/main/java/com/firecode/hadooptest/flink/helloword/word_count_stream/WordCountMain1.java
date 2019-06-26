package com.firecode.hadooptest.flink.helloword.word_count_stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
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
 * 数据流（实时流）的方式统计单词出现次数（计算实时流流数据统称：流处理）
 * @author JIANG
 */
public class WordCountMain1 {
	
	
	public static void main(String[] args) throws Exception {
		final ParameterTool params = ParameterTool.fromArgs(args);
		// 设置执行环境（上下文执行环境）
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		/**
		 * 设置Flink处理事件时间规则
		 * TimeCharacteristic.ProcessingTime（处理时间）Flink开始计算的时间，比如每隔5秒计算一次，就是开始计算的那个时间（注意：如果是计算时间段数据这个可能会不准，因为先产生的数据可能后到达Flink）
		 * TimeCharacteristic.EventTime（事件时间）数据产生的时间（单条数据里面应该有个属性是描述数据的产生时间），在数据到达Flink之前就有了，比如日志数据，就是日志产生的那个时刻
		 * TimeCharacteristic.IngestionTime（摄取时间）数据进入Flink的时间
		 */
		env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
		// 设置全局参数
		env.getConfig().setGlobalJobParameters(params);
		// 连接7070端口服务去拉取流数据（注意：测试的话可以先使用：nc -lk 7070 启动服务（yum -y install nmap-ncat.x86_64），然后发送数据即可）
		DataStreamSource<String> streamDataSet = env.socketTextStream("192.168.83.143", 7070);
		// 以单条数据为Key，单词和次数为Value
		SingleOutputStreamOperator<Tuple2<String, Integer>> flatMap = streamDataSet.flatMap(new Tokenizer());
		// 数据分组（0就是按照Tuple2<String, Integer> Key分组，也可以通过对象的field名称，但用对象的field名称要自己先定义对象）
		KeyedStream<Tuple2<String, Integer>, Tuple> keyBy = flatMap.keyBy(0);
		// 每5秒统计一次
		WindowedStream<Tuple2<String, Integer>, Tuple, TimeWindow> timeWindow = keyBy.timeWindow(Time.seconds(5));
		// 求和（1就是按照Tuple2<String, Integer> Value求和，也可以通过对象的field名称，但用对象的field名称要自己先定义对象）
		SingleOutputStreamOperator<Tuple2<String, Integer>> sum = timeWindow.sum(1);
		// 打印数据
		DataStreamSink<Tuple2<String, Integer>> print = sum.print();
		// 设置并发数，默认3（注意：这个设置是针对当前算子的，不是全局的（我们这里是在 print 上设置的并发数，所以只在 print 算子上起作用））
		print.setParallelism(1);
		// 数据写入磁盘
		//sum.writeAsCsv("");
		// 执行任务（注意：如果没有调用这个函数任务是不会执行的，而只是创建每个操作并将其添加到程序的计划中而已）
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
