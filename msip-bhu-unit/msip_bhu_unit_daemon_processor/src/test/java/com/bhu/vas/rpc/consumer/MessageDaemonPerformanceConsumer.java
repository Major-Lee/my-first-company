package com.bhu.vas.rpc.consumer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.rpc.daemon.helper.DaemonHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

public class MessageDaemonPerformanceConsumer {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUDaemonSC");
		System.setProperty("zookeeper", "192.168.66.7:2181");
		System.setProperty("provider.port", "");
		//System.setProperty("provider.port", "20882");
		//System.out.println("~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath*:spring/applicationContextCore-resource.xml",
				"classpath*:/com/bhu/vas/rpc/consumer/applicationContextRpcUnitConsumer.xml" });
		context.start();
		IDaemonRpcService rpcService = (IDaemonRpcService)context.getBean("daemonRpcService");
		
		String mac = "42:43:44:42:41:42";

		ArrayList<String> payloads = new ArrayList<String>();
		//获取配置指令
		payloads.add(CMDBuilder.builderDeviceSettingQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence()));
		//获取地理位置
		payloads.add(CMDBuilder.builderDeviceLocationNotifyQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence()));
		//修改信号强度
		String config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><wifi><radio><ITEM name=\"wifi0\" power=\"20\" /></radio></wifi></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//修改admin管理密码
		config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><sys><users><ITEM password_rsa=\"8143b8e16ebff24b330ca46bd7b358d265431a323f05120d723d73bc4fab2f373fe7088d9e054698c53122161ba11cbfd5df7412afffda396d567f51299f12be\" name=\"admin\" /></users></sys></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//广告注入开启
		config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM  bhu_id=\"1000000\" bhu_ad_url=\"http://auth.wi2o.cn/ad/ad.js\" bhu_enable=\"enable\"  /></ad></net></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//重定向开启
		config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM bhu_http_redirect_enable=\"enable\" bhu_http_redirect_rule=\"20,8:00:00,22:00:00,http://www.sina.com.cn,http://www.bhunetworks.com,http://www.chinaren.com,http://www.bhunetworks.com\"/></ad></net></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//404跳转开启
		config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM bhu_http404_enable=\"enable\" bhu_http404_url=\"http://vap.bhunetworks.com/vap/rw404?bid=10002\" bhu_http404_codes=\"40*,502\"/></ad></net></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//访客网络开启
		payloads.add(CMDBuilder.builderCMD4HttpPortalResourceUpdate(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), 
				"{\"style\":\"style000\"}"));

		//查询终端速率
		payloads.add(CMDBuilder.builderDeviceTerminalsQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), 2, 10));
		//查询设备网速
		payloads.add(CMDBuilder.builderDeviceSpeedNotifyQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), 2, 10, "", ""));
		//查询设备实时速率
		payloads.add(CMDBuilder.builderDeviceRateNotifyQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), "wan", 2, 10));
		//查询设备dhcpc信息
		payloads.add(CMDBuilder.builderDhcpcStatusQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), "eth1"));
		//查询设备使用情况
		payloads.add(CMDBuilder.builderDeviceUsedStatusQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence()));
		
		DaemonHelper.daemonCmdsDown(mac, payloads, rpcService);
		
		//		System.out.println("done");
	}
}
