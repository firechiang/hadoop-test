package com.firecode.hadooptest.mapreduce.itemcf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
 * 按用户分组，计算所有物品出现的组合列表，得到用户对物品的喜爱度得分矩阵（得到用户对每个商品的评分向量）,最后得到的数据如下
 *	u13	i160:1,
 *	u14	i25:1,i223:1,
 *	u16	i252:1,
 *	u21	i266:1,
 *	u24	i64:1,i218:1,i185:1,
 *	u26	i276:1,i201:1,i348:1,i321:1,i136:1,
 * @author JIANG
 *
 */
public class Step2 {
	
	public static boolean run(Map<String,String> paths) {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "file:///");
		try {
		    Job job = Job.getInstance(conf);
			job.setJarByClass(Step2.class);
			job.setJobName("Step2");
			
			FileInputFormat.addInputPath(job, new Path(paths.get("Step2Input")));
			
			Path outputDir = new Path(paths.get("Step2Output"));
			FileOutputFormat.setOutputPath(job, outputDir);
			FileSystem fs = FileSystem.get(conf);
			if(fs.exists(outputDir)) {
				fs.delete(outputDir, true);
			}
			
			job.setMapperClass(Step2Mapper.class);
			job.setReducerClass(Step2Reduce.class);
			
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			
			return job.waitForCompletion(true);
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	static class Step2Mapper extends Mapper<LongWritable,Text,Text,Text>{

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			//如果使用，用户+物品，同时为key更优
			String[] values = value.toString().split(",");
			String item = values[0];
			String user = values[1];
			String action = values[2];
			Text k = new Text(user);
			//评分标准
			Integer integer = ItemCFMain.R.get(action);
			Text v = new Text(item+":"+integer);
			//以用户为key，商品加评分为value输出
			context.write(k, v);
		}
	}
	
	static class Step2Reduce extends Reducer<Text,Text,Text,Text> {

		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			//用户每个商品的总评分
			Map<String, Integer> r =new HashMap<String, Integer>();
			//叠加用户每个商品的总评分
			for(Text value :values){
				String[] vs =value.toString().split(":");
				//商品
				String item= vs[0];
				//评分
				Integer action = Integer.parseInt(vs[1]);
				//获取商品的总评分
				Integer sum = r.get(item);
				action += (sum == null ? 0 : sum);
				r.put(item,action);
			}
			
			//将每个（商品:评分）拼接起来，最后得到的数据如：i162:1,i165:2,i16j:2
			StringBuffer sb =new StringBuffer();
			for(Entry<String, Integer> entry :r.entrySet() ){
				sb.append(entry.getKey()+":"+entry.getValue().intValue()+",");
			}
			context.write(key,new Text(sb.toString()));
		}
	}
	
}
