package com.firecode.hadooptest.mapreduce.weather;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * 自定义Map输出的Key
 * @author JIANG
 */
public class WeatherKey implements WritableComparable<WeatherKey>{
	
	/**
	 * 年
	 */
	private Integer year;
	/**
	 * 月
	 */
	private Integer month;
	/**
	 * 日
	 */
	private Integer day;
	/**
	 * 温度
	 */
	private Integer weather;
	

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(year);
		out.writeInt(month);
		out.writeInt(day);
		out.writeInt(weather);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.year = in.readInt();
		this.month = in.readInt();
		this.day = in.readInt();
		this.weather = in.readInt();
	}

	@Override
	public int compareTo(WeatherKey o) {
		//先比较年
		int compare = Integer.compare(this.getYear(), o.getYear());
		//如果年相等
		if(compare == 0) {
			compare = Integer.compare(this.getMonth(), o.getMonth());
			//如果月相等
			if(compare == 0) {
				compare = Integer.compare(this.getDay(), o.getDay());
			}
		}
		return compare;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getWeather() {
		return weather;
	}

	public void setWeather(Integer weather) {
		this.weather = weather;
	}
	
}
