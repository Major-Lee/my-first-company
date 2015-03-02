package com.bhu.vas.api.subject.model;

import com.smartwork.msip.business.abstractmsd.click.multifieldclick.model.AbstractPKMultiFieldClick;

@SuppressWarnings("serial")
public class UrlViewClick extends AbstractPKMultiFieldClick<String> {
	public static final String Field_UrlView = "urlview";
	private long urlview;
	public UrlViewClick() {
		super();
	}
	public UrlViewClick(String id) {
		super(id);
	}
	public long getUrlview() {
		return urlview;
	}
	public void setUrlview(long urlview) {
		this.urlview = urlview;
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
