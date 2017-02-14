package com.bhu.vas.api.vto.advertise;

@SuppressWarnings("serial")
public class AdCommentVTO implements java.io.Serializable{
	private int uid;
	private String comment;
	private String reply;
	private double time;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
}
