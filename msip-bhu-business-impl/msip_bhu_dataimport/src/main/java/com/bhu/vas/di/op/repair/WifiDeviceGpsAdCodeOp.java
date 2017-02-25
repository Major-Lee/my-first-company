package com.bhu.vas.di.op.repair;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 设备定位数据从获取adcode
 * @author Yetao
 *
 */
public class WifiDeviceGpsAdCodeOp {
	
	
	public boolean wifiDeiviceGeocoding(WifiDevice entity){
		if(entity == null) return false;
		if(StringUtils.isEmpty(entity.getLat()) || StringUtils.isEmpty(entity.getLon())) return false;
		
		return false;
	}

	
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, WifiDeviceGpsAdCodeOp.class);
		long total = 0;
		ctx.start();
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceFacadeService deviceFacadeService = (DeviceFacadeService)ctx.getBean("deviceFacadeService");

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ").
			andColumnIsNotNull("lat").andColumnIsNotNull("lon").andColumnIsNull("adcode");
		mc.setPageNumber(1);
		mc.setPageSize(200);
    	EntityIterator<String, WifiDevice> itit = new KeyBasedEntityBatchIterator<String, WifiDevice>(String.class, WifiDevice.class,
    			wifiDeviceService.getEntityDao(), mc);
    	System.out.println("itit.hasNext():" + itit.hasNext());
		while(itit.hasNext()){
			if(total > 10000){
				System.out.println("reach max try limit");
				break;
			}
			List<WifiDevice> list = itit.next();
	    	System.out.println("list size:" + list.size());
			for(WifiDevice device:list){
				try{
					total ++;
						
					System.out.println("trying " + device.getId());
					deviceFacadeService.wifiDeiviceGeocoding(device);
					System.out.println("code " + device.getAdcode());
					if(StringUtils.isNotEmpty(device.getAdcode()))
							wifiDeviceService.update(device);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					System.out.println(String.format("exception exmsg[%s]",
						ex.getMessage()));
				}
			}
		}

		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
