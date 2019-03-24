package com.firecode.hadooptest.mapreduce.tfidf.itemcf;

/**
 * 协同过滤，推荐算法
 * 
 * 1，Co-occurrence Matrix（同现矩阵） 和 User Preference Vector（用户评分向量） 相乘 得到 Recommended Vector（推荐向量）
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
public class ItemCF {
	
}
