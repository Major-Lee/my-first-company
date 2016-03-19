package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceSharedNetworkRpcService;
import com.bhu.vas.rpc.facade.DeviceSharedNetworkUnitFacadeService;

/**
 * device sharednetwork rpc组件服务service 对外暴露接口
 * 处理web rest业务
 * @author Edmond Lee
 *
 */
@Service("deviceRestRpcService")
public class DeviceSharedNetworkRpcService implements IDeviceSharedNetworkRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceSharedNetworkRpcService.class);
	@Resource
	private DeviceSharedNetworkUnitFacadeService deviceSharedNetworkUnitFacadeService;

	@Override
	public RpcResponseDTO<ParamSharedNetworkDTO> fetchNetworkConf(int uid,
			String sharenetwork_type) {
		logger.info(String.format("fetchNetworkConf with uid[%s] sharenetwork_type[%s] ", uid,sharenetwork_type));
		return deviceSharedNetworkUnitFacadeService.fetchNetworkConf(uid, sharenetwork_type);
	}

	@Override
	public RpcResponseDTO<ParamSharedNetworkDTO> applyNetworkConf(int uid,
			String sharenetwork_type, String extparams) {
		logger.info(String.format("applyNetworkConf with uid[%s] sharenetwork_type[%s] extparams[%s]", uid,sharenetwork_type,extparams));
		return deviceSharedNetworkUnitFacadeService.applyNetworkConf(uid, sharenetwork_type, extparams);
	}

	@Override
	public RpcResponseDTO<Boolean> takeEffectNetworkConf(int uid,
			String sharenetwork_type, String mac) {
		logger.info(String.format("takeEffectNetworkConf with uid[%s] sharenetwork_type[%s] mac[%s]", uid,sharenetwork_type,mac));
		return deviceSharedNetworkUnitFacadeService.takeEffectNetworkConf(uid, sharenetwork_type, mac);
	}

	@Override
	public RpcResponseDTO<Boolean> pages(int uid, String sharenetwork_type) {
		logger.info(String.format("pages with uid[%s] sharenetwork_type[%s] ", uid,sharenetwork_type));
		return deviceSharedNetworkUnitFacadeService.pages(uid, sharenetwork_type);
	}
	
	
}
