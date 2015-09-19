package com.bhu.vas.business.task;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.business.ds.task.service.VasModuleCmdDefinedService;
import com.smartwork.msip.localunit.BaseTest;

public class VasModuleCmdDefinedServiceTest extends BaseTest{
	@Resource
	private VasModuleCmdDefinedService vasModuleCmdDefinedService;
	//@Test
	public void testInsert(){
		VasModuleCmdPK pk = new VasModuleCmdPK("VapModuleCMD1","style0001");
		VasModuleCmdDefined byId = new VasModuleCmdDefined();//vasModuleCmdDefinedService.getById(new VasModuleCmdPK("VapModuleCMD","style000"));
		byId.setId(pk);
		byId.setMemo("111");
		byId.setTemplate("222");
		vasModuleCmdDefinedService.insert(byId);
	}
	
	@Test
	public void taskCallbackTest(){
		VasModuleCmdDefined byId = vasModuleCmdDefinedService.getById(new VasModuleCmdPK("VapModuleCMD","style000"));
		System.out.println(byId.getTemplate());
		
		byId = vasModuleCmdDefinedService.getById(new VasModuleCmdPK("VapModuleCMD","style000"));
		
		System.out.println(byId.getTemplate());
	}
}
