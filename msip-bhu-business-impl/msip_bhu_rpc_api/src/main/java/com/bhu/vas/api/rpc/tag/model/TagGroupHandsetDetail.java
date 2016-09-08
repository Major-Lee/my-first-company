package com.bhu.vas.api.rpc.tag.model;

import java.io.Serializable;
import java.util.Date;

import com.smartwork.msip.cores.helper.DateHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.model.BaseLongModel;

@SuppressWarnings("serial")
public class TagGroupHandsetDetail extends BaseLongModel implements Serializable{
	
	private String hdmac;
	private int gid;
	private String timestr;
	private String mobileno;
	private boolean newuser;
	private String auth;
	private Date created_at;
	private Date updated_at;
	
	
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getHdmac() {
		return hdmac;
	}
	public void setHdmac(String hdmac) {
		this.hdmac = hdmac;
	}
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}
	public String getTimestr() {
		return timestr;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public boolean isNewuser() {
		return newuser;
	}
	public void setNewuser(boolean newuser) {
		this.newuser = newuser;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		
		if(this.timestr == null)
			this.timestr = DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern7);
		
		if (this.updated_at == null)
			this.updated_at = new Date();
		
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		this.updated_at = new Date();
		super.preUpdate();
	}
}
