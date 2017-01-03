package com.bhu.vas.api.rpc.advertise.iservice;


import java.text.ParseException;
import java.util.List;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseReportVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.api.vto.device.DeviceGEOPointCountVTO;

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
	 * @throws ParseException 
	 */
	public RpcResponseDTO<AdvertiseVTO> createNewAdvertise(int uid,int type,
			String image, String url,String domain, String province, String city,
			String district,double lat,double lon,String distance,String description,String title, long start, long end,String extparams) throws ParseException;

	public RpcResponseDTO<AdDevicePositionVTO> fetchDevicePositionDistribution(
			int uid ,int type, String province, String city ,String district);
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
	public RpcResponseDTO<Boolean> updateAdvertise(int uid,String advertiseId, String image,
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
	public RpcResponseDTO<Boolean> verifyAdvertise(int uid, String advertiseId,String msg,
			int state);
	/**
	 * 查询广告详情
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<AdvertiseVTO> queryAdvertise(Integer uid,String advertiseId);
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

	public RpcResponseDTO<AdvertiseListVTO> queryAdvertiseList(Integer uid,
			String province, String city, String district,
			String publishStartTime, String publishEndTime, Integer type,
			String createStartTime, String createEndTime, String mobileNo,
			int state, int pn, int ps,boolean isConsole);
	/**
	 * 取消广告
	 * @param uid
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<Boolean> escapeAdvertise(int uid, String advertiseId);

	/**
	 * 查看广告报表
	 * @param uid
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<AdvertiseReportVTO> fetchAdvertiseReport(int uid,
			String advertiseId);

	public RpcResponseDTO<List<DeviceGEOPointCountVTO>> countDeviceCountByGEOPoint(int uid,
			String province, String city, String district, double lat,
			double lon, String distances);

}
