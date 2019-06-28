package com.firecode.hadooptest.flink.connectors;

import java.time.ZoneId;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.fs.StringWriter;
import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;
import org.apache.flink.streaming.connectors.fs.bucketing.DateTimeBucketer;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomSourceFunction;

/**
 * 将数据写到文件简单测试（地址可以写HDFS的地址）
 * @author JIANG
 */
public class FileSystemSinkTest {
	
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
		DataStreamSource<Long> dataSource = env.addSource(new CustomSourceFunction());
		// 分桶就是分文件夹的sink，地址可以写hdfs地址
		BucketingSink<Long> bucketingSink = new BucketingSink<>("file:///D:/flink-data");
		bucketingSink.setBucketer(new DateTimeBucketer<>("yyyy-MM-dd--HHmm", ZoneId.of("America/Los_Angeles")));
		bucketingSink.setWriter(new StringWriter<Long>());
		// 400M写一次
		//bucketingSink.setBatchSize(1024 * 1024 * 400);
		// 5秒写一次
		bucketingSink.setBatchRolloverInterval(5);
		// 添加自定义数据输出
		dataSource.addSink(bucketingSink);
		env.execute("FileSystemSinkTest");
	}

}
