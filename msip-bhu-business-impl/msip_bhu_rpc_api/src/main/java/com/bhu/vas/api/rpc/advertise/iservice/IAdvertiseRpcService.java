package com.bhu.vas.api.rpc.advertise.iservice;

import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;

/**
 * 
 * @author xiaowei by 16/10/18
 *
 */
public interface IAdvertiseRpcService{
	/**
	 * 创建广告
	 * @param uid
	 * @param image
	 * @param url
	 * @param province
	 * @param city
	 * @param district
	 * @param start
	 * @param end
	 * @return
	 */
	public RpcResponseDTO<Boolean> createNewAdvertise(int uid,
			String image, String url,String domain, String province, String city,
			String district,String description,String title, long start, long end);

	RpcResponseDTO<List<String>> fetchDevicePositionDistribution(
			String province, String city);

}
