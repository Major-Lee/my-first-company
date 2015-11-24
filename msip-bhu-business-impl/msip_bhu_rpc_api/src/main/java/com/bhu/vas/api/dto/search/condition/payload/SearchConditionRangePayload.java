package com.bhu.vas.api.dto.search.condition.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * 对于range方式条件匹配的扩展condition payload类
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionRangePayload implements java.io.Serializable{
	//大于,大于等于对应的属性
	@JsonProperty("gtv")
	private String greaterThanValue;
	//小于,小于等于对应的属性
	@JsonProperty("ltv")
	private String lessThanValue;
	
	public String getGreaterThanValue() {
		return greaterThanValue;
	}
	public void setGreaterThanValue(String greaterThanValue) {
		this.greaterThanValue = greaterThanValue;
	}
	public String getLessThanValue() {
		return lessThanValue;
	}
	public void setLessThanValue(String lessThanValue) {
		this.lessThanValue = lessThanValue;
	}
	
	public static SearchConditionRangePayload buildRangGreaterPayload(String gtv){
		SearchConditionRangePayload rangePayload = new SearchConditionRangePayload();
		rangePayload.setGreaterThanValue(gtv);
		return rangePayload;
	}
	
	public static SearchConditionRangePayload buildRangLessPayload(String ltv){
		SearchConditionRangePayload rangePayload = new SearchConditionRangePayload();
		rangePayload.setLessThanValue(ltv);
		return rangePayload;
	}
	
	public static SearchConditionRangePayload buildRangBetweenPayload(String gtv, String ltv){
		SearchConditionRangePayload rangePayload = new SearchConditionRangePayload();
		rangePayload.setGreaterThanValue(gtv);
		rangePayload.setLessThanValue(ltv);
		return rangePayload;
	}
	
}
