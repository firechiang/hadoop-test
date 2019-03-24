package com.firecode.hadooptest.mapreduce.tfidf.last;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * 计算 逆向文件频率（IDF）
 * @author JIANG
 */
public class LastMapper extends Mapper <LongWritable,Text,Text,Text> {
	//微博总数量
	private static Map<String,Integer> countMap = null;
	//词在多少个微博中出现过（包含该词的文件总数）
	private static Map<String,Integer> dfMap = null;
	
	
	private Text keyText = new Text();
	private Text valText = new Text();
	
	
	/**
	 * Map启动之前执行
	 * @param context
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		
		if(countMap == null || countMap.isEmpty() || dfMap == null || dfMap.isEmpty()) {
			URI[] cacheFiles = context.getCacheFiles();
			if(null != cacheFiles) {
				for(int i=0;i<cacheFiles.length;i++) {
					URI uri = cacheFiles[i];
					//微博总数量文件
					if(uri.getPath().endsWith("part-r-00003")) {
						//Path path = new Path(uri.getPath());
						BufferedReader br = new BufferedReader(new FileReader(/*path.getName()*/uri.getPath()));
						String line = br.readLine();
						if(line.startsWith("count")) {
							String[] ls = line.split("\t");
							countMap = new HashMap<>();
							countMap.put(ls[0], Integer.valueOf(ls[1].trim()));
						}
						br.close();
					}
					//词在多少个微博中出现过（包含该词的文件总数）
					if(uri.getPath().endsWith("part-r-00000")) {
						dfMap = new HashMap<>();
						//Path path = new Path(uri.getPath());
						BufferedReader br = new BufferedReader(new FileReader(/*path.getName()*/uri.getPath()));
						String line = null;
						while((line = br.readLine()) != null) {
							String[] ls = line.split("\t");
							dfMap.put(ls[0], Integer.valueOf(ls[1].trim()));
						}
						br.close();
					}
				}
			}
		}
	}


	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		//获取当前Mapper task的数据片段（split）
		FileSplit inputSplit = (FileSplit)context.getInputSplit();
		//如果数据不是微博总数量数据（微博总数量数据在第3个分区，具体可看：Firstapper）
		if(!inputSplit.getPath().getName().contains("part-r-00003")) {
			//今天_3823890285529671 2   （”今天“这个词在id为3823890285529671的微博中出现2次）
			String[] values = value.toString().trim().split("\t");
			if(values.length >= 2) {
				//TF（某个词在文章中出现的次数）
				Integer tf = Integer.valueOf(values[1].trim());
				String[] split = values[0].split("_");
				if(split.length >= 2) {
					String w = split[0];
					String id = split[1];
					//计算 逆向文件频率（IDF），公式说明请看 FirstMain
					double s = tf * Math.log(countMap.get("count") / dfMap.get(w));
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMaximumFractionDigits(5);
					
					keyText.set(id);
					valText.set(w+":"+nf.format(s));
					context.write(keyText, valText);
				}
			}
		}
	}

}
