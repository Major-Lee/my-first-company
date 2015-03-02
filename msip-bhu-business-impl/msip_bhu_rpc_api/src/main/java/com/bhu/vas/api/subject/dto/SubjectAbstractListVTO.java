package com.bhu.vas.api.subject.dto;

import java.util.List;

/**
 * 用于摘要列表api返回的原文摘要和自定义摘要列表
 * @author lawliet
 *
 */
public class SubjectAbstractListVTO {
//	private String estimate; //当前用户对此文章的星级评价 没有评价过的默认为null
	private SubjectListVTO subject;
	private long estimate_c;//评星的人的数量
	private List<Integer> up_users; //顶过此文章的用户id 前10个
	private SubjectAbstractVTO fabt;//原文摘要
	private Object abts;//后续自定义分享的摘要
	
	public SubjectAbstractVTO getFabt() {
		return fabt;
	}
	public void setFabt(SubjectAbstractVTO fabt) {
		this.fabt = fabt;
	}
	public Object getAbts() {
		return abts;
	}
	public void setAbts(Object abts) {
		this.abts = abts;
	}
	public long getEstimate_c() {
		return estimate_c;
	}
	public void setEstimate_c(long estimate_c) {
		this.estimate_c = estimate_c;
	}
	//	public String getEstimate() {
//		return estimate;
//	}
//	public void setEstimate(String estimate) {
//		this.estimate = estimate;
//	}
	public SubjectListVTO getSubject() {
		return subject;
	}
	public List<Integer> getUp_users() {
		return up_users;
	}
	public void setUp_users(List<Integer> up_users) {
		this.up_users = up_users;
	}
	public void setSubject(SubjectListVTO subject) {
		this.subject = subject;
	}
}