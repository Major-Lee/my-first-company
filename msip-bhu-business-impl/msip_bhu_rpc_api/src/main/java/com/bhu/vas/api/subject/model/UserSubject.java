package com.bhu.vas.api.subject.model;

import java.util.Date;

import com.smartwork.msip.cores.orm.model.BaseIntModel;
/**
 * 用户的保存的文章实体 
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserSubject extends BaseIntModel {
	private int uid;
	private int sid;
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
	private boolean copy = false; //标记是否为副本
	//private String custom_abstract;
	private String image;
	private Date created_at;

	public UserSubject() {
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public boolean isCopy() {
		return copy;
	}

	public void setCopy(boolean copy) {
		this.copy = copy;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
}
