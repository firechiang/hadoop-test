package com.firecode.hadooptest.hbase.mapreduce;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WCMappper extends Mapper<LongWritable, Text, Text, IntWritable>{
	
	private IntWritable one = new IntWritable(1);
	private Text word = new Text();
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		
		//单词分词器
		StringTokenizer st = new StringTokenizer(value.toString());
		//每一个单词计数为1
		while(st.hasMoreTokens()) {
			word.set(st.nextToken());
			//将结果输出到下一个环节（Reduce环节）
			context.write(word, one);
		}
	}
}
