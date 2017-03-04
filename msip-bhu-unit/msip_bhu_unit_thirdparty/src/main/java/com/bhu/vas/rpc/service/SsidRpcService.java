package com.bhu.vas.rpc.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.thirdparty.iservice.ISsidRpcService;
import com.bhu.vas.api.rpc.wifi.model.SsidInfo;
import com.bhu.vas.business.search.service.wifi.SsidDataSearchService;
import com.bhu.vas.rpc.facade.SsidUnitFacadeService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 第三方业务的组件服务service 对外暴露接口
 * @author yetao
 *
 */
@Service("thirdPartyRpcService")
public class SsidRpcService implements ISsidRpcService {
	private final Logger logger = LoggerFactory.getLogger(SsidRpcService.class);

    @Resource
	private SsidUnitFacadeService ssidUnitFacadeService;
	
	@Resource
	private SsidDataSearchService ssidDataSearchService;

    
	public RpcResponseDTO<Boolean> reportSsidInfo(String bssid, String ssid, String mode, String pwd, Double lat, Double lon){
		logger.info(String.format("reportSsidInfo bssid[%s] ssid[%s] mode[%s] pwd[%s], lat[%s], lon[%s]", 
				bssid, ssid, mode, pwd, lat, lon));
		try{
			Boolean ret = ssidUnitFacadeService.reportSsidInfo(bssid, ssid, mode, pwd, lat, lon);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<SsidInfo> querySsidInfo(String bssid, String ssid, String mode){
		logger.info(String.format("querySsidInfo bssid[%s], ssid[%s], mode[%s]", bssid, ssid, mode));
		try{
			SsidInfo ret = ssidUnitFacadeService.querySsidInfo(bssid, ssid, mode);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
