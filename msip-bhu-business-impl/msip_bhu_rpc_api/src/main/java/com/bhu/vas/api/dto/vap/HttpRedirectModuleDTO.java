package com.bhu.vas.api.dto.vap;

public class HttpRedirectModuleDTO extends ModuleDTO{
	//rule="**(TBD)"
	private String rule;
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	@Override
	public String getType() {
		return ModuleDTO.Type_Redirect;
	}
}
