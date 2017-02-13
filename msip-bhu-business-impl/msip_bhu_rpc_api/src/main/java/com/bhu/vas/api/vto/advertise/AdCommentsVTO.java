package com.bhu.vas.api.vto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdCommentsVTO implements java.io.Serializable{
	private String adid;
	private List<AdCommentVTO> comments;
	
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
}
