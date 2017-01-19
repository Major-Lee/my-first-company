package com.bhu.vas.rpc.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 第三方业务的service
 * @author yetao
 *
 */
@Service
public class ThirdPartyFacadeService {
	private final Logger logger = LoggerFactory.getLogger(ThirdPartyFacadeService.class);
	
	public Boolean gomeBindDevice(String mac){
		return Boolean.TRUE;
	}
	
	public Boolean gomeUnBindDevice(String mac){
		return Boolean.TRUE;
	}
}
