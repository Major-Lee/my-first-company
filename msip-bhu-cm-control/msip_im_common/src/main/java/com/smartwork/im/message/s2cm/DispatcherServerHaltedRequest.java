package com.smartwork.im.message.s2cm;

public class DispatcherServerHaltedRequest {
	//dispathcer server mark
	private String serverMark;
	private long ts;
	public String getServerMark() {
		return serverMark;
	}
	public void setServerMark(String serverMark) {
		this.serverMark = serverMark;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
}
