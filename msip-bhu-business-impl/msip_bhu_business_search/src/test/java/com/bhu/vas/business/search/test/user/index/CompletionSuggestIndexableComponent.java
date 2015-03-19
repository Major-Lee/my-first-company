package com.bhu.vas.business.search.test.user.index;

import com.smartwork.msip.es.index.IndexableComponent;
import com.smartwork.msip.es.index.field.CompletionSuggestIndexableField;
/** 
 * 用于测试completion suggest搜索的数据模型
 * @author lawliet
 *
 */
public class CompletionSuggestIndexableComponent extends IndexableComponent{
	
	private String id;
	private String name;
	private CompletionSuggestIndexableField name_suggest;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CompletionSuggestIndexableField getName_suggest() {
		return name_suggest;
	}
	public void setName_suggest(CompletionSuggestIndexableField name_suggest) {
		this.name_suggest = name_suggest;
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
