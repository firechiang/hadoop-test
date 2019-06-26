package com.firecode.hadooptest.flink.data_stream_api.transformation;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.firecode.hadooptest.flink.data_stream_api.source.function.CustomRichParallelSourceFunction;

/**
 * Split转换数据到集合，再从集合取数据简单使用
 * @author JAING
 */
public class Split_use {
	
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		// 添加数据源
		DataStreamSource<Long> dataSource = env.addSource(new CustomRichParallelSourceFunction());
		// 设置并行度
		DataStreamSource<Long> setParallelism = dataSource.setParallelism(1);
		// 转换数据到集合
		SplitStream<Long> split = setParallelism.split((value) -> {
			List<String> list = new ArrayList<>();
			if(value % 2 == 0){
				list.add("aaa");
			}else{
				list.add("bbb");
			}
			return list;
		});
		// 从集合取数据
		DataStream<Long> select = split.select("aaa","ccc");
		// 打印数据
		select.print();
		env.execute("Split_use");
	}

}
