package com.firecode.hadooptest.mapreduce.tfidf.first;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 
 * 第一个Reduce，计算TF（某个词在文章中出现的次数）和计算N（微博总数）
 * @author JIANG
 */
public class FirstReduce extends Reducer<Text,IntWritable,Text,IntWritable>{
	
	private IntWritable valInt = new IntWritable();
	
	/**
	 * 根据Map输出的数据，我们在Reduce阶段只要求和输出数据即可
	 */
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		int sum = 0;
		for(IntWritable val:values) {
			sum+=val.get();
		}
		valInt.set(sum);
		context.write(key, valInt);
	}
}
