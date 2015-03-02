package com.bhu.vas.api.subject.model;

import com.smartwork.msip.business.abstractmsd.click.multifieldclick.model.AbstractPKMultiFieldClick;

@SuppressWarnings("serial")
public class DomainUrlClick extends AbstractPKMultiFieldClick<String> {
	public static final String Field_UrlView = "urlview";
	public static final String Field_Share = "share";
	private long urlview;
	private long share;
	public DomainUrlClick() {
		super();
	}
	public DomainUrlClick(String id) {
		super(id);
	}
	public long getUrlview() {
		return urlview;
	}
	public void setUrlview(long urlview) {
		this.urlview = urlview;
	}
	public long getShare() {
		return share;
	}
	public void setShare(long share) {
		this.share = share;
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
