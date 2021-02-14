package com.firecode.hadooptest.kafka.helloword.manual_commit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.firecode.hadooptest.kafka.helloword.AbstractKafkaConfig;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 一个线程对应一个消费者（经典模式）
 * 注意：这种模式的适用场景是对数据消费的准确性要求很高，不能漏消费（推荐生产使用）
 * 建议每一个分区对应一个消费者
 */
public class ConsumerTest02_ThreadSample {

	public static void main(String[] args) throws InterruptedException {
		KafkaConsumerRunner r1 = new KafkaConsumerRunner();
		Thread t1 = new Thread(r1);

		t1.start();

		Thread.sleep(15000);

		r1.shutdown();
	}

	public static class KafkaConsumerRunner implements Runnable {
		private final AtomicBoolean closed = new AtomicBoolean(false);
		private final KafkaConsumer consumer;

		public KafkaConsumerRunner() {
			Properties props = new Properties();
			// 消费服务地址
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AbstractKafkaConfig.SERVER_NAME);

			// 每个分组消费者启动起来后从哪个位置开始消费消息：
			// none(从最后的位置开始消费(默认配置)，latest(从最后的位置开始消费)，earliest(从0开始，就是重新把所有的消息都消费一遍))
			// config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
			// 消费者序列化和反序列化
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

			// group.id（组）（注意：enable.auto.commit=true时必须有group.id）
			props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
			// 偏移量提交时间间隔（注意：如果是手动提交偏移量该配置无效）
			props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
			// session timeout
			props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");

			// 手动提交确认偏移量（springboot结合中的enable.auto.commit为false为spring的人工提交模式（spring会自动帮我们提交）。enable.auto.commit为true是采用kafka的默认提交模式）
			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            // 创建消费者对象
			consumer = new KafkaConsumer<>(props);

			TopicPartition p0 = new TopicPartition(ManualCommitKafkaConfig.topicName, 0);
			TopicPartition p1 = new TopicPartition(ManualCommitKafkaConfig.topicName, 1);
			TopicPartition p2 = new TopicPartition(ManualCommitKafkaConfig.topicName, 2);
            // 订阅指定分区的消息
			consumer.assign(Arrays.asList(p0, p1, p2));
		}

		public void run() {
			try {
				while (!closed.get()) {
					// 处理消息
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));

					for (TopicPartition partition : records.partitions()) {
						List<ConsumerRecord<String, String>> pRecord = records.records(partition);
						// 处理每个分区的消息
						for (ConsumerRecord<String, String> record : pRecord) {
							System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
									record.offset(), record.key(), record.value());
						}

						// 最后的偏移量
						long lastOffset = pRecord.get(pRecord.size() - 1).offset();
						// 提价确认最后偏移量
						// 注意：最后的偏移量 加 1 是表示在这个偏移量之后我们都还没有消费（也包括当前这个偏移量）
						consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
					}
				}
			} catch (WakeupException e) {
				if (!closed.get()) {
					throw e;
				}
			} finally {
				consumer.close();
			}
		}

		public void shutdown() {
			closed.set(true);
			consumer.wakeup();
		}
	}

}
