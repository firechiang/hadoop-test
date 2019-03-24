package com.firecode.hadooptest.mapreduce.tfidf.two;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 统计DF（Reduce阶段）：词在多少个微博中出现过（包含该词的文件总数）
 * @author JIANG
 */
public class TwoReduce extends Reducer<Text,IntWritable,Text,IntWritable> {
	
	private IntWritable valInt = new IntWritable();

	/**
	 * 根据Map的输出我们累加即可
	 */
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		int sum = 0;
		for(IntWritable val:values) {
			sum += val.get();
		}
		valInt.set(sum);
		context.write(key, valInt);
	}
}
