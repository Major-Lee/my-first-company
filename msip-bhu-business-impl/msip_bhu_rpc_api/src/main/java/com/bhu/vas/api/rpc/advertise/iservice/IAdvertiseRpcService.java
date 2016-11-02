package com.bhu.vas.api.rpc.advertise.iservice;

import java.util.List;
import java.util.Map;

import com.bhu.vas.api.dto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.dto.advertise.AdvertiseVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.smartwork.msip.cores.orm.support.page.TailPage;

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

	public RpcResponseDTO<List<String>> fetchDevicePositionDistribution(
			String province, String city ,String district);
	/**
	 * 更新广告
	 * @param uid
	 * @param image
	 * @param url
	 * @param domain
	 * @param province
	 * @param city
	 * @param district
	 * @param description
	 * @param title
	 * @param start
	 * @param end
	 * @return
	 */
	public RpcResponseDTO<Boolean> updateAdvertise(int uid,int advertiseId, String image,
			String url, String domain, String province, String city,
			String district, String description, String title, long start,
			long end);
	
	/**
	 * 审核广告
	 * @param uid
	 * @param advertiseId
	 * @param msg
	 * @param state
	 * @return
	 */
	public RpcResponseDTO<Boolean> verifyAdvertise(int uid, int advertiseId,String msg,
			int state);
	/**
	 * 查询广告详情
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<AdvertiseVTO> queryAdvertise(int advertiseId);
	/**
	 * 查询广告列表
	 * @param uid
	 * @param province
	 * @param city
	 * @param district
	 * @param publishStartTime
	 * @param publishEndTime
	 * @param type
	 * @param createStartTime
	 * @param createEndTime
	 * @param userName
	 * @param state
	 * @return
	 */

	public RpcResponseDTO<TailPage<AdvertiseVTO>> queryAdvertiseList(Integer uid,
			String province, String city, String district,
			String publishStartTime, String publishEndTime, int type,
			String createStartTime, String createEndTime, String userName,
			int state, int pn, int ps);

}
