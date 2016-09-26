package com.bhu.vas.di.op.device;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.tag.facade.TagGroupFacadeService;
import com.bhu.vas.di.op.deviceStatitics.DeviceOrderStatitics;

public class deviceTranslation {
	public static void main(String[] args) {
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, DeviceOrderStatitics.class);
		ctx.start();
		
		TagGroupFacadeService tagGroupFacadeService = (TagGroupFacadeService) ctx.getBean("tagGroupFacadeService",TagGroupFacadeService.class);
		DeviceFacadeService deviceFacadeService = (DeviceFacadeService) ctx.getBean("deviceFacadeService",DeviceFacadeService.class);
		List<TagDevices> tagDevices= tagGroupFacadeService.getTagDevicesService().findAll();
		if(tagDevices!=null){
			for(TagDevices i:tagDevices){
				String tags=i.getTag2ES();
				String[] tagsArray=tags.split(" ");
				boolean flag=false;
				String channel_lv1=StringUtils.EMPTY;
				for(String j:tagsArray){
					if(j.equals("WA")){
						flag=true;
						channel_lv1="WA";
						break;
					}else if(j.equals("ZJ")){
						flag=true;
						channel_lv1="ZJ";
						break;
					}else if(j.equals("YYS")){
						flag=true;
						channel_lv1="YYS";
						break;
					}
				}
				if(!flag){
					channel_lv1="XSXX";
				}
				WifiDevice wifiDevice= deviceFacadeService.getWifiDeviceService().getById(i.getId());
				if(wifiDevice.getHdtype().equals("H106")||wifiDevice.getHdtype().equals("H401")||wifiDevice.getHdtype().equals("H112")||wifiDevice.getHdtype().equals("H901")||wifiDevice.getHdtype().equals("H403")){
					wifiDevice.setChannel_lv1(channel_lv1);
					deviceFacadeService.getWifiDeviceService().update(wifiDevice);
				}
			}
		}
		
		ctx.stop();
		ctx.close();
		System.exit(0);
	}
}
