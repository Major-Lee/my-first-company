package com.smartwork.im.message.u2u;

/**
 * 答题通知消息
 *  题id 题title 题用户nick 答题者uid 答题者nick 答题者avatar
 * @author Edmond
 *
 */
public class NotifyTopicAnswerMessage {
	/*private String from;
	private String to;*/
	//topic id
	private String tid;
	private String t;
	//topic owner nick
	private String tn;
	//答题者uid
	private String auid;
	//答题者nick
	private String an;
	//答题者avatar
	private String av;
	//private String 
	//topic title
	
	//答题是否正确
	private boolean c;
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
