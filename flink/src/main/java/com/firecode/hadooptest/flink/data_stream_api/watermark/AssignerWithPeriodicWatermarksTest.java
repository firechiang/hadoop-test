package com.firecode.hadooptest.flink.data_stream_api.watermark;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import com.firecode.hadooptest.flink.domain.Order;

/**
 * Watermark（水位线）简单使用和测试
 * 
 * 应用场景：
 * 数据产生到达Flink，大部分情况下，都是按照产生的时间顺序来的，但是也不排除由于网络、背压等原因，导致乱序的产生（先产生的数据后到达Flink）。
 * 但是对于先产生的数据迟迟没有到达Flink，我们又不能无限期的等下去，必须要有个机制来保证一个特定的时间后，必须触发window去进行计算了。这个特别的机制，就是watermark
 * 
 * 
 * 设置了水位线后，触发Window计算的条件
 * 1，Watermark（水位线） > Event Time（数据产生时间）
 * 2，Watermark（水位线） >= window_end_time（window窗口的结束时间），且在[window_start_time,window_end_time)中有数据存在
 * 
 * 
 * 注意：实现 AssignerWithPeriodicWatermarks 接口是定时提取Watermark（水位线），这种方式会定时提取更新wartermark（推荐生产使用）
 * @author JAING
 *
 */
public class AssignerWithPeriodicWatermarksTest {
	
	
	static Map<Long,Watermark> watermarkMap = new ConcurrentHashMap<>();
	
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 设置并行度
		env.setParallelism(1);
		/**
		 * 设置Flink处理事件时间规则
		 * TimeCharacteristic.ProcessingTime（处理时间）Flink开始计算的时间，比如每隔5秒计算一次，就是开始计算的那个时间（注意：如果是计算时间段数据这个可能会不准，因为先产生的数据可能后到达Flink）
		 * TimeCharacteristic.EventTime（事件时间）数据产生的时间（单条数据里面应该有个属性是描述数据的产生时间），在数据到达Flink之前就有了，比如日志数据，就是日志产生的那个时刻
		 * TimeCharacteristic.IngestionTime（摄取时间）数据进入Flink的时间
		 */
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		// 每1秒提取一次Watermark（水位线）（注意：env.setStreamTimeCharacteristic()函数的实现会设置一个默认值200毫秒，具体可查看源码）
		//env.getConfig().setAutoWatermarkInterval(1000);
		
		
		/*DataStreamSource<Order> dataSource = env.addSource(new CustomRichParallelSourceFunction3());
		DataStreamSource<Order> setParallelism = dataSource.setParallelism(1);*/
		
		// 连接7070端口服务去拉取流数据（注意：测试的话可以先使用：nc -lk 7070 启动服务（yum -y install nmap-ncat.x86_64），然后发送数据即可）
		// 发送的数据格式:
		//maomao,2019-06-27 15:48:43.000
		//maomao,2019-06-27 15:48:44.000
		//maomao,2019-06-27 15:50:31.000
		DataStreamSource<String> dataSource = env.socketTextStream("192.168.83.146", 7070);
		SingleOutputStreamOperator<Order> map = dataSource.map((String value)->{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Order order = new Order();
			String[] split = value.split(",");
			order.setName(split[0]);
			/*LocalDateTime of = LocalDateTime.of(2019, 6, 27, 14, 48, Integer.valueOf(split[1]));
		    Date date = Date.from(of.atZone(ZoneId.systemDefault()).toInstant());*/
		    order.setCreateTime(format.parse(split[1]));
			return order;
		});
		// 设置水位线
		SingleOutputStreamOperator<Order> watermark = map.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessGenerator());
		KeyedStream<Order, Tuple> keyBy = watermark.keyBy("name");
		// 设置一个滚动窗口，每3秒计算一次
		WindowedStream<Order, Tuple, TimeWindow> window = keyBy.window(TumblingEventTimeWindows.of(Time.seconds(5)));
		
		// 答应打印数据
		window.apply(new WindowFunction<Order, String, Tuple, TimeWindow>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void apply(Tuple key, TimeWindow window, Iterable<Order> input, Collector<String> out) throws Exception {
				List<Order> collect = StreamSupport.stream(input.spliterator(), false).sorted(new Comparator<Order>() {

					@Override
					public int compare(Order o1, Order o2) {
						
						return o1.getCreateTime().compareTo(o2.getCreateTime());
					}
				}).collect(Collectors.toList());
				
				int size = collect.size();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Date startTime = collect.get(0).getCreateTime();
				Date endTime = collect.get(size-1).getCreateTime();
				String res = "数量："+size+"，结束时间的水位线："+format.format(new Date(watermarkMap.get(endTime.getTime()).getTimestamp()))+"，Window范围：（"+format.format(new Date(window.getStart()))+"——"+format.format(new Date(window.getEnd()))+"）";
				out.collect(res);
			}
		}).print();
		env.execute("AssignerWithPeriodicWatermarksTest");
	}
	
	/**
	 * 定时提取Watermark（水位线），这种方式会定时提取更新Wartermark（水位线）
	 * @author JIANG
	 *
	 */
	private static class BoundedOutOfOrdernessGenerator implements AssignerWithPeriodicWatermarks<Order> {
		
		private static final long serialVersionUID = 1L;
        // 最大允许乱序的时间（单位毫秒）
		private final long maxOutOfOrderness = 3000;

	    private long currentMaxTimestamp;

	    /**
	     * 抽取数据产生的时间并找到最大时间
	     */
	    @Override
	    public long extractTimestamp(Order element, long previousElementTimestamp) {
	    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    	// 数据产生时间
	        long timestamp = element.getCreateTime().getTime();
	        // 每一次比较都取最大值
	        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
	        // 做测试用，记录当前数据在那个水位线上
	        Watermark currentWatermark = this.getCurrentWatermark();
	        watermarkMap.put(timestamp, currentWatermark);
	        System.err.println("时间："+format.format(element.getCreateTime())+"，水位线："+format.format(new Date(currentWatermark.getTimestamp())));
	        return timestamp;
	    }

	    /**
	     * 生成水位线（用最大时间减去最大等待时间）
	     */
	    @Override
	    public Watermark getCurrentWatermark() {
	    	Watermark watermark = new Watermark(currentMaxTimestamp - maxOutOfOrderness);
	        return watermark;
	    }
	}
}
