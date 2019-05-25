package com.firecode.hadooptest.kafka.helloword.manual_commit;

import java.time.Duration;
import java.util.Collections;
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
public class ConsumerTest extends ManualCommitKafkaConfig {

	@Test
	public void subscribe() throws InterruptedException, ExecutionException {
		// 暂停1号分区在拉取操作时返回数据给客户端 (暂停1号分区返回数据给客户端)
		// consumer.pause(new TopicPartition(topicName,1));
		// 恢复1号分区向客户端返回数据操作(恢复1号分区返回数据给客户端)
		// consumer.resume(new TopicPartition(topicName,1));
		// 上面的两个操作可以对消费速度加以控制，结合业务使用
		
		
		
		// 消费所有分区的消息(和下面的二选一使用)
		consumer.subscribe(Collections.singletonList(topicName));
		// 消费指定分区的消息(和上面的二选一使用)
		// consumer.assign(Arrays.asList(new TopicPartition(topicName,1)));

		while (true) {
			TimeUnit.SECONDS.sleep(1);
			// 每一秒钟拉取一次消息
			ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(1));
			
			// 最少处理10 条消息后才进行提交
			int minCommitSize = 10;
			// 消息计算器
			int icount = 0 ;
			
			for (ConsumerRecord<Integer, String> re : records) {
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

}
