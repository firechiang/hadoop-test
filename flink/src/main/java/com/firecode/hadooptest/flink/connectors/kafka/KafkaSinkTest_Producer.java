package com.firecode.hadooptest.flink.connectors.kafka;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic;
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomSourceFunction2;

/**
 * 往Kafka推送数据（作为Kafka的生产者）简单测试
 * 可先使用 KafkaSourceTest 消费计算数据
 * @author JIANG
 */
public class KafkaSinkTest_Producer {
	
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
		 * @param topic               主题名称
		 * @param serializationSchema 数据编码器
		 * @param properties          Kafka相关配置
		 * @param semantic            数据容错语义
		 * 
		 * 容错语义几种策略
		 * Semantic.NONE（Flink不保证任何东西。生成的记录可能会丢失，也可能会被复制）
		 * Semantic.AT_LEAST_ONCE（默认设置）：类似于setFlushOnCheckpoint(true)在 FlinkKafkaProducer010。这可以保证不会丢失任何记录（尽管它们可以重复）
		 * Semantic.EXACTLY_ONCE：使用Kafka事务写入数据，都不要忘记为任何使用Kafka记录的应用程序设置所需的isolation.level（事物级别）（read_committed 或read_uncommitted- 后者是默认值）
		 */
		FlinkKafkaProducer<String> sinkFunction = new FlinkKafkaProducer<>("flink-test",
				                                                           new KeyedSerializationSchemaWrapper<>(new SimpleStringSchema()), 
				                                                           properties,
				                                                           Semantic.AT_LEAST_ONCE);
		// 允许记录在写入Kafka时，添加时间戳（注意：只支持Kafka 0.10+版本）
		sinkFunction.setWriteTimestampToKafka(true);
		// 添加数据源
		DataStreamSource<String> dataSource = env.addSource(new CustomSourceFunction2());
		dataSource.addSink(sinkFunction);
		env.execute("KafkaSinkTest");
	}
}
