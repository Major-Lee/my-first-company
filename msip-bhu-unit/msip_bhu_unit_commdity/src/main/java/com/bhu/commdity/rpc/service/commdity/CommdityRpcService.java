package com.bhu.commdity.rpc.service.commdity;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.commdity.rpc.facade.OrderUnitFacadeService;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.ICommdityRpcService;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;

@Service("commdityRpcService")
public class CommdityRpcService implements ICommdityRpcService{
	private final Logger logger = LoggerFactory.getLogger(CommdityRpcService.class);
	
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;


}
