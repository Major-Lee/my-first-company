package com.bhu.vas.api.rpc.tag.vto;

@SuppressWarnings("serial")
public class TagGroupUserConnectDataVTO implements java.io.Serializable {
	//日期
	private String date;
	//新增用户数
	private String newly;
	//连接用户数
	private String total;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNewly() {
		return newly;
	}
	public void setNewly(String newly) {
		this.newly = newly;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
}
