package com.firecode.hadooptest.mapreduce.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 
 * 单词统计Reduce阶段
 * Text            输入Key类型（Map阶段输出的结果Key的类型）
 * IntWritable     输入Value类型（Map阶段输出的结果Value的类型）
 * Text            输出结果Key类型
 * IntWritable     输出结果Valuel类型
 * @author JIANG
 */
public class WordCountReduce extends Reducer<Text,IntWritable,Text,IntWritable> {
	
	private IntWritable result = new IntWritable();

	/**
	 * 相同的key为一组，调用一次reduce方法，在方法内部迭代这一组数据进行计算
	 */
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		//统计数量
		int sum = 0;
		for(IntWritable val:values) {
			sum+=val.get();
		}
		result.set(sum);
		//输出统计结果
		context.write(key, result);
	}
	
}
