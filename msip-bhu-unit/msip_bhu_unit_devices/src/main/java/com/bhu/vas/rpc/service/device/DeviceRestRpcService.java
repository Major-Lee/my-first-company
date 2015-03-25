package com.bhu.vas.rpc.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.search.WifiDeviceSearchDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.rpc.facade.DeviceRestBusinessFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.es.request.QueryResponse;
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
	 * 获取在线设备列表
	 */
	@Override
	public TailPage<WifiDeviceVTO> fetchWDevicesByKeyword(String keyword, int pageNo, int pageSize) {
		logger.info(String.format("DeviceRestRPC fetchWDevicesByKeyword invoke pageNo [%s] pageSize [%s]", pageNo, pageSize));
		
		try{
			return deviceRestBusinessFacadeService.fetchWDeviceByKeyword(keyword, pageNo, pageSize);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(String.format("DeviceRestRPC fetchWDevicesByKeyword exception pageNo [%s] pageSize [%s] exmsg[%s]",
					pageNo, pageSize, ex.getMessage()), ex);
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
	
}
