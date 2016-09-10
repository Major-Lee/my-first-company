package com.bhu.vas.api.rpc.tag.vto;

@SuppressWarnings("serial")
public class TagGroupRankUsersVTO implements java.io.Serializable{
	private String date;
	private String count;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
}
