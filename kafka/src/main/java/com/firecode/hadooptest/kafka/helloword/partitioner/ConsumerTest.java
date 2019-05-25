package com.firecode.hadooptest.kafka.helloword.partitioner;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;

/**
 * kafka消费者测试（可同时运行多个实列，来看看是不是一个消息只能被一个消费者消费）
 * 
 * @author JIANG
 */
public class ConsumerTest extends PartitionerKafkaConfig {
	
	
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
