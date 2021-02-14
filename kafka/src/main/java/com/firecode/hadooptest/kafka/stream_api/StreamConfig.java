package com.firecode.hadooptest.kafka.stream_api;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsConfig;

import com.firecode.hadooptest.kafka.helloword.AbstractKafkaConfig;

/**
 * topic相关配置
 * @author JIANG
 */
public abstract class StreamConfig extends AbstractKafkaConfig {
	
	// 数据来源Topic
	protected static final String TOPIC_IN = "test-test-01-in";
	// 数据分析后进入Topic
	protected static final String TOPIC_OUT = "test-test-01-out";
	
	@Override
	public void config() throws InterruptedException, ExecutionException {
		// 流服务地址
		streamConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,SERVER_NAME);
		// 流应用名称
		streamConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
		// 数据源 key value类型
		streamConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,Serdes.String().getClass());
		streamConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());
		
		
		
		// 消费服务地址
		streamConsumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER_NAME);
		//每个分组消费者启动起来后从哪个位置开始消费消息： none(从最后的位置开始消费(默认配置)，latest(从最后的位置开始消费)，earliest(从0开始，就是重新把所有的消息都消费一遍))
		//config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
		//消费者序列化和反序列化
		streamConsumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
		streamConsumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,LongDeserializer.class.getName());
		
		// 自动提交确认偏移量（springboot结合中的enable.auto.commit为false为spring的人工提交模式（spring会自动帮我们提交）。enable.auto.commit为true是采用kafka的默认提交模式）
		// 注意：自动提交确认偏移量不推荐使用，因为它消费不到消费者启动之前发送的消息（建议使用手动提交确认偏移量，因为它没有这个问题）
		streamConsumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		//group.id（组）（注意：enable.auto.commit=true时必须有group.id）
		streamConsumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
		//偏移量提交时间间隔（注意：如果是手动提交偏移量该配置无效）
		streamConsumerConfig.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		//session timeout
		streamConsumerConfig.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		
		this.streamConsumer = new KafkaConsumer<>(streamConsumerConfig);
	}
	
	
	@Override
	public void before() throws InterruptedException, ExecutionException {
		// 获取所有主题
		ListTopicsResult listTopics = admin.listTopics();
		Set<String> names = listTopics.names().get();
		if(!names.contains(TOPIC_IN)){
			//创建主题，1个分区，1个副本
			NewTopic topic = new NewTopic(TOPIC_IN,1,Short.parseShort("1"));
			admin.createTopics(Arrays.asList(topic));
		}
		if(!names.contains(TOPIC_OUT)){
			//创建主题，1个分区，1个副本
			NewTopic topic = new NewTopic(TOPIC_OUT,1,Short.parseShort("1"));
			admin.createTopics(Arrays.asList(topic));
		}
	}

}
