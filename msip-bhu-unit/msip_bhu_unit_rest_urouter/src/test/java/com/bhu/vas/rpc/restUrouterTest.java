package com.bhu.vas.rpc;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.rpc.facade.DeviceURouterRestBusinessFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

public class restUrouterTest extends BaseTest{
	@Resource
	DeviceURouterRestBusinessFacadeService deviceURouterRestBusinessService;
	
	@Test
	public void test001(){
		RpcResponseDTO<Map<String,Object>> result = deviceURouterRestBusinessService.urouterAllHdList(100153, "84:82:f4:19:01:0c", 0, 5, false);
		System.out.println(JsonHelper.getJSONString(result));
	}
}
