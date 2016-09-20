package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.smartwork.msip.localunit.BaseTest;

public class DeviceRestBusinessFacadeServiceTest extends BaseTest{
	@Resource
	private DeviceBusinessFacadeService deviceBusinessFacadeService;
	
/*	@Test
	public void fetchBySearchConditionMessage(){
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备从未上线
		SearchCondition sc_neveronline = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "0");
		//设备业务线为urouter
		SearchCondition sc_urouter = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TU");
		searchConditions.add(sc_neveronline);
		searchConditions.add(sc_urouter);
		
		SearchConditionMessage searchConditionMessage = new SearchConditionMessage(searchConditions);
		String message = JsonHelper.getJSONString(searchConditionMessage);
		System.out.println("REQUEST JSON:"+ message);
		
		RpcResponseDTO<TailPage<WifiDeviceVTO1>> result = deviceRestBusinessFacadeService.fetchBySearchConditionMessage(message, 1, 10);
		TailPage<WifiDeviceVTO1> page = result.getPayload();
		System.out.println("total count:"+page.getTotalItemsCount());
		System.out.println("total page count:"+page.getTotalPageCount());
		
		List<WifiDeviceVTO1> items = page.getItems();
		for(WifiDeviceVTO1 vto : items){
			System.out.println(vto.getId());
		}
		
		System.out.println(1);
		System.out.println(2);
		System.out.println(3);
		System.out.println(4);
		System.out.println(5);
		
	}*/
	@Test
	public void testOnlineProcesser(){
		HandsetDeviceDTO dto = new HandsetDeviceDTO();
		dto.setAction("online");
		dto.setVapname("wlan0");
		dto.setChannel("11");
		dto.setSsid("晓玮啊测试");
		dto.setPhy_rate("0M");
		dto.setRssi("0dRM");
		dto.setSnr("0db");
		dto.setEthernet("false");
		dto.setDhcp_name("huanghhadaw");
		dto.setIp("192.168.78.111");
		dto.setMac("68:3e:34:48:b7:35");
		dto.setBssid("64:68:75:00:00:00");
		
		
		deviceBusinessFacadeService.handsetDeviceOffline("0000000564687500005c0000000000000000100000007",dto,"84:82:f4:2f:3a:50");
		System.out.println("111");
	}
	
}
