package com.firecode.hadooptest.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.junit.After;
import org.junit.Before;

/**
 * kafka配置相关
 * 
 * @author JIANG
 */
public abstract class KafkaConfig {
	
	protected String topicName = "test_test_2";
	protected Producer<String,String> procuder;
	protected Consumer<String,String> consumer;
	protected AdminClient admin;
	protected Map<String, Object> config = new HashMap<String, Object>();

	/**
	 * 初始化配置
	 */
	@Before
	public void init() {
		config.put("bootstrap.servers", "192.168.229.133:9092,192.168.229.129:9092,192.168.229.134:9092");
		//序列化
		config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		//返序列化
		config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("group.id", "MyGroup");
		this.procuder = new KafkaProducer<String,String>(config);
		this.consumer = new KafkaConsumer<String,String>(config);
		
		this.admin = AdminClient.create(config);
		//创建主题，1个分区，1个副本
		NewTopic topic = new NewTopic(topicName,1,Short.parseShort("1"));
		admin.createTopics(Arrays.asList(topic));
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
