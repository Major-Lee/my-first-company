package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

/**
 * urouter的周边探测的社区类型
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterWSCommunityVTO implements Serializable{
	//探测终端总数量
	private long total;
	//社区类型
	private String ct;
	//社区终端类型对应的探测数量
	private List<URouterWSCommunityHDVTO> hdts;
	
	public String getCt() {
		return ct;
	}
	public void setCt(String ct) {
		this.ct = ct;
	}
	public List<URouterWSCommunityHDVTO> getHdts() {
		return hdts;
	}
	public void setHdts(List<URouterWSCommunityHDVTO> hdts) {
		this.hdts = hdts;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
