package com.firecode.hadooptest.mapreduce.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author JIANG
 */
public class PageRankMapper extends Mapper<Text,Text,Text,Text>{
	
	private Text keyText = new Text();
	private Text valueText = new Text();

	/**
	 * 首次执行输入数据的格式（A指向了B，D两各链接）
	 * A	B	D 
	 * 
	 * 非首次执行输入数据的格式（A指向了B，D两各链接，0.3是A的权重）
	 * A   0.3  B	D
	 * 
	 */
	@Override
	protected void map(Text key, Text value, Mapper<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		
		//这个是我们在配置对象里面自定义的
		int runCount = context.getConfiguration().getInt("runCount",1);
		
		String page = key.toString();
		Node node = null;
		if(runCount == 1) {
			//第一次计算，权重默认为1.0
			node = Node.fromMR("1.0", value.toString());
		}else {
			node = Node.fromMR(value.toString());
		}
		
		keyText.set(page);
		valueText.set(node.toString());
		//A	1.0  B  D  //传递老的PR值和页面的对用关系（A是key，value是 1.0 B D（1.0是A的总权重，B和D是A的出链））
		context.write(keyText, valueText);
		
		//如果所指向节点的列表不为空
		if(node.containsAdjacentNodes()) {
			//各个被指向节点所能得到的权重（具体看算法说明）
			double outValue = node.getPageRank() / node.getAdjacentNodeNames().length;
			for(int i=0;i<node.getAdjacentNodeNames().length;i++) {
				String outPage = node.getAdjacentNodeNames()[i];
				keyText.set(outPage);
				valueText.set(outValue+"");
				//B 0.5（页面A投给谁，谁作为Key，Value是这个被投节点所得到的权重值）
				context.write(keyText, valueText);
			}
		}
	}
	
	

}
