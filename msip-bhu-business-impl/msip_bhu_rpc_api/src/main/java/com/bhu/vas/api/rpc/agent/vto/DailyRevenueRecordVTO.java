package com.bhu.vas.api.rpc.agent.vto;

@SuppressWarnings("serial")
public class DailyRevenueRecordVTO implements java.io.Serializable{
	private int index;
	private String date;
	//Revenue
	private String r;
	//当日在线总设备数onlinedevices
	private long od;
	//当日上线终端总数
	private long oh;
	//compare 相较上月同日收益比较百分比 正负 n%  （currentR-lastR）/lastR
	private String c;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getR() {
		return r;
	}
	public void setR(String r) {
		this.r = r;
	}
	public long getOd() {
		return od;
	}
	public void setOd(long od) {
		this.od = od;
	}
	public long getOh() {
		return oh;
	}
	public void setOh(long oh) {
		this.oh = oh;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
}
