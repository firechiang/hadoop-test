package com.firecode.hadooptest.kafka.helloword;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;

import com.firecode.hadooptest.kafka.KafkaConfig;

/**
 * kafka生产者测试
 * 
 * @author JIANG
 */
public class ConsumerTest extends KafkaConfig {
	
	
	@Test
	public void subscribe() throws InterruptedException, ExecutionException {
		//消费所有分区的消息(和下面的二选一使用)
		consumer.subscribe(Arrays.asList(topicName));
		//消费指定分区的消息(和上面的二选一使用)
		//consumer.assign(Arrays.asList(new TopicPartition(topicName,1)));
        while(true){
        	TimeUnit.SECONDS.sleep(1);
        	//每一秒钟拉取一次消息
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofSeconds(1));
            for(ConsumerRecord<String,String> re:records){
                System.err.println("消费了："+re.value());
            }
        }
	}
	
}
