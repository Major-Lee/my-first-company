package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
/**
 * 用来保存用户的当前有效的设备
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserMobileDevice extends BaseIntModel{
	//device mac
	private String dm;
	//每台设备的厂商授权token
	private String dt;
	//device type
	private String d;
	//push type
	private String pt;
	private Date created_at;
	
	public UserMobileDevice() {
		super();
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
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

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public String getPt() {
		return pt;
	}

	public void setPt(String pt) {
		this.pt = pt;
	}

	public String getDm() {
		return dm;
	}

	public void setDm(String dm) {
		this.dm = dm;
	}
		
}
