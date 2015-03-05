package com.smartwork.im.message.u2u;

/**
 * 答题复活通知消息
 *  题id 题title 题用户nick 答题者uid 答题者nick 答题者avatar
 * @author Edmond
 *
 */
public class NotifyTopicReviveMessage {
	/*private String from;
	private String to;*/
	//topic id
	private String tid;
	//topic owner nick
	private String tn;
	//topic title
	private String t;
	//答题是否正确
	private boolean c;
	
	//答题者uid
	private String auid;
	//答题者nick
	private String an;
	//答题者avatar
	private String av;
	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public boolean isC() {
		return c;
	}

	public void setC(boolean c) {
		this.c = c;
	}

	public String getTn() {
		return tn;
	}

	public void setTn(String tn) {
		this.tn = tn;
	}

	public String getAuid() {
		return auid;
	}

	public void setAuid(String auid) {
		this.auid = auid;
	}

	public String getAn() {
		return an;
	}

	public void setAn(String an) {
		this.an = an;
	}

	public String getAv() {
		return av;
	}

	public void setAv(String av) {
		this.av = av;
	}
	
}
