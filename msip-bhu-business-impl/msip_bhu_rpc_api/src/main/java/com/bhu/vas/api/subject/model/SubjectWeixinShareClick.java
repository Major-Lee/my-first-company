package com.bhu.vas.api.subject.model;

import java.util.Date;

import com.smartwork.msip.business.abstractmsd.click.multifieldclick.model.AbstractPKMultiFieldClick;

@SuppressWarnings("serial")
public class SubjectWeixinShareClick extends AbstractPKMultiFieldClick<String> {
	public static final int VisibleState_Normal = 1;//正常
//	public static final int VisibleState_Uncheck = 2;//未审核
	public static final int VisibleState_Inblack = 3;//黑名单列表
	
	public static final String Field_Click = "click";
	private long click;
	private String url;
	private String domain;
	private int visible_state;
	private Date created_at;
	
	public SubjectWeixinShareClick() {
		super();
	}
	public SubjectWeixinShareClick(String id) {
		super(id);
	}
	public long getClick() {
		return click;
	}
	public void setClick(long click) {
		this.click = click;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public int getVisible_state() {
		return visible_state;
	}
	public void setVisible_state(int visible_state) {
		this.visible_state = visible_state;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	protected Class<String> getPKClass() {
		return String.class;
	}
	@Override
	public String getId() {
		return super.getId();
	}
	@Override
	public void setId(String id) {
		super.setId(id);
	}
}
