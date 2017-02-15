package com.bhu.vas.api.vto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdCommentsVTO implements java.io.Serializable{
	private String adid;
	private List<AdCommentVTO> comments;
	private boolean sign = false;
	private int comment_sum;
	
	public String getAdid() {
		return adid;
	}
	public void setAdid(String adid) {
		this.adid = adid;
	}
	public List<AdCommentVTO> getComments() {
		return comments;
	}
	public void setComments(List<AdCommentVTO> comments) {
		this.comments = comments;
	}
	public boolean isSign() {
		return sign;
	}
	public void setSign(boolean sign) {
		this.sign = sign;
	}
	public int getComment_sum() {
		return comment_sum;
	}
	public void setComment_sum(int comment_sum) {
		this.comment_sum = comment_sum;
	}
}
