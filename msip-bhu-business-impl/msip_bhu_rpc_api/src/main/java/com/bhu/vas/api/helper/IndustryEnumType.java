package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

public enum IndustryEnumType{
		Catering("100","餐饮美食"),
		Hotel("101", "酒店住宿"),
		Entertainment("102", "娱乐休闲"),
		Retail("103", "商业零售"),
		Education("104", "教育培训"),
		CityLady("105", "都市丽人"),
		PublicFacility("106", "公共设施"),
		HostpitalBank("107", "医院银行"),
		Station("108", "车站机场"),
		Tourist("109", "旅游景区"),
		Park("110", "公园广场"),
		Family("111", "家庭使用"),
		Others("112", "其它"),
		;
	
		private String index;
		private String name;
		
		static Map<String, IndustryEnumType> allIndustrys;
		
		private IndustryEnumType(String index,String name){
			this.index = index;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}

		static {
			allIndustrys = new HashMap<String, IndustryEnumType>();
			IndustryEnumType[] tps = values();
			for (IndustryEnumType tp : tps){
				allIndustrys.put(tp.getIndex(), tp);
			}
		}

		public static boolean isValideIndustry(String index){
			return allIndustrys.containsKey(index);
		}
		
		public static String getNameByIndex(String index){
			IndustryEnumType t = allIndustrys.get(index);
			return (t == null)?null:t.getName();
		}
		
		public static void main(String[] argv){
		}
}
