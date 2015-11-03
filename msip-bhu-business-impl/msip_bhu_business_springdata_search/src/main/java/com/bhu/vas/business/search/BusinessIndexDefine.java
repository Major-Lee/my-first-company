package com.bhu.vas.business.search;

import java.util.HashMap;
import java.util.Map;

public interface BusinessIndexDefine {
	
	interface WifiDevice{
		public static final String IndexName	= "wifi_device_index3";
		public static final String Type 		= "wifiDevice";
		public static final int Shards		    = 5;
		public static final int replicas 		= 1;
		public static final String refreshInterval = "-1";
		interface Field{
			public static final String ID 		= "id";
			public static final String SN 		= "sn";
			public static final String ORIGSWVER 		= "origswver";
			public static final String ORIGVAPMODULE 		= "origvapmodule";
			public static final String WORKMODEL 		= "workmodel";
			public static final String CONFIGMODEL 		= "configmodel";
			public static final String DEVICETYPE 		= "devicetype";
			public static final String GEOPOINT 		= "geopoint";
			public static final String ADDRESS 		= "address";
			public static final String ONLINE 		= "online";
			public static final String MODULEONLINE 		= "moduleonline";
			public static final String GROUPS 		= "groups";
			public static final String NVD 		= "nvd";
			
			public static final String COUNT 		= "count";
			public static final String REGISTEREDAT 		= "registeredat";
			public static final String UPDATEDAT 		= "updatedat";
		}
		enum Field1{
			ID("id", null),
			Unkown(null, null),
			;
			//基本索引字段名称
			String name;
			//权重得分，排序，计算专用字段名称
			String score_name;
			
			Field1(String name, String score_name){
				this.name = name;
				this.score_name = score_name;
			}
			
			private static Map<String, Field1> wifiDeviceFieldMaps;
			
			static {
				wifiDeviceFieldMaps = new HashMap<String, Field1>();
				Field1[] items = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
				for (Field1 item : items){
					wifiDeviceFieldMaps.put(item.name, item);
				}
			}
			
			public static Field1 getByName(String name) {
				Field1 ret = wifiDeviceFieldMaps.get(name);
				if(ret == null) return Field1.Unkown;
				return ret;
			}
			
		}
	}
}
