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
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 * 把同现矩阵（同现次数）和得分（评分）矩阵相乘
 * @author JIANG
 *
 */
public class Step4 {
	
	public static boolean run(Map<String,String> paths) {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "file:///");
		try {
		    Job job = Job.getInstance(conf);
			job.setJarByClass(Step4.class);
			job.setJobName("Step4");
			
			//两个输入文件
			FileInputFormat.setInputPaths(job,new Path[] { new Path(paths.get("Step4Input1")),new Path(paths.get("Step4Input2"))});
			Path outputDir = new Path(paths.get("Step4Output"));
			
			FileOutputFormat.setOutputPath(job, outputDir);
			FileSystem fs = FileSystem.get(conf);
			if(fs.exists(outputDir)) {
				fs.delete(outputDir, true);
			}
			
			job.setMapperClass(Step4Mapper.class);
			job.setReducerClass(Step4Reduce.class);
			
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			
			return job.waitForCompletion(true);
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	static class Step4Mapper extends Mapper<LongWritable,Text,Text,Text>{
		// 是 A同现矩阵 还是  B得分矩阵
		private String flag;
		

		//每个maptask，初始化时调用一次
		protected void setup(Context context) throws IOException,
				InterruptedException {
			FileSplit split = (FileSplit) context.getInputSplit();
			// 判断读的数据集
			flag = split.getPath().getParent().getName();
		}

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			String[] tokens = Pattern.compile("[\t,]").split(value.toString());
			// 同现矩阵（同现次数数据），数据格式：
			// i100:i105	1
			if (flag.equals("step3")) {
				//tokens[0] = i100:i105
				String[] v1 = tokens[0].split(":");
				//商品A
				String itemID1 = v1[0];
				//商品B
				String itemID2 = v1[1];
				//同现次数
				String num = tokens[1];
				
				//Map会输出A:B 3和B:A 3这样的数据。
				//这里用前一个物品为key 比如i100
				Text k = new Text(itemID1);
				//用字符A加后一个商品加同现次数为value最后等于 A:i105,1
				Text v = new Text("A:" + itemID2 + "," + num);
				context.write(k, v);
			// 用户对物品喜爱得分矩阵（用户评分）
			} else if (flag.equals("step2")) {
				
				//u26	i276:1,i201:1,i348:1,i321:1,i136:1,
				String userID = tokens[0];
				//重第二个开始，因为第一个是user
				for (int i = 1; i < tokens.length; i++) {
					String[] vector = tokens[i].split(":");
					String itemID = vector[0];// 物品id
					String pref = vector[1];// 喜爱分数

					Text k = new Text(itemID); // 以物品为key 比如：i100
					Text v = new Text("B:" + userID + "," + pref); // B:u401,2

					context.write(k, v);
				}
			}
		}
	}
	
	static class Step4Reduce extends Reducer<Text, Text, Text, Text> {

		@Override
		protected void reduce(Text key, Iterable<Text> values,
				Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			
			//某一个物品，针对它和其他所有物品的同现次数，都在mapA集合中
			// A同现矩阵 
			Map<String, Integer> mapA = new HashMap<String, Integer>();
			//所有用户对这个商品的评分（得分矩阵）
			Map<String, Integer> mapB = new HashMap<String, Integer>();

			
			//A  > reduce   相同的KEY为一组
			//value:2类:
			//物品同现A:b:2  c:4   d:8
			//评分数据B:u1:18  u2:33   u3:22
			for (Text line : values) {
				String val = line.toString();
				if (val.startsWith("A:")) {// 表示物品同现数字
					// A:i109,1
					String[] kv = Pattern.compile("[\t,]").split(
							val.substring(2));
					try {
						mapA.put(kv[0], Integer.parseInt(kv[1]));
										//物品同现A:b:2  c:4   d:8
						//基于 A,物品同现次数
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (val.startsWith("B:")) {
					 // B:u401,2
					String[] kv = Pattern.compile("[\t,]").split(
							val.substring(2));
							//评分数据B:u1:18  u2:33   u3:22		
					try {
						mapB.put(kv[0], Integer.parseInt(kv[1]));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			double result = 0;
			Iterator<String> iter = mapA.keySet().iterator();//同现
			while (iter.hasNext()) {
				//商品
				String mapk = iter.next();// itemID
                //商品的同现次数
				int num = mapA.get(mapk).intValue();  //对于A的同现次数
				
				//所有用户
				Iterator<String> iterb = mapB.keySet().iterator();//评分
				while (iterb.hasNext()) {
					// 用户（userID）
					String mapkb = iterb.next();
					//用户发评分
					int pref = mapB.get(mapkb).intValue();
					result = num * pref;// 矩阵乘法相乘计算

					Text k = new Text(mapkb);  //用户ID为key
					Text v = new Text(mapk + "," + result);//基于A物品,其他物品的同现与评分(所有用户对A物品)乘机
					context.write(k, v);
				}
			}
		}
	}
	
	
}
