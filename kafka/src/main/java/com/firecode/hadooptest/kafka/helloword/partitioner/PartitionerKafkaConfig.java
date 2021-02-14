package com.firecode.hadooptest.kafka.helloword.partitioner;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;

import com.firecode.hadooptest.kafka.helloword.AbstractKafkaConfig;

/**
 * queue相关配置
 * @author JIANG
 */
public abstract class PartitionerKafkaConfig extends AbstractKafkaConfig {
	
	protected String topicName = "test-test-104";
	
	@Override
	public void config() throws InterruptedException, ExecutionException{
		//指定生成者分区类
		producerConfig.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, SimplePartitioner.class.getName());
	}
	
	@Override
	public void before() throws InterruptedException, ExecutionException {
		ListTopicsResult listTopics = admin.listTopics();
		Set<String> names = listTopics.names().get();
		if(!names.contains(topicName)){
			//创建主题，1个分区，1个副本
			NewTopic topic = new NewTopic(topicName,1,Short.parseShort("1"));
			admin.createTopics(Arrays.asList(topic));
		}
	}

}
