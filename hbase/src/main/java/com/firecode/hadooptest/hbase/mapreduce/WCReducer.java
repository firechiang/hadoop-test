package com.firecode.hadooptest.hbase.mapreduce;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WCReducer extends TableReducer<Text, IntWritable, ImmutableBytesWritable>{

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, ImmutableBytesWritable, Mutation>.Context context)
			throws IOException, InterruptedException {
		
		//统计数量
		int sum = 0;
		for(IntWritable val:values) {
			sum+=val.get();
		}
		Put put = new Put(Bytes.toBytes(key.toString()));
		put.addColumn(Bytes.toBytes("cf"),Bytes.toBytes("ct"),Bytes.toBytes(String.valueOf(sum)));
		//写出数据到HBase
		context.write(null, put);
	}
}
