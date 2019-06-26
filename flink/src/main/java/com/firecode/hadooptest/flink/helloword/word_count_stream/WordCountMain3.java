package com.firecode.hadooptest.flink.helloword.word_count_stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
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
public class WordCountMain3 {
	
	
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
		SingleOutputStreamOperator<WC> flatMap = streamDataSet.flatMap(new Tokenizer());
		// 数据分组（word就是按照WC对象 word属性分组）
		KeyedStream<WC, String> keyBy = flatMap.keyBy(new KeySelector<WC, String>() {
			private static final long serialVersionUID = 1L;
			
			public String getKey(WC wc) { 
				return wc.word; 
			}
	    });
		// 每5秒统计一次
		WindowedStream<WC, String, TimeWindow> timeWindow = keyBy.timeWindow(Time.seconds(5));
		// 求和（count就是按照WC对象 count属性求和）
		SingleOutputStreamOperator<WC> sum = timeWindow.sum("count");
		// 打印数据
		DataStreamSink<WC> print = sum.print();
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
	private static class Tokenizer implements FlatMapFunction<String, WC> {
		private static final long serialVersionUID = 1L;

		@Override
		public void flatMap(String value, Collector<WC> out) throws Exception {
			// 分割单条数据
			String[] tokens = value.toLowerCase().split(",");
			for (String token : tokens) {
				if (token.length() > 0) {
					out.collect(new WC(token, 1));
				}
			}
		}
	}
	
	public static class WC {
		/**
		 * 单词
		 */
		private String word;
		/**
		 * 数量
		 */
		private Integer count;
		
		public WC() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		public WC(String word, Integer count) {
			super();
			this.word = word;
			this.count = count;
		}
		public String getWord() {
			return word;
		}
		public void setWord(String word) {
			this.word = word;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
		
		@Override
		public String toString() {
			return "WC [word=" + word + ", count=" + count + "]";
		}
	}

}
