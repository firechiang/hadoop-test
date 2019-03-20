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
public class WeatherReduce extends Reducer<WeatherKey,IntWritable,Text,IntWritable> {
	
	private Text keyText = new Text();

	
	/**
	 * 经过Map以后，我们的数据是按 年月分组的，且分组数据是按温度倒叙排列的，具体格式如下：
	 * 
	 * 1949-10-01 14:12:01 39
     * 1949-10-04 14:22:01 35
     * 1949-10-01 14:52:01 34
	 */
	@Override
	protected void reduce(WeatherKey key, Iterable<IntWritable> values,
			Reducer<WeatherKey, IntWritable, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		System.err.println(key.getYear()+"-"+key.getMonth());
		int index = 0;
		int day = 0;
		/**
		 * 注意：在循环的过程当中，key会根据value的值变化而变化，也就是说value和key是一一对应的（我们的代码并没有给key重新赋值，它是怎么实现的呢看下面）
		 * 
		 * ReduceContextImpl类的RawKeyValueIterator input迭代器对象里面存储中着key-value对的元素， 以及一个只存储value的迭代器，
		 * 然后每调一次我们实现的reduce方法，就是传入ValueIterable迭代器对象和当前的key。
		 * 但是我们在方法里面调用迭代器的next方法时，其实调用了nextKeyValue，来获取下一个key和value，
		 * 并判断下一个key是否和 上一个key是否相同，然后决定hashNext方法是否结束，同时对key进行了一次重新赋值。
		 */
		for(IntWritable value:values) {
			//先把第一条结果写出去（第一温度最高的天）
			if(index == 0) {
				keyText.set(key.getYear()+"-"+key.getMonth()+"-"+key.getDay()+": "+key.getWeather());
				index++;
				//记录第一数据是哪一天
				day = key.getDay();
				context.write(keyText, value);
			}
			//如果天变了说明是另外一天（第二温度最高的天）
			if(index != 0 && day != key.getDay()) {
				keyText.set(key.getYear()+"-"+key.getMonth()+"-"+key.getDay()+": "+key.getWeather());
				context.write(keyText, value);
				break;
			}
		}
	}
}
