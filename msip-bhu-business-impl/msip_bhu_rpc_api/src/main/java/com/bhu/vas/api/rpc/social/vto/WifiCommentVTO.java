package com.bhu.vas.api.rpc.social.vto;

import java.io.Serializable;
/**
 * 
 * @author meng008
 * @ WIFI评论VTO
 *
 */
@SuppressWarnings("serial")
public class WifiCommentVTO implements Serializable{
	 //用户uid
	 private long uid;
     //wifi mac地址
	 private String bssid;
     //wifi评论内容
	 private String message;
     //评论建立时间
	 private String created_at;
	 //评论修改时间
	 //private String update_at;
	 //昵称
	 private String nick;
	 //头像
	 private String avatar;
	public long getUid() {
		return uid;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	
	
}