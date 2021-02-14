package com.firecode.hadooptest.kafka.helloword.manual_commit;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

/**
 * kafka消费者测试（可同时运行多个实列，来看看是不是一个消息只能被一个消费者消费）
 * 
 * @see org.apache.kafka.clients.producer.internals.DefaultPartitioner
 *      <p>
 * 		这个默认分区类，返回的分区总个数，对应一个消费者组里面的消费者个数
 *      </p>
 * @author JIANG
 */
public class ConsumerTest01 extends ManualCommitKafkaConfig {

	@Test
	public void subscribe() throws InterruptedException, ExecutionException {
		// 暂停1号分区在拉取操作时返回数据给客户端 (暂停1号分区返回数据给客户端)
		// consumer.pause(new TopicPartition(topicName,1));
		// 恢复1号分区向客户端返回数据操作(恢复1号分区返回数据给客户端)
		// consumer.resume(new TopicPartition(topicName,1));
		// 上面的两个操作可以对消费速度加以控制，结合业务使用
		
		
		
		// 订阅所有分区的消息(和下面的二选一使用)
		consumer.subscribe(Collections.singletonList(topicName));
		// 订阅指定分区的消息(和上面的二选一使用)
		// consumer.assign(Arrays.asList(new TopicPartition(topicName,1)));

		while (true) {
			TimeUnit.SECONDS.sleep(1);
			/**
			 * 指定从哪个分区里面的哪个偏移量开始消费
			 * 适用场景：自己控制偏移量，比如消费了100条，将offset置为101存入Redis，每次poll（拉取消息）之前，从Redis中获取最新的offset位置，每次从这个位置开始消费
			 */
			//consumer.seek(new TopicPartition(topicName,1), 400);
			//consumer.seek(new TopicPartition(topicName,1), 400);
			
			// 每一秒钟拉取一次消息
			ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(1));
			// 根据分区处理拉取的所有消息
			sign(records);
			// 直接处理拉取的所有消息
			//all(records);
		}
	}
	
	/**
	 * 根据分区处理拉取的所有消息
	 * @param records
	 */
	void sign(ConsumerRecords<Integer, String> records) {
		// 每个分区里面的数据单独处理
		for(TopicPartition partition:records.partitions()) {
			// 获取该分区里面的所有消息数据
			List<ConsumerRecord<Integer, String>> records1 = records.records(partition);
			for (ConsumerRecord<Integer, String> re : records1) {
				System.err.println("消费了：" + re.value());
			}
			// 最后的偏移量
			long lastOffset = records1.get(records1.size() - 1).offset();
			Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
			// 注意：最后的偏移量 加 1 是表示在这个偏移量之后我们都还没有消费（也包括当前这个偏移量）
			offset.put(partition, new OffsetAndMetadata(lastOffset+1));
			// 手动同步提交偏移量（这个需要将enable.auto.commit=false才可用）
			//consumer.commitSync();
			// 手动异步提交偏移量（这个需要将enable.auto.commit=false才可用）
			// 根据分区提交确认偏移量
			consumer.commitAsync(offset,(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) -> {
				if (null == exception) {
					// TODO 表示偏移量成功提交
					System.out.println("提交成功");
				} else {
					// TODO 表示提交偏移量发生了异常，根据业务进行相关处理
					System.out.println("发生了异常");
				}
			});
		}
	}
	
	/**
	 * 直接处理拉取的所有消息
	 * @param records
	 */
	void all(ConsumerRecords<Integer, String> records) {
		// 最少处理10 条消息后才进行提交
		int minCommitSize = 1;
		// 消息计算器
		int icount = 0 ;
		for (ConsumerRecord<Integer, String> re : records) {
			icount++;
			System.err.println("消费了：" + re.value());
			// 在业务逻辑处理成功后提交偏移量
	        if (icount >= minCommitSize){
				// 手动同步提交偏移量（这个需要将enable.auto.commit=false才可用）
				//consumer.commitSync();
				// 手动异步提交偏移量（这个需要将enable.auto.commit=false才可用）
				consumer.commitAsync((Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) -> {
					if (null == exception) {
						// TODO 表示偏移量成功提交
						System.out.println("提交成功");
					} else {
						// TODO 表示提交偏移量发生了异常，根据业务进行相关处理
						System.out.println("发生了异常");
					}
				});
				// 重置计数器
				icount=0; 
	        }
		}
	}

}
