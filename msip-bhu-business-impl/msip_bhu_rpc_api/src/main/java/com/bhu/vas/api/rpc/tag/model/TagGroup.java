package com.bhu.vas.api.rpc.tag.model;

import java.util.Date;

import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseIntModel;

@SuppressWarnings("serial")
public class TagGroup extends BaseIntModel implements IRedisSequenceGenable {
	public static final String DefaultGroupName = "未分组";
	
	/*
	 * 父节点
	 */
	private int pid;
	/*
	 * 
	 */
	private String path;
	
	private String name;
	
	private int children;

	private int device_count;

	private int creator;
	
	private int updator;
	
	private Date created_at;
	
	private Date updated_at;

	public TagGroup() {
		super();
	}

	public TagGroup(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getDevice_count() {
		return device_count;
	}

	public void setDevice_count(int device_count) {
		this.device_count = device_count;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getChildren() {
		return children;
	}

	public void setChildren(int children) {
		this.children = children;
	}

	public int getCreator() {
		return creator;
	}

	public void setCreator(int creator) {
		this.creator = creator;
	}

	public int getUpdator() {
		return updator;
	}

	public void setUpdator(int updator) {
		this.updator = updator;
	}

	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}

	@Override
	public void preUpdate() {
		this.updated_at = new Date();
		super.preUpdate();
	}

	@Override
	public void setSequenceKey(Long key) {
		this.setId(key.intValue());
	}
	
	public String getPath2ES(){
		
		String[] Parents = this.getPath().split("/");
		
		StringBuilder sb = new StringBuilder();
		for(String path : Parents){
			sb.append("g_").append(path).append(" ");
		}
		return sb.toString();
	}
}
