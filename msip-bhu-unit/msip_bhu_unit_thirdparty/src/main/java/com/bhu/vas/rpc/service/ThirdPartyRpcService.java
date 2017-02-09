package com.bhu.vas.rpc.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeConfigDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeDeviceDTO;
import com.bhu.vas.api.rpc.thirdparty.iservice.IThirdPartyRpcService;
import com.bhu.vas.rpc.facade.ThirdPartyUnitFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 第三方业务的组件服务service 对外暴露接口
 * @author yetao
 *
 */
@Service("thirdPartyRpcService")
public class ThirdPartyRpcService implements IThirdPartyRpcService {
	private final Logger logger = LoggerFactory.getLogger(ThirdPartyRpcService.class);

    @Resource
	private ThirdPartyUnitFacadeService thirdPartyUnitFacadeService;
	
	public RpcResponseDTO<Boolean> gomeBindDevice(String mac){
		logger.info(String.format("gomeBindDevice mac:%s",mac));
		try{
			Boolean ret = thirdPartyUnitFacadeService.gomeBindDevice(mac);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> gomeUnbindDevice(String mac){
		logger.info(String.format("gomeUnBindDevice mac:%s",mac));
		try{
			Boolean ret = thirdPartyUnitFacadeService.gomeUnBindDevice(mac);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	
	public RpcResponseDTO<Boolean> gomeDeviceControl(String mac, GomeConfigDTO dto){
		logger.info(String.format("gomeDeviceControl mac:%s, dto:[%s]",mac, JsonHelper.getJSONString(dto)));
		try{
			Boolean ret = thirdPartyUnitFacadeService.gomeDeviceControl(mac, dto);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<GomeDeviceDTO> gomeDeviceOnlineGet(String mac) {
		logger.info(String.format("gomeDeviceOnlineGet mac:%s",mac));
		try{
			GomeDeviceDTO ret = thirdPartyUnitFacadeService.gomeDeviceOnlineGet(mac);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	@Override
	public RpcResponseDTO<GomeConfigDTO> gomeDeviceStatusGet(String mac) {
		logger.info(String.format("gomeDeviceStatusGet mac:%s",mac));
		try{
			GomeConfigDTO ret = thirdPartyUnitFacadeService.gomeDeviceStatusGet(mac);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}


}