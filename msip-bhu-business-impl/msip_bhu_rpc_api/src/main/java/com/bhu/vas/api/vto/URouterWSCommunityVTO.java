package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.Map;

/**
 * urouter的周边探测的社区类型
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterWSCommunityVTO implements Serializable{
	//社区类型
	private String t;
	//社区终端类型对应的探测数量
	private Map<String,String> statics;
	
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public Map<String, String> getStatics() {
		return statics;
	}
	public void setStatics(Map<String, String> statics) {
		this.statics = statics;
	}
}
