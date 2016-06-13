package com.bhu.vas.di.op.repair;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.devices.model.UserDevicesSharedNetworks;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 更新共享网络中的open_resource为系统定义的缺省配置
 * @author Edmond
 *
 */
public class SharedNetworksSharedealOwnerRebuildEnvOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, SharedNetworksSharedealOwnerRebuildEnvOp.class);
		ctx.start();
		
		ChargingFacadeService chargingFacadeService = (ChargingFacadeService)ctx.getBean("chargingFacadeService");
		UserDeviceService userDeviceService = (UserDeviceService)ctx.getBean("userDeviceService");
		ModelCriteria mc_wdsc = new ModelCriteria();
		mc_wdsc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_wdsc.setOrderByClause("id desc");
		mc_wdsc.setPageNumber(1);
		mc_wdsc.setPageSize(200);
		AtomicInteger incr = new AtomicInteger(0);
    	EntityIterator<String, WifiDeviceSharedealConfigs> itit_wdsc = new KeyBasedEntityBatchIterator<String, WifiDeviceSharedealConfigs>(String.class, WifiDeviceSharedealConfigs.class, chargingFacadeService.getWifiDeviceSharedealConfigsService().getEntityDao(), mc_wdsc);
		while(itit_wdsc.hasNext()){
			List<WifiDeviceSharedealConfigs> list = itit_wdsc.next();
			for(WifiDeviceSharedealConfigs wdsc:list){
				//System.out.println(wdsc.getOwner());
				UserDevice userDevice = userDeviceService.fetchBindByMac(wdsc.getId());
				boolean matched = false;
				if(userDevice == null){
					if(wdsc.getOwner() == -1) matched = true;
					//System.out.println(String.format("configs current owner[%s] udevice[%s] matched[%s]", wdsc.getOwner(),"-",matched));
				}else{
					if(wdsc.getOwner() == userDevice.getUid()) matched = true;
					//System.out.println(String.format("configs current owner[%s] udevice[%s] matched[%s]", wdsc.getOwner(),userDevice.getUid(),matched));
				}
				if(!matched){
					incr.incrementAndGet();
					System.out.println(String.format("configs current owner[%s] udevice[%s] matched[%s]", wdsc.getOwner(),userDevice==null?-1:userDevice.getUid(),matched));
					wdsc.setOwner(userDevice==null?-1:userDevice.getUid());
					chargingFacadeService.getWifiDeviceSharedealConfigsService().update(wdsc);
				}
			}
		}
		System.out.println(incr.get()+"~~~ notmatched");
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
