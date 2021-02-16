package com.firecode.hadooptest.kafka.helloword.manual_commit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.TopicConfig;

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
			Map<String,String> topicConfigs = new HashMap<>();
			// 是否允许不具备ISR资格的节点（不是挂了的那个节点的副本节点）被选举为Leader（注意：这个默认就是false）
			topicConfigs.put(TopicConfig.UNCLEAN_LEADER_ELECTION_ENABLE_CONFIG, "false");
			// 单批消息最大长度（单位字节，建议使用默认的就好）
			//topicConfigs.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, "1048588");
			//  生命周期结束的数据处理方式（默认是删除）
			//topicConfigs.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE);
			// 强制刷新写入的最大缓存消息数，缓存了多少条消息就要将数据写入磁盘（下面配的是1就是每一条数据都要刷盘，这样对磁盘的压力比较大，建议根据实际情况配置）
			//topicConfigs.put(TopicConfig.FLUSH_MESSAGES_INTERVAL_CONFIG,"1");
			// 强制刷新写入的最大等待时长,就是最多等多久就要将数据写入磁盘
			//topicConfigs.put(TopicConfig.FLUSH_MS_CONFIG,"300");
			topic.configs(topicConfigs);
			admin.createTopics(Arrays.asList(topic));
		}
	}

}
