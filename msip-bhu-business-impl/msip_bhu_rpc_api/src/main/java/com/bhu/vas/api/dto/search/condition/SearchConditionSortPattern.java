package com.bhu.vas.api.dto.search.condition;

import java.util.HashMap;
import java.util.Map;
/**
 * 搜索排序定义枚举类型
 * @author tangzichao
 *
 */
public enum SearchConditionSortPattern {
	
	SortDesc("s_desc",10,2,"降序排序","降序排序"),
	SortAsc("s_asc",10,1,"升序排序","升序排序"),
	SortGeopointDistanceDesc("gdte_desc",11,2,"根据距离降序排序","根据距离降序排序"),
	SortGeopointDistanceAsc("gdte_asc",11,1,"根据距离升序排序","根据距离升序排序"),
	Unkown("Unkown",99,1,"未知条件","未知条件"),
	;
	
	
	//条件方式
	String pattern;
	//条件对应的排序方法
	int method;
	//条件对应的排序方式
	int mode;
	String name;
	String desc;

	/************************   Methods   ***************************/
	//搜索方式为正常排序
	public static final int Method_Sort = 10;
	//搜索方式为距离排序
	public static final int Method_DistanceSort = 11;
	
	/************************   Mode   ***************************/
	//排序方式为升序
	public static final int Mode_Asc = 1;
	//排序方式为降序
	public static final int Mode_Desc = 2;
	
	SearchConditionSortPattern(String pattern, int method, int mode, String name, String desc){
		this.pattern = pattern;
		this.method = method;
		this.mode = mode;
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
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public boolean isModeAsc(){
		if(this.mode == Mode_Asc) return true;
		return false;
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

	private static Map<String, SearchConditionSortPattern> mapSortPatterns;
	
	static {
		mapSortPatterns = new HashMap<String, SearchConditionSortPattern>();
		SearchConditionSortPattern[] items = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (SearchConditionSortPattern item : items){
			mapSortPatterns.put(item.pattern, item);
		}
	}
	
	public static SearchConditionSortPattern getByPattern(String pattern) {
		SearchConditionSortPattern ret = mapSortPatterns.get(pattern);
		if(ret == null) return SearchConditionSortPattern.Unkown;
		return ret;
	}
	
	public static Map<String, SearchConditionSortPattern> getMapPatterns() {
		return mapSortPatterns;
	}
	public static void setMapSName(Map<String, SearchConditionSortPattern> mapSortPatterns) {
		SearchConditionSortPattern.mapSortPatterns = mapSortPatterns;
	}
	public static void main(String[] argv){
		SearchConditionSortPattern de = SearchConditionSortPattern.getByPattern("desc");
		System.out.println(de.name);
//		System.out.println(DeviceEnum.HandSet_IOS_Type);
		//System.out.println(DeviceEnum.isHandsetDevice("O"));
	}
}
