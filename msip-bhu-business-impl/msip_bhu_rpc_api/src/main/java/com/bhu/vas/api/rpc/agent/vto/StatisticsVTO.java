package com.bhu.vas.api.rpc.agent.vto;

import java.util.Map;

@SuppressWarnings("serial")
public class StatisticsVTO implements java.io.Serializable{
	//revenueCurrentMonth
	private double rcm;
	//revenueLastMonth
	private double rlm;
	//revenueyesterday
	private double ryd;
	//onlinedevices
	private long od;
	//revenuetotal
	private double rtl;
	private Map<String,Double> charts;
	public double getRcm() {
		return rcm;
	}
	public void setRcm(double rcm) {
		this.rcm = rcm;
	}
	public double getRlm() {
		return rlm;
	}
	public void setRlm(double rlm) {
		this.rlm = rlm;
	}
	public double getRyd() {
		return ryd;
	}
	public void setRyd(double ryd) {
		this.ryd = ryd;
	}
	public long getOd() {
		return od;
	}
	public void setOd(long od) {
		this.od = od;
	}
	public double getRtl() {
		return rtl;
	}
	public void setRtl(double rtl) {
		this.rtl = rtl;
	}
	public Map<String, Double> getCharts() {
		return charts;
	}
	public void setCharts(Map<String, Double> charts) {
		this.charts = charts;
	}
}