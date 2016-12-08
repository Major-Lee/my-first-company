package com.bhu.vas.di.op.migrate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 对所有现网的urouter设备(排除特殊灰度), 全部设置为不可关闭打赏网络。
 * @author Yetao
 *
 */
public class UserSharedNetworksForceOpenrOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksForceOpenrOp.class);
		ctx.start();
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceCMDGenFacadeService deviceCMDGenFacadeService = (DeviceCMDGenFacadeService)ctx.getBean("deviceCMDGenFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		UserWifiDeviceService userWifiDeviceService = (UserWifiDeviceService)ctx.getBean("userWifiDeviceService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");

		Set<String> exp = new HashSet<String>();
		exp.add("84:82:f4:19:00:b4");
		exp.add("84:82:f4:23:05:fc");
		exp.add("84:82:f4:23:06:04");
		exp.add("84:82:f4:24:04:5c");
		exp.add("84:82:f4:24:0a:74");
		exp.add("84:82:f4:24:6a:50");
		exp.add("84:82:f4:24:6c:b4");
		exp.add("84:82:f4:24:71:20");
		exp.add("84:82:f4:24:71:84");
		exp.add("84:82:f4:24:71:e0");
		exp.add("84:82:f4:24:72:c4");
		exp.add("84:82:f4:24:73:ac");
		exp.add("84:82:f4:24:77:70");
		exp.add("84:82:f4:24:77:74");
		exp.add("84:82:f4:24:78:5c");
		exp.add("84:82:f4:24:78:b0");
		exp.add("84:82:f4:24:78:c0");
		exp.add("84:82:f4:24:78:e4");
		exp.add("84:82:f4:27:95:e8");
		exp.add("84:82:f4:27:9c:84");
		exp.add("84:82:f4:28:09:b4");
		exp.add("84:82:f4:28:24:68");
		exp.add("84:82:f4:28:29:04");
		exp.add("84:82:f4:28:2d:d0");
		exp.add("84:82:f4:28:30:30");
		exp.add("84:82:f4:28:31:18");
		exp.add("84:82:f4:28:31:5c");
		exp.add("84:82:f4:28:31:94");
		exp.add("84:82:f4:28:35:ec");
		exp.add("84:82:f4:28:36:28");
		exp.add("84:82:f4:28:40:50");
		exp.add("84:82:f4:28:40:6c");
		exp.add("84:82:f4:28:40:c0");
		exp.add("84:82:f4:28:42:a4");
		exp.add("84:82:f4:28:42:bc");
		exp.add("84:82:f4:28:43:08");
		exp.add("84:82:f4:28:43:98");
		exp.add("84:82:f4:28:43:9c");
		exp.add("84:82:f4:28:45:94");
		exp.add("84:82:f4:28:46:18");
		exp.add("84:82:f4:28:49:60");
		exp.add("84:82:f4:28:49:e8");
		exp.add("84:82:f4:28:4b:f0");
		exp.add("84:82:f4:28:4c:04");
		exp.add("84:82:f4:28:4c:64");
		exp.add("84:82:f4:28:51:90");
		exp.add("84:82:f4:28:52:e4");
		exp.add("84:82:f4:28:52:f8");
		exp.add("84:82:f4:28:56:28");
		exp.add("84:82:f4:28:57:04");
		exp.add("84:82:f4:28:57:34");
		exp.add("84:82:f4:28:58:88");
		exp.add("84:82:f4:28:5e:44");
		exp.add("84:82:f4:28:5f:1c");
		exp.add("84:82:f4:28:5f:b0");
		exp.add("84:82:f4:28:5f:c0");
		exp.add("84:82:f4:28:60:94");
		exp.add("84:82:f4:28:65:0c");
		exp.add("84:82:f4:28:66:34");
		exp.add("84:82:f4:28:66:98");
		exp.add("84:82:f4:28:67:94");
		exp.add("84:82:f4:28:68:60");
		exp.add("84:82:f4:28:69:48");
		exp.add("84:82:f4:28:6a:bc");
		exp.add("84:82:f4:28:6b:7c");
		exp.add("84:82:f4:28:6b:bc");
		exp.add("84:82:f4:28:6b:c0");
		exp.add("84:82:f4:28:6c:18");
		exp.add("84:82:f4:28:6c:70");
		exp.add("84:82:f4:28:6c:a0");
		exp.add("84:82:f4:28:6d:6c");
		exp.add("84:82:f4:28:6e:64");
		exp.add("84:82:f4:28:6f:60");
		exp.add("84:82:f4:28:6f:7c");
		exp.add("84:82:f4:28:6f:c8");
		exp.add("84:82:f4:28:70:8c");
		exp.add("84:82:f4:28:70:ac");
		exp.add("84:82:f4:28:70:d8");
		exp.add("84:82:f4:28:71:88");
		exp.add("84:82:f4:28:71:94");
		exp.add("84:82:f4:28:71:f0");
		exp.add("84:82:f4:28:73:64");
		exp.add("84:82:f4:28:73:78");
		exp.add("84:82:f4:28:75:50");
		exp.add("84:82:f4:28:75:8c");
		exp.add("84:82:f4:28:75:b4");
		exp.add("84:82:f4:28:76:34");
		exp.add("84:82:f4:28:7b:00");
		exp.add("84:82:f4:28:7b:b4");
		exp.add("84:82:f4:28:7c:4c");
		exp.add("84:82:f4:28:7d:20");
		exp.add("84:82:f4:28:7d:a0");
		exp.add("84:82:f4:28:7f:08");
		exp.add("84:82:f4:28:7f:58");
		exp.add("84:82:f4:28:7f:64");
		exp.add("84:82:f4:28:7f:78");
		exp.add("84:82:f4:28:82:10");
		exp.add("84:82:f4:28:84:24");
		exp.add("84:82:f4:28:84:98");
		exp.add("84:82:f4:28:84:9c");
		exp.add("84:82:f4:28:85:58");
		exp.add("84:82:f4:28:87:8c");
		exp.add("84:82:f4:28:88:1c");
		exp.add("84:82:f4:28:88:34");
		exp.add("84:82:f4:28:88:58");
		exp.add("84:82:f4:28:88:b8");
		exp.add("84:82:f4:28:88:d0");
		exp.add("84:82:f4:28:89:28");
		exp.add("84:82:f4:28:8b:a0");
		exp.add("84:82:f4:28:8b:e8");
		exp.add("84:82:f4:28:8d:18");
		exp.add("84:82:f4:28:8d:94");
		exp.add("84:82:f4:28:8f:60");
		exp.add("84:82:f4:28:90:48");
		exp.add("84:82:f4:28:91:bc");
		exp.add("84:82:f4:28:91:f0");
		exp.add("84:82:f4:28:93:d8");
		exp.add("84:82:f4:28:93:f0");
		exp.add("84:82:f4:28:94:40");
		exp.add("84:82:f4:28:94:b8");
		exp.add("84:82:f4:28:95:fc");
		exp.add("84:82:f4:28:96:08");
		exp.add("84:82:f4:28:99:14");
		exp.add("84:82:f4:28:9a:28");
		exp.add("84:82:f4:28:9a:4c");
		exp.add("84:82:f4:28:9a:ec");
		exp.add("84:82:f4:28:9c:ac");
		exp.add("84:82:f4:28:9c:d8");
		exp.add("84:82:f4:28:9c:f8");
		exp.add("84:82:f4:28:9d:08");
		exp.add("84:82:f4:28:9d:40");
		exp.add("84:82:f4:28:9d:44");
		exp.add("84:82:f4:28:9d:ac");
		exp.add("84:82:f4:28:9d:c4");
		exp.add("84:82:f4:28:9d:ec");
		exp.add("84:82:f4:28:9e:fc");
		exp.add("84:82:f4:28:9f:30");
		exp.add("84:82:f4:28:9f:90");
		exp.add("84:82:f4:28:9f:dc");
		exp.add("84:82:f4:28:9f:e0");
		exp.add("84:82:f4:28:a0:98");
		exp.add("84:82:f4:28:a0:b4");
		exp.add("84:82:f4:28:a3:48");
		exp.add("84:82:f4:28:a3:64");
		exp.add("84:82:f4:28:a4:cc");
		exp.add("84:82:f4:28:a5:fc");
		exp.add("84:82:f4:28:a6:60");
		exp.add("84:82:f4:28:a6:78");
		exp.add("84:82:f4:28:a6:8c");
		exp.add("84:82:f4:28:a6:e4");
		exp.add("84:82:f4:28:a6:f8");
		exp.add("84:82:f4:28:a7:f0");
		exp.add("84:82:f4:28:a8:d4");
		exp.add("84:82:f4:28:a8:e0");
		exp.add("84:82:f4:28:a9:30");
		exp.add("84:82:f4:28:a9:c4");
		exp.add("84:82:f4:28:a9:d4");
		exp.add("84:82:f4:28:aa:cc");
		exp.add("84:82:f4:28:ac:40");
		exp.add("84:82:f4:28:ac:7c");
		exp.add("84:82:f4:28:ad:10");
		exp.add("84:82:f4:28:ad:d0");
		exp.add("84:82:f4:28:ad:d4");
		exp.add("84:82:f4:28:ad:d8");
		exp.add("84:82:f4:28:ad:ec");
		exp.add("84:82:f4:28:ae:c0");
		exp.add("84:82:f4:28:af:04");
		exp.add("84:82:f4:28:af:10");
		exp.add("84:82:f4:28:af:14");
		exp.add("84:82:f4:28:af:20");
		exp.add("84:82:f4:28:b0:78");
		exp.add("84:82:f4:28:b0:a0");
		exp.add("84:82:f4:28:b0:b8");
		exp.add("84:82:f4:28:b1:c8");
		exp.add("84:82:f4:28:b1:dc");
		exp.add("84:82:f4:28:b1:ec");
		exp.add("84:82:f4:28:b2:c4");
		exp.add("84:82:f4:28:b3:f4");
		exp.add("84:82:f4:28:b3:fc");
		exp.add("84:82:f4:28:b6:d8");
		exp.add("84:82:f4:28:b6:e0");
		exp.add("84:82:f4:28:b6:e8");
		exp.add("84:82:f4:28:b6:f0");
		exp.add("84:82:f4:28:b7:04");
		exp.add("84:82:f4:28:b7:20");
		exp.add("84:82:f4:28:b7:3c");
		exp.add("84:82:f4:28:b7:40");
		exp.add("84:82:f4:28:b7:50");
		exp.add("84:82:f4:28:b7:70");
		exp.add("84:82:f4:28:b7:80");
		exp.add("84:82:f4:28:b7:98");
		exp.add("84:82:f4:28:b7:9c");
		exp.add("84:82:f4:28:b7:c0");
		exp.add("84:82:f4:28:b7:c4");
		exp.add("84:82:f4:28:b7:c8");
		exp.add("84:82:f4:28:b7:fc");
		exp.add("84:82:f4:28:b8:0c");
		exp.add("84:82:f4:28:b8:20");
		exp.add("84:82:f4:28:b8:30");
		exp.add("84:82:f4:28:b8:38");
		exp.add("84:82:f4:28:b8:3c");
		exp.add("84:82:f4:28:b8:48");
		exp.add("84:82:f4:28:b8:54");
		exp.add("84:82:f4:28:b8:8c");
		exp.add("84:82:f4:28:b8:94");
		exp.add("84:82:f4:28:b8:b8");
		exp.add("84:82:f4:28:b8:d4");
		exp.add("84:82:f4:28:b8:dc");
		exp.add("84:82:f4:28:b8:ec");
		exp.add("84:82:f4:28:b9:08");
		exp.add("84:82:f4:28:b9:1c");
		exp.add("84:82:f4:28:b9:2c");
		exp.add("84:82:f4:28:b9:34");
		exp.add("84:82:f4:28:b9:58");
		exp.add("84:82:f4:28:b9:5c");
		exp.add("84:82:f4:28:b9:60");
		exp.add("84:82:f4:28:b9:6c");
		exp.add("84:82:f4:28:b9:70");
		exp.add("84:82:f4:28:b9:98");
		exp.add("84:82:f4:28:b9:9c");
		exp.add("84:82:f4:28:b9:a8");
		exp.add("84:82:f4:28:b9:b8");
		exp.add("84:82:f4:28:b9:d4");
		exp.add("84:82:f4:28:b9:f4");
		exp.add("84:82:f4:28:ba:20");
		exp.add("84:82:f4:28:ba:24");
		exp.add("84:82:f4:28:ba:3c");
		exp.add("84:82:f4:28:be:6c");
		exp.add("84:82:f4:28:be:74");
		exp.add("84:82:f4:28:be:a8");
		exp.add("84:82:f4:28:be:b4");
		exp.add("84:82:f4:28:be:b8");
		exp.add("84:82:f4:28:be:e0");
		exp.add("84:82:f4:28:bf:08");
		exp.add("84:82:f4:28:bf:10");
		exp.add("84:82:f4:28:bf:40");
		exp.add("84:82:f4:28:bf:4c");
		exp.add("84:82:f4:28:bf:54");
		exp.add("84:82:f4:28:bf:60");
		exp.add("84:82:f4:28:bf:7c");
		exp.add("84:82:f4:28:bf:b0");
		exp.add("84:82:f4:28:bf:b4");
		exp.add("84:82:f4:28:bf:bc");
		exp.add("84:82:f4:28:bf:d4");
		exp.add("84:82:f4:29:33:48");
		exp.add("84:82:f4:29:ad:ec");
		exp.add("84:82:f4:2b:5c:10");
		exp.add("84:82:f4:2b:78:28");
		exp.add("84:82:f4:2b:8b:68");
		exp.add("84:82:f4:2b:99:84");
		exp.add("84:82:f4:2b:9e:34");
		exp.add("84:82:f4:2b:ac:04");
		exp.add("84:82:f4:2b:ad:34");
		exp.add("84:82:f4:2e:d4:e8");
		exp.add("84:82:f4:2e:f2:0c");
		exp.add("84:82:f4:2e:fc:f4");
		exp.add("84:82:f4:2e:fe:2c");
		exp.add("84:82:f4:2f:04:b0");
		exp.add("84:82:f4:2f:07:c4");
		exp.add("84:82:f4:2f:07:fc");
		exp.add("84:82:f4:2f:24:b0");
		exp.add("84:82:f4:2f:26:98");
		exp.add("84:82:f4:2f:28:44");
		exp.add("84:82:f4:2f:28:70");
		exp.add("84:82:f4:2f:28:a0");
		exp.add("84:82:f4:2f:28:c8");
		exp.add("84:82:f4:2f:28:d8");
		exp.add("84:82:f4:2f:28:dc");
		exp.add("84:82:f4:2f:28:e0");
		exp.add("84:82:f4:2f:28:fc");
		exp.add("84:82:f4:2f:29:10");
		exp.add("84:82:f4:2f:29:1c");
		exp.add("84:82:f4:2f:29:4c");
		exp.add("84:82:f4:2f:29:5c");
		exp.add("84:82:f4:2f:29:ac");
		exp.add("84:82:f4:2f:29:e8");
		exp.add("84:82:f4:2f:2a:68");
		exp.add("84:82:f4:2f:2a:a4");
		exp.add("84:82:f4:2f:2a:dc");
		exp.add("84:82:f4:2f:65:bc");
		exp.add("84:82:f4:2f:65:f4");
		exp.add("84:82:f4:2f:66:24");
		exp.add("84:82:f4:2f:66:34");
		exp.add("84:82:f4:2f:66:68");
		exp.add("84:82:f4:2f:66:d4");
		exp.add("84:82:f4:2f:66:fc");
		exp.add("84:82:f4:31:3d:cb");

		
		
		int count = 0;
		ModelCriteria mc = new ModelCriteria();
		List<String> hdlist = Arrays.asList(new String[]{"H106","H112","H401", "H901", "H801"});
		mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnIn("hdtype", hdlist).andSimpleCaulse(" first_reged_at is not null " ).andColumnNotLike("orig_swver", "%_TS%");
		mc.setOrderByClause("id desc");
		mc.setPageNumber(1);
		mc.setPageSize(200);

//		System.out.println("dev count:" + wifiDeviceService.countByModelCriteria(mc));
	
    	EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String, WifiDevice>(String.class, WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<WifiDevice> list = it.next();
			for(WifiDevice dev:list){
				//遍历所有设备
				if(exp.contains(dev.getId())){
					System.out.println("id:" + dev.getId() + " can't modify");
					continue;
				}
				
				//获取共享网络
				WifiDeviceSharedNetwork wsnk = sharedNetworksFacadeService.fetchDeviceSharedNetworkConfWhenEmptyThenCreate(dev.getId(), true, true);
				if(wsnk == null){
					System.out.println(dev.getId() + " fetch snk fail");
					continue;
				}
				
				SharedNetworkSettingDTO dto = wsnk.getInnerModel();
				if(dto == null){
					System.out.println(dev.getId() + " dto is null");
					continue;
				}
				
				//已经打开的不需要处理
				if(dto.isOn()){
					if(dto.getPsn() == null)
						System.out.println(wsnk.getId() + " ds is no, but no psn");
					continue;
				}
				
				ParamSharedNetworkDTO psn = dto.getPsn();
				
				if(psn == null){
					System.out.println(dev.getId() + " psn is null");
					//删除旧记录，重新生成一个共享网络
					sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().delete(wsnk);
					wsnk = sharedNetworksFacadeService.fetchDeviceSharedNetworkConfWhenEmptyThenCreate(dev.getId(), true, true);
					try{
						dto = wsnk.getInnerModel();
						psn = dto.getPsn();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				if(psn == null){
					System.out.println(dev.getId() + " psn still is null");
					continue;
				}
				
				dto.turnOn(psn);
				wsnk.putInnerModel(dto);
				sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(wsnk);

				count ++;
				
				{
					WifiDevice wifiDevice = wifiDeviceService.getById(wsnk.getId());
					if(wifiDevice != null && wifiDevice.isOnline()){
						String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,
								OperationDS.DS_SharedNetworkWifi_Start, wsnk.getId(), CMDBuilder.AutoGen,
								JsonHelper.getJSONString(psn), DeviceStatusExchangeDTO
										.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),
								deviceCMDGenFacadeService);
						daemonRpcService.wifiDeviceCmdDown(null, wsnk.getId(), sharedNetworkCMD);
						/*psn.switchWorkMode(WifiDeviceHelper.isWorkModeRouter(wifiDevice.getWork_mode()));
						//生成下发指令
						String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,OperationDS.DS_SharedNetworkWifi_Start, 
								snk.getId(), -1,JsonHelper.getJSONString(psn),deviceCMDGenFacadeService);
						DaemonHelper.daemonCmdDown(snk.getId(), sharedNetworkCMD, daemonRpcService);*/
						//.afterDeviceOnline(mac, needLocationQuery,payloads, daemonRpcService);
						System.out.println(String.format("mac[%s] CMD[%s]", wsnk.getId(),sharedNetworkCMD));
					}else{
						System.out.println(String.format("mac[%s] not online", wsnk.getId()));
					}
				}
			}
		}
		

		System.out.println("fix dev:" + count);
		
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
