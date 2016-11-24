package com.bhu.vas.di.op.migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.dto.DistributorType;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.SharedNetworksHelper;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * soc设备再ad系统上线后被打开共享网络。现在把他关闭
 * @author yetao
 *
 */
public class UserSharedNetworksRestore4SocAdOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksRestore4SocAdOp.class);
		ctx.start();
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		UserService userService = (UserService)ctx.getBean("userService");
		DeviceCMDGenFacadeService deviceCMDGenFacadeService = (DeviceCMDGenFacadeService)ctx.getBean("deviceCMDGenFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		ChargingFacadeService chargingFacadeService = (ChargingFacadeService)ctx.getBean("chargingFacadeService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");

//		String macs = "84:82:f4:0a:5f:68,84:82:f4:0a:60:a4,84:82:f4:0a:60:f0,84:82:f4:0a:62:08,84:82:f4:0a:62:58,84:82:f4:0a:63:c0,84:82:f4:0a:64:18,84:82:f4:0a:64:88,84:82:f4:0a:65:68,84:82:f4:0f:80:f0,84:82:f4:17:0b:ac,84:82:f4:19:17:c0,84:82:f4:19:1a:80,84:82:f4:19:20:94,84:82:f4:19:e0:f3,84:82:f4:19:ec:3f,84:82:f4:1a:05:d7,84:82:f4:1a:07:2b,84:82:f4:1a:16:4f,84:82:f4:1a:1d:8b,84:82:f4:1a:55:ef,84:82:f4:1a:58:b3,84:82:f4:1a:60:3f,84:82:f4:1a:69:ab,84:82:f4:1a:6d:23,84:82:f4:1a:71:db,84:82:f4:1a:7b:30,84:82:f4:1a:7b:54,84:82:f4:1a:7b:70,84:82:f4:1a:7b:9c,84:82:f4:1a:7b:a8,84:82:f4:1a:7b:c4,84:82:f4:1a:7b:d4,84:82:f4:1a:7b:e8,84:82:f4:1a:7b:ec,84:82:f4:1a:7c:0c,84:82:f4:1a:7c:64,84:82:f4:1a:7f:70,84:82:f4:1a:7f:90,84:82:f4:1a:7f:b8,84:82:f4:1a:80:28,84:82:f4:1a:80:f8,84:82:f4:1a:81:84,84:82:f4:1a:82:b0,84:82:f4:1a:84:4c,84:82:f4:1a:84:80,84:82:f4:1a:84:a0,84:82:f4:1a:85:90,84:82:f4:1a:87:78,84:82:f4:1a:87:a8,84:82:f4:1a:88:5c,84:82:f4:1a:8b:54,84:82:f4:1a:8b:78,84:82:f4:1a:8c:40,84:82:f4:1a:8d:64,84:82:f4:1a:8e:40,84:82:f4:1a:8f:e8,84:82:f4:1a:92:34,84:82:f4:1a:92:4c,84:82:f4:1a:93:a8,84:82:f4:1a:93:bc,84:82:f4:1a:95:e4,84:82:f4:1a:96:10,84:82:f4:1a:98:34,84:82:f4:1a:98:3c,84:82:f4:1a:98:9c,84:82:f4:1a:f0:14,84:82:f4:1a:f6:0c,84:82:f4:1a:f7:7c,84:82:f4:1a:fb:a4,84:82:f4:1b:00:08,84:82:f4:1b:01:c8,84:82:f4:1b:08:3c,84:82:f4:1b:0c:e0,84:82:f4:1b:0c:f4,84:82:f4:1b:20:2c,84:82:f4:1b:29:40,84:82:f4:1b:2a:20,84:82:f4:1b:5d:28,84:82:f4:1b:5e:60,84:82:f4:1b:5e:74,84:82:f4:1b:5f:98,84:82:f4:1b:72:7c,84:82:f4:1b:72:e4,84:82:f4:1b:73:00,84:82:f4:1b:73:4c,84:82:f4:1b:74:18,84:82:f4:1b:74:54,84:82:f4:1b:76:70,84:82:f4:1b:76:b4,84:82:f4:1b:76:fc,84:82:f4:1b:77:54,84:82:f4:1b:7b:90,84:82:f4:1b:82:f4,84:82:f4:1b:8b:1c,84:82:f4:1b:8c:a8,84:82:f4:1b:92:ac,84:82:f4:1b:9a:20,84:82:f4:1b:a8:98,84:82:f4:1b:ac:30,84:82:f4:1b:ac:7c,84:82:f4:1b:ae:90,84:82:f4:1b:b8:7c,84:82:f4:1b:c2:bc,84:82:f4:1b:c8:e8,84:82:f4:1b:da:f4,84:82:f4:1b:db:88,84:82:f4:1b:e3:a0,84:82:f4:1c:45:d4,84:82:f4:1c:5d:ec,84:82:f4:1c:62:14,84:82:f4:1c:7b:30,84:82:f4:1c:93:d0,84:82:f4:1c:96:88,84:82:f4:1c:b1:18,84:82:f4:1c:be:b0,84:82:f4:1c:c0:4c,84:82:f4:1c:c3:0c,84:82:f4:23:3a:50,84:82:f4:23:45:90,84:82:f4:23:49:28,84:82:f4:23:9f:b4,84:82:f4:23:bc:58,84:82:f4:23:bd:4c,84:82:f4:23:bf:a8,84:82:f4:23:c0:10,84:82:f4:23:c2:54,84:82:f4:23:c4:5c,84:82:f4:23:c5:0c,84:82:f4:23:c6:24,84:82:f4:23:c6:cc,84:82:f4:23:c6:e4,84:82:f4:23:ca:40,84:82:f4:24:0e:7c,84:82:f4:24:0e:b8,84:82:f4:24:0e:e4,84:82:f4:24:0f:28,84:82:f4:27:8b:cc,84:82:f4:27:b2:10,84:82:f4:27:b9:78,84:82:f4:27:bb:28,84:82:f4:27:c2:b0,84:82:f4:27:c3:ac,84:82:f4:27:d4:90,84:82:f4:27:e5:30,84:82:f4:28:00:d8,84:82:f4:28:51:e4,84:82:f4:28:73:68,84:82:f4:28:75:e0,84:82:f4:28:83:04,84:82:f4:28:90:e4,84:82:f4:28:9f:bc,84:82:f4:28:cd:f4,84:82:f4:29:03:f4,84:82:f4:29:0c:40,84:82:f4:29:79:44,84:82:f4:29:79:f4,84:82:f4:29:95:c0,84:82:f4:29:a0:28,84:82:f4:29:d5:d4,84:82:f4:2a:05:ac,84:82:f4:2a:07:ec,84:82:f4:2a:09:30,84:82:f4:2a:0d:98,84:82:f4:2a:10:e4,84:82:f4:2a:40:b4,84:82:f4:2d:a6:a0,84:82:f4:2d:a6:e0,84:82:f4:2d:a7:18,84:82:f4:2d:ac:64,84:82:f4:2d:ac:e8,84:82:f4:2d:ae:58,84:82:f4:2d:ae:a0,84:82:f4:2d:b3:78,84:82:f4:2d:b5:04,84:82:f4:2d:b7:3c,84:82:f4:2d:b7:e4,84:82:f4:2d:bb:90,84:82:f4:2d:bd:28,84:82:f4:2d:bd:a4,84:82:f4:2d:bd:dc,84:82:f4:2d:bd:e0,84:82:f4:2d:bd:f4,84:82:f4:2d:bd:f8,84:82:f4:2d:be:00,84:82:f4:2d:be:10,84:82:f4:2d:be:14,84:82:f4:2d:be:1c,84:82:f4:2d:be:48,84:82:f4:2d:be:bc,84:82:f4:2d:be:c0,84:82:f4:2d:bf:20,84:82:f4:2d:c0:fc,84:82:f4:2d:c2:10,84:82:f4:2d:c4:98,84:82:f4:2e:22:84,84:82:f4:2e:23:04,84:82:f4:2e:54:dc,84:82:f4:2e:57:dc,84:82:f4:2e:cc:f4,84:82:f4:2e:cd:8c,84:82:f4:2e:d4:80,84:82:f4:2e:d6:64,84:82:f4:31:23:e0,84:82:f4:31:50:80,84:82:f4:31:50:94,84:82:f4:31:63:40,84:82:f4:31:65:10,84:82:f4:31:65:58,84:82:f4:31:65:b4,84:82:f4:31:66:34,84:82:f4:31:66:c0,84:82:f4:31:68:f0,84:82:f4:31:6a:54,84:82:f4:31:6a:84,84:82:f4:31:6e:54,84:82:f4:31:6e:64,84:82:f4:31:6e:6c,84:82:f4:31:6e:84,84:82:f4:31:6e:88,84:82:f4:31:70:20,84:82:f4:31:70:28,84:82:f4:31:70:3c,84:82:f4:31:70:5c,84:82:f4:31:71:28,84:82:f4:31:71:78,84:82:f4:31:71:88,84:82:f4:31:71:a0,84:82:f4:31:76:c8,84:82:f4:31:87:d0,84:82:f4:31:89:ec,84:82:f4:31:8c:34,84:82:f4:31:8d:d8,84:82:f4:31:8e:38,84:82:f4:31:8e:a4,84:82:f4:31:8e:c0,84:82:f4:31:91:d8,84:82:f4:31:92:4c,84:82:f4:31:92:d8,84:82:f4:31:93:1c,84:82:f4:31:93:20,84:82:f4:31:93:f0,84:82:f4:31:95:64,84:82:f4:31:98:68,84:82:f4:31:98:74,84:82:f4:31:98:90,84:82:f4:31:98:c4,84:82:f4:31:99:8c,84:82:f4:31:9b:20,84:82:f4:31:9b:bc,84:82:f4:32:65:d4,84:82:f4:32:66:5c,84:82:f4:32:66:c8,84:82:f4:32:6d:38,84:82:f4:32:6f:18,84:82:f4:32:71:04,84:82:f4:32:71:38,84:82:f4:32:72:b8,84:82:f4:32:93:b8,84:82:f4:32:93:d0,84:82:f4:32:94:70,84:82:f4:32:94:8c,84:82:f4:32:96:2c,84:82:f4:32:96:50,84:82:f4:32:96:c8,84:82:f4:32:97:b4,84:82:f4:32:98:54,84:82:f4:32:99:54,84:82:f4:32:99:7c,84:82:f4:32:9a:38,84:82:f4:32:9a:80,84:82:f4:32:9a:b0,84:82:f4:32:9b:18,84:82:f4:32:9b:a8,84:82:f4:32:9c:1c,84:82:f4:32:9d:d0,84:82:f4:32:9e:14,84:82:f4:32:a0:10,84:82:f4:32:a2:04,84:82:f4:32:a2:68,84:82:f4:32:a2:6c,84:82:f4:32:a2:bc,84:82:f4:32:a2:c4,84:82:f4:32:a3:04,84:82:f4:32:a3:1c,84:82:f4:32:a3:6c,84:82:f4:32:a3:7c,84:82:f4:32:a3:b4,84:82:f4:32:a3:e0,84:82:f4:32:a3:e8,84:82:f4:32:a3:f0,84:82:f4:32:a4:1c,84:82:f4:32:a4:3c,84:82:f4:32:a4:58,84:82:f4:32:a4:68,84:82:f4:32:a4:6c,84:82:f4:32:a4:84,84:82:f4:32:a4:a4,84:82:f4:32:a4:b4,84:82:f4:32:a4:b8,84:82:f4:32:a5:14,84:82:f4:32:a5:28,84:82:f4:32:a5:74,84:82:f4:32:ac:40,84:82:f4:32:ad:58,84:82:f4:32:b0:c4,84:82:f4:32:b2:3c,84:82:f4:32:b6:f7,84:82:f4:32:b8:63,84:82:f4:32:b9:57,84:82:f4:32:b9:d3,84:82:f4:32:bf:37,84:82:f4:32:c0:1f,84:82:f4:32:c0:23,84:82:f4:32:c1:5f,84:82:f4:32:c2:37,84:82:f4:32:c2:a3,84:82:f4:32:c2:cb,84:82:f4:32:c2:e7,84:82:f4:32:c3:13,84:82:f4:32:c5:57,84:82:f4:32:c8:63,84:82:f4:32:c9:2b,84:82:f4:32:c9:bb,84:82:f4:32:f5:c0,84:82:f4:32:f5:c4,84:82:f4:33:0d:90,84:82:f4:33:41:68,84:82:f4:33:5c:1c,84:82:f4:33:5c:30,84:82:f4:33:5c:80,84:82:f4:33:5c:c4,84:82:f4:33:5d:c8,84:82:f4:33:5e:00,84:82:f4:33:62:3c,84:82:f4:65:5b:80,84:82:f4:90:03:98,84:82:f4:90:04:1c";
		String macs = "00:00:00:00:00:01,00:00:00:00:00:09,00:00:00:00:0f:01,00:00:00:00:10:01,00:00:00:01:01:01,00:00:00:01:01:05,00:00:00:02:00:01,00:00:00:11:11:00,00:01:00:00:00:01,00:01:01:01:01:05,00:01:01:02:02:02,00:02:03:04:05:05,00:02:03:04:06:01,00:11:11:11:11:01,00:11:11:11:11:10,00:11:22:33:44:01,00:11:22:33:44:05,00:11:22:33:44:10,12:d3:7f:be:34:12,62:68:00:00:01:00,62:68:75:00:01:05,62:68:75:00:01:0a,62:68:75:00:20:01,62:68:75:00:20:21,62:68:75:00:20:22,62:68:75:00:99:40,62:68:75:00:99:48,62:68:75:01:00:19,62:68:75:01:00:1f,62:68:75:02:00:06,62:68:75:02:00:3c,62:68:75:02:00:a1,62:68:75:02:ff:05,62:68:75:0f:0f:01,62:68:75:11:00:09,62:68:75:99:00:01,62:68:75:aa:00:00,62:68:75:aa:01:05,62:68:75:aa:05:00,62:68:75:aa:07:08,62:68:75:aa:11:05,62:68:75:af:00:10,62:68:75:af:00:14,62:68:75:ee:00:01,62:68:75:ff:ee:f0,62:68:75:ff:fa:01,62:68:75:ff:fe:05,62:68:75:ff:fe:0a,84:82:00:11:22:01,84:82:11:23:11:23,84:82:f4:00:0b:61,84:82:f4:02:00:24,84:82:f4:05:53:c4,84:82:f4:05:54:f0,84:82:f4:06:54:27,84:82:f4:07:0e:70,84:82:f4:07:54:27,84:82:f4:07:b1:0c,84:82:f4:08:00:00,84:82:f4:0a:5f:a8,84:82:f4:0a:5f:dc,84:82:f4:0a:62:9c,84:82:f4:0a:65:9c,84:82:f4:0c:a1:40,84:82:f4:0f:74:48,84:82:f4:0f:88:fc,84:82:f4:0f:92:78,84:82:f4:0f:92:94,84:82:f4:10:fb:d0,84:82:f4:11:12:11,84:82:f4:12:0f:5c,84:82:f4:12:0f:cc,84:82:f4:12:16:04,84:82:f4:12:1a:34,84:82:f4:12:1b:00,84:82:f4:12:1b:5c,84:82:f4:12:1d:54,84:82:f4:12:1e:e4,84:82:f4:12:20:24,84:82:f4:12:20:b4,84:82:f4:12:20:ec,84:82:f4:12:25:70,84:82:f4:12:26:44,84:82:f4:12:28:50,84:82:f4:12:38:ec,84:82:f4:13:0b:64,84:82:f4:13:70:e4,84:82:f4:13:84:c0,84:82:f4:13:95:a8,84:82:f4:13:a3:c8,84:82:f4:13:a3:d0,84:82:f4:13:a4:08,84:82:f4:13:a4:10,84:82:f4:13:a4:54,84:82:f4:13:a4:70,84:82:f4:17:0c:5c,84:82:f4:17:0f:bc,84:82:f4:17:10:f8,84:82:f4:17:44:e4,84:82:f4:17:46:a8,84:82:f4:17:49:40,84:82:f4:17:65:00,84:82:f4:17:65:d4,84:82:f4:17:65:f4,84:82:f4:17:65:fc,84:82:f4:17:67:40,84:82:f4:17:68:a0,84:82:f4:17:68:c8,84:82:f4:17:6c:a4,84:82:f4:17:6f:20,84:82:f4:17:70:5c,84:82:f4:17:70:e0,84:82:f4:17:c1:4c,84:82:f4:17:c1:58,84:82:f4:17:c1:68,84:82:f4:17:c1:70,84:82:f4:17:c1:e4,84:82:f4:17:c1:f8,84:82:f4:17:c2:24,84:82:f4:17:c2:6c,84:82:f4:17:c2:94,84:82:f4:17:c2:9c,84:82:f4:18:27:26,84:82:f4:18:27:70,84:82:f4:18:2d:8c,84:82:f4:18:2d:90,84:82:f4:18:2e:e4,84:82:f4:18:2f:3c,84:82:f4:18:2f:68,84:82:f4:18:33:20,84:82:f4:18:33:4c,84:82:f4:18:33:60,84:82:f4:18:35:30,84:82:f4:18:e1:dc,84:82:f4:18:ee:f8,84:82:f4:18:ef:58,84:82:f4:18:ef:90,84:82:f4:18:f0:10,84:82:f4:18:f2:2c,84:82:f4:18:f7:48,84:82:f4:18:fb:04,84:82:f4:18:fb:4c,84:82:f4:18:fb:a4,84:82:f4:18:fb:d8,84:82:f4:18:fb:f0,84:82:f4:18:fc:04,84:82:f4:18:fd:a0,84:82:f4:19:01:1c,84:82:f4:19:02:dc,84:82:f4:19:03:34,84:82:f4:19:04:9c,84:82:f4:19:06:e0,84:82:f4:19:07:80,84:82:f4:19:08:80,84:82:f4:19:0c:68,84:82:f4:19:11:d8,84:82:f4:19:13:b8,84:82:f4:19:1f:0c,84:82:f4:19:20:e0,84:82:f4:19:22:10,84:82:f4:19:29:c0,84:82:f4:19:dd:c0,84:82:f4:19:e0:6b,84:82:f4:19:e2:07,84:82:f4:19:e4:4f,84:82:f4:19:ea:eb,84:82:f4:19:ec:db,84:82:f4:19:f0:17,84:82:f4:19:f0:ab,84:82:f4:19:f1:03,84:82:f4:19:f7:47,84:82:f4:1a:09:93,84:82:f4:1a:14:1b,84:82:f4:1a:20:b3,84:82:f4:1a:21:c7,84:82:f4:1a:22:bf,84:82:f4:1a:26:5f,84:82:f4:1a:52:63,84:82:f4:1a:67:d7,84:82:f4:1a:70:e7,84:82:f4:1a:7a:c0,84:82:f4:1a:7d:b4,84:82:f4:1a:7d:d4,84:82:f4:1a:7f:14,84:82:f4:1a:7f:cc,84:82:f4:1a:80:0c,84:82:f4:1a:80:84,84:82:f4:1a:82:50,84:82:f4:1a:84:10,84:82:f4:1a:84:e8,84:82:f4:1a:8a:48,84:82:f4:1a:8c:0c,84:82:f4:1a:8c:fc,84:82:f4:1a:8d:e0,84:82:f4:1a:8e:00,84:82:f4:1a:8e:18,84:82:f4:1a:8f:0c,84:82:f4:1a:8f:f8,84:82:f4:1a:91:28,84:82:f4:1a:91:38,84:82:f4:1a:91:c4,84:82:f4:1a:92:f0,84:82:f4:1a:97:30,84:82:f4:1a:9a:5c,84:82:f4:1a:9e:f0,84:82:f4:1a:a1:38,84:82:f4:1a:a2:50,84:82:f4:1a:a3:b4,84:82:f4:1a:a5:6c,84:82:f4:1a:aa:fc,84:82:f4:1a:ab:34,84:82:f4:1a:ab:6c,84:82:f4:1a:b0:40,84:82:f4:1a:b1:0c,84:82:f4:1a:b1:cc,84:82:f4:1a:b4:80,84:82:f4:1a:b6:38,84:82:f4:1a:bc:1c,84:82:f4:1a:bc:44,84:82:f4:1a:bc:88,84:82:f4:1a:bc:ac,84:82:f4:1a:bd:f4,84:82:f4:1a:c1:74,84:82:f4:1a:c4:e8,84:82:f4:1a:c6:ac,84:82:f4:1a:c7:4c,84:82:f4:1a:ca:0c,84:82:f4:1a:ca:7c,84:82:f4:1a:cb:ec,84:82:f4:1a:cc:68,84:82:f4:1a:cd:3c,84:82:f4:1a:cd:58,84:82:f4:1a:cd:f8,84:82:f4:1a:cf:c0,84:82:f4:1a:d1:f4,84:82:f4:1a:d2:5c,84:82:f4:1a:d5:a0,84:82:f4:1a:d6:00,84:82:f4:1a:d6:e4,84:82:f4:1a:df:b0,84:82:f4:1a:df:c4,84:82:f4:1a:df:cc,84:82:f4:1a:df:ec,84:82:f4:1a:e0:20,84:82:f4:1a:e0:34,84:82:f4:1a:e0:68,84:82:f4:1a:e1:40,84:82:f4:1a:e1:c8,84:82:f4:1a:e1:cc,84:82:f4:1a:e3:18,84:82:f4:1a:ea:0c,84:82:f4:1a:eb:84,84:82:f4:1a:ee:90,84:82:f4:1a:ee:dc,84:82:f4:1a:f2:f4,84:82:f4:1a:f5:24,84:82:f4:1b:01:a4,84:82:f4:1b:05:d8,84:82:f4:1b:0a:18,84:82:f4:1b:0c:8c,84:82:f4:1b:0d:6c,84:82:f4:1b:0f:84,84:82:f4:1b:12:38,84:82:f4:1b:15:5c,84:82:f4:1b:19:00,84:82:f4:1b:1d:48,84:82:f4:1b:1f:d8,84:82:f4:1b:20:38,84:82:f4:1b:20:88,84:82:f4:1b:23:8c,84:82:f4:1b:2a:24,84:82:f4:1b:2b:44,84:82:f4:1b:2b:84,84:82:f4:1b:2d:90,84:82:f4:1b:32:70,84:82:f4:1b:33:30,84:82:f4:1b:33:6c,84:82:f4:1b:3a:f4,84:82:f4:1b:3b:6c,84:82:f4:1b:41:54,84:82:f4:1b:46:44,84:82:f4:1b:51:68,84:82:f4:1b:51:74,84:82:f4:1b:52:b8,84:82:f4:1b:53:bc,84:82:f4:1b:56:d4,84:82:f4:1b:5f:f8,84:82:f4:1b:77:04,84:82:f4:1b:7a:24,84:82:f4:1b:7e:18,84:82:f4:1b:82:98,84:82:f4:1b:87:44,84:82:f4:1b:88:c8,84:82:f4:1b:8d:40,84:82:f4:1b:8d:58,84:82:f4:1b:8d:f8,84:82:f4:1b:92:c8,84:82:f4:1b:99:c0,84:82:f4:1b:9a:50,84:82:f4:1b:9e:94,84:82:f4:1b:a4:c8,84:82:f4:1b:a4:e4,84:82:f4:1b:a8:54,84:82:f4:1b:a8:78,84:82:f4:1b:a8:80,84:82:f4:1b:a9:84,84:82:f4:1b:aa:4c,84:82:f4:1b:ab:84,84:82:f4:1b:ac:28,84:82:f4:1b:ad:e4,84:82:f4:1b:ae:cc,84:82:f4:1b:b2:f4,84:82:f4:1b:b5:a8,84:82:f4:1b:b8:88,84:82:f4:1b:ba:7c,84:82:f4:1b:ba:fc,84:82:f4:1b:bd:e4,84:82:f4:1b:bf:a8,84:82:f4:1b:cc:20,84:82:f4:1b:cd:c4,84:82:f4:1b:d6:74,84:82:f4:1b:d7:54,84:82:f4:1b:d8:50,84:82:f4:1b:d9:b8,84:82:f4:1b:da:ec,84:82:f4:1b:db:90,84:82:f4:1b:de:58,84:82:f4:1b:e0:ac,84:82:f4:1b:e2:78,84:82:f4:1b:e2:fc,84:82:f4:1b:e4:d4,84:82:f4:1c:45:74,84:82:f4:1c:45:b4,84:82:f4:1c:45:bc,84:82:f4:1c:45:cc,84:82:f4:1c:45:f0,84:82:f4:1c:45:f8,84:82:f4:1c:46:04,84:82:f4:1c:46:14,84:82:f4:1c:46:28,84:82:f4:1c:46:40,84:82:f4:1c:46:88,84:82:f4:1c:46:b0,84:82:f4:1c:46:c0,84:82:f4:1c:46:c8,84:82:f4:1c:46:cc,84:82:f4:1c:54:d4,84:82:f4:1c:95:b4,84:82:f4:1c:96:94,84:82:f4:1c:97:24,84:82:f4:1c:a0:1c,84:82:f4:1c:b1:18,84:82:f4:1c:c0:ec,84:82:f4:1f:00:c0,84:82:f4:20:20:f0,84:82:f4:23:17:5c,84:82:f4:23:18:08,84:82:f4:23:1a:44,84:82:f4:23:1a:8c,84:82:f4:23:1b:08,84:82:f4:23:1f:40,84:82:f4:23:1f:90,84:82:f4:23:20:74,84:82:f4:23:2f:2c,84:82:f4:23:3d:d4,84:82:f4:23:3f:e4,84:82:f4:23:43:44,84:82:f4:23:44:7c,84:82:f4:23:46:7c,84:82:f4:23:46:f0,84:82:f4:23:48:74,84:82:f4:23:4f:a0,84:82:f4:23:51:30,84:82:f4:23:53:a0,84:82:f4:23:54:d4,84:82:f4:23:5f:58,84:82:f4:23:62:b8,84:82:f4:23:63:a8,84:82:f4:23:63:dc,84:82:f4:23:65:c0,84:82:f4:23:67:78,84:82:f4:23:67:8c,84:82:f4:23:6a:1c,84:82:f4:23:9e:9c,84:82:f4:23:9f:a0,84:82:f4:23:ab:dc,84:82:f4:23:ac:28,84:82:f4:23:ad:a4,84:82:f4:23:af:44,84:82:f4:23:b1:b4,84:82:f4:23:b4:a4,84:82:f4:23:b4:d0,84:82:f4:23:b7:30,84:82:f4:23:b7:5c,84:82:f4:23:bf:38,84:82:f4:23:c1:4c,84:82:f4:23:fa:10,84:82:f4:23:fa:64,84:82:f4:23:fa:78,84:82:f4:23:fa:d4,84:82:f4:23:fd:d0,84:82:f4:23:ff:14,84:82:f4:24:00:c0,84:82:f4:2d:a8:10,84:82:f4:2d:a8:1c,84:82:f4:2d:a8:5c,84:82:f4:2d:a8:cc,84:82:f4:2d:a8:dc,84:82:f4:2d:ac:58,84:82:f4:2d:ac:d0,84:82:f4:2d:ae:54,84:82:f4:2d:ae:98,84:82:f4:2d:ae:a8,84:82:f4:2d:b1:50,84:82:f4:2d:b4:28,84:82:f4:2d:b4:6c,84:82:f4:2d:b6:5c,84:82:f4:2d:c3:28,84:82:f4:2d:c3:b0,84:82:f4:2d:c3:c0,84:82:f4:2d:c4:a8,84:82:f4:2d:c5:88,84:82:f4:31:35:f0,84:82:f4:31:60:a4,84:82:f4:31:63:44,84:82:f4:31:63:cc,84:82:f4:31:63:e4,84:82:f4:31:63:ec,84:82:f4:31:63:f4,84:82:f4:31:69:e0,84:82:f4:31:70:60,84:82:f4:31:7c:b0,84:82:f4:31:8d:58,84:82:f4:31:8e:ec,84:82:f4:31:8f:d4,84:82:f4:31:92:24,84:82:f4:31:94:30,84:82:f4:31:96:3c,84:82:f4:31:96:f8,84:82:f4:31:97:50,84:82:f4:31:98:50,84:82:f4:31:99:5c,84:82:f4:31:9a:e0,84:82:f4:31:9b:28,84:82:f4:31:9b:50,84:82:f4:31:9b:ec,84:82:f4:31:9b:f4,84:82:f4:32:2f:84,84:82:f4:32:41:20,84:82:f4:32:54:64,84:82:f4:32:59:b0,84:82:f4:32:63:28,84:82:f4:32:64:18,84:82:f4:32:64:64,84:82:f4:32:64:c8,84:82:f4:32:64:cc,84:82:f4:32:65:58,84:82:f4:32:65:60,84:82:f4:32:65:f4,84:82:f4:32:66:28,84:82:f4:32:66:70,84:82:f4:32:66:9c,84:82:f4:32:68:fc,84:82:f4:32:69:a4,84:82:f4:32:6b:04,84:82:f4:32:6c:fc,84:82:f4:32:6d:40,84:82:f4:32:6f:a0,84:82:f4:32:70:1c,84:82:f4:32:71:a8,84:82:f4:32:72:88,84:82:f4:32:72:a0,84:82:f4:32:72:d4,84:82:f4:32:93:f0,84:82:f4:32:94:44,84:82:f4:32:94:6c,84:82:f4:32:94:b0,84:82:f4:32:94:d8,84:82:f4:32:94:fc,84:82:f4:32:95:18,84:82:f4:32:95:b0,84:82:f4:32:98:3c,84:82:f4:32:98:64,84:82:f4:32:98:a8,84:82:f4:32:98:dc,84:82:f4:32:99:90,84:82:f4:32:99:b0,84:82:f4:32:9b:3c,84:82:f4:32:9b:6c,84:82:f4:32:a3:88,84:82:f4:32:a5:78,84:82:f4:32:ad:14,84:82:f4:32:b0:9c,84:82:f4:32:b3:b3,84:82:f4:32:b7:9f,84:82:f4:32:b7:cb,84:82:f4:32:b7:d3,84:82:f4:32:b7:df,84:82:f4:32:b8:bb,84:82:f4:32:b9:0b,84:82:f4:32:b9:3f,84:82:f4:32:b9:7b,84:82:f4:32:ba:e3,84:82:f4:32:ba:ff,84:82:f4:32:bb:a7,84:82:f4:32:bb:d7,84:82:f4:32:bb:e3,84:82:f4:32:bb:ef,84:82:f4:32:bc:7b,84:82:f4:32:bd:bf,84:82:f4:32:be:53,84:82:f4:32:bf:1f,84:82:f4:32:bf:7f,84:82:f4:32:bf:ab,84:82:f4:32:bf:bf,84:82:f4:32:c0:1b,84:82:f4:32:c0:2b,84:82:f4:32:c0:43,84:82:f4:32:c0:4b,84:82:f4:32:c0:4f,84:82:f4:32:c0:57,84:82:f4:32:c0:7f,84:82:f4:32:c0:97,84:82:f4:32:c0:bf,84:82:f4:32:c1:1f,84:82:f4:32:c1:4f,84:82:f4:32:c1:5f,84:82:f4:32:c1:6f,84:82:f4:32:c1:7b,84:82:f4:32:c1:87,84:82:f4:32:c1:9f,84:82:f4:32:c1:b3,84:82:f4:32:c1:cf,84:82:f4:32:c1:e7,84:82:f4:32:c1:fb,84:82:f4:32:c2:27,84:82:f4:32:c2:4f,84:82:f4:32:c2:57,84:82:f4:32:c2:63,84:82:f4:32:c2:ab,84:82:f4:32:c2:af,84:82:f4:32:c2:b3,84:82:f4:32:c2:cf,84:82:f4:32:c2:d3,84:82:f4:32:c2:d7,84:82:f4:32:c2:ff,84:82:f4:32:c3:03,84:82:f4:32:c3:07,84:82:f4:32:c3:0f,84:82:f4:32:c3:23,84:82:f4:32:c3:27,84:82:f4:32:c3:57,84:82:f4:32:c3:67,84:82:f4:32:c3:6f,84:82:f4:32:c3:ef,84:82:f4:32:c3:f3,84:82:f4:32:c4:0b,84:82:f4:32:c4:0f,84:82:f4:32:c4:33,84:82:f4:32:c5:0f,84:82:f4:32:c5:1b,84:82:f4:32:c5:37,84:82:f4:32:c5:6b,84:82:f4:32:c5:9f,84:82:f4:32:c5:d7,84:82:f4:33:41:d8,84:82:f4:33:43:44,84:82:f4:33:4f:dc,84:82:f4:33:50:fc,84:82:f4:33:59:88,84:82:f4:33:5a:a0,84:82:f4:33:5b:40,84:82:f4:33:5b:4c,84:82:f4:33:5b:fc,84:82:f4:33:5c:0c,84:82:f4:33:5c:18,84:82:f4:33:5c:28,84:82:f4:33:5c:3c,84:82:f4:33:5c:84,84:82:f4:33:5e:cc,84:82:f4:33:5f:ec,84:82:f4:33:60:e4,84:82:f4:33:61:34,84:82:f4:33:61:60,84:82:f4:33:61:e0,84:82:f4:33:62:04,84:82:f4:33:62:b0,84:82:f4:33:63:00,84:82:f4:33:63:14,84:82:f4:33:63:40,84:82:f4:33:63:44,84:82:f4:33:63:4c,84:82:f4:33:63:f4,84:82:f4:33:65:84,84:82:f4:33:65:98,84:82:f4:33:65:fc,84:82:f4:33:66:4c,84:82:f4:33:66:58,84:82:f4:33:67:70,84:82:f4:33:68:e8,84:82:f4:33:69:38,84:82:f4:33:6a:e0,84:82:f4:55:54:27,84:82:f4:60:3c:20,84:82:f4:60:61:00,84:82:f4:60:70:00,84:82:f4:60:a1:40,84:82:f4:77:77:00,84:82:f4:80:02:28,84:82:f4:80:04:3e,84:82:f4:88:88:00,84:82:f4:90:00:28,84:82:f4:90:03:78,84:82:f4:90:4e:57,84:82:f4:99:99:00,84:82:f4:aa:aa:01,84:82:f5:06:55:27,92:66:75:02:00:05";
//		String macs = "84:82:f4:23:07:28";
		String[] arr = macs.split(",");
		int count = 0;
		for(String mac:arr){
			WifiDeviceSharedNetwork snk = sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().getById(mac);
			if(snk == null){
				System.out.println("snk is null " + mac );
				continue;
			}
			SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
			if(snkDTO == null){
				System.out.println("snk DTO is null " + mac );
				continue;
			}
			snkDTO.turnOff();
			snk.putInnerModel(snkDTO);
			sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(snk);
				
				
			{
				WifiDevice wifiDevice = wifiDeviceService.getById(snk.getId());
				if(wifiDevice != null && wifiDevice.isOnline()){
					String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,
							OperationDS.DS_SharedNetworkWifi_Stop, snk.getId(), CMDBuilder.AutoGen,
							JsonHelper.getJSONString(snkDTO.getPsn()), DeviceStatusExchangeDTO
									.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),
							deviceCMDGenFacadeService);
					daemonRpcService.wifiDeviceCmdDown(null, snk.getId(), sharedNetworkCMD);
					count ++;
					System.out.println(String.format("mac[%s] CMD[%s]", snk.getId(),sharedNetworkCMD));
				}else{
					System.out.println(String.format("mac[%s] not online", snk.getId()));
				}
			}
		}
		System.out.println("finished, send online cmd:"  + count);
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
