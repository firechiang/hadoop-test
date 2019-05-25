package com.firecode.hadooptest.kafka.helloword.queue;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import com.firecode.hadooptest.kafka.AbstractKafkaConfig;

/**
 * queue相关配置
 * @author JIANG
 */
public abstract class QueueKafkaConfig extends AbstractKafkaConfig {
	
	protected String topicName = "test-test-109";
	
	@Override
	public void config() throws InterruptedException, ExecutionException{
		
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
