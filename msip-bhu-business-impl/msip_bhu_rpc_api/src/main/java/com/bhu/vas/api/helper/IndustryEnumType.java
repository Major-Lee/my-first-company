package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.smartwork.msip.cores.i18n.LocalI18NMessageSource;

public enum IndustryEnumType{
		Catering("100","餐饮美食", "dict.industry.catering"),
		Hotel("101", "酒店住宿", "dict.industry.hotel"),
		Entertainment("102", "娱乐休闲", "dict.industry.entertainment"),
		Retail("103", "商业零售", "dict.industry.retail"),
		Education("104", "教育培训", "dict.industry.education"),
		CityLady("105", "都市丽人", "dict.industry.citylady"),
		PublicFacility("106", "公共设施", "dict.industry.publicfacility"),
		HostpitalBank("107", "医院银行", "dict.industry.hostpitalbank"),
		Station("108", "车站机场", "dict.industry.station"),
		Tourist("109", "旅游景区", "dict.industry.tourist"),
		Park("110", "公园广场", "dict.industry.park"),
		Family("111", "家庭使用", "dict.industry.family"),
		Corps("112", "企事业单位", "dict.industry.corps"),
		Others("199", "其它", "dict.industry.others"),
		;
	
		private String index;
		private String name;
		private String name_key;
		
		static Map<String, IndustryEnumType> allIndustrys;
		
		private IndustryEnumType(String index,String name, String name_key){
			this.index = index;
			this.name = name;
			this.name_key = name_key;
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

		public String getName_key() {
			return name_key;
		}
		public void setName_key(String name_key) {
			this.name_key = name_key;
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

		public String getNameByLocale(Locale locale){
			return LocalI18NMessageSource.getInstance().getMessage(name_key, locale);//t.getName();
		}

		public static String getNameByIndex(String index, Locale locale){
			IndustryEnumType t = allIndustrys.get(index);
			return (t == null)?null:(LocalI18NMessageSource.getInstance().getMessage(t.getName_key(), locale));//t.getName();
		}
		
		public static void main(String[] argv){
			System.out.println(IndustryEnumType.getNameByIndex("100"));
		}
}
