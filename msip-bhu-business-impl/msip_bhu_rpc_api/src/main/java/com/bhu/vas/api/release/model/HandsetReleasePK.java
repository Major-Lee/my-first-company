package com.bhu.vas.api.release.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HandsetReleasePK implements Serializable {
	private String channelid;
	private String platform;
	
	public HandsetReleasePK(){
	}
	public HandsetReleasePK(String channelid, String platform){
		this.channelid = channelid;
		this.platform = platform;
	}
	public String getChannelid() {
		return channelid;
	}
	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	@Override
	public String toString() {
		return channelid+"-"+platform;
	}
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof HandsetReleasePK){
			HandsetReleasePK oo = (HandsetReleasePK)o;
			return (this.channelid.equals(oo.channelid) && this.platform.equals(oo.platform));
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
}
