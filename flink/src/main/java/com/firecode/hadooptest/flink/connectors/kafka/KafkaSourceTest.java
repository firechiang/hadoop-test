package com.firecode.hadooptest.flink.connectors.kafka;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

/**
 * 以Kafka为数据源（从Kafka拉取数据）简单测试
 * @author JIANG
 */
public class KafkaSourceTest {
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
		// 第版本Kafka才需要配置Zookeeper地址
	    //properties.setProperty("zookeeper.connect", "localhost:2181");
		// 消费者组的名称（顺便起）
		properties.setProperty("group.id", "flink-test-group");
		/**
		 * @param topic              主题名称
		 * @param valueDeserializer  数据解码器
		 * @param properties         Kafka相关配置
		 */
		DataStream<String> stream = env.addSource(new FlinkKafkaConsumer<>("flink-test", new SimpleStringSchema(), properties));
		// 打印消息
		stream.print();
		env.execute("KafkaSourceTest");
	}
}
