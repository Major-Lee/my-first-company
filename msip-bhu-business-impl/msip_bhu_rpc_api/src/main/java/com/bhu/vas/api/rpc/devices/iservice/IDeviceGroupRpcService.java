package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.DeviceGroupDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserAccessStatisticsDTO;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IDeviceGroupRpcService {
	
	RpcResponseDTO<List<DeviceGroupVTO>> birthTree(Integer uid, int pid, int pageNo, int pageSize);
	
	RpcResponseDTO<DeviceGroupVTO> save(Integer uid,int gid,int pid, String name);
	
	RpcResponseDTO<DeviceGroupVTO> detail(Integer uid, int gid, int pageNo, int pageSize);

	RpcResponseDTO<Boolean> remove(Integer uid, String gids);
	
	RpcResponseDTO<Boolean> grant(Integer uid, int gid,String wifi_ids);
	
	RpcResponseDTO<Boolean> ungrant(Integer uid, int gid,String wifi_ids);
	
}
