package com.bhu.vas.api.rpc.advertise.iservice;


import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.model.UserConsumptiveWalletLog;
import com.bhu.vas.api.vto.advertise.AdCommentsVTO;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseReportVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseResponseVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseUserDetailVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.api.vto.device.DeviceGEOPointCountVTO;
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
	 * @throws ParseException 
	 */
	public 	RpcResponseDTO<AdvertiseVTO> createNewAdvertise(int uid, Integer vuid,String adid, int tag, int type, String image, String url,
			String domain, String province, String city, String district,String adcode,double lat, double lon, String distance, String description,
			String title, long start, long end, boolean isTop,String extparams)throws ParseException;

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

	public RpcResponseDTO<List<DeviceGEOPointCountVTO>> countDeviceCountByGEOPoint(int uid,
			String province, String city, String district, double lat,
			double lon, String distances);

	public RpcResponseDTO<List<TailPage<AdvertiseVTO>>> fetchBySearchConditionMessages(String mac,
		   String umac,int pageNo, int pageSize,boolean customize, String...messages);

	public RpcResponseDTO<Boolean> advertiseOperation(int uid, String adid,
			boolean isTop, boolean isRefresh);

	public RpcResponseDTO<Boolean> advertiseComment(int uid,Integer vuid, String adid,
			String message, int type, Double score);

	public RpcResponseDTO<List<AdCommentsVTO>> fetchCommentDetail(String[] adids,int pn,int ps);

	public RpcResponseDTO<AdvertiseUserDetailVTO> userAdvertiseDetail(int uid);

	public RpcResponseDTO<List<AdvertiseVTO>> queryRandomAdvertiseDetails(String mac ,String umac);

	public RpcResponseDTO<Boolean> confirmPay(int uid, String adid);

	public RpcResponseDTO<Boolean> destoryTips(int uid, String adid);

	public RpcResponseDTO<Boolean> advertiseCPMNotify(String[] adids, String userid,
			String sourcetype, String systype);

	public RpcResponseDTO<AdvertiseResponseVTO> fetchAdListByPortal(String mac,
			String umac, String sourcetype, String systype, int pageSize,
			int pageNo);

	public RpcResponseDTO<List<AdvertiseVTO>> fetchAdListByAPP(double lat, double lon,
			String adcode, int pageSize, int pageNo);

	public RpcResponseDTO<List<Map<String, Object>>> fetchAdvertiseChartReport(
			int uid, String advertiseId, int type, Long start, Long end,
			int pageNo, int pageSize);

	public RpcResponseDTO<List<UserConsumptiveWalletLog>> fetchAdvertiseReport(
			int uid, String advertiseId, Long start, Long end, int pageNo,
			int pageSize);

}
