package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.DeviceGroupDTO;
import com.bhu.vas.api.rpc.statistics.dto.UserAccessStatisticsDTO;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IDeviceGroupRpcService {
	
//	public RpcResponseDTO<List<DeviceGroupVTO>> birthTree(Integer uid, int pid);

	RpcResponseDTO<List<DeviceGroupVTO>> birthTree(Integer uid, int pid, int pageNo, int pageSize);
	
	public RpcResponseDTO<DeviceGroupVTO> save(Integer uid,int gid,int pid, String name);
	
	RpcResponseDTO<DeviceGroupVTO> detail(Integer uid, int gid, int pageNo, int pageSize);

	public RpcResponseDTO<Boolean> remove(Integer uid, String gids);
	
	public RpcResponseDTO<Boolean> grant(Integer uid, int gid,String wifi_ids);
	
	public RpcResponseDTO<Boolean> ungrant(Integer uid, int gid,String wifi_ids);
	
}
