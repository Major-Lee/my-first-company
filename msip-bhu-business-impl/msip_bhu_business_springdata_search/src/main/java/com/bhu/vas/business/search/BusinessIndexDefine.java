package com.bhu.vas.business.search;

public interface BusinessIndexDefine {
	
	interface WifiDevice{
		public static final String IndexName	= "wifi_device_index2";
		public static final String Type 		= "wifiDevice";
		public static final int Shards		    = 5;
		public static final int replicas 		= 1;
		public static final String refreshInterval = "-1";
		interface Field{
			public static final String ID 		= "id";
			public static final String SN 		= "sn";
			public static final String ORIGSWVER 		= "origswver";
			public static final String WORKMODEL 		= "workmodel";
			public static final String CONFIGMODEL 		= "configmodel";
			public static final String DEVICETYPE 		= "devicetype";
			public static final String GEOPOINT 		= "geopoint";
			public static final String ADDRESS 		= "address";
			public static final String ONLINE 		= "online";
			public static final String GROUPS 		= "groups";
			public static final String NVD 		= "nvd";
			
			public static final String COUNT 		= "count";
			public static final String REGISTEREDAT 		= "registeredat";
			public static final String UPDATEDAT 		= "updatedat";
		}
	}
}
