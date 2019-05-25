package com.firecode.hadooptest.kafka.helloword.timestamps_search;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

/**
 * kafka消费者测试（可同时运行多个实列，来看看是不是一个消息只能被一个消费者消费）
 *  @see org.apache.kafka.clients.producer.internals.DefaultPartitioner <p>这个默认分区类，返回的分区总个数，对应一个消费者组里面的消费者个数</p>
 * @author JIANG
 */
public class ConsumerTest extends TimestampsSearchKafkaConfig {
	
	
	@Test
	public void subscribe() throws InterruptedException, ExecutionException {
		//暂停1号分区在拉取操作时返回数据给客户端 (暂停1号分区返回数据给客户端)
		//consumer.pause(new TopicPartition(topicName,1));
		//恢复1号分区向客户端返回数据操作(恢复1号分区返回数据给客户端)
		//consumer.resume(new TopicPartition(topicName,1));
		//上面的两个操作可以对消费速度加以控制，结合业务使用
		
		
		
		TopicPartition partition = new TopicPartition(topicName,0);
		//消费指定分区的消息
		consumer.assign(Arrays.asList(partition));
		Map<TopicPartition, Long> timestampsSearch = new HashMap<TopicPartition,Long>();
		// 设置查询12 小时之前消息的偏移量
		timestampsSearch.put(partition, (System.currentTimeMillis() - 12 * 3600 * 1000));
		//返回时间大于等于查找时间的第一个偏移量
		Map<TopicPartition, OffsetAndTimestamp> offsetMap = consumer.offsetsForTimes(timestampsSearch);
		OffsetAndTimestamp offsetTimestamp = null;
	    // 这里依然用for 轮询，当然由于本例是查询的一个分区，因此也可以用if 处理
	    for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : offsetMap.entrySet()) {
	        // 若查询时间大于时间戳索引文件中较大记录索引时间，
	        // 此时value 为空,即待查询时间点之后没有新消息生成
	        offsetTimestamp = entry.getValue();
	        if (null != offsetTimestamp) {
		        // 重置消费起始偏移量
		        consumer.seek(partition, entry.getValue().offset());
	        }
	    }
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
