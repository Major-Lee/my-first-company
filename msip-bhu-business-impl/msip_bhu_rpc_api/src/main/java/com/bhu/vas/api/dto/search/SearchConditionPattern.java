package com.bhu.vas.api.dto.search;

import java.util.HashMap;
import java.util.Map;
/**
 * 搜索多组合条件定义枚举类型
 * @author tangzichao
 *
 */
public enum SearchConditionPattern {
	
	Contain("ctn",1,1,"包含匹配条件","模糊匹配字段包含某些关键词,支持输入空格自动分词"),
	NotContain("nctn",1,2,"非包含匹配条件","模糊匹配字段不包含某些关键词,支持输入空格自动分词"),
	Equal("eq",2,1,"完全匹配条件","完全匹配字段等于某些关键词,支持输入空格自动分词"),
	NotEqual("neq",2,2,"非完全匹配条件","完全匹配字段不等于某些关键词,支持输入空格自动分词"),
	GreaterThan("gt",3,1,"大于匹配条件","匹配字段大于该项"),
	GreaterThanEqual("gte",3,1,"大于等于匹配条件","匹配字段大于等于该项"),
	LessThan("lt",3,1,"小于匹配条件","匹配字段小于该项"),
	LessThanEqual("lte",3,1,"小于等于匹配条件","匹配字段小于等于该项"),
	PrefixContain("pctn",4,1,"前缀包含匹配条件","前缀匹配字段包含某个关键词"),
	NotPrefixContain("npctn",4,2,"前缀非包含匹配条件","前缀匹配字段不包含某个关键词"),
	Range("rge",3,1,"范围匹配条件","范围匹配字段大于并且小于条件"),
	NotRange("nrge",3,2,"非范围匹配条件","范围匹配字段非大于并且小于条件"),
	Unkown("Unkown",99,1,"未知条件","未知条件"),
	;
	
	
	//条件方式
	String pattern;
	//条件对应的搜索方式
	int method;
	//条件的必要性类型
	int necessity;
	String name;
	String desc;

	/************************   Methods   ***************************/
	//搜索方式为模糊匹配
	public static final int Method_Wildcard = 1;
	//搜索方式为项匹配
	public static final int Method_Term = 2;
	//搜索方式为范围匹配
	public static final int Method_Range = 3;
	//搜索方式为前缀匹配
	public static final int Method_Prefix = 4;
	
	/************************   Necessity   ***************************/
	//必要性为必须
	public static final int Necessity_Must = 1;
	//必要性为必须不
	public static final int Necessity_MustNot = 2;
	//必要性为可以有
	public static final int Necessity_Should = 3;
	
	SearchConditionPattern(String pattern, int method, int necessity, String name, String desc){
		this.pattern = pattern;
		this.method = method;
		this.necessity = necessity;
		this.name = name;
		this.desc = desc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public int getMethod() {
		return method;
	}
	public void setMethod(int method) {
		this.method = method;
	}
	public int getNecessity() {
		return necessity;
	}
	public void setNecessity(int necessity) {
		this.necessity = necessity;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

//	/**
//	 * 是否是匹配类型的条件
//	 * @param scp
//	 * @return
//	 */
//	public boolean isMatchType(SearchConditionPattern scp){
//		if(scp == null) return false;
//		if(Type_Match == scp.getType()) return true;
//		return false;
//	}
//	
//	/**
//	 * 是否排序类型的条件
//	 * @param scp
//	 * @return
//	 */
//	public boolean isSortType(SearchConditionPattern scp){
//		if(scp == null) return false;
//		if(Type_Sort == scp.getType()) return true;
//		return false;
//	}

	private static Map<String, SearchConditionPattern> mapPatterns;
	
	static {
		mapPatterns = new HashMap<String, SearchConditionPattern>();
		SearchConditionPattern[] items = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (SearchConditionPattern item : items){
			mapPatterns.put(item.pattern, item);
		}
	}
	
	public static SearchConditionPattern getByPattern(String pattern) {
		SearchConditionPattern ret = mapPatterns.get(pattern);
		if(ret == null) return SearchConditionPattern.Unkown;
		return ret;
	}
	
	public static Map<String, SearchConditionPattern> getMapPatterns() {
		return mapPatterns;
	}
	public static void setMapSName(Map<String, SearchConditionPattern> mapPatterns) {
		SearchConditionPattern.mapPatterns = mapPatterns;
	}
	public static void main(String[] argv){
		SearchConditionPattern de = SearchConditionPattern.getByPattern("ctn");
		System.out.println(de.name);
//		System.out.println(DeviceEnum.HandSet_IOS_Type);
		//System.out.println(DeviceEnum.isHandsetDevice("O"));
	}
}
