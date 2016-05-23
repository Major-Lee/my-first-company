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
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.UserMobileDevice;
import com.bhu.vas.api.rpc.user.model.pk.UserDevicePK;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserMobileDeviceService;
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
		
		UserDeviceService userDeviceService = (UserDeviceService)actx.getBean("userDeviceService");
		UserMobileDeviceService userMobileDeviceService = (UserMobileDeviceService)actx.getBean("userMobileDeviceService");
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc.setOrderByClause("uid");
		mc.setPageNumber(1);
    	mc.setPageSize(200);
		EntityIterator<UserDevicePK, UserDevice> it = new KeyBasedEntityBatchIterator<UserDevicePK,UserDevice>(UserDevicePK.class
				,UserDevice.class, userDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<UserDevicePK> pks = it.nextKeys();
			Set<Integer> uids = new HashSet<Integer>();
			for(UserDevicePK userDevicePk : pks){
				uids.add(userDevicePk.getUid());
			}
			List<UserMobileDevice> userMobileDevices = userMobileDeviceService.findByIds(new ArrayList<Integer>(uids));
			Map<Integer, UserMobileDevice> userMobileDevicesMap = new HashMap<Integer, UserMobileDevice>();
			for(UserMobileDevice userMobileDevice : userMobileDevices){
				userMobileDevicesMap.put(userMobileDevice.getId(), userMobileDevice);
			}
			
			DeviceMobilePresentDTO presentDto = null;
			UserMobileDevice userMobileDevice = null;
			for(UserDevicePK userDevicePk : pks){
				userMobileDevice = userMobileDevicesMap.get(userDevicePk.getUid());
				if(userMobileDevice != null){
					presentDto  = new DeviceMobilePresentDTO(userDevicePk.getUid(), userMobileDevice.getD(), userMobileDevice.getDt(), 
							userMobileDevice.getPt(), userMobileDevice.getDm());
					WifiDeviceMobilePresentStringService.getInstance().setMobilePresent(userDevicePk.getMac(), 
							JsonHelper.getJSONString(presentDto));
				}

			}
		}
		
		System.out.println("数据修复完成，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");
	}
}
