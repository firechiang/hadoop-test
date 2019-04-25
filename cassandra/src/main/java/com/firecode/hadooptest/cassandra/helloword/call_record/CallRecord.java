package com.firecode.hadooptest.cassandra.helloword.call_record;

import java.util.Date;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * 通话记录
 * @author JIANG
 *
 */
@Table(name = "call_record")
public class CallRecord {
	/**
	 * 主键
	 */
	@ClusteringColumn(0)
	private Long id;
	
	/**
	 * 分区列
	 * 对方手机号码
	 */
	@PartitionKey
	@Column(name="dnum_phone")
	private String dnumPhone;
	
	/**
	 * 通话时长
	 */
	//@ClusteringColumn(1) 主键
	private Integer length;
	/**
	 * 通话类型：1 呼出，2 呼近
	 */
	private Integer type;
	/**
	 * 通话时间
	 */
	@Column(name="create_time")
	private Date createTime;
	
	
	@Override
	public String toString() {
		return "CallRecord [id=" + id + ", dnumPhone=" + dnumPhone + ", length=" + length + ", type=" + type
				+ ", createTime=" + createTime + "]";
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDnumPhone() {
		return dnumPhone;
	}
	public void setDnumPhone(String dnumPhone) {
		this.dnumPhone = dnumPhone;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
