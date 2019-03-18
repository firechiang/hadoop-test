package com.firecode.hadooptest.mapreduce.weather;

import org.apache.hadoop.io.WritableComparator;

/**
 * 自定义分组后排序比较器（组内排序，调Reduce之前的排序,同一组数据的排序）
 * 
 * @author JIANG
 */
public class WeatherSortComparator extends WritableComparator {

	public WeatherSortComparator() {
		/**
		 * 帮我们把数据序列化成WeatherKey对象，以便下面比较函数（compare(Object a, Object b)）使用
		 */
		super(WeatherKey.class, true);
	}

	@Override
	public int compare(Object a, Object b) {
		WeatherKey w1 = (WeatherKey)a;
		WeatherKey w2 = (WeatherKey)b;
		
		int compare = Integer.compare(w1.getYear(), w2.getYear());
		if(compare == 0) {
			compare = Integer.compare(w1.getMonth(), w2.getMonth());
			if(compare == 0) {
				//倒序
				return -Integer.compare(w1.getWeather(), w2.getWeather());
			}
		}
		return compare;
	}
}
