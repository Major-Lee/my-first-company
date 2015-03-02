package com.bhu.vas.api.subject.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;

@SuppressWarnings("serial")
public class Subject extends BaseIntModel {//implements ISequenceGenable{
	public static final int VisibleState_Normal = 1;//正常
	public static final int VisibleState_Uncheck = 2;//未审核
	public static final int VisibleState_Inblack = 3;//黑名单列表
	public static final int VisibleState_Weixin = 10;//微信二维码
	
	public static final int HasTaged = 1;//包含系统标签
	public static final int NoTaged = 0;//不包含系统标签
	
	private int uid;
	private String uuid;
	private String subject_code;
	private String title;
	private String type;
	private String authors;
	private String year;
	private String source;
	private String nedb_uuid;
	private String details;
	private String url_md5;
	private String url;
	private String original_abstract;
	private int taged;//是否有系统tag (不计算第三方tag)
	//private String custom_abstract;
	private int visible_state = VisibleState_Normal; //头条可见状态
	private String image;
	private double estimate; 
	private String third_tags;//第三方网站提交的tags 
	private String domain; //第三方网站域名
	private Date created_at;

	public Subject() {
	}

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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getNedb_uuid() {
		return nedb_uuid;
	}

	public void setNedb_uuid(String nedb_uuid) {
		this.nedb_uuid = nedb_uuid;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getUrl_md5() {
		return url_md5;
	}

	public void setUrl_md5(String url_md5) {
		this.url_md5 = url_md5;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOriginal_abstract() {
		return original_abstract;
	}

	public void setOriginal_abstract(String original_abstract) {
		this.original_abstract = original_abstract;
	}

	public int getTaged() {
		return taged;
	}

	public void setTaged(int taged) {
		this.taged = taged;
	}

	public int getVisible_state() {
		return visible_state;
	}

	public void setVisible_state(int visible_state) {
		this.visible_state = visible_state;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public double getEstimate() {
		return estimate;
	}

	public void setEstimate(double estimate) {
		this.estimate = estimate;
	}

	public String getThird_tags() {
		return third_tags;
	}

	public void setThird_tags(String third_tags) {
		this.third_tags = third_tags;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	/*@Override
	public void setSequenceKey(Integer key) {
		this.setId(key);
	}*/
}
