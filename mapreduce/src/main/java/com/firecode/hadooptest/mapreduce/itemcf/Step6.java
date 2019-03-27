package com.firecode.hadooptest.mapreduce.itemcf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 * 按照推荐得分降序排序，每个用户列出10个推荐物品
 * 
 * @author root
 *
 */
public class Step6 {
	
	public static boolean run(Map<String,String> paths) {
		Configuration conf = new Configuration(true);
		conf.set("fs.defaultFS", "file:///");
		try {
		    Job job = Job.getInstance(conf);
			job.setJarByClass(Step6.class);
			job.setJobName("Step6");
			
			FileInputFormat.addInputPath(job, new Path(paths.get("Step6Input")));
			Path outputDir = new Path(paths.get("Step6Output"));
			
			FileOutputFormat.setOutputPath(job, outputDir);
			FileSystem fs = FileSystem.get(conf);
			if(fs.exists(outputDir)) {
				fs.delete(outputDir, true);
			}
			
			job.setMapperClass(Step6Mapper.class);
			job.setReducerClass(Step6Reduce.class);
			
			job.setSortComparatorClass(NumSort.class);
			job.setGroupingComparatorClass(UserGroup.class);
			
			job.setMapOutputKeyClass(PairWritable.class);
			job.setMapOutputValueClass(Text.class);
			
			return job.waitForCompletion(true);
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static Text K = new Text();
	private static Text V = new Text();
	
	static class Step6Mapper extends Mapper<LongWritable,Text,PairWritable,Text>{
		

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, PairWritable, Text>.Context context)
				throws IOException, InterruptedException {
			String[] tokens = Pattern.compile("[\t,]").split(value.toString());
			String u = tokens[0];
			String item = tokens[1];
			String num = tokens[2];
			PairWritable k =new PairWritable();
			k.setUid(u);
			k.setNum(Double.parseDouble(num));
			V.set(item+":"+num);
			context.write(k, V);
		}
	}
	
	static class Step6Reduce extends Reducer<PairWritable, Text, Text, Text> {

		@Override
		protected void reduce(PairWritable key, Iterable<Text> values,
				Reducer<PairWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			
			int i=0;
			StringBuffer sb =new StringBuffer();
			for(Text v :values){
				if(i==10)
					break;
				sb.append(v.toString()+",");
				i++;
			}
			K.set(key.getUid());
			V.set(sb.toString());
			context.write(K, V);
		}
	}
	
	
	static class PairWritable implements WritableComparable<PairWritable>{

//		private String itemId;
		private String uid;
		private double num;
		public void write(DataOutput out) throws IOException {
			out.writeUTF(uid);
//			out.writeUTF(itemId);
			out.writeDouble(num);
		}

		public void readFields(DataInput in) throws IOException {
			this.uid=in.readUTF();
//			this.itemId=in.readUTF();
			this.num=in.readDouble();
		}

		public int compareTo(PairWritable o) {
			int r =this.uid.compareTo(o.getUid());
			if(r==0){
				return Double.compare(this.num, o.getNum());
			}
			return r;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public double getNum() {
			return num;
		}

		public void setNum(double num) {
			this.num = num;
		}
		
	}
	
	static class NumSort extends WritableComparator{
		public NumSort(){
			super(PairWritable.class,true);
		}
		
		public int compare(WritableComparable a, WritableComparable b) {
			PairWritable o1 =(PairWritable) a;
			PairWritable o2 =(PairWritable) b;
			
			int r =o1.getUid().compareTo(o2.getUid());
			if(r==0){
				return -Double.compare(o1.getNum(), o2.getNum());
			}
			return r;
		}
	}
	
	static class UserGroup extends WritableComparator{
		public UserGroup(){
			super(PairWritable.class,true);
		}
		
		public int compare(WritableComparable a, WritableComparable b) {
			PairWritable o1 =(PairWritable) a;
			PairWritable o2 =(PairWritable) b;
			return o1.getUid().compareTo(o2.getUid());
		}
	}
}
