package com.bhu.vas.rpc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IThirdPartyRpcService;

/**
 * 第三方业务的组件服务service 对外暴露接口
 * @author yetao
 *
 */
@Service("deviceMessageDispatchRpcService")
public class ThirdPartyRpcService implements IThirdPartyRpcService {
	private final Logger logger = LoggerFactory.getLogger(ThirdPartyRpcService.class);

	public RpcResponseDTO<Boolean> gomeBindDevice(String mac){
		return null;
	}

}
