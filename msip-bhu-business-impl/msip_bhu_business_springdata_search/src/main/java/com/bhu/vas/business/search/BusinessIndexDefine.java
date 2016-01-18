package com.bhu.vas.business.search;

import java.util.HashMap;
import java.util.Map;

public interface BusinessIndexDefine {
	
	interface WifiDevice{
		public static final String IndexName	= "wifi_device_index3";
		public static final String IndexNameNew	= "wifi_device_index_new";
		public static final String Type 		= "wifiDevice";
		public static final int Shards		    = 5;
		public static final int replicas 		= 1;
		public static final String refreshInterval = "-1";
/*		interface Field{
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
		}*/
		enum Field implements FieldDefine{
			ID("id", null),//设备mac
			UPDATEDAT("updatedat", null),//索引记录的最后更新时间
			/**** 设备信息 device ****/
			D_MAC("d_mac", null),//设备mac
			D_SN("d_sn", null),//设备的sn编号
			D_ORIGSWVER("d_origswver", null),//设备的原始软件版本号
			D_ORIGVAPMODULE("d_origvapmodule", null),//设备的原始软件增值模块版本号
			D_WORKMODEL("d_workmodel", null),//设备的工作模式
			D_CONFIGMODEL("d_configmodel", null),//设备的配置模式
			D_TYPE("d_type", null),//设备的类型
			D_TYPE_SNAME("d_type_sname", null),//设备的类型的sname
			D_GEOPOINT("d_geopoint", null),//设备所在位置的经纬度坐标
			D_ADDRESS("d_address", null),//设备所在位置的详细地址
			D_ONLINE("d_online", null),//设备在线状态 -1 从未上线 1 在线 0 离线
			D_MODULEONLINE("d_monline", null),//设备增值模块是否在线 -1 从未上线 1 在线 0 离线
//			D_GROUPS("d_groups", null),//设备所属群组
//			D_NVD("d_nvd", null),//是否是新版本设备
			D_HANDSETONLINECOUNT("d_hoc", null),//设备终端在线数量
			D_LASTREGEDAT("d_lastregedat", null),//设备的最新的上线时间
			D_LASTLOGOUTAT("d_lastlogoutat", null),//设备的最新的下线时间
			D_CREATEDAT("d_createdat", null),//设备的接入时间(入库时间)
			D_DEVICEUNITTYPE("d_dut", null),//设备的业务线定义
			D_UPTIME("d_uptime", null),//设备在线总时长 单位秒
			/**** 运营信息 operate ****/
			O_TEMPLATE("o_template",null),//运营模板编号
			O_GRAYLEVEL("o_graylevel", null),//设备的灰度定义
			O_BATCH("o_batch", null),//设备的导入批次
			O_OPERATE("o_operate", null),//是否是可运营设备
			
			/**** 用户信息 user ****/
			U_ID("u_id",null),//绑定的用户id
			U_NICK("u_nick",null),//绑定的用户名称
			U_MOBILENO("u_mno",null),//绑定的用户的手机号码
			U_MOBILECOUNTRYCODE("u_mcc",null),//绑定的用户的手机号码的区域号码
			U_TYPE("u_type",null),//用户类型 代理商，普通，销售等等
			U_BINDED("u_binded", null),//用户是否绑定设备
			U_DNICK("u_dnick", null),//用户绑定的设备的昵称
			/**** 代理商信息 agent ****/
			A_ID("a_id",null),//代理商的用户id
			A_NICK("a_nick",null),//代理商的用户名称
			A_ORG("a_org",null),//代理商的公司名称
			
			;
			//基本索引字段名称
			String name;
			//权重得分，排序，计算专用字段名称
			String score_name;
			
			Field(String name, String score_name){
				this.name = name;
				this.score_name = score_name;
			}
			@Override
			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}
			@Override
			public String getScore_name() {
				return score_name;
			}

			public void setScore_name(String score_name) {
				this.score_name = score_name;
			}



			private static Map<String, Field> wifiDeviceFieldMaps;
			
			static {
				wifiDeviceFieldMaps = new HashMap<String, Field>();
				Field[] items = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
				for (Field item : items){
					wifiDeviceFieldMaps.put(item.name, item);
				}
			}
			

			public static Field getByName(String name) {
				Field ret = wifiDeviceFieldMaps.get(name);
				return ret;
			}
			
		}
	}
}
