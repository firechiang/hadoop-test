package com.firecode.hadooptest.kafka.helloword.manual_commit;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import com.firecode.hadooptest.kafka.helloword.AbstractKafkaConfig;
/**
 * 手动提交确认偏移量相关配置
 * @author JIANG
 */
public abstract class ManualCommitKafkaConfig extends AbstractKafkaConfig {
	
	public static String topicName = "test-test-101";
	
	@Override
	public void config() throws InterruptedException, ExecutionException{
		//手动提交确认偏移量（springboot结合中的enable.auto.commit为false为spring的人工提交模式（spring会自动帮我们提交）。enable.auto.commit为true是采用kafka的默认提交模式）
		consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
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
