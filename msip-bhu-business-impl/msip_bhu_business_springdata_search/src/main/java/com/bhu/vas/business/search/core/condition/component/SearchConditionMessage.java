package com.bhu.vas.business.search.core.condition.component;

import java.util.ArrayList;
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
	private List<SearchConditionPack> searchConditionPacks;
	@JsonProperty("search_ss")
	private List<SearchConditionSort> searchConditionSorts;
	
	public SearchConditionMessage(){
		
	}
	
	public SearchConditionMessage(List<SearchConditionPack> searchConditionPacks){
		this.searchConditionPacks = searchConditionPacks;
	}
	
	public SearchConditionMessage(byte searchType, List<SearchConditionPack> searchConditionPacks){
		this.searchType = searchType;
		this.searchConditionPacks = searchConditionPacks;
	}

	public List<SearchConditionPack> getSearchConditionPacks() {
		return searchConditionPacks;
	}
	public void setSearchConditionPacks(List<SearchConditionPack> searchConditionPacks) {
		this.searchConditionPacks = searchConditionPacks;
	}
	
	public void addPacks(SearchConditionPack... packs){
		if(packs == null || packs.length == 0) return;
		
		if(searchConditionPacks == null) 
			searchConditionPacks = new ArrayList<SearchConditionPack>();
		searchConditionPacks.addAll(ArrayHelper.toList(packs));
	}
	
	public List<SearchConditionSort> getSearchConditionSorts() {
		return searchConditionSorts;
	}
	public void setSearchConditionSorts(List<SearchConditionSort> searchConditionSorts) {
		this.searchConditionSorts = searchConditionSorts;
	}
	
	public void addSorts(SearchConditionSort... sorts){
		if(sorts == null || sorts.length == 0) return;
		
		if(searchConditionSorts == null) 
			searchConditionSorts = new ArrayList<SearchConditionSort>();
		searchConditionSorts.addAll(ArrayHelper.toList(sorts));
	}
	
	public byte getSearchType() {
		return searchType;
	}
	public void setSearchType(byte searchType) {
		this.searchType = searchType;
	}
	
	public static SearchConditionMessage builderSearchConditionMessage(){
		return new SearchConditionMessage();
	}
	
	public static SearchConditionMessage builderSearchConditionMessage(SearchConditionPack... packs){
		return builderSearchConditionMessage(null, packs);
	}
	
	public static SearchConditionMessage builderSearchConditionMessage(Byte searchType, SearchConditionPack... packs){
		SearchConditionMessage searchConditionMessage = new SearchConditionMessage();
		if(searchType != null){
			searchConditionMessage.setSearchType(searchType);
		}
		if(packs != null){
			searchConditionMessage.setSearchConditionPacks(ArrayHelper.toList(packs));
		}
		return searchConditionMessage;
	}
}
