package com.bhu.vas.push.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceOnlinePushDTO;
import com.bhu.vas.api.dto.push.WifiDeviceRebootPushDTO;
import com.bhu.vas.push.business.PushService;
import com.smartwork.msip.localunit.BaseTest;

public class PushServiceTest extends BaseTest{

	@Resource
	PushService pushService;
	

	//@Test
	public void testPushHandsetDeviceOnline(){
		HandsetDeviceOnlinePushDTO pushDto = new HandsetDeviceOnlinePushDTO();
		pushDto.setMac("84:82:f4:19:01:0c");
		pushDto.setHd_mac("11:11:11:11:11:11");
		pushService.push(pushDto);
	}
	
	@Test
	public void testPushUserCmdDeviceReboot(){
		WifiDeviceRebootPushDTO pushDto = new WifiDeviceRebootPushDTO("84:82:f4:19:01:0c", WifiDeviceDTO.UserCmdRebootReason);
		pushService.push(pushDto);
	}
}
