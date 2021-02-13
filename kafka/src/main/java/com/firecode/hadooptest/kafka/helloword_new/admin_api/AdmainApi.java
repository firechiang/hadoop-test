package com.firecode.hadooptest.kafka.helloword_new.admin_api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;

/**
 * Kafka 管理API相关演示
 * @author chiangfire
 */
public class AdmainApi {
	
	static final String TOPIC_NAME_1 = "test-test-1";
	static final String TOPIC_NAME_2 = "test-test-2";
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// 创建消息主题
		createTopic();
		// 获取所有主题信息
		getTopics();
		// 查询主题详细信息
		getTopicInfo();
		// 查询Kafka配置信息
		getConfigInfo();
		// 修改Kafka配置信息
		updateConfig();
		// 增加分区数(注意：分区数只能增加不能减少)
		incrPartitions();
	}
	
	/**
	 * 获取所有主题信息
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	static void getTopics() throws InterruptedException, ExecutionException {
		AdminClient adminClient = adminClient();
		ListTopicsOptions options = new ListTopicsOptions();
		// 设置是否列出内部主题（就是Kafka自己的主题，不是我们创建的）
		options.listInternal(true);
		ListTopicsResult listTopics = adminClient.listTopics(options);
		Set<String> sets = listTopics.names().get();
		for(String name:sets) {
			System.err.println(name);
		}
		System.out.println();
	}
	
	/**
	 * 查询主题详细信息
	 * name=test-test-1（主题名称）
	 * internal=false, （是否内部主题）
	 * partitions=(partition=0, leader=127.0.0.1:9092 (id: 0 rack: null), （分区数）
	 * replicas=127.0.0.1:9092 (id: 0 rack: null), isr=127.0.0.1:9092 (id: 0 rack: null)),（副本数）
	 * authorizedOperations=null（）
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	static void getTopicInfo() throws InterruptedException, ExecutionException {
		System.out.println("主题详细信息----------------------------------------------------------");
		AdminClient adminClient = adminClient();
		DescribeTopicsResult describeTopics = adminClient.describeTopics(Arrays.asList(TOPIC_NAME_1));
		Map<String, TopicDescription> map = describeTopics.all().get();
		for(String key:map.keySet()) {
			System.err.println(key+":"+map.get(key));
		}
	}
	
	/**
	 * 查询Kafka配置信息
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	static void getConfigInfo() throws InterruptedException, ExecutionException {
		System.out.println("Kafka配置详细信息----------------------------------------------------------");
		AdminClient adminClient = adminClient();
		ConfigResource resources = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME_1);
		Map<ConfigResource, Config> map = adminClient.describeConfigs(Arrays.asList(resources)).all().get();
		for(ConfigResource key:map.keySet()) {
			System.err.println(key+":"+map.get(key));
		}
		
	}
	
	/**
	 * 修改Kafka配置信息
	 */
	static void updateConfig() {
		AdminClient adminClient = adminClient();
		Map<ConfigResource,Collection<AlterConfigOp>> map = new HashMap<>();
		ConfigResource resources = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME_1);
		// 注意：这个值默认是false的
		AlterConfigOp config = new AlterConfigOp(new ConfigEntry("preallocate", "true"),AlterConfigOp.OpType.APPEND);
		map.put(resources, Arrays.asList(config));
		adminClient.incrementalAlterConfigs(map);
	}
	
	/**
	 * 增加分区数(注意：分区数只能增加不能减少)
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	static void incrPartitions() throws InterruptedException, ExecutionException {
		AdminClient adminClient = adminClient();
		Map<String,NewPartitions> map = new HashMap<>();
		map.put(TOPIC_NAME_1, NewPartitions.increaseTo(3));
		CreatePartitionsResult createPartitions = adminClient.createPartitions(map);
		// 确认增加
		//createPartitions.all().get();
	}
	
	private static KafkaFuture<Set<String>> all() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 创建消息主题
	 */
	static CreateTopicsResult createTopic() {
		AdminClient adminClient = adminClient();
		int numPartitions = 1;
		short replicationFactor = 1;
		/**
		 * @param name              主题名称
		 * @param numPartitions     分区数
		 * @param replicationFactor 副本数
		 */
		NewTopic newTopic = new NewTopic(TOPIC_NAME_1,numPartitions,replicationFactor);
		return adminClient.createTopics(Arrays.asList(newTopic));
	}
	
	/**
	 * 删除主题
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	static void delTopic() throws InterruptedException, ExecutionException {
		AdminClient adminClient = adminClient();
		DeleteTopicsResult deleteTopics = adminClient.deleteTopics(Arrays.asList(TOPIC_NAME_2));
		// 确认删除
		deleteTopics.all().get();
	}
	
	
	/**
	 * 创建Kafka管理端对象
	 * @return
	 */
	static AdminClient adminClient() {
		Properties properties = new Properties();
		properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		AdminClient adminClient = AdminClient.create(properties);
		return adminClient;
	}
	
}
