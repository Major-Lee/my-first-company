package com.bhu.vas.business.charging;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.charging.vto.DeviceGroupPaymentStatisticsVTO;
import com.bhu.vas.business.ds.charging.facade.ChargingStatisticsFacadeService;
import com.smartwork.msip.localunit.BaseTest;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChargingStatisticsFacadeServiceTest extends BaseTest{

	@Resource
	private ChargingStatisticsFacadeService chargingStatisticsFacadeService;
	

    @Test
	public void test004CalculateSharedeal(){
    	int uid = 100245;
    	String groupid = "10009";
    	String group_path = "10009/";
    	DeviceGroupPaymentStatisticsVTO vto = chargingStatisticsFacadeService.fetchDeviceGroupPaymentStatistics(uid, groupid, group_path);
    	System.out.println(vto);
    }
}
