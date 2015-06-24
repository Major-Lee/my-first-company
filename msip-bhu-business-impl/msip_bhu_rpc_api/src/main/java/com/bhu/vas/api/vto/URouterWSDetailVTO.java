package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter的周边探测的细节流水信息vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterWSDetailVTO implements Serializable{
	//探测上线时间
	private long st_ts;
	//持续时长
	private String d;
	
	public long getSt_ts() {
		return st_ts;
	}
	public void setSt_ts(long st_ts) {
		this.st_ts = st_ts;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
}
