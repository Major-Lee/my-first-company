package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.DeviceGroupDTO;


public interface IDeviceGroupRpcService {
	
	public RpcResponseDTO<List<DeviceGroupDTO>> birthTree(Integer uid, Integer pid);
	
	public RpcResponseDTO<DeviceGroupDTO> save(Integer uid,Integer gid,Integer pid, String name);
	
	public RpcResponseDTO<DeviceGroupDTO> detail(Integer uid, Integer gid);
	
	public RpcResponseDTO<Boolean> remove(Integer uid, String gids);
	
	public RpcResponseDTO<Boolean> grant(Integer uid, Integer gid,String wifi_ids);
	
	public RpcResponseDTO<Boolean> ungrant(Integer uid, Integer gid,String wifi_ids);
	
}
