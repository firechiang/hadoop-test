package com.firecode.hadooptest.mapreduce.tfidf.first;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;

/**
 * 第一个Map自定义分区
 * @author JIANG
 */
public class FirstPartition extends HashPartitioner<Text,IntWritable>{

	@Override
	public int getPartition(Text key, IntWritable value, int numReduceTasks) {
		//计算微博总数量的分区
		if(key.equals(new Text("count"))) {
			return 3;
		//计算词语出现次数的分区	
		}else {
			//Hash分区模3（numReduceTasks - 1 = 3），最后得到值只会是0，1，2
			return super.getPartition(key, value, numReduceTasks - 1);
		}
	}

}
