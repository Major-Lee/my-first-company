package com.bhu.vas.api.rpc.devicegroup.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.DeviceGroupDetailVTO;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IDeviceGroupRpcService {
	
	RpcResponseDTO<TailPage<DeviceGroupVTO>> birthTree(Integer uid, long pid, int pageNo, int pageSize);
	
	RpcResponseDTO<DeviceGroupVTO> deviceGroupSave(Integer creator, long gid,long pid, String name);
	
	RpcResponseDTO<DeviceGroupDetailVTO> deviceGroupDetail(Integer uid, long gid);

	RpcResponseDTO<Boolean> deviceGroupCleanUpByIds(Integer uid, String gids);
	RpcResponseDTO<Boolean> assignUserSearchCondition4DeviceGroup(Integer assignor,Long gid, String message, String desc);
	
	/*RpcResponseDTO<Boolean> grant(Integer uid, long gid,String wifi_ids);
	
	RpcResponseDTO<Boolean> ungrant(Integer uid, long gid,String wifi_ids);*/
	
}
