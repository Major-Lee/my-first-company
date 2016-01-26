package com.bhu.vas.business.search.core.condition.component;

import org.springframework.util.StringUtils;

import com.bhu.vas.business.search.core.exception.SearchQueryValidateException;

/**
 * 搜索引擎的通用多组合条件定义具体类
 * 一个条件对象相当于搜索引擎中一个具体的filter条件
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SearchCondition extends SearchConditionLogic implements ICondition{
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
	
	@Override
	public void check() throws SearchQueryValidateException {
		//if(StringUtils.isEmpty(key) || StringUtils.isEmpty(pattern))
		if(StringUtils.isEmpty(pattern))
			throw new SearchQueryValidateException(String.format("SearchCondition data illegal key[%s] pattern[%s]", key, pattern));
	}
	
	public static SearchCondition builderSearchCondition(SearchConditionLogicEnumType logic, String key,
			String pattern, String payload){
		SearchCondition condition = new SearchCondition();
		if(logic == null){
			logic = SearchConditionLogicEnumType.Must;
		}
		condition.setLogic(logic.getName());
		condition.setKey(key);
		condition.setPattern(pattern);
		condition.setPayload(payload);
		return condition;
	}
	
	public static SearchCondition builderSearchCondition(String key, String pattern, String payload){
		return builderSearchCondition(null, key, pattern, payload);
	}
	
	public static SearchCondition builderSearchConditionWithAll(){
		return builderSearchCondition(null,SearchConditionPattern.All.getPattern(), null);
	}
	
/*	public static void main(String[] args) {
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
		
	}*/

}
