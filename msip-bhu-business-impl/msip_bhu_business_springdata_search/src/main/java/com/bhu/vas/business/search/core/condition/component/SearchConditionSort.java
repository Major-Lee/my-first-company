package com.bhu.vas.business.search.core.condition.component;

import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.StringUtils;

import com.bhu.vas.business.search.core.exception.SearchQueryValidateException;


/**
 * 搜索引擎的通用多组合条件定义排序类
 * 一个条件对象相当于搜索引擎中一个具体的sort条件
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchConditionSort implements java.io.Serializable, ICondition{
	//搜索引擎中对应的索引字段名称
	private String key;
	//排序条件
	private String pattern;
	//排序方式
	private String order;
	//特殊排序条件需附带
	private String payload;

	public SearchConditionSort(){
		
	}
	
	public SearchConditionSort(String key, String pattern, String order, String payload){
		this.key = key;
		this.pattern = pattern;
		this.order = order;
		this.payload = payload;
	}
	
	@Override
	public void check() throws SearchQueryValidateException {
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(pattern))
			throw new SearchQueryValidateException(String.format("SearchConditionSort data illegal key[%s] pattern[%s]", key, pattern));
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
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public static SearchConditionSort builderSearchConditionSort(String key, String pattern, 
			SortOrder sortOrder, String payload){
		SearchConditionSort conditionSort = new SearchConditionSort();
		conditionSort.setKey(key);
		conditionSort.setPattern(pattern);
		if(sortOrder != null){
			conditionSort.setOrder(sortOrder.name());
		}
		conditionSort.setPayload(payload);
		return conditionSort;
	}
	
	public static SearchConditionSort builderSearchConditionSort(String key, String pattern, String payload){
		return builderSearchConditionSort(key, pattern, null, payload);
	}
}
