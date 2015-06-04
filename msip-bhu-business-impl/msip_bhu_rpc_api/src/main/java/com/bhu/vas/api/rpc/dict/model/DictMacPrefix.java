package com.bhu.vas.api.rpc.dict.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseStringModel;


/**
 * 注意：元素标签，新元素标签 色素标签
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class DictMacPrefix extends BaseStringModel {
	private String name;
	private String alias_names;
	private String class_name;
	private String synonyms;
	private String org;
	private String address;
	private boolean visibility = true;
	private Date created_at;
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias_names() {
		return alias_names;
	}

	public void setAlias_names(String alias_names) {
		this.alias_names = alias_names;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(String synonyms) {
		this.synonyms = synonyms;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}
	
	
}
