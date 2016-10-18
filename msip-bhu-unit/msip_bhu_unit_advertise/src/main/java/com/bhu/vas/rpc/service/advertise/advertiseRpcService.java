package com.bhu.vas.rpc.service.advertise;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

@Service("advertiseRpcService")
public class advertiseRpcService{
	private final Logger logger = LoggerFactory.getLogger(advertiseRpcService.class);
}
