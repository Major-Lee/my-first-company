package com.bhu.vas.business.search;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.bhu.vas.business.search.core.field.FieldDefine;

public interface BusinessIndexDefine {
	
	interface WifiDevice{
		//public static final String IndexName	= "wifi_device_index3";
		public static final String IndexNameNew	= "wifi_device_index_new";
		public static final String IndexName	= "wifi_device_index_v6";
		public static final String Type 		= "wifiDevice";
		public static final int Shards		    = 5;
		public static final int replicas 		= 1;
		public static final String refreshInterval = "-1";
		public static final int RetryOnConflict = 3;
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
			D_ORIGMODEL("d_origmodel", null),//设备的原始设备型号
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
			D_FIRSTREGEDAT("d_firstregedat", null),//设备的首次的上线时间
			D_LASTREGEDAT("d_lastregedat", null),//设备的最新的上线时间
			D_LASTLOGOUTAT("d_lastlogoutat", null),//设备的最新的下线时间
			D_CREATEDAT("d_createdat", null),//设备的接入时间(入库时间)
			D_DEVICEUNITTYPE("d_dut", null),//设备的业务线定义
			D_DEVICE_BSNAME("d_mn", null),//设备的软件版本N属性
			D_UPTIME("d_uptime", null),//设备在线总时长 单位秒
			D_WANIP("d_wanip", null),//设备wanip
			D_INDUSTRY("d_industry", null),//设备行业信息
			D_SHAREDNETWORK_OWNER("d_snk_owner", null),//设备的共享网络类型
			D_SHAREDNETWORK_TYPE("d_snk_type", null),//设备的共享网络类型
			D_SHAREDNETWORK_TEMPLATE("d_snk_template", null),//设备的共享网络模板
			D_SHAREDNETWORK_TURNSTATE("d_snk_turnstate", null),//设备的共享网络的开启状态
			D_SHAREDNETWORK_ALLOWTURNOFF("d_snk_allowturnoff", null),//设备的访客网络是否可关闭
			D_TAGS("d_tags", null),//设备的tags
			D_PROVINCE("d_province", null),//设备的地址位置-省
			D_CITY("d_city", null),//设备的地址位置-市
			D_DISTRICT("d_district",null),//设备的地址位置-区
			D_CHANNEL_LV1("d_channel_lv1", null), //设备的以及出货渠道
			D_CHANNEL_LV2("d_channel_lv2", null), //设备的以及出货渠道
			D_DISTRIBUTOR_TYPE("d_distributor_type", null),//城市运营商 or 渠道商类型
			D_DISTRIBUTOR_ID("d_distributor_id", null),//城市运营商 or 渠道商

			
			//D_EXTENSION("d_extension", null),//设备业务扩展字段
			/**** 运营信息 operate ****/
			O_TEMPLATE("o_template",null),//运营模板编号
			O_GRAYLEVEL("o_graylevel", null),//设备的灰度定义
			O_SCALELEVEL("o_scalelevel", null),//设备规模级别
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
			
			
			
			/**** 第三方业务信息 ucloud ****/
			T_UC_EXTENSION("t_uc_extension",null),//ucloud自定义搜索业务字段
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
	
	interface Advertise{
		public static final String IndexName	= "advertise_index_v2";
		public static final String Type 		= "advertise";
		public static final int Shards		    = 5;
		public static final int replicas 		= 1;
		public static final String refreshInterval = "-1";
		public static final int RetryOnConflict = 3;
		
		enum Field implements FieldDefine{
			ID("id", null),
			A_TITLE("a_title",null),
			A_TAG("a_tag",null),
			A_TOP("a_top",null),
			A_TYPE("a_type",null),
			A_DESC("a_desc",null),
			A_IMAGE("a_image",null),
			A_URL("a_url",null),
			A_DOMAIN("a_domain",null),
			A_PROVINCE("a_province",null),
			A_CITY("a_city",null),
			A_DISTRICT("a_district",null),
			A_GEOPOINT("a_geopoint", null),
			A_DISTANCE("a_distance",null),
			A_CASH("a_cash",null),
			A_COUNT("a_count",null),
			A_START("a_start",null),
			A_END("a_end",null),
			A_DURATION("a_duration",null),
			A_ABLEDEVICESNUM("a_abledevices_num",null),
			A_STATE("a_state",null),
			A_REJECT_REASON("a_reject_reason",null),
			A_VERIFY_UID("a_verify_uid",null),
			A_PROCESS_STATE("a_process_state",null),
			A_EXTPARAMS("a_extparams",null),
			A_CREATED_AT("a_created_at",null),
			A_UPDATED_AT("a_updated_at",null),
			A_SCORE("a_score",null),
			U_ID("u_id",null),
			;
			String name;
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



			private static Map<String, Field> advertiseFieldMaps;
			
			static {
				advertiseFieldMaps = new HashMap<String, Field>();
				Field[] items = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
				for (Field item : items){
					advertiseFieldMaps.put(item.name, item);
				}
			}
			

			public static Field getByName(String name) {
				Field ret = advertiseFieldMaps.get(name);
				return ret;
			}
		}
	}
	
	
	
	interface Ssid{
		public static final String IndexName	= "ssid_index_v1";
		public static final String Type 		= "ssid";
		public static final int Shards		    = 5;
		public static final int replicas 		= 1;
		public static final String refreshInterval = "-1";
		public static final int RetryOnConflict = 3;
		
		enum Field implements FieldDefine{
			ID("id", null),
			S_BSSID("s_bssid",null),
			S_SSID("s_ssid", null),
			S_DEVICE("s_device", null),
			S_GEOPOINT("s_geopoint", null),
			S_PWD("s_pwd", null),

			S_CREATED_AT("s_created_at",null),
			S_UPDATED_AT("s_updated_at",null),
			;
			String name;
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



			private static Map<String, Field> ssidFieldMaps;
			
			static {
				ssidFieldMaps = new HashMap<String, Field>();
				Field[] items = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
				for (Field item : items){
					ssidFieldMaps.put(item.name, item);
				}
			}
			

			public static Field getByName(String name) {
				Field ret = ssidFieldMaps.get(name);
				return ret;
			}
		}
	}
}
