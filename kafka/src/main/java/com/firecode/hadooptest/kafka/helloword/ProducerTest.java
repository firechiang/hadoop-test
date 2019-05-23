package com.firecode.hadooptest.kafka.helloword;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

import com.firecode.hadooptest.kafka.KafkaConfig;

/**
 * kafka生产者测试
 * 
 * @author JIANG
 */
public class ProducerTest extends KafkaConfig{
	

	@Test
	public void send() throws InterruptedException, ExecutionException {
		for(int i=0;i<100;i++){
			TimeUnit.SECONDS.sleep(1);
			String message = "测试消息"+i;
			//主题会自动创建
	        ProducerRecord<String, String> msg = new ProducerRecord<String, String>(topicName, message);
	        //发送消息
			Future<RecordMetadata> future = procuder.send(msg);
			int partition = future.get().partition();
			System.out.println("消息发送成功，消息所在分区："+partition);
		}
	}
	

	

}
