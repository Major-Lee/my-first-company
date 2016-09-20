package com.bhu.vas.di.op.repair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.rpc.user.model.UserMobileDevice;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 数据修复
 * 根据用户绑定设备表进行设备push消息redis数据重建
 * @author Edmond
 *
 */
public class WifiDeviceMobilePresentRepairOp {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		//if(argv.length <1) return;
		//String oper = argv[0];// ADD REMOVE
		
		ApplicationContext actx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		long t0 = System.currentTimeMillis();
		
		UserWifiDeviceService userWifiDeviceService = (UserWifiDeviceService)actx.getBean("userWifiDeviceService");
		UserMobileDeviceService userMobileDeviceService = (UserMobileDeviceService)actx.getBean("userMobileDeviceService");
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc.setOrderByClause("uid");
		mc.setPageNumber(1);
    	mc.setPageSize(200);
		EntityIterator<String, UserWifiDevice> it = new KeyBasedEntityBatchIterator<String,UserWifiDevice>(String.class
				,UserWifiDevice.class, userWifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<UserWifiDevice> userWifiDevices = it.next();
			Set<Integer> uids = new HashSet<Integer>();
			for(UserWifiDevice userWifiDevice : userWifiDevices){
				uids.add(userWifiDevice.getUid());
			}
			List<UserMobileDevice> userMobileDevices = userMobileDeviceService.findByIds(new ArrayList<Integer>(uids));
			Map<Integer, UserMobileDevice> userMobileDevicesMap = new HashMap<Integer, UserMobileDevice>();
			for(UserMobileDevice userMobileDevice : userMobileDevices){
				userMobileDevicesMap.put(userMobileDevice.getId(), userMobileDevice);
			}
			
			DeviceMobilePresentDTO presentDto = null;
			UserMobileDevice userMobileDevice = null;
			for(UserWifiDevice userWifiDevice : userWifiDevices){
				userMobileDevice = userMobileDevicesMap.get(userWifiDevice.getUid());
				if(userMobileDevice != null){
					presentDto  = new DeviceMobilePresentDTO(userWifiDevice.getUid(), userMobileDevice.getD(), userMobileDevice.getDt(), 
							userMobileDevice.getPt(), userMobileDevice.getDm());
					WifiDeviceMobilePresentStringService.getInstance().setMobilePresent(userWifiDevice.getId(), 
							JsonHelper.getJSONString(presentDto));
				}

			}
		}
		
		System.out.println("数据修复完成，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");
	}
}
