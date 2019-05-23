package com.firecode.hadooptest.kafka;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.Test;

/**
 * 简单测试kafka admin
 */
public class KafkaAdminTest extends KafkaConfig {
	
	/**
	 * 创建主题
	 */
	@Test
	public void createTopic(){
		String topicName = "test_test_3";
		//创建主题，1个分区，1个副本
		NewTopic topic = new NewTopic(topicName,1,Short.parseShort("1"));
		CreateTopicsResult res = admin.createTopics(Arrays.asList(topic));
		System.err.println(res.toString());
	}
	
	/**
	 * 获取主题列表
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void getTopics() throws InterruptedException, ExecutionException{
		ListTopicsResult listTopics = admin.listTopics();
		Set<String> names = listTopics.names().get();
		System.err.println(names);
	}
	
	/**
	 * 查看主题详细信息
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void topicDescribe() throws InterruptedException, ExecutionException{
		DescribeTopicsResult describeTopics = admin.describeTopics(Arrays.asList("test_test_3"));
		System.err.println(describeTopics.all().get());
	}
	
	/**
	 * 删除主题
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void deleteTopic() throws InterruptedException, ExecutionException{
		DeleteTopicsResult deleteTopics = admin.deleteTopics(Arrays.asList("test_test_3"));
		System.err.println(deleteTopics.all().get());
	}

}
