package com.bhu.vas.rpc.service.device;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.vto.modulestat.ModuleDefinedVTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.redis.RegionCountDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDetailDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.bhu.vas.api.vto.SearchConditionVTO;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO1;
import com.bhu.vas.api.vto.agent.UserAgentVTO;
import com.bhu.vas.rpc.facade.DeviceRestBusinessFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device rpc组件服务service 对外暴露接口
 * 处理web rest业务
 * @author tangzichao
 *
 */
@Service("deviceRestRpcService")
public class DeviceRestRpcService implements IDeviceRestRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceRestRpcService.class);
	
	@Resource
	private DeviceRestBusinessFacadeService deviceRestBusinessFacadeService;
	
	/**
	 * 按照wifi设备的总接入用户数量从大到小排序获取wifi设备
	 */
	@Override
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize) {
		logger.info(String.format("DeviceRestRPC fetchWDevicesOrderMaxHandset invoke pageNo [%s] pageSize [%s]", pageNo, pageSize));
		
		try{
			return deviceRestBusinessFacadeService.fetchWDevicesOrderMaxHandset(pageNo, pageSize);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchWDevicesOrderMaxHandset exception pageNo [%s] pageSize [%s] exmsg[%s]",
					pageNo, pageSize, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	/**
	 * 根据keyword来搜索wifi设备数据
	 * keyword 可以是 mac 或 地理名称
	 */
//	@Override
//	public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword, int pageNo, int pageSize) {
//		logger.info(String.format("DeviceRestRPC fetchWDevicesByKeyword invoke pageNo [%s] pageSize [%s]", pageNo, pageSize));
//		
//		try{
//			return deviceRestBusinessFacadeService.fetchWDeviceByKeyword(keyword, pageNo, pageSize);
//		}catch(Exception ex){
//			ex.printStackTrace(System.out);
//			logger.error(String.format("DeviceRestRPC fetchWDevicesByKeyword exception pageNo [%s] pageSize [%s] exmsg[%s]",
//					pageNo, pageSize, ex.getMessage()), ex);
//			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
//		}
//	}
	
	/**
	 * region表示 需要包含的地域名称
	 * excepts表示 需要不包含的地域名称 逗号分割
	 * 根据keyword进行分词来搜索地理位置
	 * @param keyword 可以是 mac 或 地理名称
	 * @param region 北京市
	 * @param excepts 广东省,上海市
	 */
	@Override
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword, String region,
			String excepts, int pageNo, int pageSize) {
		logger.info(String.format("DeviceRestRPC fetchWDevicesByKeyword invoke pageNo [%s] pageSize [%s]", pageNo, pageSize));
		
		try{
			//return deviceRestBusinessFacadeService.fetchWDeviceByKeyword(keyword, region, excepts, pageNo, pageSize);
			return null;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchWDevicesByKeyword exception pageNo [%s] pageSize [%s] exmsg[%s]",
					pageNo, pageSize, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
	@Override
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeywords(
			String mac, 
			String sn, 
			String orig_swver, 
			String origvapmodule,
			String adr, 
			String work_mode,
			String config_mode, 
			String devicetype, 
			Boolean online, 
			Boolean moduleonline,
			Boolean newVersionDevice, 
			Boolean canOperateable,
			String region, String excepts, String groupids, String groupids_excepts, int pageNo, int pageSize) {
		logger.info(String.format("DeviceRestRPC fetchWDevicesByKeywords invoke mac [%s] orig_swver [%s] adr [%s]"
				+ " work_mode [%s] config_mode [%s] devicetype [%s] region [%s] excepts [%s] pageNo [%s] pageSize [%s]", mac, orig_swver, 
				adr, work_mode, config_mode, devicetype, region, excepts, pageNo, pageSize));
		
		try{
//			return deviceRestBusinessFacadeService.fetchWDeviceByKeywords(mac, sn, orig_swver,origvapmodule, adr, work_mode, config_mode,
//					devicetype, online, moduleonline, newVersionDevice,canOperateable, region, excepts, groupids, groupids_excepts, pageNo, pageSize);
			return null;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchWDevicesByKeywords invoke mac [%s] orig_swver [%s] adr [%s]"
					+ " work_mode [%s] config_mode [%s] devicetype [%s] region [%s] excepts [%s] pageNo [%s] pageSize [%s] exmsg [%s]", mac, orig_swver, 
					adr, work_mode, config_mode, devicetype, region, excepts, pageNo, pageSize, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
	/**
	 * 获取统计通用数据展示
	 */
	@Override
	public StatisticsGeneralVTO fetchStatisticsGeneral() {
		logger.info(String.format("DeviceRestRPC fetchStatisticsGeneral invoke"));
		
		try{
			return deviceRestBusinessFacadeService.fetchStatisticsGeneral();
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchStatisticsGeneral exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	/**
	 * 获取wifi设备地域分布饼图
	 */
	@Override
	public List<RegionCountDTO> fetchWDeviceRegionCount(String regions) {
		logger.info(String.format("DeviceRestRPC fetchWDeviceRegionCount invoke regions [%s] ", regions));
		try{
			//return deviceRestBusinessFacadeService.fetchWDeviceRegionCount(regions);
			return null;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchWDeviceRegionCount exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
	@Override
	public TailPage<WifiDeviceVTO> fetchRecentWDevice(int pageNo, int pageSize) {
		logger.info(String.format("DeviceRestRPC fetchRecentWDevice invoke pageNo [%s] pageSize [%s]", pageNo, pageSize));
		try{
			//return deviceRestBusinessFacadeService.fetchRecentWDevice(pageNo, pageSize);
			return null;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchRecentWDevice exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
	@Override
	public TailPage<HandsetDeviceVTO> fetchHDevices(String wifiId, int pageNo, int pageSize){
		logger.info(String.format("DeviceRestRPC fetchHDevicesOnline invoke wifiId [%s] pageNo [%s] pageSize [%s]", wifiId, pageNo, pageSize));
		try{
			return deviceRestBusinessFacadeService.fetchHDevices(wifiId, pageNo, pageSize);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchHDevicesOnline exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

	@Override
	public RpcResponseDTO<List<PersistenceCMDDetailDTO>> fetchDevicePersistenceDetailCMD(String wifiId) {
		logger.info(String.format("DeviceRestRPC fetchDevicePersistenceDetailCMD invoke wifiId [%s]", wifiId));
		try{
			return deviceRestBusinessFacadeService.fetchDevicePersistenceDetailCMD(wifiId);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchDevicePersistenceDetailCMD exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

	@Override
	public RpcResponseDTO<String> fetchDevicePresent(String wifiId) {
		logger.info(String.format("DeviceRestRPC fetchDevicePresent invoke wifiId [%s]", wifiId));
		try {
			return deviceRestBusinessFacadeService.fetchDevicePresent(wifiId);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchDevicePersistenceDetailCMD exception exmsg[%s]", ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

	@Override
	public RpcResponseDTO<TailPage<WifiDeviceVTO1>> fetchBySearchConditionMessage(String message, int pageNo, int pageSize) {
		logger.info(String.format("DeviceRestRPC fetchBySearchConditionMessage invoke pageNo [%s] pageSize [%s] message [%s]", pageNo, pageSize, message));
		try{
			return deviceRestBusinessFacadeService.fetchBySearchConditionMessage(message, pageNo, pageSize);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchBySearchConditionMessage exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> storeUserSearchCondition(int uid,String message,String desc) {
		logger.info(String.format("DeviceRestRPC storeUserSearchCondition invoke uid [%s] message [%s] desc [%s]", uid, message, desc));
		try{
			return deviceRestBusinessFacadeService.storeUserSearchCondition(uid, message, desc);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC storeUserSearchCondition exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

	@Override
	public RpcResponseDTO<Boolean> removeUserSearchCondition(int uid, long ts) {
		logger.info(String.format("DeviceRestRPC removeUserSearchCondition invoke uid [%s] ts [%s]", uid, ts));
		try{
			return deviceRestBusinessFacadeService.removeUserSearchCondition(uid, ts);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC removeUserSearchCondition exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
	@Override
	public RpcResponseDTO<Boolean> removeUserSearchConditions(int uid, String message_ts_splits) {
		logger.info(String.format("DeviceRestRPC removeUserSearchConditions invoke uid [%s] message_ts_splits [%s]", uid, message_ts_splits));
		try{
			return deviceRestBusinessFacadeService.removeUserSearchConditions(uid, message_ts_splits);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC removeUserSearchConditions exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

	@Override
	public RpcResponseDTO<TailPage<SearchConditionVTO>> fetchUserSearchConditions(int uid, int pageNo, int pageSize) {
		logger.info(String.format("DeviceRestRPC fetchUserSearchConditions invoke uid [%s] pageNo [%s] pageSize [%s]", uid, pageNo, pageSize));
		try{
			return deviceRestBusinessFacadeService.fetchUserSearchConditions(uid, pageNo, pageSize);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchUserSearchConditions exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	@Override
	public RpcResponseDTO<List<UserAgentVTO>> fetchAgents(int uid) {
		logger.info(String.format("DeviceRestRPC fetchAgents invoke uid [%s]", uid));
		try{
			return deviceRestBusinessFacadeService.fetchAgents(uid);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchAgents exception exmsg[%s]",ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

}
