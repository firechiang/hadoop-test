package com.firecode.hadooptest.mapreduce.weather;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Map阶段
 * @author JIANG
 */
public class WeatherMapper extends Mapper<LongWritable,Text,WeatherKey,WeatherKey> {
	
	private WeatherKey weatherKey = new WeatherKey();

	/**
	   *    数据格式如下：
	 * 1949-10-01 14:12:01 34c
     * 1949-10-01 14:22:01 35c
     * 1949-10-01 14:52:01 34c
     * 1949-10-01 14:52:01 34c
	 */
	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, WeatherKey, WeatherKey>.Context context)
			throws IOException, InterruptedException {
		//用制表符拆分数据
		String[] split = StringUtils.split(value.toString(),"\t");
		LocalDate localDate = LocalDate.parse(split[0], DateTimeFormatter.ISO_DATE);
		weatherKey.setYear(localDate.getYear());
		weatherKey.setMonth(localDate.getMonthValue());
		weatherKey.setDay(localDate.getDayOfMonth());
		//温度
		String weather = split[2].replace("c", "");
		weatherKey.setWeather(Integer.valueOf(weather));
		//写出去
		context.write(weatherKey,weatherKey);
	}
}
