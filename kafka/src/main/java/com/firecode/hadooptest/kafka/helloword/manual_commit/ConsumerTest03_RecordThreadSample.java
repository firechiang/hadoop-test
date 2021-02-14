package com.firecode.hadooptest.kafka.helloword.manual_commit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.firecode.hadooptest.kafka.helloword.AbstractKafkaConfig;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 只有一个消费者，消息处理交给线程池
 * 注意：这种模式的适用场景是对数据消费的准确性没有那么高（就是消费成功就成功失败就失败），自动提价确认偏移量。因为它是在另一个线程里面处理消息，无法手动提交确认偏移量
 */
public class ConsumerTest03_RecordThreadSample {

	public static void main(String[] args) throws InterruptedException {
		String groupId = "DemoConsumer";
		int workerNum = 5;
		CunsumerExecutor consumers = new CunsumerExecutor(AbstractKafkaConfig.SERVER_NAME, groupId,
				ManualCommitKafkaConfig.topicName);
		consumers.execute(workerNum);

		Thread.sleep(1000000);

		consumers.shutdown();

	}

	// Consumer处理
	public static class CunsumerExecutor {
		private final KafkaConsumer<String, String> consumer;
		private ExecutorService executors;

		public CunsumerExecutor(String brokerList, String groupId, String topic) {
			Properties props = new Properties();
			// 消费服务地址
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,brokerList);
			// 消费者序列化和反序列化
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			// group.id（组）（注意：enable.auto.commit=true时必须有group.id）
			props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			// 偏移量提交时间间隔（注意：如果是手动提交偏移量该配置无效）
			props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
			// session timeout
			props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
			// 自动提交确认偏移量（注意：自动提交确认偏移量不推荐使用，因为它消费不到消费者启动之前发送的消息（建议使用手动提交确认偏移量，因为它没有这个问题））
			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
			// 创建消费者
			consumer = new KafkaConsumer<>(props);
			// 订阅消息
			consumer.subscribe(Arrays.asList(topic));
		}

		public void execute(int workerNum) {
            // 创建线程池
			executors = new ThreadPoolExecutor(workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
					new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
            // 不断拉取消息
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(200);
				for (final ConsumerRecord record : records) {
					// 将消息交给线程池处理
					executors.submit(new ConsumerRecordWorker(record));
				}
			}
		}

		public void shutdown() {
			if (consumer != null) {
				consumer.close();
			}
			if (executors != null) {
				executors.shutdown();
			}
			try {
				if (!executors.awaitTermination(10, TimeUnit.SECONDS)) {
					System.out.println("Timeout.... Ignore for this case");
				}
			} catch (InterruptedException ignored) {
				System.out.println("Other thread interrupted this shutdown, ignore for this case.");
				Thread.currentThread().interrupt();
			}
		}

	}

	// 记录处理
	public static class ConsumerRecordWorker implements Runnable {

		private ConsumerRecord<String, String> record;

		public ConsumerRecordWorker(ConsumerRecord record) {
			this.record = record;
		}

		@Override
		public void run() {
			System.out.println("Thread - " + Thread.currentThread().getName());
			System.err.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(),
					record.offset(), record.key(), record.value());
		}

	}
}
