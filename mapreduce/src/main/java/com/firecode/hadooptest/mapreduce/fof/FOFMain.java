package com.firecode.hadooptest.mapreduce.fof;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 好友推荐
 * 思路：两两一组，标记他们两的关系（直接关系还是间接关系），然后相同的key为一组，叠加他们间接关系的数量。实列如下：
 * 
 * 王五 有朋友 张三，毛文。 那么张三和毛文就是间接关系；
 * 李四 有朋友 张三，毛文 。那么张三和毛文就是间接关系；
 * 通过上面两组数据可以得到，张三和毛文有两个间接关系。
 * @author JIANG
 *
 */
public class FOFMain {
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(true);
		Job job = Job.getInstance(conf);
		job.setJarByClass(FOFMain.class);
		//使用本地文件
		conf.set("fs.defaultFS", "file:///");
		
		//input,output
		Path inputPath = new Path("D:/hadoop-test/data/fof.txt");
		FileInputFormat.addInputPath(job,inputPath);
		Path outputPath = new Path("D:/hadoop-test/result/fof");
		FileOutputFormat.setOutputPath(job, outputPath);
		
		
		//map
		job.setMapperClass(FOFMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		
		//reduce
		job.setReducerClass(FOFReduce.class);
		job.waitForCompletion(true);
	}

}
