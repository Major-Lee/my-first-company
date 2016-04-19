package com.bhu.vas.business.charging;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchImport;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChargingFacadeServiceTest extends BaseTest{
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
    @Test
	public void test001BatchCreateDefault(){
    	System.out.println(Integer.MAX_VALUE);
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		chargingFacadeService.getWifiDeviceBatchImportService().deleteByModelCriteria(mc);
		chargingFacadeService.getUserWithdrawCostConfigsService().deleteByModelCriteria(mc);
		chargingFacadeService.getWifiDeviceSharedealConfigsService().deleteByModelCriteria(mc);
		
		WifiDeviceBatchImport batchimport = new WifiDeviceBatchImport();
		
		batchimport.setImportor(3);
		batchimport.setRemark("hello world!");
		batchimport.setFailed(200);
		batchimport.setSucceed(300);
		batchimport.setFilepath("/BHUData/aaa.zip");
		chargingFacadeService.getWifiDeviceBatchImportService().insert(batchimport);
		
		
		chargingFacadeService.getUserWithdrawCostConfigsService().addDefault();
		chargingFacadeService.getWifiDeviceSharedealConfigsService().addDefault();
	}	
}
