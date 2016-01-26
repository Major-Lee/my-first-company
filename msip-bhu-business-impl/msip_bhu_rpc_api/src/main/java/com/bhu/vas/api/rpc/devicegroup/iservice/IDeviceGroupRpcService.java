package com.bhu.vas.api.rpc.devicegroup.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IDeviceGroupRpcService {
	
	TailPage<DeviceGroupVTO> birthTree(Integer uid, long pid, int pageNo, int pageSize);
	
	RpcResponseDTO<DeviceGroupVTO> save(Integer uid,long gid,long pid, String name);
	
	RpcResponseDTO<DeviceGroupVTO> detail(Integer uid, long gid, int pageNo, int pageSize);

	RpcResponseDTO<Boolean> remove(Integer uid, String gids);
	
	RpcResponseDTO<Boolean> grant(Integer uid, long gid,String wifi_ids);
	
	RpcResponseDTO<Boolean> ungrant(Integer uid, long gid,String wifi_ids);
	
}
