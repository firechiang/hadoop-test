package com.firecode.hadooptest.mapreduce.tfidf.last;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 * 计算 逆向文件频率（IDF）
 * 需 FirstMain 和 TwoMain 执行完成之后才能执行这个
 * @author JIANG
 *
 */
public class LastMain {
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "file:///");
		
		
		FileSystem fs = FileSystem.get(conf);
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(LastMain.class);
		job.setJobName("LastMain");
		
		//以下是配置程序分布式跑，但是在本地执行Main函数开始执行
		//当前程序,打成jar包所在地址
		//conf.set("mapreduce.job.jar", "D:\\text\\tfidf.jar");
		//开启自适应平台支持，用于Windows系统（自动识别系统）
		//conf.set("mapreduce.app-submission.cross-platform", "true");
		//job.setJar("D:\\\\text\\\\tfidf.jar");
		
		//把微博总数量文件加入到缓存（它会自动移动到需要它的MapReduce机器上去）
		//这是第一次MapReduce计算结果，注意这种缓存文件只适用于小文件，大小不要超过50M
		job.addCacheFile(new Path("D:/hadoop-test/result/weibo/1/part-r-00003").toUri());
		
		//把 词在多少个微博中出现过（包含该词的文件总数）的文件加入到缓存（它会自动移动到需要它的MapReduce机器上去）
		//这是第二次MapReduce计算结果，注意这种缓存文件只适用于小文件，大小不要超过50M
		job.addCacheFile(new Path("D:/hadoop-test/result/weibo/2/part-r-00000").toUri());
		
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(LastMapper.class);
		job.setReducerClass(LastReduce.class);
		
		FileInputFormat.addInputPath(job, new Path("D:/hadoop-test/result/weibo/1"));
		Path outputDir = new Path("D:/hadoop-test/result/weibo/3");
		if(fs.exists(outputDir)) {
			fs.delete(outputDir,true);
		}
		FileOutputFormat.setOutputPath(job, outputDir);
		
		job.waitForCompletion(true);
	}
}
