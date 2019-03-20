package com.firecode.hadooptest.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.firecode.hadooptest.mapreduce.wordcount.WordCountMapper;
import com.firecode.hadooptest.mapreduce.wordcount.WordCountReduce;

/**
 * 使用系统本地文件统计单词数量
 * @author JIANG
 *
 */
public class LocalFileWordCountMain {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(true);
		
		//使用本地文件
		conf.set("fs.defaultFS", "file:///");
		
		Job job = Job.getInstance(conf);
		//任务启动类
		job.setJarByClass(LocalFileWordCountMain.class);
		//任务名称
		job.setJobName("WordCount");
		//添加输入文件路径
		FileInputFormat.addInputPath(job, new Path("E:/hadoop-test-data/test_txt/wordcount.txt"));
		//将每个切片的大小设置为1（切片小了，Map就多了，线程就多了（一个Map对应一个切片）），线程多了就加快了计算速度
		//FileInputFormat.setMaxInputSplitSize(job, 1);
		//增大切片大小 就 减少Map数量 就 减少线程数量
		//FileInputFormat.setMinInputSplitSize(job, 1000);
		
		Path outputDir = new Path("E:/hadoop-test-data/test_txt/result/wordcount1.txt");
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
		
		//设置Reduce数量这个数量不是乱写的，应该是根据实际业务，数据有多少分组，而计算出来的
		//job.setNumReduceTasks(1);
		
		//设置分区类
		//job.setPartitionerClass(cls);
		
		//Reduce计算类
		job.setReducerClass(WordCountReduce.class);
		job.waitForCompletion(true);
	}
}
