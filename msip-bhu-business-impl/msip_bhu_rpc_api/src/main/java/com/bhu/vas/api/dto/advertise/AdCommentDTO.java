package com.bhu.vas.api.dto.advertise;

@SuppressWarnings("serial")
public class AdCommentDTO implements java.io.Serializable{
	private int uid;
	private String comment;
	private String reply;
	
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
}
