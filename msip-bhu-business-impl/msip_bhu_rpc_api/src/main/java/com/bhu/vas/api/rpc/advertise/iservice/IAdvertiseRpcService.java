package com.bhu.vas.api.rpc.advertise.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;

/**
 * 
 * @author xiaowei by 16/10/18
 *
 */
public interface IAdvertiseRpcService{

	RpcResponseDTO<List<String>> fetchDevicePositionDistribution(
			String province, String city);

}
