package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GroupCountOnlineVTO implements Serializable {
	private String gid;
	private long online;
	private long offline;
	private long count;
	
	public long getOffline() {
		return offline;
	}
	public void setOffline(long offline) {
		this.offline = offline;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
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
