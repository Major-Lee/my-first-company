package com.bhu.vas.business.charging;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.bhu.vas.api.rpc.charging.model.UserWithdrawCostConfigs;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchImport;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
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
		
		UserWithdrawCostConfigs addDefault = chargingFacadeService.getUserWithdrawCostConfigsService().addDefault();
		//System.out.println(JsonHelper.getJSONString(addDefault));
		WifiDeviceSharedealConfigs addDefault2 = chargingFacadeService.getWifiDeviceSharedealConfigsService().addDefault();
		//System.out.println(JsonHelper.getJSONString(addDefault2));
	}
    
    private static String Default_DMac = "84:82:f4:23:06:e8";
    @Test
	public void test002FetchCertainDeviceSharedealConfigs(){
    	WifiDeviceSharedealConfigs  configs = chargingFacadeService.userfulWifiDeviceSharedealConfigs(Default_DMac);
    	System.out.println(JsonHelper.getJSONString(configs));
    }
    
    @Test
	public void test003CalculateSharedeal(){
    	SharedealInfo calculateSharedeal = chargingFacadeService.calculateSharedeal(Default_DMac, "test00012", 789.03d);
    	System.out.println(JsonHelper.getJSONString(calculateSharedeal));
    }
}
