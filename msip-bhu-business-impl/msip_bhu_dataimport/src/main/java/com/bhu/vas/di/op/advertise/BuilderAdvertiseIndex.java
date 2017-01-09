package com.bhu.vas.di.op.advertise;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocumentHelper;
import com.bhu.vas.business.search.model.device.WifiDeviceDocument;
import com.bhu.vas.business.search.model.device.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.advertise.AdvertiseDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class BuilderAdvertiseIndex {
	
	private static AdvertiseService advertiseService;
	private static AdvertiseDataSearchService advertiseDataSearchService;

	
	public static void main(String[] args) {
		initialize();
		List<Advertise> entitys = advertiseService.findAll();
		advertiseIndexs(entitys);
	}
	
	public static void initialize(){
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		advertiseService = (AdvertiseService)ctx.getBean("advertiseService");
		advertiseDataSearchService = (AdvertiseDataSearchService)ctx.getBean("advertiseDataSearchService");
	}
	
	public static void advertiseIndexs(List<Advertise> entitys){
		try{
			List<AdvertiseDocument> docs = new ArrayList<AdvertiseDocument>();
			AdvertiseDocument doc = null;
			int index = 0;
			for(Advertise advertise : entitys){
				doc = AdvertiseDocumentHelper.fromNormalAdvertise(advertise);
				if(doc != null){
					System.out.println("index :"+ ++index);
					advertiseDataSearchService.insertIndex(doc, false, false);
				}
			}
			advertiseDataSearchService.refresh(true);
			
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		System.exit(1);
	}
}
