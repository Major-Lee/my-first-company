package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter的周边探测的社区终端vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterWSCommunityHDVTO implements Serializable{
	//终端类型名称
	private String hd_tn;
	//终端类型对应的数量
	private long hd_tc;
	//终端类型对应的占比
	private String hd_tr;
	
	public String getHd_tn() {
		return hd_tn;
	}
	public void setHd_tn(String hd_tn) {
		this.hd_tn = hd_tn;
	}
	public long getHd_tc() {
		return hd_tc;
	}
	public void setHd_tc(long hd_tc) {
		this.hd_tc = hd_tc;
	}
	public String getHd_tr() {
		return hd_tr;
	}
	public void setHd_tr(String hd_tr) {
		this.hd_tr = hd_tr;
	}
}
