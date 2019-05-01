package com.firecode.hadooptest.hbase.mapreduce;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.security.authentication.util.KerberosName;

/**
 * 将MapReduce计算结果直接写入HBase
 * @author ChiangFire
 *
 */
public class WCMain2 {
	
	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ClassNotFoundException {
		
		Configuration conf = new Configuration(true);
		
		conf.set("fs.defaultFS", "hdfs://server002:8020");
		/**
		 * 我们没有使用 Kerberos 做权限控制，但是HBase默认要验证Kerberos Name，所以我们设置了一个默认值，以防止报错
		 * @see org.apache.hadoop.security.HadoopKerberosName   # 77行
		 */
		KerberosName.setRules("RULE:[1:$1] RULE:[2:$1]");
		/**
		 * 一般配置Zookeeper集群即可，HBase客户端会去Zookeeper集群拿Hbase集群信息，然后直接连接HMaster和HRegionServer写数据
		 */
		conf.set("hbase.zookeeper.quorum", "server002:2181,server003:2181,server004:2181");
        //设置hbase表名称
		conf.set(TableOutputFormat.OUTPUT_TABLE, "wc");
        //hbase超时退出时间
		conf.set("dfs.socket.timeout", "180000");
		Job job = Job.getInstance(conf);
		
		FileInputFormat.addInputPath(job, new Path("/test_txt/wordcount.txt"));
		
		job.setJarByClass(WCMain2.class);
		job.setMapperClass(WCMappper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.waitForCompletion(true);
	}

}
