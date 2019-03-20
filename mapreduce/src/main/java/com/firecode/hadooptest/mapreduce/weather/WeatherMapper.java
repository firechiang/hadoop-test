package com.firecode.hadooptest.mapreduce.weather;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Map阶段
 * @author JIANG
 */
public class WeatherMapper extends Mapper<LongWritable,Text,WeatherKey,IntWritable> {
	
	private WeatherKey weatherKey = new WeatherKey();
	private IntWritable weatherVal = new IntWritable();

	/**
	   *    数据格式如下：
	 * 1949-10-01 14:12:01 34c
     * 1949-10-01 14:22:01 35c
     * 1949-10-01 14:52:01 34c
     * 1949-10-01 14:52:01 34c
	 */
	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, WeatherKey, IntWritable>.Context context)
			throws IOException, InterruptedException {
		//用制表符拆分数据
		String[] split = StringUtils.split(value.toString(),' ');
		LocalDate localDate = LocalDate.parse(split[0], DateTimeFormatter.ISO_DATE);
		weatherKey.setYear(localDate.getYear());
		weatherKey.setMonth(localDate.getMonthValue());
		weatherKey.setDay(localDate.getDayOfMonth());
		//温度
		String weather = split[2].replace("c", "");
		weatherKey.setWeather(Integer.valueOf(weather));
		weatherVal.set(weatherKey.getWeather());
		//写出去
		context.write(weatherKey,weatherVal);
	}
}
