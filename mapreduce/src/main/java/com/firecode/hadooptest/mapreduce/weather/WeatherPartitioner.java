package com.firecode.hadooptest.mapreduce.weather;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * 自定义分区
 * @author JIANG
 *
 */
public class WeatherPartitioner extends Partitioner<WeatherKey,IntWritable>{

	/**
	 * Map每输出一次就会调用一次这个分区函数
	 */
	@Override
	public int getPartition(WeatherKey key, IntWritable value, int numPartitions) {
		
		return key.hashCode() % numPartitions;
	}


}
