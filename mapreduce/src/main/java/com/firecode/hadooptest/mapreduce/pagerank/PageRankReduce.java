package com.firecode.hadooptest.mapreduce.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author JIANG
 */
public class PageRankReduce extends Reducer<Text,Text,Text,Text>{
	
	private Text valueText = new Text();

	/**
	 * 相同的Key为一组
	 * 
	 * 根据Map的输出我们在这里可以得到两类数据
	 * 
	 * 一类：A	1.0  B  D  //传递老的PR值和页面的对用关系（A是key，value是 1.0 B D（1.0是A的总权重，B和D是A的出链））
	 * 二类：A   0.5       //（页面投给谁，谁作为Key，Value是这个被投节点所得到的权重值）
	 */
	@Override
	protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		
		double sum = 0.0;
		Node sourceNode = null;
		for(Text value:values) {
			Node node = Node.fromMR(value.toString());
			//如果所指向节点的列表不为空，说明是第一类数据，它包含节点对应关系
			if(node.containsAdjacentNodes()) {
				sourceNode = node;
			}else {
				//计算该节点所得到的权重总和
				sum+=node.getPageRank();
			}
		}
		//新的权重值，计算规则请看（PageRank算法说明.md的公式），4.0是我们的页面总数，0.15是公式的1-0.85
		double newPR = (0.15 / 4.0) + (0.85 * sum);
		//新的权重减去旧的权重，得到差值
		double d = newPR - sourceNode.getPageRank();
		//放大一千倍
		int j = (int)(d * 1000.0);
		//如果参数是非负数，则返回该参数。如果参数是负数，则返回该参数的相反数
		j = Math.abs(j);
		//将差值写入计数器
		context.getCounter(PageRankCount.CUSTOM).increment(j);
		
		//设置新的权重，以便下一次计算
		sourceNode.setPageRank(newPR);
		
		valueText.set(sourceNode.toString());
		context.write(key, valueText);
	}

}
