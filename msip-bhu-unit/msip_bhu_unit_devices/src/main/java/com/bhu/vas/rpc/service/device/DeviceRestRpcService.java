package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.rpc.facade.DeviceRestBusinessFacadeService;
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
	
}
