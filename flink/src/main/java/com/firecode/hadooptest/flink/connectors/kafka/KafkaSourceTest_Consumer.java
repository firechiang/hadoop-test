package com.firecode.hadooptest.flink.connectors.kafka;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

/**
 * 以Kafka为数据源（从Kafka拉取数据）简单测试
 * 可先使用 KafkaSinkTest 推送数据
 * @author JIANG
 */
public class KafkaSourceTest_Consumer {
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		/**
		 * Flink在处理流数据时，每个function和operator都可以是有状态的。
		 * 为了使状态容错，Flink需要对状态进行checkpoint（检查点）。检查点允许Flink恢复流中的状态和位置。
		 * 可以在一定时间内重复使用持久化的数据源。此类源的示例：
		 * 持久消息队列(例如Apache Kafka、RabbitMQ、Amazon Kinesis、谷歌PubSub)或文件系统(例如HDFS、S3、GFS、NFS、Ceph)。
         */
		// 每5秒执行一次消费偏移量验证
		env.enableCheckpointing(5000);
		// 设置验证模式为  Exactly-once（精确一次）（默认）。
		// At-least-once（至少一次）可能与某些超低延迟(持续几毫秒)的应用程序相关
		env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
		// 验证超时时间（检查点必须在60秒内完成，或者被丢弃）
		env.getCheckpointConfig().setCheckpointTimeout(60000);
		// 同一时间只允许进行1个验证
		env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
		/*************************容错验证配置 end*****************************/
		
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
		// 低版本Kafka才需要配置Zookeeper地址
	    //properties.setProperty("zookeeper.connect", "localhost:2181");
		// 消费者组的名称（顺便起）
		properties.setProperty("group.id", "flink-test-group");
		/**
		 * @param topic              主题名称
		 * @param valueDeserializer  数据解码器
		 * @param properties         Kafka相关配置
		 */
		FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>("flink-test", new SimpleStringSchema(), properties);
		/************************ 数据消费策略 start ******************************/
		// 从 Group.ID 已经提交的偏移量开始消费数据，如果没有找到就重新开始（注意：默认就是这个策略）
		kafkaConsumer.setStartFromGroupOffsets();
		// 从最早的偏移量开始消费数据
		//kafkaConsumer.setStartFromEarliest();   
		// 从最后的偏移量开始消费数据
		//kafkaConsumer.setStartFromLatest();
		// 从指定的epoch时间戳开始（毫秒）消费数据
		//kafkaConsumer.setStartFromTimestamp(12121121212L); 
		/************************ 数据消费策略 end ******************************/
		
		DataStream<String> stream = env.addSource(kafkaConsumer);
		// 打印消息
		stream.print();
		env.execute("KafkaSourceTest");
	}
}
