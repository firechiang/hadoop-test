package com.firecode.hadooptest.mapreduce.fof;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;

/**
 * 好友推荐Map阶段
 * @author JIANG
 *
 */
public class FOFMapper extends Mapper<LongWritable,Text,Text,IntWritable> {
	
	private Text textKey = new Text();
	private IntWritable intValue = new IntWritable();

	/**
	 * 两两一组，标记他们两的关系（直接关系还是间接关系）
	 */
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable,Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		
		/**
		 * 
		 * 张三有李四，王五，赵六三个朋友，数据如下
		 * 
		 * 张三 李四 王五 赵六
		 * 
		 * 朋友间的关系如下，这个也是下面代码执行的结果：
		 * 
		 * 张三:李四       0  （直接关系）
         * 张三:王五       0 （直接关系）
         * 张三:赵六       0 （直接关系）
         * 
         * 李四:王五       1 （间接关系）
         * 李四:赵六       1 （间接关系）
         * 王五:赵六       1 （间接关系）
		 */
		String[] values = StringUtils.split(value.toString(),' ');
		
		for(int i=1;i<values.length;i++) {
			//直接关系
			textKey.set(getFOF(values[0],values[i]));
			//0表示直接关系
			intValue.set(0);
			context.write(textKey, intValue);
			//间接关系
			for(int j=i+1;j<values.length;j++) {
				textKey.set(getFOF(values[i],values[j]));
				//1表示间接关系
				intValue.set(1);
				context.write(textKey, intValue);
			}
		}
	}
	
	/**
	 * 保证字符串拼接的顺序一致，两个参数无论谁传前谁传后，最后拼接的结果都是一样的，如下：
	 * 
	 * 张三，李四  = 张三:李四
	 * 李四，张三  = 张三:李四
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	private static String getFOF(String s1,String s2) {
		if(s1.compareTo(s2) < 0) {
			return s1+":"+s2;
		}
		return s2+":"+s1;
	}
	
	public static void main(String[] args) {
		String data = "张三 李四 王五 赵六";
		/**
		 * 张三 李四 王五 赵六
		 */
		String[] values = StringUtils.split(data.toString(),' ');
		
		for(int i=1;i<values.length;i++) {
			//直接关系
			System.out.println("直接关系："+getFOF(values[0],values[i]));
			//间接关系（重后一个开始）
			for(int j=i+1;j<values.length;j++) {
				System.err.println("间接关系："+getFOF(values[i],values[j]));
			}
		}
	}
}
