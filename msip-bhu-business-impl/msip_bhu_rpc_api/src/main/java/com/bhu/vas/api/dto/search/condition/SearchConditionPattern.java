package com.bhu.vas.api.dto.search.condition;

import java.util.HashMap;
import java.util.Map;
/**
 * 搜索多组合条件定义枚举类型
 * @author tangzichao
 *
 */
public enum SearchConditionPattern {
	
	Contain("ctn",1,0,1,"包含匹配条件","模糊匹配字段包含某些关键词"),
	NotContain("nctn",1,0,2,"非包含匹配条件","模糊匹配字段不包含某些关键词"),
	Equal("eq",2,0,1,"完全匹配条件","完全匹配字段等于某些关键词"),
	NotEqual("neq",2,0,2,"非完全匹配条件","完全匹配字段不等于某些关键词"),
	StringEqual("seq",5,0,1,"字符串匹配条件","字符串匹配字段不匹配某些关键词,支持输入空格自动分词"),
	NotStringEqual("nseq",5,0,2,"非字符串匹配条件","字符串匹配字段不匹配某些关键词,支持输入空格自动分词"),
	PrefixContain("pctn",4,0,1,"前缀包含匹配条件","前缀匹配字段包含某个关键词"),
	NotPrefixContain("npctn",4,0,2,"前缀非包含匹配条件","前缀匹配字段不包含某个关键词"),
	
	GreaterThan("gt",3,302,1,"大于匹配条件","匹配字段大于该项"),
	GreaterThanEqual("gte",3,303,1,"大于等于匹配条件","匹配字段大于等于该项"),
	LessThan("lt",3,304,1,"小于匹配条件","匹配字段小于该项"),
	LessThanEqual("lte",3,305,1,"小于等于匹配条件","匹配字段小于等于该项"),
	Between("btn",3,301,1,"范围匹配条件","范围匹配字段大于并且小于条件"),
	NotBetween("nbtn",3,301,2,"非范围匹配条件","范围匹配字段非大于并且小于条件"),
	
	Missing("miss",6,0,1,"不存在的匹配条件","不存在的匹配条件"),
	Existing("exist",7,0,1,"存在的匹配条件","存在的匹配条件"),
	
	GeopointDistance("gdtc",8,801,1,"坐标点圆心半径","坐标点为圆心半径内匹配"),
	GeopointRectangle("grte",8,802,1,"坐标点长方形","左上右下2个坐标点构成的长方形内匹配"),
	
	Unkown("Unkown",99,0,0,"未知条件","未知条件"),
	;
	
	
	//条件方式
	String pattern;
	//条件对应的搜索方式
	int method;
	//条件对应的搜索方式的扩展子方式
	int method_ext;
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
	//搜索方式为特殊范围匹配 大于条件和小于条件同时存在
	public static final int MethodExt_Range_Between = 301;
	//搜索方式为特殊范围匹配 大于条件
	public static final int MethodExt_Range_GreaterThan = 302;
	//搜索方式为特殊范围匹配 大于等于条件
	public static final int MethodExt_Range_GreaterThanEqual = 303;
	//搜索方式为特殊范围匹配 小于条件
	public static final int MethodExt_Range_LessThan = 304;
	//搜索方式为特殊范围匹配 小于等于条件
	public static final int MethodExt_Range_LessThanEqual = 305;
	
	
	//搜索方式为前缀匹配
	public static final int Method_Prefix = 4;
	//搜索方式为字符串匹配(支持搜索分词多项匹配)
	public static final int Method_String = 5;
	//搜索方式为空匹配
	public static final int Method_Missing = 6;
	//搜索方式为存在匹配
	public static final int Method_Existing = 7;
	
	//搜索方式为坐标点的方式匹配
	public static final int Method_Geopoint = 8;
	//搜索方式为特殊的坐标点为圆心半径内匹配
	public static final int MethodExt_GeopointDistance = 801;
	//搜索方式为特殊的左上右下2个坐标点构成的长方形内匹配
	public static final int MethodExt_GeopointRectangle = 802;
	
	/************************   Necessity   ***************************/
	//必要性为必须
	public static final int Necessity_Must = 1;
	//必要性为必须不
	public static final int Necessity_MustNot = 2;
	//必要性为可以有
	public static final int Necessity_Should = 3;
	
	SearchConditionPattern(String pattern, int method, int method_ext, int necessity, String name, String desc){
		this.pattern = pattern;
		this.method = method;
		this.method_ext = method_ext;
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
	public int getMethod_ext() {
		return method_ext;
	}
	public void setMethod_ext(int method_ext) {
		this.method_ext = method_ext;
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
