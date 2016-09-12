package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GroupStatDetailVTO implements Serializable {
	private int userTotal;
	private int connTotal;
	private int authTotal;
	
	public int getUserTotal() {
		return userTotal;
	}
	public void setUserTotal(int userTotal) {
		this.userTotal = userTotal;
	}
	public int getConnTotal() {
		return connTotal;
	}
	public void setConnTotal(int connTotal) {
		this.connTotal = connTotal;
	}
	public int getAuthTotal() {
		return authTotal;
	}
	public void setAuthTotal(int authTotal) {
		this.authTotal = authTotal;
	}
	
	
}
