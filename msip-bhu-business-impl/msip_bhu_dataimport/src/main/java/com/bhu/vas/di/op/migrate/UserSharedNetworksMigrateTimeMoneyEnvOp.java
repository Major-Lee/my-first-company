package com.bhu.vas.di.op.migrate;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.helper.VapEnumType.SharedNetworkType;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigsOld;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsOldService;
import com.bhu.vas.business.ds.device.facade.SharedNetworksFacadeService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 共享网络的使用时长的金额，从sharedelconfig中移动到设备的portal配置中
 * @author Yetao
 *
 */
public class UserSharedNetworksMigrateTimeMoneyEnvOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserSharedNetworksMigrateTimeMoneyEnvOp.class);
		ctx.start();
	    WifiDeviceSharedealConfigsOldService wifiDeviceSharedealConfigsOldService = (WifiDeviceSharedealConfigsOldService)ctx.getBean("wifiDeviceSharedealConfigsOldService");
	    SharedNetworksFacadeService sharedNetworksFacadeService = (SharedNetworksFacadeService)ctx.getBean("sharedNetworksFacadeService");
		
	    
		//遍历以后的sharedealconfigs表数据
		ModelCriteria mc_usersharednetwork = new ModelCriteria();
		mc_usersharednetwork.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_usersharednetwork.setOrderByClause("id desc");
		mc_usersharednetwork.setPageNumber(1);
		mc_usersharednetwork.setPageSize(200);
		

    	EntityIterator<String, WifiDeviceSharedealConfigsOld> itit = new KeyBasedEntityBatchIterator<String, WifiDeviceSharedealConfigsOld>(String.class, WifiDeviceSharedealConfigsOld.class, wifiDeviceSharedealConfigsOldService.getEntityDao(), mc_usersharednetwork);
		while(itit.hasNext()){
			List<WifiDeviceSharedealConfigsOld> list = itit.next();
			for(WifiDeviceSharedealConfigsOld sdc:list){
				if(WifiDeviceSharedealConfigsOld.Default_ConfigsWifiID.equals(sdc.getId()))
					continue;
				WifiDeviceSharedNetwork snk = sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().getById(sdc.getId());
				if(snk == null){
					System.out.println(String.format("[%s] snk is null", sdc.getId()));
					continue;
				}
				if(!SharedNetworkType.SafeSecure.getKey().equals(snk.getSharednetwork_type())){
					System.out.println(String.format("[%s] snk is not a SafeSecure network", sdc.getId()));
					continue;
				}
				
				SharedNetworkSettingDTO snkdto = snk.getInnerModel();
				ParamSharedNetworkDTO psn = snkdto.getPsn();
				psn.setRange_cash_mobile(sdc.getRange_cash_mobile());
				psn.setRange_cash_pc(sdc.getRange_cash_pc());
				psn.setAit_mobile(sdc.getAit_mobile());
				psn.setAit_pc(sdc.getAit_pc());
				
				snkdto.setPsn(psn);
				snk.replaceInnerModel(snkdto);
				
				sharedNetworksFacadeService.getWifiDeviceSharedNetworkService().update(snk);
			}
		}
		System.out.println("total cost:"+ (System.currentTimeMillis() - t0) /1000);
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
