package com.bhu.vas.rpc.service.device;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.redis.DeviceUsedStatisticsDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.vto.URouterAdminPasswordVTO;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdDetailVTO;
import com.bhu.vas.api.vto.URouterHdHostNameVTO;
import com.bhu.vas.api.vto.URouterMainEnterVTO;
import com.bhu.vas.api.vto.URouterModeVTO;
import com.bhu.vas.api.vto.URouterPeakSectionsDTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.api.vto.URouterSettingVTO;
import com.bhu.vas.api.vto.URouterVapPasswordVTO;
import com.bhu.vas.api.vto.URouterWSCommunityVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigMutilVTO;
import com.bhu.vas.api.vto.config.URouterDeviceConfigVTO;
import com.bhu.vas.api.vto.guest.URouterVisitorListVTO;
import com.bhu.vas.rpc.facade.DeviceURouterRestBusinessFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device urouter rest rpc组件服务service 对外暴露接口
 * 处理urouter rest业务
 * @author tangzichao
 *
 */
@Service("deviceURouterRestRpcService")
public class DeviceURouterRestRpcService implements IDeviceURouterRestRpcService {
	private final Logger logger = LoggerFactory.getLogger(DeviceURouterRestRpcService.class);
	
	@Resource
	private DeviceURouterRestBusinessFacadeService deviceURouterRestBusinessFacadeService;
	
	/**
	 * 获取主入口界面数据
	 */
	@Override
	public RpcResponseDTO<URouterMainEnterVTO> urouterMainEnter(Integer uid,
			String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterMainEnter invoke uid [%s] mac [%s]", uid, wifiId));
		try{
			return deviceURouterRestBusinessFacadeService.urouterMainEnter(uid, wifiId.toLowerCase());
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterMainEnter failed uid [%s] mac [%s] ", uid, wifiId));
			throw ex;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterMainEnter exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	/**
	 * 获取入口界面数据
	 */
	@Override
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterEnter invoke uid [%s] mac [%s]", uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterEnter(uid, wifiId.toLowerCase());
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterEnter failed uid [%s] mac [%s] ", uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterEnter exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Map<String,Object>> urouterHdList(Integer uid, String wifiId, int status,
			int start, int size,Boolean filterWiredHandset) {
		logger.info(String.format("DeviceURouterRestRPC urouterHdOnlineList invoke uid [%s] mac [%s] st [%s] ps [%s] filterWiredHandset[%s]", 
				uid, wifiId, start, size,filterWiredHandset));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterHdList(uid, wifiId.toLowerCase(), status, start, size,filterWiredHandset);
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterHdOnlineList failed uid [%s] mac [%s] st [%s] ps [%s]",
					uid, wifiId, start, size));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterHdOnlineList exception uid [%s] mac [%s] st [%s] ps [%s] exmsg[%s]",
					uid, wifiId, start, size, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<URouterHdDetailVTO> urouterHdDetail(Integer uid, String wifiId, String hd_mac) {
		logger.info(String.format("DeviceURouterRestRPC urouterHdDetail invoke uid [%s] wifi_mac [%s] hd_mac [%s]",
				uid, wifiId, hd_mac));

		try{
			return deviceURouterRestBusinessFacadeService.urouterHdDetail(uid, wifiId, hd_mac);
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterHdDetail failed uid [%s] wifi_mac [%s] hd_mac [%s]",
					uid, wifiId, hd_mac));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.info(String.format("DeviceURouterRestRPC urouterHdDetail exception uid [%s] wifi_mac [%s] hd_mac [%s] exmsg[%s]",
					uid, wifiId, hd_mac, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Long> urouterHdModifyAlias(Integer uid, String wifiId, String hd_mac, String alias) {
		logger.info(String.format("DeviceURouterRestRPC urouterHdDetail invoke uid [%s] wifi_mac [%s] hd_mac [%s] alias [%s]",
				uid, wifiId, hd_mac, alias));

		try{
			return deviceURouterRestBusinessFacadeService.urouterHdModifyAlias(uid, wifiId, hd_mac, alias);
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterHdDetail failed uid [%s] wifi_mac [%s] hd_mac [%s], alias [%s]",
					uid, wifiId, hd_mac, alias));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.info(String.format("DeviceURouterRestRPC urouterHdDetail exception uid [%s] wifi_mac [%s] hd_mac [%s] alias [%s] exmsg[%s]",
					uid, wifiId, hd_mac,alias, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterRealtimeRate invoke uid [%s] mac [%s] ", 
				uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterRealtimeRate(uid, wifiId.toLowerCase());
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterRealtimeRate failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterRealtimeRate exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<String> urouterPeakSection(Integer uid, String wifiId, int type, int period, int duration) {
		logger.info(String.format("DeviceURouterRestRPC urouterPeakSection invoke uid [%s] mac [%s] type [%s] period [%s] duration [%s] ",
				uid, wifiId, type, period, duration ));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterPeakSection(uid, wifiId.toLowerCase(), type, period, duration);
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterPeakSection failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterPeakSection exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<URouterPeakSectionsDTO> urouterPeakSectionFetch(Integer uid, String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterPeakSectionFetch invoke uid [%s] mac [%s] ", 
				uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterPeakSectionFetch(uid, wifiId.toLowerCase());
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterPeakSectionFetch failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterPeakSectionFetch exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Map<String,Object>> urouterBlockList(Integer uid, String wifiId, int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterBlockList invoke uid [%s] mac [%s] st [%s] ps [%s]", 
				uid, wifiId, start, size));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterBlockList(uid, wifiId.toLowerCase(), start, size);
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterBlockList failed uid [%s] mac [%s] st [%s] ps [%s]",
					uid, wifiId, start, size));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterBlockList exception uid [%s] mac [%s] st [%s] ps [%s] exmsg[%s]",
					uid, wifiId, start, size, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<URouterSettingVTO> urouterSetting(Integer uid, String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterSetting invoke uid [%s] mac [%s]", 
				uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterSetting(uid, wifiId.toLowerCase());
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterSetting failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterSetting exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<URouterModeVTO> urouterLinkMode(Integer uid,
			String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterLinkMode invoke uid [%s] mac [%s]", 
				uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterLinkMode(uid, wifiId.toLowerCase());
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterLinkMode failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterLinkMode exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceRegister(Integer uid,
			String d, String dt, String dm, String cv, String pv, String ut, String pt) {
		
		logger.info(String.format("DeviceURouterRestRPC urouterUserMobileDeviceRegister invoke uid [%s] d [%s] dt [%s]", 
				uid, d, dt));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterUserMobileDeviceRegister(uid, d, dt, 
					dm, cv, pv, ut, pt);
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterUserMobileDeviceRegister failed uid [%s] d [%s] dt [%s]",
					uid, d, dt));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterUserMobileDeviceRegister exception uid [%s] d [%s] dt [%s] exmsg[%s]",
					uid, d, dt, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Boolean> urouterUserMobileDeviceDestory(Integer uid,
			String d, String dt) {
		logger.info(String.format("DeviceURouterRestRPC urouterUserMobileDeviceDestory invoke uid [%s] d [%s] dt [%s]", 
				uid, d, dt));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterUserMobileDeviceDestory(uid, d, dt);
		}
		catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterUserMobileDeviceDestory failed uid [%s] d [%s] dt [%s]",
					uid, d, dt));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterUserMobileDeviceDestory exception uid [%s] d [%s] dt [%s] exmsg[%s]",
					uid, d, dt, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<Map<String,Object>> urouterPlugins(Integer uid, String wifiId){
		logger.info(String.format("DeviceURouterRestRPC urouterPlugins invoke uid [%s] mac [%s]", 
				uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterPlugins(uid, wifiId);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterPlugins failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterPlugins exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<Boolean> urouterUpdPluginTerminalOnline(Integer uid,
			String wifiId, boolean on, boolean stranger_on, boolean alias_on, String timeslot,
			int timeslot_mode) {
		logger.info(String.format("DeviceURouterRestRPC urouterUpdPluginTerminalOnline invoke uid [%s] mac [%s]", 
				uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterUpdPluginTerminalOnline(uid, wifiId, 
					on, stranger_on,alias_on, timeslot, timeslot_mode);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterUpdPluginTerminalOnline failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterUpdPluginTerminalOnline exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<Boolean> urouterUpdPluginWifisniffer(Integer uid,
			String wifiId, boolean on) {
		logger.info(String.format("DeviceURouterRestRPC urouterUpdPluginWifisniffer invoke uid [%s] mac [%s]", 
				uid, wifiId));
		try{
			return deviceURouterRestBusinessFacadeService.urouterUpdPluginWifisniffer(uid, wifiId,on);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterUpdPluginWifisniffer failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterUpdPluginWifisniffer exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<DeviceUsedStatisticsDTO> urouterDeviceUsedStatusQuery(Integer uid,String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterDeviceUsedStatusQuery invoke uid [%s] mac [%s]", 
				uid, wifiId));
		try{
			return deviceURouterRestBusinessFacadeService.urouterDeviceUsedStatusQuery(uid, wifiId);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterDeviceUsedStatusQuery failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterDeviceUsedStatusQuery exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	
	@Override
	public RpcResponseDTO<URouterDeviceConfigVTO> urouterConfigs(Integer uid, String mac) {
		logger.info(String.format("DeviceURouterRestRPC urouterConfigs invoke uid [%s] mac [%s]", 
				uid, mac));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterConfigs(uid, mac);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterConfigs failed uid [%s] mac [%s]",
					uid, mac));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterConfigs exception uid [%s] mac [%s] exmsg[%s]",
					uid, mac, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<URouterDeviceConfigMutilVTO> urouterConfigsSupportMulti(Integer uid, String mac) {
		logger.info(String.format("DeviceURouterRestRPC urouterConfigsSupportMulti invoke uid [%s] mac [%s]", 
				uid, mac));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterConfigsSupportMulti(uid, mac);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterConfigsSupportMulti failed uid [%s] mac [%s]",
					uid, mac));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterConfigsSupportMulti exception uid [%s] mac [%s] exmsg[%s]",
					uid, mac, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<List<URouterHdHostNameVTO>> terminalHostnames(Integer uid, String macs) {
		logger.info(String.format("DeviceURouterRestRPC terminalHostnames invoke uid [%s] macs [%s]", 
				uid, macs));
		
		try{
			return deviceURouterRestBusinessFacadeService.terminalHostnames(uid, macs);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC terminalHostnames failed uid [%s] macs [%s]",
					uid, macs));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC terminalHostnames exception uid [%s] macs [%s] exmsg[%s]",
					uid, macs, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<URouterAdminPasswordVTO> urouterAdminPassword(Integer uid, String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterAdminPassword invoke uid [%s] mac [%s]",
				uid, wifiId));
		return deviceURouterRestBusinessFacadeService.urouterAdminPassword(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<URouterVapPasswordVTO> urouterVapPassword(Integer uid, String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterVapPassword invoke uid [%s] mac [%s]",
				uid, wifiId));
		return deviceURouterRestBusinessFacadeService.urouterVapPassword(uid, wifiId);
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> urouterWSRecent(Integer uid,
			String mac, int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterWSRecent invoke uid [%s] mac [%s]", 
				uid, mac));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterWSRecent(uid, mac, start, size);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterWSRecent failed uid [%s] mac [%s]",
					uid, mac));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterWSRecent exception uid [%s] mac [%s] exmsg[%s]",
					uid, mac, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Map<String, Object>> urouterWSNeighbour(Integer uid,
			String mac, int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterWSNeighbour invoke uid [%s] mac [%s]", 
				uid, mac));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterWSNeighbour(uid, mac, start, size);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterWSNeighbour failed uid [%s] mac [%s]",
					uid, mac));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterWSNeighbour exception uid [%s] mac [%s] exmsg[%s]",
					uid, mac, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Boolean> urouterWSFocus(Integer uid, String hd_mac, boolean focus) {
		logger.info(String.format("DeviceURouterRestRPC urouterWSNeighbour invoke uid [%s] hd_mac [%s]", 
				uid, hd_mac));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterWSFocus(uid, hd_mac, focus);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterWSNeighbour failed uid [%s] hd_mac [%s]",
					uid, hd_mac));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterWSNeighbour exception uid [%s] hd_mac [%s] exmsg[%s]",
					uid, hd_mac, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	@Override
	public RpcResponseDTO<Boolean> urouterWSNick(Integer uid, String hd_mac, String nick) {
		logger.info(String.format("DeviceURouterRestRPC urouterWSNeighbour invoke uid [%s] hd_mac [%s] nick [%s]", 
				uid, hd_mac, nick));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterWSNick(uid, hd_mac, nick);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterWSNeighbour failed uid [%s] hd_mac [%s] nick [%s]",
					uid, hd_mac, nick));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterWSNeighbour exception uid [%s] hd_mac [%s] nick [%s] exmsg[%s]",
					uid, hd_mac, nick, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<Map<String,Object>> urouterWSDetails(Integer uid, String mac, String hd_mac, int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterWSDetails invoke uid [%s] mac [%s] hd_mac [%s]", 
				uid, mac, hd_mac));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterWSDetails(uid, mac, hd_mac, start, size);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterWSDetails failed uid [%s] mac [%s] hd_mac [%s]",
					uid, mac, hd_mac));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterWSDetails exception uid [%s] mac [%s] hd_mac [%s] exmsg[%s]",
					uid, mac, hd_mac, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<URouterWSCommunityVTO> urouterWSCommunity(Integer uid, String mac) {
		logger.info(String.format("DeviceURouterRestRPC urouterWSCommunity invoke uid [%s] mac [%s]", 
				uid, mac));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterWSCommunity(uid, mac);
		}catch(BusinessI18nCodeException ex){
			logger.info(String.format("DeviceURouterRestRPC urouterWSCommunity failed uid [%s] mac [%s]",
					uid, mac));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterWSCommunity exception uid [%s] mac [%s] exmsg[%s]",
					uid, mac, ex.getMessage()), ex);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}


	@Override
	public RpcResponseDTO<URouterVisitorListVTO> urouterVisitorList(Integer uid, String mac, int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterVisitorList invoke uid [%s] mac [%s] start[%s] size[%s]",
				uid, mac, start, size));
		return deviceURouterRestBusinessFacadeService.urouterVisitorList(uid, mac, start, size);
	}


	@Override
	public RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListOffline(Integer uid, String mac, int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterVisitorListOffline invoke uid [%s] mac [%s] start[%s] size[%s]",
				uid, mac, start, size));
		return deviceURouterRestBusinessFacadeService.urouterVisitorListOffline(uid, mac, start, size);
	}

	@Override
	public RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListOnline(Integer uid, String mac, int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterVisitorListOnline invoke uid [%s] mac [%s] start[%s] size[%s]",
				uid, mac, start, size));
		return deviceURouterRestBusinessFacadeService.urouterVisitorListOnline(uid, mac, start, size);
	}

	@Override
	public RpcResponseDTO<URouterVisitorListVTO> urouterVisitorListAll(Integer uid, String mac, int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterVisitorListAll invoke uid [%s] mac [%s] start[%s] size[%s]",
				uid, mac, start, size));
		return deviceURouterRestBusinessFacadeService.urouterVisitorListAll(uid, mac, start, size);
	}

	@Override
	public RpcResponseDTO<Boolean> urouterVisitorRemoveHandset(Integer uid, String mac, String hd_mac) {
		logger.info(String.format("DeviceURouterRestRPC urouterVisitorRemoveHandset invoke uid[%s] mac[%s] hd_mac[%s]",
				uid, mac, hd_mac));
		return deviceURouterRestBusinessFacadeService.urouterVisitorRemoveHandset(uid, mac, hd_mac);
	}
	
	@Override
	public RpcResponseDTO<TailPage<UserDeviceDTO>> urouterFetchBySearchConditionMessage(Integer uid, String message, 
			int pageNo, int pageSize) {
		logger.info(String.format("DeviceURouterRestRPC fetchBySearchConditionMessage invoke uid[%s] message[%s] pageNo[%s] pageSize[%s]",
				uid, message, pageNo, pageSize));
		return deviceURouterRestBusinessFacadeService.urouterFetchBySearchConditionMessage(uid, message, pageNo, pageSize);
	}

}
