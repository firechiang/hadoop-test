package com.firecode.hadooptest.mapreduce.weather;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reduce阶段
 * @author JIANG
 *
 */
public class WeatherReduce extends Reducer<WeatherKey,WeatherKey,Text,IntWritable> {
	
	private Text keyText = new Text();
	private IntWritable val = new IntWritable();

	
	/**
	 * 经过Map以后，我们的数据是按 年月分组的，且分组数据是按温度倒叙排列的，具体格式如下：
	 * 
	 * 1949-10-01 14:12:01 39
     * 1949-10-04 14:22:01 35
     * 1949-10-01 14:52:01 34
	 */
	@Override
	protected void reduce(WeatherKey key, Iterable<WeatherKey> values,
			Reducer<WeatherKey, WeatherKey, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		
		int index = 0;
		int day = 0;
		for(WeatherKey value:values) {
			//先把第一条结果写出去（第一温度最高的天）
			if(index == 0) {
				keyText.set(value.getYear()+"-"+value.getMonth()+"-"+value.getDay()+": "+value.getWeather());
				val.set(value.getWeather());
				index++;
				//记录第一数据是哪一天
				day = value.getDay();
				context.write(keyText, val);
			}
			//如果天变了说明是另外一天（第二温度最高的天）
			if(index != 0 && day != value.getDay()) {
				keyText.set(value.getYear()+"-"+value.getMonth()+"-"+value.getDay()+": "+value.getWeather());
				val.set(value.getWeather());
				context.write(keyText, val);
				break;
			}
			
		}
	}
	
	
}
