package com.wecite.toplines.business.filter;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.filter.model.BlackList;
import com.bhu.vas.business.filter.service.BlackListService;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.localunit.BaseTest;

public class BlackListServiceTest extends BaseTest{

	@Resource
	BlackListService blackListService;

	@Test
	public void testRowLock(){
		
		String url = "www.google.com.hk/images/srpr/logo3w.png";
		
		BlackList black = new BlackList();
		black.setId(HttpHelper.parseTopDomain(url));
		
		BlackList byId = blackListService.getById(black.getId());
		if(byId != null){
			System.out.println("exist!");
		}else{
			blackListService.insert(black);
		}
	}
}
