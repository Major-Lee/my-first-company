package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceGroupRpcService;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.bhu.vas.rpc.facade.DeviceGroupUnitFacadeRpcService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("deviceGroupRpcService")
public class DeviceGroupRpcService implements IDeviceGroupRpcService{

	private final Logger logger = LoggerFactory.getLogger(DeviceGroupRpcService.class);
	@Resource
	private DeviceGroupUnitFacadeRpcService deviceGroupUnitFacadeRpcService;
	@Override
	public TailPage<DeviceGroupVTO> birthTree(Integer uid,
			long pid, int pageNo, int pageSize) {
		logger.info(String.format("birthTree uid:%s pid:%s",uid,pid));
		return deviceGroupUnitFacadeRpcService.birthTree(uid, pid, pageNo, pageSize);
	}
	@Override
	public RpcResponseDTO<DeviceGroupVTO> save(Integer uid, long gid,
			long pid, String name) {
		logger.info(String.format("save uid:%s gid:%s pid:%s name:%s",uid,gid,pid,name));
		return deviceGroupUnitFacadeRpcService.save(uid, gid, pid, name);
	}

	@Override
	public RpcResponseDTO<DeviceGroupVTO> detail(Integer uid, long gid, int pageNo, int pageSize) {
		logger.info(String.format("detail uid:%s gid:%s pageNo:%s pageSize:%s",uid,gid,pageNo,pageSize));
		return deviceGroupUnitFacadeRpcService.detail(uid, gid, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<Boolean> remove(Integer uid, String gids) {
		logger.info(String.format("detail uid:%s gids:%s",uid,gids));
		return deviceGroupUnitFacadeRpcService.remove(uid, gids);
	}
	@Override
	public RpcResponseDTO<Boolean> grant(Integer uid, long gid, String wifi_ids,String group_ids) {
		logger.info(String.format("grant uid:%s gid:%s wifi_ids:%s grup_ids:%s",uid,gid,wifi_ids,group_ids));
		return deviceGroupUnitFacadeRpcService.grant(uid, gid, wifi_ids,group_ids);
	}
	@Override
	public RpcResponseDTO<Boolean> ungrant(Integer uid, long gid,String wifi_ids, String group_ids) {
		logger.info(String.format("ungrant uid:%s gid:%s wifi_ids:%s grup_ids:%s",uid,gid,wifi_ids,group_ids));
		return deviceGroupUnitFacadeRpcService.ungrant(uid, gid, wifi_ids,group_ids);
	}

	
}
