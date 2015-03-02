package com.bhu.vas.api.subject.dto;

public class URLStatisticsDTO {
	private String url;
	private String md5;
	private long urlview;
	private long share;
	private double estimate;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public double getEstimate() {
		return estimate;
	}
	public void setEstimate(double estimate) {
		this.estimate = estimate;
	}
	public long getUrlview() {
		return urlview;
	}
	public void setUrlview(long urlview) {
		this.urlview = urlview;
	}
	public long getShare() {
		return share;
	}
	public void setShare(long share) {
		this.share = share;
	}
	
	
}
