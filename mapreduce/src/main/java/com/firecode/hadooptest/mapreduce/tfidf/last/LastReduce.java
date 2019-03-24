package com.firecode.hadooptest.mapreduce.tfidf.last;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 计算 逆向文件频率（IDF）
 * @author JIANG
 */
public class LastReduce extends Reducer<Text,Text,Text,Text>{
	
	private Text valText = new Text();

	@Override
	protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		
		StringBuilder sb = new StringBuilder();
		for(Text val:values) {
			sb.append(val.toString()).append("\t");
		}
		valText.set(sb.toString());
		context.write(key, valText);
	}
}
