package com.bhu.vas.business.device;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionFW;
import com.bhu.vas.business.ds.device.service.WifiDeviceVersionFWService;
import com.smartwork.msip.localunit.BaseTest;

public class WifiDeviceGroupTest extends BaseTest{

	//@Resource
	//WifiDeviceGroupService wifiDeviceGroupService;
	
//	@Test
//	public void testCleanUpByIds(){
//		wifiDeviceGroupService.cleanUpByIds(3, "70,71");
//	}



	/*@Test
	public void testDevicesGroup() {

		WifiDeviceGroup wifiDeviceGroup = wifiDeviceGroupService.getById(100);
		List<String> lists = wifiDeviceGroup.getInnerModels();

		System.out.println(lists);



	}*/
    @Resource
    private WifiDeviceVersionFWService wifiDeviceVersionFWService;
    
    @Test
    public void test001(){
    	WifiDeviceVersionFW wifiDeviceVersionFW =new WifiDeviceVersionFW();
    	wifiDeviceVersionFW.setId("AP106P06V1.5.0Build1111_TU");
    	wifiDeviceVersionFW.setName("AP106P06V1.5.0Build1111_TU");
    	wifiDeviceVersionFW.setDuts("TU_106");
    	wifiDeviceVersionFW.setMinid(null);
    	wifiDeviceVersionFW.setUpgrade_url("http://7xq32u.com2.z0.glb.qiniucdn.com/@/TU_H106/build/AP106P06V1.5.5Build9548_TU");
    	wifiDeviceVersionFW.setUpgrade_slaver_urls("http://devicefw.oss-cn-beijing.aliyuncs.com/TU_H106/build/AP106P06V1.5.5Build9548_TU");
    	wifiDeviceVersionFWService.insert(wifiDeviceVersionFW);
    }
}
