package com.smartwork.im.message.u2u;


public class ChatReceiptMessage{
	//逗号分割
	private String cids;
	private long ts;
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public String getCids() {
		return cids;
	}
	public void setCids(String cids) {
		this.cids = cids;
	}
	
}
