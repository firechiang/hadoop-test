package com.firecode.hadooptest.kafka.helloword.manual_commit;

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
public class ProducerTest extends ManualCommitKafkaConfig{
	

	@Test
	public void send() throws InterruptedException, ExecutionException {
		for(int i=0;i<100;i++){
			TimeUnit.SECONDS.sleep(1);
			String message = "测试消息"+i;
			//消息记录，可指定发送到特定分区(没有指定分区，默认使用key哈希取模得到分区)
	        ProducerRecord<Integer, String> msg = new ProducerRecord<Integer, String>(topicName,i,message);
	        //发送消息
	        Future<RecordMetadata> future = procuder.send(msg);
	        //发送消息带回调
			/*Future<RecordMetadata> future = procuder.send(msg,(RecordMetadata metadata, Exception exception)->{
				System.err.println("消息发送完成回调");
			});*/
			int partition = future.get().partition();
			System.out.println("消息发送成功，消息所在分区："+partition);
		}
	}
	

	

}
