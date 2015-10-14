package com.bhu.vas.api.dto.vap;

public class Http404ModuleDTO extends ModuleDTO{
	//codes="404,50*" url="vap.bhunetworks.com/urlwrite"
	private String codes;
	private String url;
	public String getCodes() {
		return codes;
	}
	public void setCodes(String codes) {
		this.codes = codes;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String getType() {
		return ModuleDTO.Type_Http404;
	}
	
}
