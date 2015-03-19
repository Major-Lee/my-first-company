package com.bhu.vas.business.search.test.user.index;

import com.smartwork.msip.es.index.IndexableComponent;
/**
 * 用于测试multi match query搜索的数据模型
 * @author lawliet
 *
 */
public class MultiMatchIndexableComponent extends IndexableComponent{
	
	private String id;
	private String showname;
	private String name;
	private String alname;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getShowname() {
		return showname;
	}
	public void setShowname(String showname) {
		this.showname = showname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlname() {
		return alname;
	}
	public void setAlname(String alname) {
		this.alname = alname;
	}
	@Override
	public String id() {
		return id;
	}
	@Override
	public String routing() {
		return null;
	}
	
	
}
