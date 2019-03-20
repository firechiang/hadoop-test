package com.firecode.hadooptest.mapreduce.weather;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 * 找出每个月气温最高的2天
 * 
 * 数据格式如下
 * 
 * 1949-10-01 14:12:01 34c
 * 1949-10-01 14:22:01 35c
 * 1949-10-01 14:52:01 34c
 * 1949-10-01 14:52:01 34c
 * 1949-10-05 14:52:01 35c
 * 1949-10-01 14:52:01 31c
 * 1949-10-07 14:52:01 29c
 * 1949-10-02 14:52:01 15c
 * 
 * 思路：将 年+月+日+温度 作为Key，Value就是温度
 * 
 * @author JIANG
 *
 */
public class WeatherMain {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(true);
		Job job = Job.getInstance(conf);
		//任务启动类
		job.setJarByClass(WeatherMain.class);
		//任务名称
		job.setJobName("Weather");
		//添加输入文件路径
		FileInputFormat.addInputPath(job, new Path("/test_txt/weather.txt"));
		
		Path outputDir = new Path("/test_txt/result/weather");
		//判断文件是否存在，存在就删除
		if(outputDir.getFileSystem(conf).exists(outputDir)) {
			outputDir.getFileSystem(conf).delete(outputDir,true);
		}
		//添加计算结果文件输出路径
		FileOutputFormat.setOutputPath(job, outputDir);
		//Map计算类
		job.setMapperClass(WeatherMapper.class);
		//Map计算结果输出Key的类型
		job.setMapOutputKeyClass(WeatherKey.class);
		//Map计算结果输出Value的类型
		job.setMapOutputValueClass(IntWritable.class);
		
		
		
		//Reduce计算类
		job.setReducerClass(WeatherReduce.class);
		job.setNumReduceTasks(2);
		
		
		//设置自定义分区（注意：自定义分区如果数据量少的话，必须设置setNumReduceTasks()否则不会调用分区代码）
		job.setPartitionerClass(WeatherPartitioner.class);
		//自定义分组后排序比较器（组内排序，调Reduce之前的排序,同一组数据的排序）
		job.setSortComparatorClass(WeatherSortComparator.class);
		//自定义分组比较器（调Reduce之前分组（相同的Key为一组））
		job.setGroupingComparatorClass(WeatherGroupingComparator.class);
		
		//组合器（在Map阶段先做一次组合，它的输出和Map的输出一致，在Reduce之前执行）
		//job.setCombinerClass(WeatherReduce.class);
		//自定义分组比较器（调Reduce之前分组（相同的Key为一组））
		//job.setCombinerKeyGroupingComparatorClass(WeatherGroupingComparator.class);
		//等待计算完成
		job.waitForCompletion(true);
	}
	
}
