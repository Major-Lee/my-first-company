package com.bhu.vas.rpc.service.advertise;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.advertise.AdvertiseVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.rpc.facade.AdvertiseUnitFacadeService;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service("advertiseRpcService")
public class AdvertiseRpcService implements IAdvertiseRpcService{
	private final Logger logger = LoggerFactory.getLogger(AdvertiseRpcService.class);
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

	@Override
	public RpcResponseDTO<Boolean> createNewAdvertise(int uid, String image,
			String url,String domain, String province, String city, String district,String description,String title,
			long start, long end) {
		logger.info(String.format("createNewAdvertise with uid[%s] image[%s] url[%s] domain[%s] province[%s] city[%s] district[%s] title[%s] description[%s] start[%s] start[%s]",
				uid, image, url,domain, province, city, district,description, start, end));
		return advertiseUnitFacadeService.createNewAdvertise(uid, image, url,domain, province, city, district,description,title, start, end);
	}

	@Override
	public RpcResponseDTO<Boolean> updateAdvertise(int uid,int advertiseId, String image,
			String url, String domain, String province, String city,
			String district, String description, String title, long start,
			long end) {
		logger.info(String.format("updateAdvertise with uid[%s] advertiseId[%s] image[%s] url[%s] domain[%s] province[%s] city[%s] district[%s] title[%s] description[%s] start[%s] start[%s]",
				uid,advertiseId, image, url,domain, province, city, district,description, start, end));
		return advertiseUnitFacadeService.updateAdvertise(uid,advertiseId, image, url,domain, province, city, district,description,title, start, end);
	}

	@Override
	public RpcResponseDTO<Boolean> verifyAdvertise(int uid, int advertiseId,String msg,
			int state) {
		logger.info(String.format("verifyAdvertise with uid[%s] advertiseId[%s] msg[%s] state[%s]",
				uid,advertiseId, msg,state));
		return advertiseUnitFacadeService.verifyAdvertise(uid,advertiseId,msg,state);
	}

	@Override
	public RpcResponseDTO<AdvertiseVTO> queryAdvertise(int advertiseId) {
		logger.info(String.format("queryAdvertise advertiseId[%s]",
				advertiseId));
		return advertiseUnitFacadeService.queryAdvertiseInfo(advertiseId);
	}

}
