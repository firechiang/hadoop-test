package com.firecode.hadooptest.mapreduce.weather;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 分区，排序完成之后执行
 * 自定义分组比较器（调Reduce之前分组（将相同的Key放到同一组））
 * @author JIANG
 */
public class WeatherGroupingComparator extends WritableComparator{

	
	public WeatherGroupingComparator() {
		/**
		 * 帮我们把数据序列化成WeatherKey对象，以便下面比较函数（compare(Object a, Object b)）使用
		 */
		super(WeatherKey.class, true);
	}

	/**
	 * 我们将年月相同的Key分在同一组，以便找出每个月气温最高的2天
	 * 
	 * 
	 * 
	 * 注意：参数必须和父类一致(WritableComparable a, WritableComparable b)，不能用Object，否则当前排序类不会起调用
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		WeatherKey w1 = (WeatherKey)a;
		WeatherKey w2 = (WeatherKey)b;
		
		int compare = Integer.compare(w1.getYear(), w2.getYear());
		if(compare == 0) {
			compare = Integer.compare(w1.getMonth(), w2.getMonth());
		}
		return compare;
	}

}
