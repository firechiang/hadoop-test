package com.firecode.hadooptest.mapreduce.wordcount;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 单词统计Map阶段
 * Object          输入Key类型
 * Text            输入Value类型
 * Text            输出结果Key类型
 * IntWritable     输出结果Value类型
 * 
 * @author JIANG
 */
public class WordCountMapper extends Mapper<Object,Text,Text,IntWritable> {
	
	private IntWritable one = new IntWritable(1);
	private Text word = new Text();

	/**
	 * Map计算阶段，one和word可以定义为成员变量是因为：context.write(word, one) 方法之后数据马上被序列化到缓冲区，所以one和word可以重复利用
	 * @param key       文件偏移量（数据所在文件里的位置（二进制数组的下标））
	 * @param value     值
	 * @param context   
	 */
	@Override
	protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
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
