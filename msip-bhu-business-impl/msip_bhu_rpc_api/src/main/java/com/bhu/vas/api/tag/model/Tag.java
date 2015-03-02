package com.bhu.vas.api.tag.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
/**
 * 文章tag实体
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class Tag extends BaseIntModel{
	private String name;//tag名称
	private int pid;//父级id
	private String path;//用于方便查询的path结构 
	private int uid; //创建用户id
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
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}
