package com.bhu.vas.api.feedback.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
/**
 * 移动端错误反馈
 * @author lawliet
 */
@SuppressWarnings("serial")
public class HandsetFeedback extends BaseIntModel{

	private Integer uid;
	//设备
	private String device;
	//设备详细描述
	private String device_detail;
	//客户端app版本
	private String pv;
	//详细错误内容, 线索，蛛丝马迹
	private String clue;
	
	private Date created_at;
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	@Override
	public void preUpdate() {
		super.preUpdate();
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getDevice_detail() {
		return device_detail;
	}
	public void setDevice_detail(String device_detail) {
		this.device_detail = device_detail;
	}
	public String getPv() {
		return pv;
	}
	public void setPv(String pv) {
		this.pv = pv;
	}
	public String getClue() {
		return clue;
	}
	public void setClue(String clue) {
		this.clue = clue;
	}
}
