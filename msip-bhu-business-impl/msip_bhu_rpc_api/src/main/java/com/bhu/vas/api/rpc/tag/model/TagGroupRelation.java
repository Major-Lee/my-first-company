package com.bhu.vas.api.rpc.tag.model;

import java.io.Serializable;
import java.util.Date;
import com.smartwork.msip.cores.orm.model.BaseIntModel;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class TagGroupRelation extends BaseStringModel implements Serializable{
	
	/*
	 * 父节点
	 */
	private int gid;
	/*
	 * 
	 */
	private int uid;
	
	private String path;
	
	private Date created_at;
	private Date updated_at;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
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
	
	public String getPath2ES(){
		
		String[] Parents = this.getPath().split("/");
		
		StringBuilder sb = new StringBuilder();
		for(String path : Parents){
			sb.append("g_").append(path).append(" ");
		}
		return sb.toString();
	}
}
