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

		String macs = "84:82:f4:0a:5f:68,84:82:f4:0a:60:a4,84:82:f4:0a:60:f0,84:82:f4:0a:62:08,84:82:f4:0a:62:58,84:82:f4:0a:63:c0,84:82:f4:0a:64:18,84:82:f4:0a:64:88,84:82:f4:0a:65:68,84:82:f4:0f:80:f0,84:82:f4:17:0b:ac,84:82:f4:19:17:c0,84:82:f4:19:1a:80,84:82:f4:19:20:94,84:82:f4:19:e0:f3,84:82:f4:19:ec:3f,84:82:f4:1a:05:d7,84:82:f4:1a:07:2b,84:82:f4:1a:16:4f,84:82:f4:1a:1d:8b,84:82:f4:1a:55:ef,84:82:f4:1a:58:b3,84:82:f4:1a:60:3f,84:82:f4:1a:69:ab,84:82:f4:1a:6d:23,84:82:f4:1a:71:db,84:82:f4:1a:7b:30,84:82:f4:1a:7b:54,84:82:f4:1a:7b:70,84:82:f4:1a:7b:9c,84:82:f4:1a:7b:a8,84:82:f4:1a:7b:c4,84:82:f4:1a:7b:d4,84:82:f4:1a:7b:e8,84:82:f4:1a:7b:ec,84:82:f4:1a:7c:0c,84:82:f4:1a:7c:64,84:82:f4:1a:7f:70,84:82:f4:1a:7f:90,84:82:f4:1a:7f:b8,84:82:f4:1a:80:28,84:82:f4:1a:80:f8,84:82:f4:1a:81:84,84:82:f4:1a:82:b0,84:82:f4:1a:84:4c,84:82:f4:1a:84:80,84:82:f4:1a:84:a0,84:82:f4:1a:85:90,84:82:f4:1a:87:78,84:82:f4:1a:87:a8,84:82:f4:1a:88:5c,84:82:f4:1a:8b:54,84:82:f4:1a:8b:78,84:82:f4:1a:8c:40,84:82:f4:1a:8d:64,84:82:f4:1a:8e:40,84:82:f4:1a:8f:e8,84:82:f4:1a:92:34,84:82:f4:1a:92:4c,84:82:f4:1a:93:a8,84:82:f4:1a:93:bc,84:82:f4:1a:95:e4,84:82:f4:1a:96:10,84:82:f4:1a:98:34,84:82:f4:1a:98:3c,84:82:f4:1a:98:9c,84:82:f4:1a:f0:14,84:82:f4:1a:f6:0c,84:82:f4:1a:f7:7c,84:82:f4:1a:fb:a4,84:82:f4:1b:00:08,84:82:f4:1b:01:c8,84:82:f4:1b:08:3c,84:82:f4:1b:0c:e0,84:82:f4:1b:0c:f4,84:82:f4:1b:20:2c,84:82:f4:1b:29:40,84:82:f4:1b:2a:20,84:82:f4:1b:5d:28,84:82:f4:1b:5e:60,84:82:f4:1b:5e:74,84:82:f4:1b:5f:98,84:82:f4:1b:72:7c,84:82:f4:1b:72:e4,84:82:f4:1b:73:00,84:82:f4:1b:73:4c,84:82:f4:1b:74:18,84:82:f4:1b:74:54,84:82:f4:1b:76:70,84:82:f4:1b:76:b4,84:82:f4:1b:76:fc,84:82:f4:1b:77:54,84:82:f4:1b:7b:90,84:82:f4:1b:82:f4,84:82:f4:1b:8b:1c,84:82:f4:1b:8c:a8,84:82:f4:1b:92:ac,84:82:f4:1b:9a:20,84:82:f4:1b:a8:98,84:82:f4:1b:ac:30,84:82:f4:1b:ac:7c,84:82:f4:1b:ae:90,84:82:f4:1b:b8:7c,84:82:f4:1b:c2:bc,84:82:f4:1b:c8:e8,84:82:f4:1b:da:f4,84:82:f4:1b:db:88,84:82:f4:1b:e3:a0,84:82:f4:1c:45:d4,84:82:f4:1c:5d:ec,84:82:f4:1c:62:14,84:82:f4:1c:7b:30,84:82:f4:1c:93:d0,84:82:f4:1c:96:88,84:82:f4:1c:b1:18,84:82:f4:1c:be:b0,84:82:f4:1c:c0:4c,84:82:f4:1c:c3:0c,84:82:f4:23:3a:50,84:82:f4:23:45:90,84:82:f4:23:49:28,84:82:f4:23:9f:b4,84:82:f4:23:bc:58,84:82:f4:23:bd:4c,84:82:f4:23:bf:a8,84:82:f4:23:c0:10,84:82:f4:23:c2:54,84:82:f4:23:c4:5c,84:82:f4:23:c5:0c,84:82:f4:23:c6:24,84:82:f4:23:c6:cc,84:82:f4:23:c6:e4,84:82:f4:23:ca:40,84:82:f4:24:0e:7c,84:82:f4:24:0e:b8,84:82:f4:24:0e:e4,84:82:f4:24:0f:28,84:82:f4:27:8b:cc,84:82:f4:27:b2:10,84:82:f4:27:b9:78,84:82:f4:27:bb:28,84:82:f4:27:c2:b0,84:82:f4:27:c3:ac,84:82:f4:27:d4:90,84:82:f4:27:e5:30,84:82:f4:28:00:d8,84:82:f4:28:51:e4,84:82:f4:28:73:68,84:82:f4:28:75:e0,84:82:f4:28:83:04,84:82:f4:28:90:e4,84:82:f4:28:9f:bc,84:82:f4:28:cd:f4,84:82:f4:29:03:f4,84:82:f4:29:0c:40,84:82:f4:29:79:44,84:82:f4:29:79:f4,84:82:f4:29:95:c0,84:82:f4:29:a0:28,84:82:f4:29:d5:d4,84:82:f4:2a:05:ac,84:82:f4:2a:07:ec,84:82:f4:2a:09:30,84:82:f4:2a:0d:98,84:82:f4:2a:10:e4,84:82:f4:2a:40:b4,84:82:f4:2d:a6:a0,84:82:f4:2d:a6:e0,84:82:f4:2d:a7:18,84:82:f4:2d:ac:64,84:82:f4:2d:ac:e8,84:82:f4:2d:ae:58,84:82:f4:2d:ae:a0,84:82:f4:2d:b3:78,84:82:f4:2d:b5:04,84:82:f4:2d:b7:3c,84:82:f4:2d:b7:e4,84:82:f4:2d:bb:90,84:82:f4:2d:bd:28,84:82:f4:2d:bd:a4,84:82:f4:2d:bd:dc,84:82:f4:2d:bd:e0,84:82:f4:2d:bd:f4,84:82:f4:2d:bd:f8,84:82:f4:2d:be:00,84:82:f4:2d:be:10,84:82:f4:2d:be:14,84:82:f4:2d:be:1c,84:82:f4:2d:be:48,84:82:f4:2d:be:bc,84:82:f4:2d:be:c0,84:82:f4:2d:bf:20,84:82:f4:2d:c0:fc,84:82:f4:2d:c2:10,84:82:f4:2d:c4:98,84:82:f4:2e:22:84,84:82:f4:2e:23:04,84:82:f4:2e:54:dc,84:82:f4:2e:57:dc,84:82:f4:2e:cc:f4,84:82:f4:2e:cd:8c,84:82:f4:2e:d4:80,84:82:f4:2e:d6:64,84:82:f4:31:23:e0,84:82:f4:31:50:80,84:82:f4:31:50:94,84:82:f4:31:63:40,84:82:f4:31:65:10,84:82:f4:31:65:58,84:82:f4:31:65:b4,84:82:f4:31:66:34,84:82:f4:31:66:c0,84:82:f4:31:68:f0,84:82:f4:31:6a:54,84:82:f4:31:6a:84,84:82:f4:31:6e:54,84:82:f4:31:6e:64,84:82:f4:31:6e:6c,84:82:f4:31:6e:84,84:82:f4:31:6e:88,84:82:f4:31:70:20,84:82:f4:31:70:28,84:82:f4:31:70:3c,84:82:f4:31:70:5c,84:82:f4:31:71:28,84:82:f4:31:71:78,84:82:f4:31:71:88,84:82:f4:31:71:a0,84:82:f4:31:76:c8,84:82:f4:31:87:d0,84:82:f4:31:89:ec,84:82:f4:31:8c:34,84:82:f4:31:8d:d8,84:82:f4:31:8e:38,84:82:f4:31:8e:a4,84:82:f4:31:8e:c0,84:82:f4:31:91:d8,84:82:f4:31:92:4c,84:82:f4:31:92:d8,84:82:f4:31:93:1c,84:82:f4:31:93:20,84:82:f4:31:93:f0,84:82:f4:31:95:64,84:82:f4:31:98:68,84:82:f4:31:98:74,84:82:f4:31:98:90,84:82:f4:31:98:c4,84:82:f4:31:99:8c,84:82:f4:31:9b:20,84:82:f4:31:9b:bc,84:82:f4:32:65:d4,84:82:f4:32:66:5c,84:82:f4:32:66:c8,84:82:f4:32:6d:38,84:82:f4:32:6f:18,84:82:f4:32:71:04,84:82:f4:32:71:38,84:82:f4:32:72:b8,84:82:f4:32:93:b8,84:82:f4:32:93:d0,84:82:f4:32:94:70,84:82:f4:32:94:8c,84:82:f4:32:96:2c,84:82:f4:32:96:50,84:82:f4:32:96:c8,84:82:f4:32:97:b4,84:82:f4:32:98:54,84:82:f4:32:99:54,84:82:f4:32:99:7c,84:82:f4:32:9a:38,84:82:f4:32:9a:80,84:82:f4:32:9a:b0,84:82:f4:32:9b:18,84:82:f4:32:9b:a8,84:82:f4:32:9c:1c,84:82:f4:32:9d:d0,84:82:f4:32:9e:14,84:82:f4:32:a0:10,84:82:f4:32:a2:04,84:82:f4:32:a2:68,84:82:f4:32:a2:6c,84:82:f4:32:a2:bc,84:82:f4:32:a2:c4,84:82:f4:32:a3:04,84:82:f4:32:a3:1c,84:82:f4:32:a3:6c,84:82:f4:32:a3:7c,84:82:f4:32:a3:b4,84:82:f4:32:a3:e0,84:82:f4:32:a3:e8,84:82:f4:32:a3:f0,84:82:f4:32:a4:1c,84:82:f4:32:a4:3c,84:82:f4:32:a4:58,84:82:f4:32:a4:68,84:82:f4:32:a4:6c,84:82:f4:32:a4:84,84:82:f4:32:a4:a4,84:82:f4:32:a4:b4,84:82:f4:32:a4:b8,84:82:f4:32:a5:14,84:82:f4:32:a5:28,84:82:f4:32:a5:74,84:82:f4:32:ac:40,84:82:f4:32:ad:58,84:82:f4:32:b0:c4,84:82:f4:32:b2:3c,84:82:f4:32:b6:f7,84:82:f4:32:b8:63,84:82:f4:32:b9:57,84:82:f4:32:b9:d3,84:82:f4:32:bf:37,84:82:f4:32:c0:1f,84:82:f4:32:c0:23,84:82:f4:32:c1:5f,84:82:f4:32:c2:37,84:82:f4:32:c2:a3,84:82:f4:32:c2:cb,84:82:f4:32:c2:e7,84:82:f4:32:c3:13,84:82:f4:32:c5:57,84:82:f4:32:c8:63,84:82:f4:32:c9:2b,84:82:f4:32:c9:bb,84:82:f4:32:f5:c0,84:82:f4:32:f5:c4,84:82:f4:33:0d:90,84:82:f4:33:41:68,84:82:f4:33:5c:1c,84:82:f4:33:5c:30,84:82:f4:33:5c:80,84:82:f4:33:5c:c4,84:82:f4:33:5d:c8,84:82:f4:33:5e:00,84:82:f4:33:62:3c,84:82:f4:65:5b:80,84:82:f4:90:03:98,84:82:f4:90:04:1c";
//		String macs = "84:82:f4:23:07:28";
		String[] arr = macs.split(",");
		for(String mac:arr){
			WifiDeviceSharedNetwork snk = sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().getById(mac);
			SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
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
					System.out.println(String.format("mac[%s] CMD[%s]", snk.getId(),sharedNetworkCMD));
				}else{
					System.out.println(String.format("mac[%s] not online", snk.getId()));
				}
			}
		}
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
