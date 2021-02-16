package com.firecode.hadooptest.kafka.helloword.topic;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

/**
 * kafka生产者测试
 * 
 * @author JIANG
 */
public class ProducerTest extends TopicKafkaConfig{
	

	/**
	 * 异步发送消息
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void send() throws InterruptedException, ExecutionException {
		for(int i=0;i<100;i++) {
			TimeUnit.SECONDS.sleep(1);
			String message = "测试消息"+i;
			//消息记录，可指定发送到特定分区(没有指定分区，默认使用key哈希取模得到分区)
	        ProducerRecord<Integer, String> msg = new ProducerRecord<Integer, String>(topicName,i,message);
	        //异步发送消息
			Future<RecordMetadata> future = procuder.send(msg,(RecordMetadata metadata, Exception exception)->{
				if(null != metadata) {
					System.out.println("异步消息发送成功，消息所在分区："+metadata.partition()+",偏移量："+metadata.offset());
				}else {
					exception.printStackTrace();
				}
			});
			// 注意：这个位置使用get()获取返回结果就相当于同步发送了
			//RecordMetadata recordMetadata = future.get();
			//System.out.println("同步消息发送成功，消息所在分区："+recordMetadata.partition()+",偏移量："+recordMetadata.offset());
		}
	}
	

	

}
