package com.bhu.vas.rpc.facade;

import java.util.Date;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePositionListService;

@Service
public class AdvertiseUnitFacadeService {
	@Resource
	private AdvertiseService advertiseService;
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	public RpcResponseDTO<Boolean> createNewAdvertise(int uid,
			String image, String url, String province, String city,
			String district,String description,String title, long start, long end) {
		try{
			Advertise entity=new Advertise();
			entity.setCity(city);
			
			long count=wifiDeviceDataSearchService.searchCountByPosition(province, city, district);
			entity.setCount(count);
			Date date=new Date();
			entity.setCreated_at(date);
			entity.setDistrict(district);
			Date endDate=new Date(end);
			entity.setEnd(endDate);
			entity.setImage(image);
			entity.setProvince(province);
			Date startDate=new Date(start);
			entity.setStart(startDate);
			entity.setState(AdvertiseType.UnPaid.getType());
			
			entity.setDescription(description);
			entity.setTitle(title);
			entity.setUid(uid);
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

	/**
	 * 获取现有设备地理位置
	 * @param province
	 * @param city
	 * @return
	 */
	public List<String> fetchDevicePositionDistribution(String province,String city){
		if(city !=null){
			return WifiDevicePositionListService.getInstance().fetchCity(city);
		}else if(province !=null){
			return WifiDevicePositionListService.getInstance().fetchProvince(province);
		}else{
			return WifiDevicePositionListService.getInstance().fetchAllProvince();
		}
	}
}
