package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.DeviceGroupDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceGroupRpcService;
import com.bhu.vas.rpc.facade.DeviceGroupUnitFacadeRpcService;

@Service("deviceGroupRpcService")
public class DeviceGroupRpcService implements IDeviceGroupRpcService{

	private final Logger logger = LoggerFactory.getLogger(DeviceGroupRpcService.class);
	@Resource
	private DeviceGroupUnitFacadeRpcService deviceGroupUnitFacadeRpcService;
	@Override
	public RpcResponseDTO<List<DeviceGroupDTO>> birthTree(Integer uid,
			Integer pid) {
		return deviceGroupUnitFacadeRpcService.birthTree(uid, pid);
	}
	@Override
	public RpcResponseDTO<DeviceGroupDTO> save(Integer uid, Integer gid,
			Integer pid, String name) {
		return deviceGroupUnitFacadeRpcService.save(uid, gid, pid, name);
	}
	@Override
	public RpcResponseDTO<DeviceGroupDTO> detail(Integer uid, Integer gid) {
		return deviceGroupUnitFacadeRpcService.detail(uid, gid);
	}
	@Override
	public RpcResponseDTO<Boolean> remove(Integer uid, String gids) {
		return deviceGroupUnitFacadeRpcService.remove(uid, gids);
	}
	@Override
	public RpcResponseDTO<Boolean> grant(Integer uid, Integer gid, String wifi_ids) {
		return deviceGroupUnitFacadeRpcService.grant(uid, gid, wifi_ids);
	}
	@Override
	public RpcResponseDTO<Boolean> ungrant(Integer uid, Integer gid,
			String wifi_ids) {
		return deviceGroupUnitFacadeRpcService.ungrant(uid, gid, wifi_ids);
	}

	
}
