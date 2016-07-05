package com.bhu.vas.api.rpc.user.dto;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.rpc.user.vto.UserOAuthStateVTO;
import com.smartwork.msip.cores.helper.encrypt.Base64Helper;

/**
 * OAuth用户数据DTO
 * @author Edmond Lee
 *
 */
@SuppressWarnings("serial")
public class UserOAuthStateDTO implements java.io.Serializable{
	//如果是微信 则代表unionid
	private String auid;
	//如果是微信 则代表openid 如果值支付宝 则和auid值相等
	private String openid;
	private String nick;
	private String avatar;
	private String identify;
	
	public String getAuid() {
		return auid;
	}
	public void setAuid(String auid) {
		this.auid = auid;
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
	public String getIdentify() {
		return identify;
	}
	public void setIdentify(String identify) {
		this.identify = identify;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	public UserOAuthStateVTO toVTO(){
		UserOAuthStateVTO vto = new UserOAuthStateVTO();
		vto.setAuid(auid);
		vto.setAvatar(avatar);
		vto.setIdentify(identify);
		if(StringUtils.isNotEmpty(nick)){
			try{
				vto.setNick(new String(Base64Helper.decode(nick)));
			}catch(Exception ex){
				vto.setNick(nick);
			}
		}else
			vto.setNick(nick);
		vto.setOpenid(openid);
		return vto;
	}
}
