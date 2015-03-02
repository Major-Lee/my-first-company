package com.bhu.vas.api.subject.dto;

import java.util.List;

import com.bhu.vas.api.subject.model.Subject;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.MD5Helper;

public class SubjectDTO {
	public static final String Type_Share = "1";
	public static final String Type_Save = "0";
	
	private int uid; 
	private String uuid;
	private String subject_code;
	private String title;
	private String type;
	private String author;
	private String year;
	private String source;
	private String details;//暂时先用string接收 后续去掉
	private SubjectDetailDTO detail;
	private String url;
	private List<String> third_tags;
	
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
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
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
	public SubjectDetailDTO getDetail() {
		return detail;
	}
	public void setDetail(SubjectDetailDTO detail) {
		this.detail = detail;
	}
	public List<String> getThird_tags() {
		return third_tags;
	}
	public void setThird_tags(List<String> third_tags) {
		this.third_tags = third_tags;
	}
	public String getThirdTagsAppend(){
		if(third_tags == null || third_tags.isEmpty()){
			return null;
		}
		StringBuffer tag_sb = new StringBuffer();
		for(String tag : third_tags){
			if(tag_sb.length() > 0){
				tag_sb.append(StringHelper.WHITESPACE_STRING_GAP);
			}
			tag_sb.append(tag);
		}
		return tag_sb.toString();
	}
	/**
	 * 使用DTO进行文章实体数据填充, 并返回文章实体
	 * @param subjectDto
	 * @return
	 */
	public Subject injectionSubjectPropertyWithDTO(){
		Subject entity = new Subject();
		entity.setUid(this.getUid());
		entity.setUuid(this.getUuid());
		entity.setSubject_code(this.getSubject_code());
		entity.setTitle(this.getTitle());
		entity.setType(this.getType());
		entity.setAuthors(this.getAuthor());
		entity.setYear(this.getYear());
		entity.setSource(this.getSource());
		entity.setUrl_md5(MD5Helper.md5(this.getUrl()));
		entity.setUrl(this.getUrl());
		entity.setDomain(StringHelper.parseDomain(this.getUrl().toLowerCase()));
		if(StringHelper.isNotEmpty(this.getDetail().getOriginalAbstract())){
			entity.setOriginal_abstract(this.getDetail().getOriginalAbstract());
		}else if(StringHelper.isNotEmpty(this.getDetail().getCustomAbstract())){
			entity.setOriginal_abstract(this.getDetail().getCustomAbstract());
		}
		entity.setThird_tags(this.getThirdTagsAppend());
		entity.setImage(this.getDetail().getImage());
		return entity;
	}
}
