package com.bhu.vas.api.rpc.agent.vto;

import java.util.Map;

@SuppressWarnings("serial")
public class StatisticsVTO implements java.io.Serializable{
	//revenueCurrentMonth
	private String rcm;
	//revenueLastMonth
	private String rlm;
	//revenueyesterday
	private String ryd;
	//onlinedevices
	private String od;
	//revenuetotal
	private String rtl;
	private Map<String,Double> charts;
	
	public String getOd() {
		return od;
	}
	public void setOd(String od) {
		this.od = od;
	}
	public Map<String, Double> getCharts() {
		return charts;
	}
	public void setCharts(Map<String, Double> charts) {
		this.charts = charts;
	}
	public String getRcm() {
		return rcm;
	}
	public void setRcm(String rcm) {
		this.rcm = rcm;
	}
	public String getRlm() {
		return rlm;
	}
	public void setRlm(String rlm) {
		this.rlm = rlm;
	}
	public String getRyd() {
		return ryd;
	}
	public void setRyd(String ryd) {
		this.ryd = ryd;
	}
	public String getRtl() {
		return rtl;
	}
	public void setRtl(String rtl) {
		this.rtl = rtl;
	}
	
	
}