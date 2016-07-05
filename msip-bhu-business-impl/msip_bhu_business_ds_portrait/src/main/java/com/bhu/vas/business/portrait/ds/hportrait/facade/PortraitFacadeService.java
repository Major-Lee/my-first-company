package com.bhu.vas.business.portrait.ds.hportrait.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.portrait.ds.hportrait.service.HandsetPortraitService;

@Service
public class PortraitFacadeService {
	
	@Resource
	private HandsetPortraitService handsetPortraitService;

}
