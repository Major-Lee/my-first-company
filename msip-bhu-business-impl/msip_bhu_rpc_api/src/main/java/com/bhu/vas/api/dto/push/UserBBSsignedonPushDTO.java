package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;

/**
 * 用户bbs登录push dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class UserBBSsignedonPushDTO extends PushDTO{
//	private int uid;
//	private int countrycode;
//	private String acc;
	private String secretkey;
	
/*	public int getUid() {
		return uid;
	}


	public void setUid(int uid) {
		this.uid = uid;
	}


	public int getCountrycode() {
		return countrycode;
	}


	public void setCountrycode(int countrycode) {
		this.countrycode = countrycode;
	}


	public String getAcc() {
		return acc;
	}


	public void setAcc(String acc) {
		this.acc = acc;
	}
*/

	public String getSecretkey() {
		return secretkey;
	}


	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}


	@Override
	public String getPushType() {
		return PushType.UserBBSsignedon.getType();
	}
	
}
