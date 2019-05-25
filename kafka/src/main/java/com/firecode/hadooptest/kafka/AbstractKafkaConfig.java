package com.firecode.hadooptest.kafka;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
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
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.229.133:9092,192.168.229.129:9092,192.168.229.134:9092");
		//消息发送类型 sync(同步)，async(异步)
		//config.put("producer.type","async");
		//消息压缩类型
		//config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy");
		
		//生产者序列化和反序列化
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		//指定了必须要有多少个分区副本收到消息，生产者才会认为消息写入是成功的
		//acks=0 生产者在成功写入消息之前不会等待任何来自服务器的响应，可能会有消息丢失的情况
		//acks = 1 指定了必须要有多少个分区副本收到消息，生产者才会认为消息写入是成功的（推荐生产使用）
		//acks=all 只有当所有参与复制的节点全部收到消息时，生产者才会收到一个来自服务器的成功响应
		config.put(ProducerConfig.ACKS_CONFIG,"1");
		
		//每个分组消费者启动起来后从哪个位置开始消费消息： none(从最后的位置开始消费(默认配置)，latest(从最后的位置开始消费)，earliest(从0开始，就是重新把所有的消息都消费一遍))
		//config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
		//消费者序列化和反序列化
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,IntegerDeserializer.class.getName());
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
		//自动提交确认（springboot结合中的enable.auto.commit为false为spring的人工提交模式（spring会自动帮我们提交）。enable.auto.commit为true是采用kafka的默认提交模式）
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		//group.id（组）（注意：enable.auto.commit=true时必须有group.id）
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
		//偏移量提交时间间隔
		config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		//session timeout
		config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		this.config();
		this.procuder = new KafkaProducer<>(config);
		this.consumer = new KafkaConsumer<>(config);
		this.admin = AdminClient.create(config);
		this.before();
	}
	
	/**
	 * 关闭连接
	 */
	@After
	public void close() {
		procuder.close(Duration.ofSeconds(2));
		consumer.close(Duration.ofSeconds(2));
	}
	
	protected void config() throws InterruptedException, ExecutionException{}
	
	protected void before() throws InterruptedException, ExecutionException{}

}
