package com.bhu.vas.api.dto.search.condition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.bhu.vas.api.dto.search.condition.payload.SearchConditionRangePayload;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 搜索引擎的通用多组合条件定义类
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchCondition implements java.io.Serializable{
	//搜索引擎中对应的索引字段名称
	private String key;
	//条件匹配方式
	private String pattern;
	//条件匹配payoad
	private String payload;
	
	public SearchCondition(){
		
	}
	
	public SearchCondition(String key, String pattern, String payload){
		this.key = key;
		this.pattern = pattern;
		this.payload = payload;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SearchCondition sc1 = new SearchCondition();
		sc1.setKey("sn");
		sc1.setPattern(SearchConditionPattern.Contain.getPattern());
		sc1.setPayload("102");
		
		SearchCondition sc2 = new SearchCondition();
		sc2.setKey("mac");
		sc2.setPattern(SearchConditionPattern.Between.getPattern());
		
		SearchConditionRangePayload range_payload = new SearchConditionRangePayload();
		range_payload.setGreaterThanValue("1");
		range_payload.setLessThanValue("10");
		
		String range_payload_json = JsonHelper.getJSONString(range_payload);
		sc2.setPayload(range_payload_json);
		
		List<SearchCondition> sclist = new ArrayList<SearchCondition>();
		sclist.add(sc1);
		sclist.add(sc2);
		
		String json = JsonHelper.getJSONString(sclist);
		System.out.println(json);
		
		sclist = JsonHelper.getDTOList(json, SearchCondition.class);
		for(SearchCondition sc : sclist){
			System.out.println(sc.getPayload());
		}
//		System.out.println(SearchConditionPattern.Contain.equals(SearchConditionPattern.Contain));
//		SearchConditionPattern type = SearchConditionPattern.Contain;
//		switch(type){
//			case Equal:
//				System.out.println("1");
//				break;
//			case Contain:
//				System.out.println("2");
//				break;
//			default:
//				System.out.println("0");
//		}
		
	}
}
