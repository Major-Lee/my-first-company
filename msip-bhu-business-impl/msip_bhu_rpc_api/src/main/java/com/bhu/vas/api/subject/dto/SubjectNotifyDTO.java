package com.bhu.vas.api.subject.dto;

import com.bhu.vas.api.subject.model.Subject;

public class SubjectNotifyDTO {
	
	private int uid; 
	private int sid;
	private String uuid;
	private String subject_code;
	private String title;
	private String type;
	private String authors;
	private String year;
	private String source;
	private String details;//暂时先用string接收 后续去掉
	private String orginal_abstract;
	private String custom_abstract;
	private String url;
	private long ts;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getSubject_code() {
		return subject_code;
	}
	public void setSubject_code(String subject_code) {
		this.subject_code = subject_code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getAuthors() {
		return authors;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getOrginal_abstract() {
		return orginal_abstract;
	}
	public void setOrginal_abstract(String orginal_abstract) {
		this.orginal_abstract = orginal_abstract;
	}
	public String getCustom_abstract() {
		return custom_abstract;
	}
	public void setCustom_abstract(String custom_abstract) {
		this.custom_abstract = custom_abstract;
	}
	
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	/**
	 * 使用DTO进行文章实体数据填充, 并返回文章实体
	 * @param subjectDto
	 * @return
	 */
	public static SubjectNotifyDTO fromSubject(Subject subject){
		SubjectNotifyDTO dto = new SubjectNotifyDTO();
		dto.setSid(subject.getId());
		dto.setUid(subject.getUid());
		dto.setUuid(subject.getUuid());
		dto.setSubject_code(subject.getSubject_code());
		dto.setTitle(subject.getTitle());
		dto.setType(subject.getType());
		dto.setAuthors(subject.getAuthors());
		dto.setYear(subject.getYear());
		dto.setSource(subject.getSource());
		dto.setUrl(subject.getUrl());
		dto.setTs(subject.getCreated_at().getTime());
		dto.setOrginal_abstract(subject.getOriginal_abstract());
		return dto;
	}
}
