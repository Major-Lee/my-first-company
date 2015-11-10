package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.dto.search.condition.SearchCondition;
import com.bhu.vas.api.dto.search.condition.SearchConditionMessage;
import com.bhu.vas.api.dto.search.condition.SearchConditionPattern;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.WifiDeviceVTO1;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;

public class DeviceRestBusinessFacadeServiceTest extends BaseTest{
	@Resource
	private DeviceRestBusinessFacadeService deviceRestBusinessFacadeService;
	
	@Test
	public void fetchBySearchConditionMessage(){
		List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();
		//设备从未上线
		SearchCondition sc_neveronline = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_ONLINE.getName(), SearchConditionPattern.Equal.getPattern(), "0");
		//设备业务线为urouter
		SearchCondition sc_urouter = new SearchCondition(BusinessIndexDefine.WifiDevice.
				Field1.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.Equal.getPattern(), "TU");
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
	}
}
