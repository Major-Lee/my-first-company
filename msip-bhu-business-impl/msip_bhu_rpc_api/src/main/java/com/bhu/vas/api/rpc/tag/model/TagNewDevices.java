package com.bhu.vas.api.rpc.tag.model;

import java.util.Date;
import java.util.Set;

import com.smartwork.msip.cores.orm.model.extjson.SetJsonExtStringModel;

@SuppressWarnings("serial")
public class TagNewDevices extends SetJsonExtStringModel<String> {
	
	/**
	 * 标签名
	 */
	private String tag;
	private Date created_at;
	private Date update_at;
	
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdate_at() {
		return update_at;
	}
	public void setUpdate_at(Date update_at) {
		this.update_at = update_at;
	}
	
	public Set<String> fetchTags(String tag){
		return this.getInnerModels();
	}
	public boolean addTag(String tag){
		boolean ret = this.putInnerModel(tag);
		return ret;
	}
	
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
	
	
}
