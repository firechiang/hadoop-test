package com.firecode.hadooptest.kafka.helloword.partitioner;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

/**
 * 自定义分区（生产者使用），生产者通过这个类的分区规则，将消息发送到特定分区
 * 注意：我们一定要先知道有哪几个分区，如果分区不存在，消息将发不出去
 * @author JIANG
 */
public class SimplePartitioner implements Partitioner {

	@Override
	public void configure(Map<String, ?> configs) {
		
	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		// 一般是根据key做自定义分区处理
		System.err.println("我在分区");
		return 0;
	}

	@Override
	public void close() {
		
	}

}
