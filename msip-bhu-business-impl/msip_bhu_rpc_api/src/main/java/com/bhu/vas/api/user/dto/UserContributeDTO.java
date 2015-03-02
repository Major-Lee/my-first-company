package com.bhu.vas.api.user.dto;
/**
 * 用户贡献DTO
 * @author lawliet
 *
 */
public class UserContributeDTO {
	private int uid;
	private long share;//分享次数
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public long getShare() {
		return share;
	}
	public void setShare(long share) {
		this.share = share;
	}
}
