package com.firecode.hadooptest.mapreduce.pagerank;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 搜索引擎排序算法（PageRank），具体说明请看：PageRank算法说明.md 文件
 * @author JIANG
 *
 */
public class PageRankMain {
	
	
	
	/**
	 * 数据格式如下
	 * 
	 * A	B	D     #A链接里面有指向B，D链接
     * B	C         #B链接里面有指向C链接
     * C	A	B     #C链接里面有指向A，B链接
     * D	B	C     #D链接里面有指向B，C链接
     * 
     * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(true);
		//开启自适应平台支持，用于Windows系统（自动识别系统）
		//conf.set("mapreduce.app-submission.cross-platform", "true");
		//本地运行
		//conf.set("mapreduce.framework.name", "local");
		double d = 0.001;
		int i = 0;
		//每次循环提交一个作业（job）
		while(true) {
			i++;
			//传我们自定义的参数，我们要再Map阶段取这个参数
			conf.setInt("runCount", i);
			FileSystem fs = FileSystem.get(conf);
			Job job = Job.getInstance(conf);
			job.setJarByClass(PageRankMain.class);
			job.setJobName("pr"+i);
			job.setMapperClass(PageRankMapper.class);
			job.setReducerClass(PageRankReduce.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			//Map阶段前格式化Key Value数据，以首次出现空格分割数据，左边为Key，右边为Value
			//（在Map阶段这个类会帮我们把数据先做一次格式化）
			job.setInputFormatClass(KeyValueTextInputFormat.class);
			
			Path inputPath = new Path("/data/pagerank/input");
			//第二次开始我们要使用上一次的计算结果作为输入
			if(i > 1 ) {
				inputPath = new Path("/data/pagerank/output/pr"+(i-1));
			}
			FileInputFormat.addInputPath(job, inputPath);
			
			Path outputPath = new Path("/data/pagerank/output/pr"+i);
			if(fs.exists(outputPath)) {
				fs.delete(outputPath,true);
			}
			FileOutputFormat.setOutputPath(job, outputPath);
			
			boolean f = job.waitForCompletion(true);
			//Reduce计算完成
			if(f) {
				//Reduce只要计算完了，会把新的权重值减去老的权重值所得到的差值放到计数器里面，以PageRankCount.CUSTOM做为标识
				//我们在这里取的是所有差值的总和
				int sum = (int) job.getCounters().findCounter(PageRankCount.CUSTOM).getValue();
				System.err.println("第 "+i+" 次，计算完成，差值的总和为："+sum);
				//我们的数据只有4各页面，正常除以4即可，但这里除以了4000是因为我们把差值乘以了1000（具体看Reduce）
				double avgd = sum / 4000.0;
				//这一轮与上一轮计算结果的差值的平均值小于0.001了我们就拿PR值来用了，就不再计算了
				if(avgd < d) {
					break;
				}
			}
		}
	}

}
