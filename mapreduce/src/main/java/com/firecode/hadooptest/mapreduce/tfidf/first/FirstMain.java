package com.firecode.hadooptest.mapreduce.tfidf.first;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 1，TF-IDF是一种统计方法，用以评估一字词对于一个文件集或一个语料库中的其中一份文件的重要程度。
 *    1.1，字词的重要性随着它在文件中出现的次数成正比增加
 *    1.2，但同时会随着它在语料库中出现的频率成反比下降
 *    
 * 2，TF-IDF加权的各种形式常被搜索引擎应用
 *    2.1，作为文件与用户查询之间相关程度的度量或评级
 *    2.2，除了TF-IDF以外，英特网上的搜索引擎还会使用基于链接分析（PageRank算法）的评级方法，以确定文件在搜索结果中出现的顺序 
 * 
 * 3，TF_IDF主要思想
 *    3.1，如果某个词或短语在一篇文章中出现的频率（TF）高，并且在其它文章中很少出现，则认为此词或短语具有很好的类别区分能力，适合用来分类 
 *    
 * 4，我们用这个算法来做微博推广，我们拿到所有微博数据按某个词来分类，我们拿到这个分类下所有博主，给他们推广告       
 * 
 * 
 * 计算TF-IDF公式：TF-IDF = 词频（TF） * 逆向文件频率（IDPF）
 * 
 * 
 * 
 *                某个词在文章中出现的次数
 * 词频（TF） = —————————————————————————————
 *                    文章的总词数
 * 
 * 
 * 
 *                            语料库中的文件总数
 * 逆向文件频率（IDF） = log ( ————————————————— )
 *                            包含该词的文件总数
 *                            
 *                            
 * 注：log表示对得到的值取对数
 * 
 * 
 * 我们写了FirstMain，TwoMain，LastMain 三个任务计算，最终只得到了 （逆向文件频率（IDF） ）的结果
 * （词频（TF））并未计算，这个自己可以考虑一下怎么做（其实在（词频（TF））公式中我们只差（文章的总词数）这个数据未计算出来，把这个计算出来，我们就可以用公式计算了）
 * 
 * 
 *                            
 * @author JIANG
 */
public class FirstMain {
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "file:///");
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(FirstMain.class);
		job.setJobName("TFIDF");
		
		
		Path inputPath = new Path("D:/hadoop-test/data/weibo.txt");
		FileInputFormat.addInputPath(job, inputPath);
		
		Path outputPath = new Path("D:/hadoop-test/result/weibo/1");
		
		FileSystem fs = FileSystem.get(conf);
		if(fs.exists(outputPath)) {
			fs.delete(outputPath,true);
		}
		FileOutputFormat.setOutputPath(job, outputPath);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setMapperClass(Firstapper.class);
		job.setReducerClass(FirstReduce.class);
		//组合器（在Map阶段先做一次组合，它的输出和Map的输出一致，在Reduce之前执行）
		job.setCombinerClass(FirstReduce.class);
		//分区
		job.setPartitionerClass(FirstPartition.class);
		
		//4Rduce
		job.setNumReduceTasks(4);
		
		
		
		job.waitForCompletion(true);
		
		
	}

}
