package com.bhu.vas.api.subject.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;


@SuppressWarnings("serial")
public class SubjectUserAbstract extends BaseIntModel{
	private int uid;
	private int subjectid;
	private String custom_abstract;
	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getSubjectid() {
		return subjectid;
	}

	public void setSubjectid(int subjectid) {
		this.subjectid = subjectid;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	
	public String getCustom_abstract() {
		return custom_abstract;
	}

	public void setCustom_abstract(String custom_abstract) {
		this.custom_abstract = custom_abstract;
	}
	
}
