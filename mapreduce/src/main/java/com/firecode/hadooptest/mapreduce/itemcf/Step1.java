package com.firecode.hadooptest.mapreduce.itemcf;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
/**
 * 去除重复数据
 * @author JIANG
 *
 */
public class Step1 {
	
	public static boolean run(Map<String,String> paths) {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "file:///");
		try {
		    Job job = Job.getInstance(conf);
			job.setJarByClass(Step1.class);
			job.setJobName("Step1");
			
			FileInputFormat.addInputPath(job, new Path(paths.get("Step1Input")));
			
			Path outputDir = new Path(paths.get("Step1Output"));
			FileOutputFormat.setOutputPath(job, outputDir);
			FileSystem fs = FileSystem.get(conf);
			if(fs.exists(outputDir)) {
				fs.delete(outputDir, true);
			}
			
			job.setMapperClass(Step1Mapper.class);
			job.setReducerClass(Step1Reduce.class);
			
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(NullWritable.class);
			
			return job.waitForCompletion(true);
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	static class Step1Mapper extends Mapper<LongWritable,Text,Text,NullWritable>{

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, NullWritable>.Context context)
				throws IOException, InterruptedException {
			/**
			 * 如果不是第一行数据就原样输出
			 */
			if(key.get() != 0) {
				context.write(value, NullWritable.get());
			}
		}
	}
	
	static class Step1Reduce extends Reducer<Text,NullWritable,Text,NullWritable> {

		/**
		 * 直接把key输出
		 */
		@Override
		protected void reduce(Text key, Iterable<NullWritable> values,
				Reducer<Text, NullWritable, Text, NullWritable>.Context context)
				throws IOException, InterruptedException {
			context.write(key, NullWritable.get());
		}
		
	}
	
}
