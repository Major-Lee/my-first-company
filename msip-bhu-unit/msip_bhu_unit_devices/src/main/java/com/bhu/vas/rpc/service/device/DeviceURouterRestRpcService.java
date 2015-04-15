package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.rpc.facade.DeviceURouterRestBusinessFacadeService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
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
	 * 获取入口界面数据
	 */
	@Override
	public RpcResponseDTO<URouterEnterVTO> urouterEnter(Integer uid, String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterEnter invoke uid [%s] mac [%s]", uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterEnter(uid, wifiId.toLowerCase());
		}
		catch(RpcBusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterEnter failed uid [%s] mac [%s] ", uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterEnter exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

	@Override
	public RpcResponseDTO<List<URouterHdVTO>> urouterHdList(Integer uid, String wifiId, int status,
			int start, int size) {
		logger.info(String.format("DeviceURouterRestRPC urouterHdOnlineList invoke uid [%s] mac [%s] st [%s] ps [%s]", 
				uid, wifiId, start, size));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterHdList(uid, wifiId.toLowerCase(), status, start, size);
		}
		catch(RpcBusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterHdOnlineList failed uid [%s] mac [%s] st [%s] ps [%s]",
					uid, wifiId, start, size));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterHdOnlineList exception uid [%s] mac [%s] st [%s] ps [%s] exmsg[%s]",
					uid, wifiId, start, size, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	
	@Override
	public RpcResponseDTO<URouterRealtimeRateVTO> urouterRealtimeRate(Integer uid, String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterRealtimeRate invoke uid [%s] mac [%s] ", 
				uid, wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterRealtimeRate(uid, wifiId.toLowerCase());
		}
		catch(RpcBusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterRealtimeRate failed uid [%s] mac [%s]",
					uid, wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterRealtimeRate exception uid [%s] mac [%s] exmsg[%s]",
					uid, wifiId, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}
	

}
