package com.bhu.vas.rpc.facade;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.AdvertiseHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.user.dto.UserInnerExchangeDTO;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class AdvertiseUnitFacadeService {
	@Resource
	private AdvertiseService advertiseService;
	public RpcResponseDTO<Boolean> createNewAdvertise(int uid,
			String image, String url, String province, String city,
			String district, long start, long end) {
		try{
			Advertise entity=new Advertise();
			entity.setCity(city);
			//
			entity.setCount(0);
			Date date=new Date();
			entity.setCreated_at(date);
			entity.setDistrict(district);
			Date endDate=new Date(end);
			entity.setEnd(endDate);
			entity.setImage(image);
			entity.setProvince(province);
			Date startDate=new Date(start);
			entity.setStart(startDate);
			entity.setState("0");
			//间隔天数
			long between_days = (end - start) / (1000 * 3600 * 24);
			int duration=Integer.parseInt(String.valueOf(between_days));
			
			entity.setDuration(duration);
			entity.setUrl(url);
			advertiseService.insert(entity);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
