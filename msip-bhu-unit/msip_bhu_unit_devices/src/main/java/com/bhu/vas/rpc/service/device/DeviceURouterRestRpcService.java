package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.vto.URouterEnterVTO;
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
	public URouterEnterVTO urouterEnter(String wifiId) {
		logger.info(String.format("DeviceURouterRestRPC urouterEnter invoke mac [%s]", wifiId));
		
		try{
			return deviceURouterRestBusinessFacadeService.urouterEnter(wifiId);
		}
		catch(RpcBusinessI18nCodeException ex){
			logger.info(String.format("DeviceMessageRPC urouterEnter failed mac [%s] ", wifiId));
			throw ex;
		}
		catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceURouterRestRPC urouterEnter exception mac [%s] exmsg[%s]",
					wifiId, ex.getMessage()), ex);
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR.code());
		}
	}

}
