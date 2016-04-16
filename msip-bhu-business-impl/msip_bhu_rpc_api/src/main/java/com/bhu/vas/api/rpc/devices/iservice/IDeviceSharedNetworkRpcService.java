package com.bhu.vas.api.rpc.devices.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkDeviceDTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.SharedNetworkSettingDTO;
import com.bhu.vas.api.vto.device.DeviceProfileVTO;
import com.bhu.vas.api.vto.device.UserSnkPortalVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IDeviceSharedNetworkRpcService {
	
	
	RpcResponseDTO<UserSnkPortalVTO> fetchUserSnks4Portal(int uid);
	RpcResponseDTO<DeviceProfileVTO> fetchDeviceSnks4Portal(String mac);
	/**
	 * 获取指定用户的指定共享网络类型的配置
	 * @param uid
	 * @param sharenetwork_type
	 * @return
	 */
	RpcResponseDTO<ParamSharedNetworkDTO> fetchUserNetworkConf(int uid, String sharenetwork_type,String template);
	
	RpcResponseDTO<List<ParamSharedNetworkDTO>> fetchUserNetworksConf(int uid, String sharenetwork_type);
	
	RpcResponseDTO<SharedNetworkSettingDTO> fetchDeviceNetworkConf(int uid, String mac);
	/**
	 * 指定用户的指定共享网络类型的配置更新及应用到所有此sharenetwork_type相关设备
	 * @param uid
	 * @param sharenetwork_type
	 * @param extparams
	 * @return
	 */
	RpcResponseDTO<ParamSharedNetworkDTO> applyNetworkConf(int uid, String sharenetwork_type,String template, String extparams);
	
	/**
	 * 具体某个设备生效用户的指定共享网络类型配置
	 * @param uid
	 * @param sharenetwork_type
	 * @param mac
	 * @return
	 */
	RpcResponseDTO<Boolean> takeEffectNetworkConf(int uid,boolean on,String sharenetwork_type,String template,List<String> macs);
	
	
	/**
	 * 指定共享网络类型的设备分页列表
	 * @param uid
	 * @param sharenetwork_type
	 * @param mac
	 * @return
	 */
	RpcResponseDTO<TailPage<SharedNetworkDeviceDTO>> pages(int uid, String sharedNetwork_type, String template, String d_dut, int pageNo, int pageSize);
}
