package com.firecode.hadooptest.kafka.stream_api;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.junit.Test;

/**
 * Stream流处理测试说明：
 * 1，启动 StreamTest（数据处理器）
 * 2，启动 ProducerTest（发送要处理的数据）
 * 3，启动 ConsumerTest（消费数据处理结果）
 * @author chiangfire
 *
 */
public class StreamTest extends StreamConfig {
	
	@Test
	public void test() throws InterruptedException {
		// 构建数据分析过程
		StreamsBuilder builder = builder();
		// 构建kafka流对象
		KafkaStreams streams = new KafkaStreams(builder.build(),streamConfig);
		// 启动kafka流应用
		streams.start();
		// 要阻塞否则无法处理数据
		TimeUnit.DAYS.sleep(1);
	}
	
	/**
	 * 构建数据分析过程
	 * @param builder
	 */
	StreamsBuilder builder() {
		StreamsBuilder builder = new StreamsBuilder();
		// 源数据流（注意：这里使用的是一个 Topic）
		KStream<String,String> source = builder.stream(TOPIC_IN);
		/**
		 * 分析数据
		 * 1，将数据根据空格将分割
		 * 2，将分割后的数据分组
		 * 3，统计每组里面的数据数量
		 */
		KTable<String, Long> countTable = source.flatMapValues(value-> {System.out.println(value);return Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" "));})
												     .groupBy((key,value)->value)
												     .count();
		/**
		 * 将分析完成的数据写入Topic
		 * @param topic     数据分析完成后写入Topic
		 * @param produced  写入Topic的数据 key value类型
		 */
		countTable.toStream().to(TOPIC_OUT,Produced.with(Serdes.String(), Serdes.Long()));
		return builder;
	}
}
