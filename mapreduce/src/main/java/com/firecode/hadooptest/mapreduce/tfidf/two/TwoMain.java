package com.firecode.hadooptest.mapreduce.tfidf.two;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 统计DF（Map阶段）：词在多少个微博中出现过（包含该词的文件总数）
 * 需 FirstMain 执行完成之后才能执行这个
 * @author JIANG
 */
public class TwoMain {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "file:///");
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(TwoMain.class);
		job.setJobName("twoMain");
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setMapperClass(TwoMapper.class);
		job.setReducerClass(TwoReduce.class);
		job.setCombinerClass(TwoReduce.class);
		
		//使用第一次计算结果作为输入文件
		FileInputFormat.addInputPath(job, new Path("D:/hadoop-test/result/weibo/1"));
		
		Path outputDir = new Path("D:/hadoop-test/result/weibo/2");
		FileSystem fs = FileSystem.get(conf);
		if(fs.exists(outputDir)) {
			fs.delete(outputDir,true);
		}
		FileOutputFormat.setOutputPath(job, outputDir);
		job.waitForCompletion(true);
	}

}
