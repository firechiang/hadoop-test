package com.firecode.hadooptest.kafka.helloword.queue;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;

/**
 * kafka消费者测试（可同时运行多个实列，来看看是不是一个消息只能被一个消费者消费）
 * 同一个topic下的同一个group.id下，多个consumer只会有一个会收到消息，这个就是queue
 * 总结：一个topic里面只有一个消费者组(组里面可以有多个消费者)，那么它就是queue(点对点)
 *  @see org.apache.kafka.clients.producer.internals.DefaultPartitioner <p>这个默认分区类，返回的分区总个数，对应一个消费者组里面的消费者个数</p>
 * @author JIANG
 */
public class ConsumerTest extends QueueKafkaConfig {
	
	
	@Test
	public void subscribe() throws InterruptedException, ExecutionException {
		//暂停1号分区在拉取操作时返回数据给客户端 (暂停1号分区返回数据给客户端)
		//consumer.pause(new TopicPartition(topicName,1));
		//恢复1号分区向客户端返回数据操作(恢复1号分区返回数据给客户端)
		//consumer.resume(new TopicPartition(topicName,1));
		//上面的两个操作可以对消费速度加以控制，结合业务使用
		
		//消费所有分区的消息(和下面的二选一使用)
		consumer.subscribe(Collections.singletonList(topicName));
		//消费指定分区的消息(和上面的二选一使用)
		//consumer.assign(Arrays.asList(new TopicPartition(topicName,1)));
		
        while(true){
        	TimeUnit.SECONDS.sleep(1);
        	//每一秒钟拉取一次消息
            ConsumerRecords<Integer,String> records = consumer.poll(Duration.ofSeconds(1));
            for(ConsumerRecord<Integer,String> re:records){
                System.err.println("消费了："+re.value());
            }
        }
	}
}
