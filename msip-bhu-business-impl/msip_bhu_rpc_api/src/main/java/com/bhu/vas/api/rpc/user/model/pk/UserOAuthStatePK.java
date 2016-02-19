package com.bhu.vas.api.rpc.user.model.pk;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserOAuthStatePK implements Serializable {
	private int uid;
	private String identify;
	
	public UserOAuthStatePK(){
	}
	public UserOAuthStatePK(int uid,String identify){
		this.uid = uid;
		this.identify = identify;
	}
	@Override
	public String toString() {
		return uid+"-"+identify;
	}
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getIdentify() {
		return identify;
	}
	public void setIdentify(String identify) {
		this.identify = identify;
	}
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof UserOAuthStatePK){
			UserOAuthStatePK oo = (UserOAuthStatePK)o;
			return (this.uid == oo.uid && this.identify.equals(oo.identify));
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
}
