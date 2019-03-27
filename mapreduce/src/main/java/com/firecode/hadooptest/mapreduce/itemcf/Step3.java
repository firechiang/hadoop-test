package com.firecode.hadooptest.mapreduce.itemcf;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 对物品组合列表进行计数，建立物品的同现矩阵（两商品的同现次数）
 * i100:i100	3
 * i100:i105	1
 * i100:i106	1
 * i100:i109	1
 * i100:i114	1
 * i100:i124	1
 * @author JIANG
 */
public class Step3 {
	
	public static boolean run(Map<String,String> paths) {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "file:///");
		try {
		    Job job = Job.getInstance(conf);
			job.setJarByClass(Step3.class);
			job.setJobName("Step3");
			
			FileInputFormat.addInputPath(job, new Path(paths.get("Step3Input")));
			
			Path outputDir = new Path(paths.get("Step3Output"));
			FileOutputFormat.setOutputPath(job, outputDir);
			FileSystem fs = FileSystem.get(conf);
			if(fs.exists(outputDir)) {
				fs.delete(outputDir, true);
			}
			
			job.setMapperClass(Step3Mapper.class);
			job.setReducerClass(Step3Reduce.class);
			
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(IntWritable.class);
			
			return job.waitForCompletion(true);
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	 private final static Text K = new Text();
     private final static IntWritable V = new IntWritable(1);
	
	static class Step3Mapper extends Mapper<LongWritable,Text,Text,IntWritable>{

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			//输入数据格式如下
			//u3244	i469:1,i498:1,i154:1,i73:1,i162:1,
			String[]  tokens=value.toString().split("\t");
			String[] items =tokens[1].split(",");
			/**
			 * 如果 items 的数据是  A:1,B:3,R:5那么最后得到的是
			 * A:A
			 * A:B
			 * A:R
			 * B:A
			 * B:B
			 * B:R
			 * R:A
			 * R:B
			 * R:R
			 */
			for (int i = 0; i < items.length; i++) {
				String itemA = items[i].split(":")[0];
				for (int j = 0; j < items.length; j++) {
					String itemB = items[j].split(":")[0];
					K.set(itemA+":"+itemB);
					//最后输出就是（商品A:商品B）同现1次，比如   A:B 1
					context.write(K, V);
				}
			}
		}
	}
	
	static class Step3Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Reducer<Text, IntWritable, Text, IntWritable>.Context context)
				throws IOException, InterruptedException {
			//叠加同现次数
			int sum =0;
			for(IntWritable v :values) {
				sum =sum+v.get();
			}
			V.set(sum);
			context.write(key, V);
		}
	}
	
}
