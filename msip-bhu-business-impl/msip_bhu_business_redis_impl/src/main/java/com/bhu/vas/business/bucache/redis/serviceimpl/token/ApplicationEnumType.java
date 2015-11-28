package com.bhu.vas.business.bucache.redis.serviceimpl.token;

/**
 * 用于定义平台的前缀key，支持多平台定义
 * @author Edmond
 *
 */
public enum ApplicationEnumType {
	Default(1,"缺省平台",""),
	Agent(	2,"分销商平台","A."),
	//Value added platform
	Vap(	3,"增值平台","V."),
	;
	private int index;//序号
	private String keyPrefix;//定义的前缀key
	private String name;//灰度名称
	
	private ApplicationEnumType(int index,String name,String keyPrefix){
		this.index = index;
		this.name = name;
		this.keyPrefix = keyPrefix;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getKeyPrefix() {
		return keyPrefix;
	}
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
}
