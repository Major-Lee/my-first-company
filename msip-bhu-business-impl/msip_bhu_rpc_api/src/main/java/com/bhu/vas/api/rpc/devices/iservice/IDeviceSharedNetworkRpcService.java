package com.bhu.vas.api.rpc.devices.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;

public interface IDeviceSharedNetworkRpcService {
	
	/**
	 * 获取指定用户的指定共享网络类型的配置
	 * @param uid
	 * @param sharenetwork_type
	 * @return
	 */
	RpcResponseDTO<ParamSharedNetworkDTO> fetchNetworkConf(int uid, String sharenetwork_type);
	
	/**
	 * 指定用户的指定共享网络类型的配置更新及应用到所有此sharenetwork_type相关设备
	 * @param uid
	 * @param sharenetwork_type
	 * @param extparams
	 * @return
	 */
	RpcResponseDTO<ParamSharedNetworkDTO> applyNetworkConf(int uid, String sharenetwork_type, String extparams);
	
	/**
	 * 具体某个设备生效用户的指定共享网络类型配置
	 * @param uid
	 * @param sharenetwork_type
	 * @param mac
	 * @return
	 */
	RpcResponseDTO<Boolean> takeEffectNetworkConf(int uid,String sharenetwork_type,String mac);
	
	
	/**
	 * 指定共享网络类型的设备分页列表
	 * @param uid
	 * @param sharenetwork_type
	 * @param mac
	 * @return
	 */
	RpcResponseDTO<Boolean> pages(int uid,String sharenetwork_type);
}
