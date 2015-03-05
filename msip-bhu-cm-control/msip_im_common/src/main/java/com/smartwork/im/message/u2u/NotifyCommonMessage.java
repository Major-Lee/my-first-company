package com.smartwork.im.message.u2u;

/**
 * 答题通知消息
 *  题id 题title 题用户nick 答题者uid 答题者nick 答题者avatar
 * @author Edmond
 *
 */
public class NotifyCommonMessage {
	//消息体
	private String body;
	//时间
	private long ts;
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
}
