package com.firecode.hadooptest.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Before;

/**
 * kafka配置相关
 * 
 * @author JIANG
 */
public abstract class AbstractKafkaConfig {
	
	protected String topicName = "test-test-1";
	protected Producer<Integer,String> procuder;
	protected Consumer<Integer,String> consumer;
	protected AdminClient admin;
	protected Map<String,Object> config = new HashMap<>();

	/**
	 * 初始化配置
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Before
	public void init() throws InterruptedException, ExecutionException {
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		//序列化
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		//返序列化
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,IntegerDeserializer.class.getName());
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
		//自动提交确认
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		//session timeout
		config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		//group.id（组）
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
		this.procuder = new KafkaProducer<>(config);
		this.consumer = new KafkaConsumer<>(config);
		
		this.admin = AdminClient.create(config);
		ListTopicsResult listTopics = admin.listTopics();
		Set<String> names = listTopics.names().get();
		if(!names.contains(topicName)){
			//创建主题，2个分区，1个副本（注意：多少个分区对应多少个消费者）
			NewTopic topic = new NewTopic(topicName,2,Short.parseShort("1"));
			admin.createTopics(Arrays.asList(topic));
		}
	}
	
	
	/**
	 * 关闭连接
	 */
	@After
	public void close() {
		procuder.close(Duration.ofSeconds(2));
		consumer.close(Duration.ofSeconds(2));
	}

}
