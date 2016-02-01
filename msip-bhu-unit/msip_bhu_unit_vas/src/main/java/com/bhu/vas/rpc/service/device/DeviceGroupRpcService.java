package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devicegroup.iservice.IDeviceGroupRpcService;
import com.bhu.vas.api.vto.DeviceGroupDetailVTO;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.bhu.vas.rpc.facade.DeviceGroupUnitFacadeRpcService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("deviceGroupRpcService")
public class DeviceGroupRpcService implements IDeviceGroupRpcService{
	private final Logger logger = LoggerFactory.getLogger(DeviceGroupRpcService.class);
	@Resource
	private DeviceGroupUnitFacadeRpcService deviceGroupUnitFacadeRpcService;
	@Override
	public RpcResponseDTO<TailPage<DeviceGroupVTO>> birthTree(Integer uid,
			long pid, int pageNo, int pageSize) {
		logger.info(String.format("birthTree uid:%s pid:%s",uid,pid));
		return deviceGroupUnitFacadeRpcService.birthTree(uid, pid, pageNo, pageSize);
	}
	@Override
	public RpcResponseDTO<DeviceGroupVTO> deviceGroupSave(Integer uid, long gid,long pid, String name) {
		logger.info(String.format("deviceGroupSave uid:%s gid:%s pid:%s name:%s",uid,gid,pid,name));
		return deviceGroupUnitFacadeRpcService.deviceGroupSave(uid, gid, pid, name);
	}

	@Override
	public RpcResponseDTO<DeviceGroupDetailVTO> deviceGroupDetail(Integer uid, long gid) {
		logger.info(String.format("deviceGroupDetail uid:%s gid:%s ",uid,gid));
		return deviceGroupUnitFacadeRpcService.deviceGroupDetail(uid, gid);
	}

	@Override
	public RpcResponseDTO<Boolean> deviceGroupCleanUpByIds(Integer uid, String gids) {
		logger.info(String.format("deviceGroupCleanUpByIds uid:%s gids:%s",uid,gids));
		return deviceGroupUnitFacadeRpcService.deviceGroupCleanUpByIds(uid, gids);
	}
	@Override
	public RpcResponseDTO<Boolean> assignUserSearchCondition4DeviceGroup(
			Integer assignor, Long gid, String message, String desc) {
		logger.info(String.format("assignUserSearchCondition4DeviceGroup assignor:%s gid:%s message:%s desc:%s",assignor,gid,message,desc));
		return deviceGroupUnitFacadeRpcService.assignUserSearchCondition4DeviceGroup(assignor, gid, message, desc);
	}
	/*@Override
	public RpcResponseDTO<Boolean> grant(Integer uid, long gid, String wifi_ids) {
		logger.info(String.format("grant uid:%s gid:%s wifi_ids:%s",uid,gid,wifi_ids));
		return deviceGroupUnitFacadeRpcService.grant(uid, gid, wifi_ids);
	}
	@Override
	public RpcResponseDTO<Boolean> ungrant(Integer uid, long gid,String wifi_ids) {
		logger.info(String.format("ungrant uid:%s gid:%s wifi_ids:%s",uid,gid,wifi_ids));
		return deviceGroupUnitFacadeRpcService.ungrant(uid, gid, wifi_ids);
	}*/
}
