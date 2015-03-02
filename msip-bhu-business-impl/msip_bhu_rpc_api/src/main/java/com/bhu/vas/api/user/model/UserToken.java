package com.bhu.vas.api.user.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.business.token.service.TokenServiceHelper;
import com.smartwork.msip.cores.orm.model.BaseIntModel;
@SuppressWarnings("serial")
public class UserToken extends BaseIntModel{
	private String access_token;
	private String refresh_token;
	
	private Date created_at;
	public UserToken() {
		super();
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		if(StringUtils.isEmpty(this.access_token))
			this.access_token = TokenServiceHelper.generateAccessToken4User(this.id.intValue());
		if(StringUtils.isEmpty(this.refresh_token))
			this.refresh_token = TokenServiceHelper.generateRefreshToken4User(this.id.intValue());
		super.preInsert();
	}
	
	/**
	 * 刷新accesstoken时，accesstoken和refreshtoken都会重新刷新
	 */
	public void doTokenRefresh(){
		this.access_token = TokenServiceHelper.generateAccessToken4User(this.id.intValue());
		this.refresh_token = TokenServiceHelper.generateRefreshToken4User(this.id.intValue());
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
		
	
}
