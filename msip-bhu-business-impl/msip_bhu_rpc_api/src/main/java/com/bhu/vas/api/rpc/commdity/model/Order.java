package com.bhu.vas.api.rpc.commdity.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
@SuppressWarnings("serial")
public class Order extends BaseIntModel{
	
	private Integer itemid;
	
	private Date created_at;
	public Order() {
		super();
	}
	/*public User(String email, String plainpwd) {
		super();
		this.email = email;
		this.plainpwd = plainpwd;
	}*/	

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public Integer getItemid() {
		return itemid;
	}

	public void setItemid(Integer itemid) {
		this.itemid = itemid;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

}
