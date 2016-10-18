package com.bhu.vas.rpc.service.advertise;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;

@Service("advertiseRpcService")
public class advertiseRpcService implements IAdvertiseRpcService{
	private final Logger logger = LoggerFactory.getLogger(advertiseRpcService.class);
	
}
