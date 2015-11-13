package com.bhu.vas.api.vto;

import java.io.Serializable;
/**
 * 搜索条件vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionVTO implements Serializable{
	//搜索条件保存的时间戳
	private long ts;
	//搜索条件数据
	private String message;
	
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
