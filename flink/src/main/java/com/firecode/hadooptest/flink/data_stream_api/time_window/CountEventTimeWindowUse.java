package com.firecode.hadooptest.flink.data_stream_api.time_window;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomRichParallelSourceFunction2;
import com.firecode.hadooptest.flink.domain.User;
/**
 * 
 * Count窗口（按数量切分计算数据）：当窗口内数据到达指定数量后（窗口内不会有相同数据）进行计算
 * 
 * 
 * Flink 事件时间描述
 * 1，Processing time（处理时间）Flink开始计算的时间，比如每隔5秒计算一次，就是开始计算的那个时间（注意：如果是计算时间段数据这个可能会不准，因为先产生的数据可能后到达Flink）
 * 2，Event time（事件时间）数据产生的时间（单条数据里面应该有个属性是描述数据的产生时间），在数据到达Flink之前就有了，比如日志数据，就是日志产生的那个时刻
 * 3，Ingestion time（摄取时间）数据进入Flink的时间
 * @author JIANG
 */
public class CountEventTimeWindowUse {
	
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
		// 添加数据源
		DataStreamSource<User> dataSource = setParallelism.addSource(new CustomRichParallelSourceFunction2());
		// 按照名称分组
		KeyedStream<User, Tuple> keyBy = dataSource.keyBy("name");
		/**
		 * （注意：如果上一步执行了keyBy()函数，那么后面的就是window或timeWindow，而window或timeWindow的后面是并行多任务执行的）
		 */
		/**
		 * size  指定数据数量
		 * slide 指定计算间隔时间
		 */
		WindowedStream<User, Tuple, GlobalWindow> window = keyBy.countWindow(5/*, 5*/);
		window.sum("age").print();
		
		
		/*timeWindow.process(new ProcessWindowFunction<User, User, Tuple, TimeWindow>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void process(Tuple key, ProcessWindowFunction<User, User, Tuple, TimeWindow>.Context context,
					Iterable<User> elements, Collector<User> out) throws Exception {
				for(User u:elements){
					System.err.println(u);
				}
			}
		});*/
		env.execute("CountEventTimeWindowUse");
	}

}
