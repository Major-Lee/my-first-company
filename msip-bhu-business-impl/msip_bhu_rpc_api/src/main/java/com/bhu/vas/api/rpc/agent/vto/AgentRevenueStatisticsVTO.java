package com.bhu.vas.api.rpc.agent.vto;

import java.util.Map;

@SuppressWarnings("serial")
public class AgentRevenueStatisticsVTO implements java.io.Serializable{
	//revenueCurrentMonth
	private String rcm;
	//revenueLastMonth
	private String rlm;
	//revenueyesterday
	private String ryd;
	//onlinedevices
	//private String od;
	//revenuetotal
	//private String rtl;
	//Total Revenue 所有设备产生的收入
	private String tr;
	//settled Revenue 除去当月之外的所有被结算金额
	private String sr;
	//unsettle Revenue 除去当月之外的未结算金额 
	private String ur;
	
	private Map<String,Object> charts;
	
	/*public String getOd() {
		return od;
	}
	public void setOd(String od) {
		this.od = od;
	}*/
	
	public String getRcm() {
		return rcm;
	}
	public Map<String, Object> getCharts() {
		return charts;
	}
	public void setCharts(Map<String, Object> charts) {
		this.charts = charts;
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
	/*public String getRtl() {
		return rtl;
	}
	public void setRtl(String rtl) {
		this.rtl = rtl;
	}*/
	public String getTr() {
		return tr;
	}
	public void setTr(String tr) {
		this.tr = tr;
	}
	public String getUr() {
		return ur;
	}
	public void setUr(String ur) {
		this.ur = ur;
	}
	public String getSr() {
		return sr;
	}
	public void setSr(String sr) {
		this.sr = sr;
	}
	
}