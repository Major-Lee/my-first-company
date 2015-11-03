package com.bhu.vas.api.dto.search;

import java.util.ArrayList;
import java.util.List;

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
	private String value;
	
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SearchCondition sc1 = new SearchCondition();
		sc1.setKey("sn");
		sc1.setPattern(SearchConditionPattern.Contain.getPattern());
		sc1.setValue("102");
		
		SearchCondition sc2 = new SearchCondition();
		sc2.setKey("mac");
		sc2.setPattern(SearchConditionPattern.Equal.getPattern());
		sc2.setValue("84:82");
		
		List<SearchCondition> sclist = new ArrayList<SearchCondition>();
		sclist.add(sc1);
		sclist.add(sc2);
		
		System.out.println(JsonHelper.getJSONString(sclist));
		System.out.println(SearchConditionPattern.Contain.equals(SearchConditionPattern.Contain));
		SearchConditionPattern type = SearchConditionPattern.Contain;
		switch(type){
			case Equal:
				System.out.println("1");
				break;
			case Contain:
				System.out.println("2");
				break;
			default:
				System.out.println("0");
		}
		
	}
}
