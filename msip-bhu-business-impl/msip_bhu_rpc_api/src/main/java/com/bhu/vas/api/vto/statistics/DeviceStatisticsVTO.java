package com.bhu.vas.api.vto.statistics;

/**
 * 设备统计数据模型(开启安全共享网络的)
 * 在线设备数，设备总数
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class DeviceStatisticsVTO implements java.io.Serializable{
	//device count
	private long dc = 0;
	//device online count
	private long doc = 0;
	
	public long getDc() {
		return dc;
	}
	public void setDc(long dc) {
		this.dc = dc;
	}
	public long getDoc() {
		return doc;
	}
	public void setDoc(long doc) {
		this.doc = doc;
	}
}
