package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import com.smartwork.msip.localunit.BaseTest;

public class DeviceRestBusinessFacadeServiceTest extends BaseTest{
	@Resource
	private DeviceRestBusinessFacadeService deviceRestBusinessFacadeService;
	
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
}
