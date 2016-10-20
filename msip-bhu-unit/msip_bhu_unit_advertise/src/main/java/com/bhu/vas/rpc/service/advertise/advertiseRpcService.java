package com.bhu.vas.rpc.service.advertise;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.rpc.facade.AdvertiseUnitFacadeService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service("advertiseRpcService")
public class advertiseRpcService implements IAdvertiseRpcService{
	private final Logger logger = LoggerFactory.getLogger(advertiseRpcService.class);
	
	@Resource
	private AdvertiseUnitFacadeService advertiseUnitFacadeService;

	@Override
	public RpcResponseDTO<List<String>> fetchDevicePositionDistribution(String province, String city) {
		logger.info(String.format("fetchDevicePositionDistribution province[%s] city[%s]", province, city));
		try {
			List<String> list = advertiseUnitFacadeService.fetchDevicePositionDistribution(province, city);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(list);
		} catch (BusinessI18nCodeException i18nex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(), i18nex.getPayload());
		} catch (Exception ex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

}
