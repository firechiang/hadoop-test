package com.firecode.hadooptest.kafka.stream_api;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;

/**
 * Stream流处理测试说明：
 * 1，启动 StreamTest（数据处理器）
 * 2，启动 ProducerTest（发送要处理的数据）
 * 3，启动 ConsumerTest（消费数据处理结果）
 * 
 * kafka消费者测试（先运行一个实列，再修改group.id再运行一个实列，来看看是不是一个消息会被所有的消费者接收到）
 * 同一个topic下的多个group.id会收到相同的消息，这个就是topic
 * 总结：一个topic里面只有多个消费者组(组里面可以有多个消费者)，那么它就是topic(订阅发布)
 * 
 * 
 * 注意：下面这种自动提交确认偏移量的消费模式，在消费者启动之前发送的消息将消费不到（要想消费到消费者启动之前发送的消息需要使用手动提交确认偏移量）
 * @author JIANG
 */
public class ConsumerTest extends StreamConfig {
	
	/**
	 * 注意：下面这种自动提交确认偏移量的消费模式，在消费者启动之前发送的消息将消费不到（要想消费到消费者启动之前发送的消息需要使用手动提交确认偏移量）
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void subscribe() throws InterruptedException, ExecutionException {
		//暂停1号分区在拉取操作时返回数据给客户端 (暂停1号分区返回数据给客户端)
		//consumer.pause(new TopicPartition(topicName,1));
		//恢复1号分区向客户端返回数据操作(恢复1号分区返回数据给客户端)
		//consumer.resume(new TopicPartition(topicName,1));
		//上面的两个操作可以对消费速度加以控制，结合业务使用
		
		
    	
		//订阅所有分区的消息(和下面的二选一使用)
		streamConsumer.subscribe(Collections.singletonList(TOPIC_OUT));
		//订阅指定分区的消息(和上面的二选一使用)
		//consumer.assign(Arrays.asList(new TopicPartition(topicName,1)));
        while(true) {
        	TimeUnit.SECONDS.sleep(1);
			/**
			 * 指定从哪个分区里面的哪个偏移量开始消费
			 * 适用场景：自己控制偏移量，比如消费了100条，将offset置为101存入Redis，每次poll（拉取消息）之前，从Redis中获取最新的offset位置，每次从这个位置开始消费
			 */
			//consumer.seek(new TopicPartition(topicName,1), 400);
        	
        	//每一秒钟拉取一次消息（注意：拉取消息和消息发送一样都是批量）
            ConsumerRecords<String,Long> records = streamConsumer.poll(Duration.ofSeconds(1));
            for(ConsumerRecord<String,Long> re:records){
                System.err.println("key="+re.key()+",value="+re.value());
            }
        }
	}
}
