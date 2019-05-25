package com.firecode.hadooptest.kafka.helloword.timestamps_search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import com.firecode.hadooptest.kafka.AbstractKafkaConfig;

/**
 * 延迟消费相关配置相关配置
 * @author JIANG
 */
public abstract class TimestampsSearchKafkaConfig extends AbstractKafkaConfig {
	
	protected String topicName = "test-test-106";
	
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
			Map<String,String> map = new HashMap<>();	
			//记录时间戳LogAppendTime（表示broker接收到这条消息的时间），CreateTime表示（producer创建这条消息的时间）)
			//如果想要延迟消费消息，一定要记录时间戳
			map.put("message.timestamp.type", "LogAppendTime");
			topic.configs(map);
			admin.createTopics(Arrays.asList(topic));
		}
	}

}
