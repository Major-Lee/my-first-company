/*package com.bhu.vas.di.op.repair;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.IdHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

*//**
 * 数据迁移
 * WifiDevicePresentService ——》WifiDevicePresentCtxService
 * @author Edmond
 *
 *//*
public class OnlineDeviceRedisDataRepairOp {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		//if(argv.length <1) return;
		//String oper = argv[0];// ADD REMOVE
		
		ApplicationContext actx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		WifiDeviceService wifiDeviceService = (WifiDeviceService)actx.getBean("wifiDeviceService");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", 1).andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
    	mc.setPageNumber(1);
    	mc.setPageSize(200);
    	int count = 0;
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
				,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<WifiDevice> entitys = it.next();
			List<String> ids = IdHelper.getStringIds(entitys);
			List<String> presents = WifiDevicePresentService.getInstance().getPresents(ids);
			if(presents !=null && !presents.isEmpty()){
				int index = 0;
				for(String ctx:presents){
					if(!StringUtils.isEmpty(ctx)){
						String mac = ids.get(index);
						WifiDevicePresentCtxService.getInstance().addPresent(mac, ctx);
						//SessionManager.getInstance().addSession(mac, ctx);
						//logger.info(String.format("Online device[%s] ctx[%s]", mac,ctx));
						System.out.println(String.format("Online device[%s] ctx[%s]", mac,ctx));
						count++;
					}
					index++;
				}
			}
		}
		System.out.println("数据迁移成功 count:"+count);
	}
}
*/