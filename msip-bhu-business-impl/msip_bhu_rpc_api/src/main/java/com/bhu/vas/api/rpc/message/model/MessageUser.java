package com.bhu.vas.api.rpc.message.model;

import java.io.Serializable;
import java.util.Date;

import com.smartwork.msip.cores.orm.model.extjson.SetJsonExtStringModel;
/**
 * 2016/11/25
 * @author fengshibo
 *
 */
@SuppressWarnings("serial")
public class MessageUser extends SetJsonExtStringModel<String> implements Serializable{
	private String sig;
	private int sync;
	private Date created_at;
	
	public String getSig() {
		return sig;
	}

	public void setSig(String sig) {
		this.sig = sig;
	}

	public int getSync() {
		return sync;
	}

	public void setSync(int sync) {
		this.sync = sync;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
	
	@Override
	public String getId() {
		return super.getId();
	}
	@Override
	public void setId(String id) {
		super.setId(id);
	}
	
}
