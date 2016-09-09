package com.bhu.vas.di.op.migrate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.ds.device.facade.DeviceCMDGenFacadeService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 根据指定文件中提供的mac列表，给其中在线的mac重新下发共享网络配置
 * @author Yetao
 *
 */
public class DeviceSharedNetworksResendFromFileOP {
	public static void main(String[] argv){
		
		if(argv == null || argv.length < 1){
			System.out.println("Please give the input macs file");
			return;
		}
			
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, DeviceSharedNetworksResendFromFileOP.class);
		ctx.start();
		
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceCMDGenFacadeService deviceCMDGenFacadeService = (DeviceCMDGenFacadeService)ctx.getBean("deviceCMDGenFacadeService");
		SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		IDaemonRpcService daemonRpcService =  (IDaemonRpcService)ctx.getBean("daemonRpcService");
		
		AtomicInteger total = new AtomicInteger(0);
		AtomicInteger matched = new AtomicInteger(0);
		AtomicInteger matched_send = new AtomicInteger(0);
		
		
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = null;
			fis = new FileInputStream(argv[0]);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			while ((str = br.readLine()) != null) {
				String[] macs = str.split(",");
				if(macs == null || macs.length <= 0)
					continue;
				for(String mac:macs){
					mac = mac.trim();
					total.incrementAndGet();
					WifiDeviceSharedNetwork snk = sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().getById(mac);
					if(snk == null){
						System.out.println(String.format("mac[%s] snk not exists", mac));
						continue;
					}
					SharedNetworkSettingDTO snkDTO = snk.getInnerModel();
					if(snkDTO == null){
						System.out.println(String.format("mac[%s] snkDto not exists", mac));
						continue;
					}
					if(!snkDTO.isOn()){
						System.out.println(String.format("mac[%s] snkDto is not on", mac));
						continue;
					}
					matched.incrementAndGet();
					WifiDevice wifiDevice = wifiDeviceService.getById(mac);
					if(wifiDevice == null || !wifiDevice.isOnline()){
						System.out.println(String.format("mac[%s] not exists or offline", mac));
						continue;
					}

					ParamSharedNetworkDTO psn = snkDTO.getPsn();
					String sharedNetworkCMD = CMDBuilder.autoBuilderCMD4Opt(OperationCMD.ModifyDeviceSetting,
							OperationDS.DS_SharedNetworkWifi_Start, snk.getId(), CMDBuilder.AutoGen,
							JsonHelper.getJSONString(psn), DeviceStatusExchangeDTO
									.build(wifiDevice.getWork_mode(), wifiDevice.getOrig_swver()),
							deviceCMDGenFacadeService);
					daemonRpcService.wifiDeviceCmdDown(null, snk.getId(), sharedNetworkCMD);
					matched_send.incrementAndGet();
					System.out.println(String.format("mac[%s] send CMD[%s]", mac,sharedNetworkCMD));
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}		
		
		System.out.println("total:"+total + " macthed:"+matched.get() +" send:"+matched_send);
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
