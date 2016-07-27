package com.bhu.vas.business.user;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.user.dto.UserConfigsStateDTO;
import com.bhu.vas.api.rpc.user.model.UserConfigsState;
import com.bhu.vas.business.ds.user.service.UserConfigsStateService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

public class UserConfigStateSTest extends BaseTest{
	@Resource
	private UserConfigsStateService userConfigsStateService;
	
	@Test
	public void test01(){
		UserConfigsState userConfigsState = userConfigsStateService.getById(100245);
		System.out.println(userConfigsState.getExtension_content());
		UserConfigsStateDTO dto = JsonHelper.getDTO(userConfigsState.getExtension_content(), UserConfigsStateDTO.class);
		System.out.println(dto.isRn_on());
	}
}
