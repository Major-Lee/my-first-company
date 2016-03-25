package com.smartwork.msip.business.runtimeconf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;

import com.smartwork.msip.cores.helper.PropertiesHelper;
import com.smartwork.msip.cores.helper.StringHelper;

public class BusinessRuntimeConfiguration extends PropertyResourceConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(BusinessRuntimeConfiguration.class);
    
	@Override
	protected void processProperties(ConfigurableListableBeanFactory paramConfigurableListableBeanFactory,
			Properties paramProperties) throws BeansException {
		appendProperties(paramProperties);
		SystemNoneedCaptchaValidAccs.add("86 13811561274");//T1
		SystemTestUsers.add(100027);
		SystemNoneedCaptchaValidAccs.add("86 18911372223");//T2
		SystemTestUsers.add(100025);
		SystemNoneedCaptchaValidAccs.add("86 18612272825");//T3
		SystemTestUsers.add(3);
		//子超
		SystemNoneedCaptchaValidAccs.add("86 13810048517");//T3
		//SystemTestUsers.add(7);
//		SystemNoneedCaptchaValidAccs.add("86 15910908280");//T3
		//SystemTestUsers.add(100021);
		SystemNoneedCaptchaValidAccs.add("86 13910382542");//T3
		SystemTestUsers.add(100024);
		SystemNoneedCaptchaValidAccs.add("86 13671096898");//T3
		SystemTestUsers.add(100094);
		SystemNoneedCaptchaValidAccs.add("86 18605086756");//T3
		SystemTestUsers.add(100128);
		//马旭
		SystemNoneedCaptchaValidAccs.add("86 18811799611");//T3
		SystemTestUsers.add(100282);
		SystemNoneedCaptchaValidAccs.add("86 18519595789");//T3
		SystemTestUsers.add(100335);

		//宁玉玲
		SystemNoneedCaptchaValidAccs.add("86 13260307365");//T3
		SystemTestUsers.add(103987);
//		SystemNoneedCaptchaValidAccs.add("86 15910526881");//T3
//		SystemTestUsers.add(101767);
	}
	
	public static void appendProperties(Properties paramProperties) {  
        {  
        	contentFilterWord = PropertiesHelper.getBoolean("content.filter.word", paramProperties, false);
        	SameArticleSharePeruserTimesLimit = PropertiesHelper.getInt("samearticle.share.peruser.times.limit", paramProperties, SameArticleSharePeruserTimesLimit);
        	UserPushLimit = PropertiesHelper.getInt("push.limit", paramProperties, UserPushLimit);
        	IosPushKeystore = PropertiesHelper.getString("device.ios.push.keystore", paramProperties, IosPushKeystore);
        	IosPushKeyproduction = PropertiesHelper.getString("device.ios.push.keyproduction", paramProperties, IosPushKeyproduction);
        	IosPushPassword = PropertiesHelper.getString("device.ios.push.password", paramProperties, IosPushPassword);
        	//IosPushNewKeystore = PropertiesHelper.getString("device.ios.push.new.keystore", paramProperties, IosPushNewKeystore);
        	//IosPushNewPassword = PropertiesHelper.getString("device.ios.push.new.password", paramProperties, IosPushNewPassword);
        	IosPushProduction = PropertiesHelper.getBoolean("device.ios.push.production", paramProperties, IosPushProduction);
        	IosPushThreads = PropertiesHelper.getInt("device.ios.push.threads", paramProperties, IosPushThreads);
        	PushQueue = PropertiesHelper.getBoolean("push.queue", paramProperties, PushQueue);
        	
        	NormalTickerTrimLimit = PropertiesHelper.getInt("ticker.normal.trim.limit", paramProperties, NormalTickerTrimLimit);
        	SpecificTickerTrimLimit = PropertiesHelper.getInt("ticker.specific.trim.limit", paramProperties, SpecificTickerTrimLimit);
        	Weixin_Share_Uid = PropertiesHelper.getInt("weixin.share.uid", paramProperties, Weixin_Share_Uid);
        	
            GexinPushXmAppID = PropertiesHelper.getString("gexin.push.appid", paramProperties, GexinPushXmAppID);
            GexinPushXmAppKey = PropertiesHelper.getString("gexin.push.appkey", paramProperties, GexinPushXmAppKey);
            GexinPushXmAppSecret = PropertiesHelper.getString("gexin.push.appsecret", paramProperties, GexinPushXmAppSecret);
            GexinPushXmMasterSecret = PropertiesHelper.getString("gexin.push.mastersecret", paramProperties, GexinPushXmMasterSecret);
            GexinPushXmTimelive = PropertiesHelper.getInt("gexin.push.timelive", paramProperties, GexinPushXmTimelive);
        	
        	Setting_Post_Address_Interval = PropertiesHelper.getString("setting.post.address.interval", paramProperties, Setting_Post_Address_Interval);
        	Setting_Syncfrds_all_Interval = PropertiesHelper.getString("setting.syncfrds.all.interval", paramProperties, Setting_Syncfrds_all_Interval);
        	Setting_Syncfrds_increment_Interval = PropertiesHelper.getString("setting.syncfrds.increment.interval", paramProperties, Setting_Syncfrds_increment_Interval);
            
        	CheckArtificialFrdsKeepMs = PropertiesHelper.getLong("artificial.frds.keep.ms", paramProperties, CheckArtificialFrdsKeepMs);
        	CheckArtificialFrdsAddLimitCount =  PropertiesHelper.getInt("artificial.frds.add.limit", paramProperties, CheckArtificialFrdsAddLimitCount);
        	
        	UserJoinFrdLastLoginAtExpireDays = PropertiesHelper.getInt("sns.last.loginat.expire.days", paramProperties, UserJoinFrdLastLoginAtExpireDays);
        	UserJoinFrdLastActionAtExpireDays = PropertiesHelper.getInt("sns.last.actionat.expire.days", paramProperties, UserJoinFrdLastActionAtExpireDays);
        	UserFrdMaxCount = PropertiesHelper.getInt("user.frd.max.count", paramProperties, UserFrdMaxCount);
        	UserContextMsgExpireMilliSecs = PropertiesHelper.getLong("context.msg.exprie.millisecs", paramProperties, UserContextMsgExpireMilliSecs);
        	UserContextMsgExpireHours = PropertiesHelper.getString("context.msg.exprie.hours", paramProperties, UserContextMsgExpireHours);
        	UserContextMsgExpireScheduleMilliSecs = PropertiesHelper.getLong("context.msg.exprie.schedule.millisecs", paramProperties, UserContextMsgExpireScheduleMilliSecs);
        	StarUserFrdMaxCount = PropertiesHelper.getInt("star.user.frd.max.count", paramProperties, StarUserFrdMaxCount);
        	
        	Agent_Charging_Param_Filter = PropertiesHelper.getBoolean("agent.charging.param.filter", paramProperties,false);
        	Agent_Charging_Param_TimeUnit = PropertiesHelper.getInt("agent.charging.param.timeunit", paramProperties,Agent_Charging_Param_TimeUnit);
        	Agent_Charging_Param_ValueUnit = PropertiesHelper.getDouble("agent.charging.param.valueunit", paramProperties, Agent_Charging_Param_ValueUnit);
        	Agent_Charging_Param_CashBackFirstEach = PropertiesHelper.getDouble("agent.charging.param.cashbackfirsteach", paramProperties, Agent_Charging_Param_CashBackFirstEach);
        	
        	Devices_Plugin_Samba_Name = PropertiesHelper.getString("devices.plugin.samba.name", paramProperties, Devices_Plugin_Samba_Name);
        	Devices_Plugin_Samba_StartCmd = PropertiesHelper.getString("devices.plugin.samba.startcmd", paramProperties, Devices_Plugin_Samba_StartCmd);
        	Devices_Plugin_Samba_StopCmd = PropertiesHelper.getString("devices.plugin.samba.stopcmd", paramProperties, Devices_Plugin_Samba_StopCmd);
        	Devices_Plugin_Samba_DownloadPath = PropertiesHelper.getString("devices.plugin.samba.downloadpath", paramProperties, Devices_Plugin_Samba_DownloadPath);
        	Devices_Plugin_Samba_Ver = PropertiesHelper.getString("devices.plugin.samba.ver", paramProperties, Devices_Plugin_Samba_Ver);
        	
        	PaymentApiDomain = PropertiesHelper.getString("payment.api.domain", paramProperties, PaymentApiDomain);
        	
        	Terminal_Push_Notify_Exprie_Second = PropertiesHelper.getInt("terminal.push.notify.exprie.second", paramProperties, Terminal_Push_Notify_Exprie_Second);
        	logger.info("loading runtime configuration successfully!");  
            InternalCaptchaCodeSMS_Gateway = PropertiesHelper.getString("internal.captchacode.sms.gateway", paramProperties, InternalCaptchaCodeSMS_Gateway);

            Vap_Http_Res_UrlPrefix = PropertiesHelper.getString("vap.http.res.urlprefix", paramProperties, Vap_Http_Res_UrlPrefix);
            Vap_Http_Api_UrlPrefix = PropertiesHelper.getString("vap.http.api.urlprefix", paramProperties, Vap_Http_Api_UrlPrefix);
    
            
            Vap_Wifistasniffer_Url = PropertiesHelper.getString("vap.wifistasniffer.url", paramProperties, Vap_Wifistasniffer_Url);
            Vap_Wifistasniffer_Batch_Num = PropertiesHelper.getInt("vap.wifistasniffer.batch.num", paramProperties, Vap_Wifistasniffer_Batch_Num);
            Vap_Wifistasniffer_Delay = PropertiesHelper.getInt("vap.wifistasniffer.delay", paramProperties, Vap_Wifistasniffer_Delay);
            
            Device_SpeedTest_Download_url = PropertiesHelper.getString("device.speedtest.download.url", paramProperties, Device_SpeedTest_Download_url);
            Device_SpeedTest_Upload_url = PropertiesHelper.getString("device.speedtest.upload.url", paramProperties, Device_SpeedTest_Upload_url);
            Search_Result_Export_Dir = PropertiesHelper.getString("search.result.export.dir", paramProperties, Search_Result_Export_Dir);
            String initialVersions = PropertiesHelper.getString("device.firmware.initial.versions", paramProperties, ""
            		+ "AP106P06V1.2.15Build8084,AP106P06V1.2.15Build8119,AP106P06V1.2.15Build7800,AP106P07V1.3.0Build8482,AP106P06V1.3.0Build8482,AP106P06V1.3.4Build8998_TU,"
            		+ "AP106P06V1.3.2Build8715_TU,AP106P06V1.3.4Build8882_TU");
            
            if(StringUtils.isNotEmpty(initialVersions)){
            	String[] versions = initialVersions.split(StringHelper.COMMA_STRING_GAP);
            	for(String version:versions){
            		Device_Firmware_Initial_Versions.add(version);
            	}
            }
            
            Device_Firmware_ForceUpdateImmediately_AfterDays = PropertiesHelper.getInt("device.firmware.forceupdateimmediately.afterdays", paramProperties, Device_Firmware_ForceUpdateImmediately_AfterDays);
            
            User_WalletWithdraw_Default_MinLimit = PropertiesHelper.getDouble("user.walletWithdraw.default.minLimit", paramProperties, User_WalletWithdraw_Default_MinLimit);
            
            Device_SharedNetwork_Default_Start = PropertiesHelper.getBoolean("device.sharednetwork.default.start", paramProperties, Device_SharedNetwork_Default_Start);
        	Device_SharedNetwork_DUT = PropertiesHelper.getString("device.sharednetwork.dut", paramProperties, Device_SharedNetwork_DUT);
        	Device_SharedNetwork_Top_Version = PropertiesHelper.getString("device.sharednetwork.top.version", paramProperties, Device_SharedNetwork_Top_Version);
            
        	SharedNetworkWifi_Default_Open_resource 	= PropertiesHelper.getString("sharednetworkwifi.default.openresource", paramProperties, SharedNetworkWifi_Default_Open_resource);
        	SharedNetworkWifi_Default_Redirect_url 		= PropertiesHelper.getString("sharednetworkwifi.default.redirecturl", paramProperties, SharedNetworkWifi_Default_Redirect_url);
        	SharedNetworkWifi_Default_Remote_auth_url 	= PropertiesHelper.getString("sharednetworkwifi.default.remote.authurl", paramProperties, SharedNetworkWifi_Default_Remote_auth_url);
        	SharedNetworkWifi_Default_Remote_portal_server_url = PropertiesHelper.getString("sharednetworkwifi.default.remote.protalserverurl", paramProperties, SharedNetworkWifi_Default_Remote_portal_server_url);
        	SharedNetworkWifi_Default_Remote_Dns_default_ip = PropertiesHelper.getString("sharednetworkwifi.default.dns.default.ip", paramProperties, SharedNetworkWifi_Default_Remote_Dns_default_ip);
        	
        	
        	logger.info("loading business runtime configuration successfully!");  
        }  
    }  
	
	public static String SharedNetworkWifi_Default_Open_resource = "bhuwifi.com,bhunetworks.com";
	public static String SharedNetworkWifi_Default_Redirect_url = "www.bhuwifi.com";
	public static String SharedNetworkWifi_Default_Remote_auth_url = "http://123.56.227.18:9158/portal/default/reward/index_before.html";
	public static String SharedNetworkWifi_Default_Remote_portal_server_url = "123.56.227.18:18085";
	public static String SharedNetworkWifi_Default_Remote_Dns_default_ip = "123.57.26.170";
	
	
	public static String DeviceTesting_Mac_Prefix = "42:66";
	//缺省值 是否针对日志分析的数据进行过滤 agent.charging.param.filter
	public static boolean Agent_Charging_Param_Filter = false;
	//缺省值 计费时间单位 4分钟 agent.charging.param.timeunit
	public static int Agent_Charging_Param_TimeUnit = 4;
	//缺省值 每间隔单位（4分钟）收入0.01元 agent.charging.param.valueunit
	public static double Agent_Charging_Param_ValueUnit = 0.01d;
	//缺省值 新设备第一次返现额度 为30.00d  agent.charging.param.cashbackfirsteach
	public static double Agent_Charging_Param_CashBackFirstEach = 30.00d;
	
	public static String Devices_CMD_Enable = "enable";
	public static String Devices_CMD_Disable = "disable";
	
	public static String Devices_Plugin_Samba_Name = "samba";
	public static String Devices_Plugin_Samba_StartCmd = "./smbsrv.sh start";
	public static String Devices_Plugin_Samba_StopCmd = "./smbsrv.sh stop";
	//下载地址，支持逗号分割
	public static String Devices_Plugin_Samba_DownloadPath = "http://7xpatx.dl1.z0.glb.clouddn.com/uRouter/smb.package.tar.gz";
	public static String Devices_Plugin_Samba_Ver = "0.01";
	
	public static int Terminal_Push_Notify_Exprie_Second = 15 * 60;
	public static int NormalTickerTrimLimit = 400;
	public static int SpecificTickerTrimLimit = 100;
	
	//用户获取下一个新的验证码必须在前一次生成验证码的60s后 ，客户端按60s给用户倒计时提示
	public static final int UserCanFetchNextCaptchaCode = 60;//60s
	//用户验证码过期180s后 ，客户端按30s给用户提示
	public static final int UserCaptchaCodeExpired = 180;//3分钟
	//用户验证码每个用户每天最多 多少次
	public static final int UserCaptchaCodeLimit = 8;
	//支付系统api接口的域名
	public static String PaymentApiDomain = "http://upay.bhuwifi.com";
	
	//public static final String InternalCaptchaCodeSMS_Template = "尊敬的用户您好，您此次验证码为@请勿将验证码提供给他人，如非本人操作，请忽略。【华信联创】";
	
	//public static final String InternalCaptchaCodeSMS_Template = "验证码:%s,请输入以上验证码完成步骤,欢迎体验新时代的Wifi工具！";//【华信联创】
	//public static final String ForeignCaptchaCodeSMS_Template  = "【Blink】Verification:%s, please enter the verification code as the above to continue.Welcome! You are now using the IM for the new era!";//"[Blink] Code:%s,Please Input!";
	public static String InternalCaptchaCodeSMS_Gateway = "Chanzor";//【华信联创】
	//public static final String InternalCaptchaCodeSMS_Template = "【uRouter】验证码：%s,欢迎使用uRouter智能路由器！【华信联创】";//【华信联创】
	//验证码：@，请勿将验证码泄露给他人！【必虎路由】
	public static final String InternalCaptchaCodeSMS_Template = "验证码:%s，请勿将验证码泄露给他人！【必虎路由】";//【华信联创】
	public static final String InternalCaptchaCodePwdResetSMS_Template = "验证码:%s，密码重置操作，请勿将验证码泄露给他人！【必虎路由】";//【华信联创】
	//public static final String InternalCaptchaCodeSMS_Template = "[uRouter]验证码:%s,欢迎使用uRouter智能路由器！";//【华信联创】
	//public static final String WillExpiredPush_Template = "【快看】你有%s条消息将于1小时候后过期销毁，赶快过来看看吧!";
	//public static final String WillExpiredSMSPush_HasAddressBookFriend_Template = "【快看】%s在快看上给你发送的消息将于1小时候过期销毁, 赶快看看他们发了什么给你!";
	//public static final String WillExpiredSMSPush_NoAddressBookFriend_Template = "【快看】你在快看上有%s条消息将于1小时候后过期销毁，赶快看看都是什么吧!";

	public static String Default_Pwd = "vas1234";
	public static String Default_Agent_Pwd = "agent1234";
	public static String Vap_Http_Res_UrlPrefix = "http://192.168.66.7/vap/";
	public static String Vap_Http_Api_UrlPrefix = "http://192.168.66.7/bhu_api/";
	
	public static String Vap_Wifistasniffer_Url = "http://192.168.66.7/collect/wifistasniffer";
	public static int Vap_Wifistasniffer_Batch_Num = 10;
	//单位 秒 s
	public static int Vap_Wifistasniffer_Delay = 10;
	
	//设备测速下行的地址
	//public static String Device_SpeedTest_Download_url = "http://vap.bhunetworks.com/speedtest/speedtest.tar.gz";
	public static String Device_SpeedTest_Download_url = "http://dl.quickbird.com/speedtest20M.zip";
	
	//设备测速上行的地址
	public static String Device_SpeedTest_Upload_url = "uploadtest.quickbird.com:60080";
	
	//增值平台设备信息搜索结果导出文件目录
	public static String Search_Result_Export_Dir = "/BHUData/srexport/";
	
	
	public static final List<String> SystemNoneedCaptchaValidAccs = new ArrayList<String>(); 
	public static final String  DefaultCaptchaCode = "123456";
	public static final List<Integer> SystemTestUsers = new ArrayList<Integer>();
	
	//samearticle.share.peruser.times.limit
	public static int SameArticleSharePeruserTimesLimit = 3;
	public static int UserInviteTimesPerday = 1;
	public static int UserInviteTokenMaxGenPertime = 2; 
	
	//public static int IndexSearchFetchMaxSize = 500;
	public static String Setting_Post_Address_Interval = "0.2";//客户端同步用户通信录的间隔 单位小时 默认0.2小时
	public static String Setting_Syncfrds_all_Interval = "0.2";//客户端同步全量用户好友间隔 单位小时 默认0.2小时
	public static String Setting_Syncfrds_increment_Interval = "0.3";//客户端同步增量用户好友间隔 单位小时 默认0.3小时
	
	
	public static Set<String> Device_Firmware_Initial_Versions = new HashSet<String>();
	public static int Device_Firmware_ForceUpdateImmediately_AfterDays = 2;
	public static boolean isSystemNoneedCaptchaValidAcc(String acc){
		return SystemNoneedCaptchaValidAccs.contains(acc);
	}
	
	public static boolean isSystemTestUsers(Integer user){
		return SystemTestUsers.contains(user);
	}
	//console用户的最大值
	public static int UserConsoleMaxIdLimit = 100000;
	
	public static final int Guest_Uid = -1;
	public static int Weixin_Share_Uid = 65;

	
	public static String IosPushKeystore = "D:/push.store.p12";
	public static String IosPushKeyproduction = "D:/push.production.p12";
	//public static String IosPushKeystore = "/VCData/data/iospush/push.production.p12";
	public static String IosPushPassword = "20140707";
	
	public static boolean PushQueue = true;
	public static int PushQueueMaxBatch = 100;
	public static int UserPushLimit = 5;
	//个推push
	public static String GexinPushXmAppID = "kabvIAAmRH6dogidIj3Kh8";
	public static String GexinPushXmAppKey = "tsXOTz31WI7Mo6V1LdsFJ8";
	public static String GexinPushXmAppSecret = "opxXHlLFRk9jbhzeoqXdx7";
	public static String GexinPushXmMasterSecret = "mXCpKttZtv9qLISUFU7m65";
	public static String GexinPushXmApi = "http://sdk.open.api.igexin.com/apiex.htm";
	public static int GexinPushXmTimelive = 3600000;//1小时
	
	
	public static boolean IosPushProduction = true;
	public static int IosPushThreads = 2;
	public static String IosPushSoundP = "justforyou.caf";//ios push 单发聊天消息的push声音
	public static String IosPushSoundM = "push.caf";//ios push 群发聊天消息的push声音
	
	public static String Push_UserFrdApply_Template = "%s 申请添加您为好友";
	public static String Push_UserFrdJoin_Template = "%s 已经与您成为好友, 可以进行聊天了";
	public static String Push_UserFrdJoin_Address_Template = "%s 已经入驻, 可以进行聊天了";
	public static String Push_UserFrdIntro_Template = "%s 给您介绍了一些朋友, 赶快查看哦";
	public static String Push_UserChatMedia_Template = "%s 给你发送了一条新消息, 赶快查看哦";
	public static String Push_UserChatReaded_Template = "%s 已经查看了你发送的消息";
	public static String Push_ContextMsg_Willbe_Exprie_Template = "您有%s条未读消息即将过期, 请尽快查看哦";
	
	public static boolean contentFilterWord = false;//"content.filter.word"

	
	public static int[] InviteCheckinDefaultBingsUserid = {100000,100238,242875};
	
	//public static final String AfterUserRegisterFirstNotifyMsg_Template = "【Blink提示】%s 您好,Blink旅程愉快,随机号:%s";
	public static final String SystemAdminUserID = "1000";
	
	public static final String[] NotifyCommonMsgs = new String[]{"Hi，如果觉得有趣的话可以在右上角“菜单”选项中邀请其他好友和你一起玩哦！",
		"通讯录好友太少？可以在右上角“菜单”选项中选择邀请其他好友一起加入你哦!", "右上角“菜单”选项中可以有更多方式邀请好友加入哦！快去让他们一起来玩吧！",
		"快到右上角“菜单”选项中邀请其他好友和你一起来玩快看吧！"};
	//用户登陆距当前时间days内,才会在定时任务中执行好友入驻
	public static int UserJoinFrdLastLoginAtExpireDays = 30;
	//用户的第三方sns最后执行时间在days外,才会在定时任务和账号登陆的时候执行好友入驻
	public static int UserJoinFrdLastActionAtExpireDays = 7;
	public static int UserFrdMaxCount = 500;//好友最多500个
	public static int StarUserFrdMaxCount = 200; //明星用户好友数量最多200个 
	//public static String[] UserDefaultCover = {"chairs.jpg","dust.jpg","sunrise.jpg","typewriter.jpg","waterboy.jpg"};
	
	//任务等待回应的timeout时间
	public static long TaskTimeoutMilliSecs = 10 * 1000;//3600*1000;
	//1天  24小时
	public static long UserContextMsgExpireMilliSecs = 1*24*3600*1000;//3600*1000;//
	public static String UserContextMsgExpireHours = "24";
	//即将过期消息的判定时间为22小时
	public static long UserContextMsgExpireScheduleMilliSecs = 1*22*3600*1000;//3600*1000;//
	
	//public static String Media_Access_EndPoint_Prefix = "http://jingfm.duomi.com";
	//public static String Media_Access_EndPoint_Prefix_CC = "http://cc.cdn.jing.fm";
	//public static String Image_Access_EndPoint_Prefix_CC = "http://image.jing.fm";
	//public static String Download_Access_EndPoint_Prefix_CC = "http://d.jing.fm";
	//public static String PathFile_Blur_BadgeNormalDict 		= "/VCData/data/dic/blurdic/badges.normal.dic";
	//public static String PathFile_Blur_BadgeSpecialDict 	= "/VCData/index/dic/blurdic/badges.special.dic";
	//public static String PathFile_Blur_UsersDict 			= "/VCData/data/dic/blurdic/users.dic";
	
	//public static String PathFile_ReservedWordDict 		= "/VCData/data/dic/reservedword_business.normal.dic";
	
	//public static String PathFile_ReservedWordDict 		= "/VCData/data/dic/reservedword_business.normal.dic";
	//public static String PathFile_Blur_BillboardNormalDict 		= "/VCData/data/dic/blurdic/billboard.normal.dic";
	//public static String DefaultAvatarURL = "http://jing.fm/images/defaults/avatar/30.jpg";
	//public static String JingWatermarkPngPath = "/VCData/data/watermark/jing-water.png";
	//public static String JingWatermarkAvatar1xCoverPngPath =  "/VCData/data/watermark/avatarMask@1x.png";
	//public static String JingWatermarkAvatar2xCoverPngPath =  "/VCData/data/watermark/avatarMask@2x.png";
	//public static String JingWatermarkCoverPngPath =  "/VCData/data/watermark/coverMask.png";
	
	public static long CheckArtificialFrdsKeepMs = 24 * 60 * 60 * 1000;//24小时的毫秒数 超过时间删除客服好友关系
	public static int CheckArtificialFrdsAddLimitCount = 5;//5个好友数量, 超过数量不在分配客服

	
	public static double User_WalletWithdraw_Default_MinLimit = 10.00d;//5个好友数量, 超过数量不在分配客服
	
	public static boolean Device_SharedNetwork_Default_Start = false;
	public static String Device_SharedNetwork_DUT = "TU";
	public static String Device_SharedNetwork_Top_Version = "1.5.6";
	
	/**
	 * 判断是否是console用户
	 * @param uid
	 * @return
	 */
	public static boolean isConsoleUser(Integer uid){
		if(uid != null && uid.intValue() <= UserConsoleMaxIdLimit) return true;
		return false;
	}
	
	
	public static boolean isInviteTokenNeedRelation(int inviteid){
		if(inviteid <=0) return false;
		for(int specuid:InviteCheckinDefaultBingsUserid){
			if(inviteid == specuid) return false;
		}
		return true;
	}
	
	public static boolean isInitialDeviceFirmwareVersion(String orig_swver){
		return Device_Firmware_Initial_Versions.contains(orig_swver);
	}
}
