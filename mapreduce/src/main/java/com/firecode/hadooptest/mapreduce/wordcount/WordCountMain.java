package com.firecode.hadooptest.mapreduce.wordcount;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
   *    统计单词数量
 * @author JIANG
 *
 */
public class WordCountMain {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(true);
		Job job = Job.getInstance(conf);
		//任务启动类
		job.setJarByClass(WordCountMain.class);
		//任务名称
		job.setJobName("WordCount");
		//添加输入文件路径
		FileInputFormat.addInputPath(job, new Path("/test_txt/wordcount.txt"));
		Path outputDir = new Path("/test_txt/result/wordcount");
		//判断文件是否存在，存在就删除
		if(outputDir.getFileSystem(conf).exists(outputDir)) {
			outputDir.getFileSystem(conf).delete(outputDir,true);
		}
		//添加计算结果文件输出路径
		FileOutputFormat.setOutputPath(job, outputDir);
		//Map计算类
		job.setMapperClass(WordCountMapper.class);
		//Map计算结果输出Key的类型
		job.setMapOutputKeyClass(Text.class);
		//Map计算结果输出Value的类型
		job.setMapOutputValueClass(IntWritable.class);
		
		//Reduce计算类
		job.setReducerClass(WordCountReduce.class);
		job.waitForCompletion(true);
	}
}
