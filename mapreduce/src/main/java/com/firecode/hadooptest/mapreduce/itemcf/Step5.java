package com.firecode.hadooptest.mapreduce.itemcf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 * 把相乘之后的矩阵相加获得结果矩阵
 * 
 * @author root
 *
 */
public class Step5 {
	
	public static boolean run(Map<String,String> paths) {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "file:///");
		try {
		    Job job = Job.getInstance(conf);
			job.setJarByClass(Step5.class);
			job.setJobName("Step5");
			
			FileInputFormat.addInputPath(job, new Path(paths.get("Step5Input")));
			Path outputDir = new Path(paths.get("Step5Output"));
			
			FileOutputFormat.setOutputPath(job, outputDir);
			FileSystem fs = FileSystem.get(conf);
			if(fs.exists(outputDir)) {
				fs.delete(outputDir, true);
			}
			
			job.setMapperClass(Step5Mapper.class);
			job.setReducerClass(Step5Reduce.class);
			
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			
			return job.waitForCompletion(true);
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	static class Step5Mapper extends Mapper<LongWritable,Text,Text,Text>{

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			//输入数据格式
			//u2732	i281,1.0
			String[] tokens = Pattern.compile("[\t,]").split(value.toString());
			Text k = new Text(tokens[0]);// 用户为key
			//（商品,矩阵乘法结果） value最后数据为 i281,1.0
			Text v = new Text(tokens[1] + "," + tokens[2]);
			context.write(k, v);
		}
	}
	
	static class Step5Reduce extends Reducer<Text, Text, Text, Text> {

		/**
		 * 用户为key比如  u2732 
		 * value（商品,矩阵乘法结果）如下
		 *  101, 12
		 * 	101, 8
		 *	102, 12
	     *  102, 32
		 */
		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			Map<String, Double> map = new HashMap<String, Double>();// 结果

			for (Text line : values) {
				// i9,4.0
				String[] tokens = line.toString().split(",");
				String itemID = tokens[0];
				Double score = Double.parseDouble(tokens[1]);

				// 用户对每个商品的矩阵乘法求和计算
				if (map.containsKey(itemID)) {
					map.put(itemID, map.get(itemID) + score);
				} else {
					map.put(itemID, score);
				}
			}
         
			Iterator<String> iter = map.keySet().iterator();
			while (iter.hasNext()) {
				String itemID = iter.next();
				double score = map.get(itemID);
				Text v = new Text(itemID + "," + score);
				//key为用户，value是商品和商品的矩阵乘法求和的值
				context.write(key, v);
			}
		}
	}
}
