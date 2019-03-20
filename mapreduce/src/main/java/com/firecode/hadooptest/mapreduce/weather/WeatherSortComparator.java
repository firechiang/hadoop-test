package com.firecode.hadooptest.mapreduce.weather;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 
 * 分区后的排序比较器（区内排序，调Reduce之前的排序,同一分区数据的排序）
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

	/**
	 * 我们的需求是取出每个月温度最高的两天，所以先按年月排序，再按温度倒序排列，排序后数据如下
	 * 
     * 1949-10-01 14:22:01 35c
     * 1949-10-01 14:52:01 34c
     * 1949-10-01 14:52:01 34c
     * 1949-11-05 14:52:01 35c
     * 1949-11-01 14:52:01 31c
     * 1949-11-07 14:52:01 29c
     * 1949-11-02 14:52:01 15c
     * 
     * 注意：参数必须和父类一致(WritableComparable a, WritableComparable b)，不能用Object，否则当前排序类不会起调用
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
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
