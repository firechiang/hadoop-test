package com.firecode.hadooptest.kafka.helloword.topic;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import com.firecode.hadooptest.kafka.helloword.AbstractKafkaConfig;

/**
 * topic相关配置
 * @author JIANG
 */
public abstract class TopicKafkaConfig extends AbstractKafkaConfig {
	
	protected String topicName = "test-test-102";
	
	@Override
	public void config() throws InterruptedException, ExecutionException {
		//group.id（组）（注意：enable.auto.commit=true时必须有group.id）
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer2");
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
