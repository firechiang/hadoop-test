package com.firecode.hadooptest.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * 分割数据
 *
 */
public class LineSplitter implements FlatMapFunction<String,Tuple2<String, Integer>> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 分隔符
	 */
	private String delimiter;
	
	public LineSplitter(String delimiter) {
		super();
		this.delimiter = delimiter;
	}

	@Override
	public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
		String[] split = value.split(" ");
		for(String s:split){
			out.collect(new Tuple2<String, Integer>(s, 1));
		}
	}

	public String getDelimiter() {
		return delimiter;
	}
}
