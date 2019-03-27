package com.firecode.hadooptest.mapreduce.itemcf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 协同过滤，推荐算法
 * 
 * 1，Co-occurrence Matrix（两个商品的同现矩阵） 和 User Preference Vector（用户评分向量） 相乘 得到 Recommended Vector（推荐向量）
 * 2，基于全量数据的统计，产生同现矩阵
 *    2.1，体现商品间的关联性
 *    2.2，每件商品都有自己对其它全部商品的关联性（每件商品的特征）
 * 3，用户评分向量体现的是用户对一些商品的评分
 * 4，任意商品需要
 *    4.1，用户评分向量乘以基于该商品的其它商品关联值
 *    4.2，求和得出针对该商品的推荐向量
 *    4.3，排序去TopN即可   
 * @author JIANG
 *
 */
public class ItemCFMain {
	
	/**
	 * 用户评分标准
	 */
	public static Map<String,Integer> R = new HashMap<>();
	
	static {
		R.put("click", 1);//点击一分
		R.put("collect", 2);
		R.put("cart", 3);
		R.put("alipay", 4);
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		/**
		 * 所有计算输入和输出地址
		 */
		Map<String,String> paths = new HashMap<>();
		paths.put("Step1Input", "D:/hadoop-test/data/rec_tmall_log.csv");
		paths.put("Step1Output", "D:/hadoop-test/result/itemcf/output/step1");
		
		paths.put("Step2Input", paths.get("Step1Output"));
		paths.put("Step2Output", "D:/hadoop-test/result/itemcf/output/step2");
		
		paths.put("Step3Input", paths.get("Step2Output"));
		paths.put("Step3Output", "D:/hadoop-test/result/itemcf/output/step3");
		
		paths.put("Step4Input1", paths.get("Step2Output"));
		paths.put("Step4Input2", paths.get("Step3Output"));
		paths.put("Step4Output", "D:/hadoop-test/result/itemcf/output/step4");
		
		paths.put("Step5Input", paths.get("Step4Output"));
		paths.put("Step5Output", "D:/hadoop-test/result/itemcf/output/step5");
		
		paths.put("Step6Input", paths.get("Step5Output"));
		paths.put("Step6Output", "D:/hadoop-test/result/itemcf/output/step6");
		
		//Step1.run(paths);
		//Step2.run(paths);
		//Step3.run(paths);
		//Step4.run(paths);
		//Step5.run(paths);
		Step6.run(paths);
	}

}
