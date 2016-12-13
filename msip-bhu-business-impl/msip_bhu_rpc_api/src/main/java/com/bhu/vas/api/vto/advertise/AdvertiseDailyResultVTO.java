package com.bhu.vas.api.vto.advertise;

@SuppressWarnings("serial")
public class AdvertiseDailyResultVTO implements java.io.Serializable{
	private String date;
	private int adApplyCount;
	private int adPV;
	private int adUV;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getAdApplyCount() {
		return adApplyCount;
	}
	public void setAdApplyCount(int adApplyCount) {
		this.adApplyCount = adApplyCount;
	}
	public int getAdPV() {
		return adPV;
	}
	public void setAdPV(int adPV) {
		this.adPV = adPV;
	}
	public int getAdUV() {
		return adUV;
	}
	public void setAdUV(int adUV) {
		this.adUV = adUV;
	}
}
