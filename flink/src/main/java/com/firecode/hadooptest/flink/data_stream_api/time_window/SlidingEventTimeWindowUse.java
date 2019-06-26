package com.firecode.hadooptest.flink.data_stream_api.time_window;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomRichParallelSourceFunction2;
import com.firecode.hadooptest.flink.domain.User;
/**
 * 
 * 滑动窗口（按时间间隔计算前一段时间的数据）：将指定时间范围内数据放到指定大小的窗口当中，再指定一个滑动时间。
 * 比如：设置10分钟的窗口滑动5分钟。每隔5分钟就会得到一个窗口，其中包含过去10分钟内的数据（注意：如果滑动时间小于窗口时间，则可能出现当前窗口和上一个窗口有相同的数据）
 * 适用场景：每隔半小时统计一下前一个小时的TOP N（每隔一段时间，计算一下前N段时间的数据）
 * 
 * 
 * Flink 事件时间描述
 * 1，Processing time（处理时间）Flink开始计算的时间，比如每隔5秒计算一次，就是开始计算的那个时间（注意：如果是计算时间段数据这个可能会不准，因为先产生的数据可能后到达Flink）
 * 2，Event time（事件时间）数据产生的时间（单条数据里面应该有个属性是描述数据的产生时间），在数据到达Flink之前就有了，比如日志数据，就是日志产生的那个时刻
 * 3，Ingestion time（摄取时间）数据进入Flink的时间
 * @author JIANG
 */
public class SlidingEventTimeWindowUse {
	
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
		// 设置5秒的窗口滑动2秒。每隔2秒就会得到一个窗口，其中包含过去5秒内某一段的数据（注意：如果滑动时间小于窗口时间，则可能出现当前窗口和上一个窗口有相同的数据）
		WindowedStream<User, Tuple, TimeWindow> window = keyBy.window(SlidingProcessingTimeWindows.of(Time.seconds(5), Time.seconds(2)));
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
		env.execute("TumblingEventTimeWindowUse");
	}

}
