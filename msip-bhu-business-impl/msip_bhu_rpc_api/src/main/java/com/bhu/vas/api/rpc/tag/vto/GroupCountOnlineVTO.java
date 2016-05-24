package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GroupCountOnlineVTO implements Serializable {
	private String gid;
	private long online;
	
	public String getGid() {
		return gid;
	}
	public void setGid(String gid2) {
		this.gid = gid2;
	}
	public long getOnline() {
		return online;
	}
	public void setOnline(long l) {
		this.online = l;
	}
}
