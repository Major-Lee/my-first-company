package com.bhu.vas.api.rpc.tag.model;

import java.io.Serializable;
import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

@SuppressWarnings("serial")
public class TagName extends BaseLongModel implements Serializable{
	/**
	 * 标签名
	 */
	private String tag;
	
	/**
	 * 当前操作用户
	 */
	private int operator; 
	
	private Date created_at;
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	@Override
	public void preInsert() {
		if (created_at == null) {
			this.created_at = new Date();
		}
		super.preInsert();
	}
}
