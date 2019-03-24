package com.firecode.hadooptest.mapreduce.tfidf.first;

import java.io.IOException;
import java.io.StringReader;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * 第一个Map，计算TF（某个词在文章中出现的次数）和计算N（微博总数）
 * 
 * 输入数据格式如下：
 * 3823890256464940	约起来！次饭去！
 * @author JIANG
 */
public class Firstapper extends Mapper<LongWritable,Text,Text,IntWritable> {
	
	private Text keyText = new Text();
	private IntWritable valInt = new IntWritable();

	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		//3823890285529671	今天约了广场一起滑旱冰
		String[] values = value.toString().trim().split("\t");
		if(values.length > 0) {
			String id = values[0].trim();
			String content = values[1].trim();
			
			StringReader sr = new StringReader(content);
			//分词器
			IKSegmenter iks = new IKSegmenter(sr, true);
			Lexeme word = null;
			while((word=iks.next())!= null) {
				//获取词语
				String w = word.getLexemeText();
				
				//key就是 词语加上ID
				keyText.set(w+"_"+id);
				//出现一次
				valInt.set(1);
				//今天_3823890285529671 1      （今天_3823890285529671是key，value是1（就是“今天”这个词出，在id为3823890285529671的微博中出现一次））
				context.write(keyText, valInt);
			}
			keyText.set("count");
			valInt.set(1);
			/**
			 * 因为这个输出数据和上面那个输出数据的计算规则不一样，所以我们在自定义分区里面将count放到同一分区。
			 * 具体请看分区代码：FirstPartition
			 */
			//count 1    （count是key，value是1，记录微博数量一篇）
			context.write(keyText, valInt);
		}
	}
}
