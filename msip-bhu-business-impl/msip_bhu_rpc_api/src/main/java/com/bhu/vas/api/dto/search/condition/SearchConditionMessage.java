package com.bhu.vas.api.dto.search.condition;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartwork.msip.cores.helper.ArrayHelper;

/**
 * 用于Search Condition接口的交互消息定义类
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionMessage implements java.io.Serializable{	
	/**
	 * 搜索模式 这个byte值与ES的SearchType类中的定义吻合 具体可查SearchType类 
	 * 默认为1
	 * 	
	 	0=DFS_QUERY_THEN_FETCH
		1=QUERY_THEN_FETCH 
		2=DFS_QUERY_AND_FETCH
		3=QUERY_AND_FETCH
		4=SCAN=SCAN
		5=COUNT=COUNT
	 */
	@JsonProperty("search_t")
	private byte searchType = 1;
	//搜索具体条件列表(匹配，排序)
	@JsonProperty("search_cs")
	private List<SearchCondition> searchConditions;

	
	public SearchConditionMessage(){
		
	}
	
	public SearchConditionMessage(List<SearchCondition> searchConditions){
		this.searchConditions = searchConditions;
	}
	
	public SearchConditionMessage(byte searchType, List<SearchCondition> searchConditions){
		this.searchType = searchType;
		this.searchConditions = searchConditions;
	}
	
	public List<SearchCondition> getSearchConditions() {
		return searchConditions;
	}
	public void setSearchConditions(List<SearchCondition> searchConditions) {
		this.searchConditions = searchConditions;
	}
	public byte getSearchType() {
		return searchType;
	}
	public void setSearchType(byte searchType) {
		this.searchType = searchType;
	}
	
	public static SearchConditionMessage builderSearchConditionMessage(SearchCondition... conditions){
		return builderSearchConditionMessage(null,conditions);
	}
	
	public static SearchConditionMessage builderSearchConditionMessage(Byte searchType, SearchCondition... conditions){
		SearchConditionMessage searchConditionMessage = new SearchConditionMessage();
		if(searchType != null){
			searchConditionMessage.setSearchType(searchType);
		}
		if(conditions != null){
			searchConditionMessage.setSearchConditions(ArrayHelper.toList(conditions));
		}
		return searchConditionMessage;
	}
}
