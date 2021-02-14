package com.firecode.hadooptest.kafka.helloword;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
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

import com.fasterxml.jackson.databind.deser.std.NumberDeserializers.LongDeserializer;

/**
 * kafka配置相关
 * 
 * @author JIANG
 */
public abstract class AbstractKafkaConfig {
	
	/**
	 * 注意:KafkaProducer是线程安全的，创建一个就够了
	 */
	protected Producer<Integer,String> procuder;
	/**
	 * 注意：KafkaConsumer非线程安全
	 */
	protected Consumer<Integer,String> consumer;
	protected Consumer<String,Long> streamConsumer;
	protected AdminClient admin;
	protected Map<String,Object> adminConfig = new HashMap<>();
	protected Map<String,Object> producerConfig = new HashMap<>();
	protected Map<String,Object> consumerConfig = new HashMap<>();
	protected Map<String,Object> streamConsumerConfig = new HashMap<>();
	protected Properties streamConfig = new Properties();
	// 多节点用逗号隔开 192.168.229.133:9092,192.168.229.129:9092,192.168.229.134:9092
	public static final String SERVER_NAME = "127.0.0.1:9092";

	/**
	 * 初始化配置
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Before
	public void init() throws InterruptedException, ExecutionException {
		// 管理端服务地址
		adminConfig.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,SERVER_NAME);
		
		// 生产服务地址
		producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER_NAME);
		//消息发送类型 sync(同步)，async(异步)
		//config.put("producer.type","async");
		//消息压缩类型
		//config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy");
		
		// 消息发送失败重试次数
		producerConfig.put(ProducerConfig.RETRIES_CONFIG, "0");
		// 消息发送最大间隔时间，一毫秒发送一次消息（注意：Kafka的消息不是一条一条发送的是批量发送的）
		producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, "1");
		// 批量发送消息最大数量（注意：只要消息数量到达这个预值就会触发发送，即使没有到达消息发送最大间隔时间）
		producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
		// 生产者内存缓冲区大小
		producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
		//生产者序列化和反序列化
		producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
		producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		//指定了必须要有多少个分区副本收到消息，生产者才会认为消息写入是成功的
		//acks=0 生产者在成功写入消息之前不会等待任何来自服务器的响应，可能会有消息丢失的情况
		//acks = 1 只要leader写入成功，生产者就会认为消息写入成功，如果此时follwer还没有同步数据而leader挂了，消息数据就会丢失（推荐生产使用）
		//acks=all 只有当所有参与复制的节点全部收到消息时，生产者才会收到一个来自服务器的成功响应（只要有一个节点存活就不会丢失消息数据）
		producerConfig.put(ProducerConfig.ACKS_CONFIG,"1");
		
		// 消费服务地址
		consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER_NAME);
		
		//每个分组消费者启动起来后从哪个位置开始消费消息： none(从最后的位置开始消费(默认配置)，latest(从最后的位置开始消费)，earliest(从0开始，就是重新把所有的消息都消费一遍))
		//config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
		//消费者序列化和反序列化
		consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,IntegerDeserializer.class.getName());
		consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
		// 自动提交确认偏移量（springboot结合中的enable.auto.commit为false为spring的人工提交模式（spring会自动帮我们提交）。enable.auto.commit为true是采用kafka的默认提交模式）
		// 注意：自动提交确认偏移量不推荐使用，因为它消费不到消费者启动之前发送的消息（建议使用手动提交确认偏移量，因为它没有这个问题）
		consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		//group.id（组）（注意：enable.auto.commit=true时必须有group.id）
		consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
		//偏移量提交时间间隔（注意：如果是手动提交偏移量该配置无效）
		consumerConfig.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		//session timeout
		consumerConfig.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		this.config();
		// 注意:KafkaProducer是线程安全的，创建一个就够了
		this.procuder = new KafkaProducer<>(producerConfig);
		this.consumer = new KafkaConsumer<>(consumerConfig);
		this.admin = AdminClient.create(adminConfig);
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
