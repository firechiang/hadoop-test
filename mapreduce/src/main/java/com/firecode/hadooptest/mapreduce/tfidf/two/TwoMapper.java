package com.firecode.hadooptest.mapreduce.tfidf.two;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * 统计DF（Map阶段）：词在多少个微博中出现过（包含该词的文件总数）
 * @author JIANG
 */
public class TwoMapper extends Mapper<LongWritable,Text,Text,IntWritable> {
	
	private Text keyText = new Text();
	private IntWritable valInt = new IntWritable();

	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		
		//获取当前Mapper task的数据片段（split）
		FileSplit fs = (FileSplit)context.getInputSplit();
		//如果数据不是微博总数量数据（微博总数量数据在第3个分区，具体可看：Firstapper）
		if(!fs.getPath().getName().contains("part-r-00003")) {
			//今天_3823890285529671 2   （”今天“这个词在id为3823890285529671的微博中出现2次）
			String[] values = value.toString().trim().split("\t");
			if(values.length >= 2) {
				String[] split = values[0].split("_");
				if(split.length >= 2) {
					String w = split[0];
					//词
					keyText.set(w);
					//这个微博中出现
					valInt.set(1);
					context.write(keyText, valInt);
				}
			}
		}
	}

}
