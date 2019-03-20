package com.firecode.hadooptest.mapreduce.fof;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 好友推荐Reduce阶段
 * 
 * 0表示直接关系
 * 1表示间接关系
 * @author ChiangFire
 *
 */
public class FOFReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
	
	private IntWritable intValue = new IntWritable();

	//相同的Key为一组
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		
		int flag = 0;
		int sum = 0;
		for(IntWritable value:values) {
			//直接关系
			if(value.get() == 0) {
				flag = 1;
			}
			sum+=value.get();
		}
		intValue.set(sum);
		//这一组数据没有直接关系
		if(flag == 0) {
			context.write(key, intValue);
		}
	}
}
