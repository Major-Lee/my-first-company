package com.bhu.vas.rpc.service.advertise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.dto.advertise.AdvertiseVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.rpc.facade.AdvertiseUnitFacadeService;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service("advertiseRpcService")
public class WholeCityRpcService implements IAdvertiseRpcService{
	private final Logger logger = LoggerFactory.getLogger(WholeCityRpcService.class);
	@Resource
	private AdvertiseUnitFacadeService advertiseUnitFacadeService;
	@Resource
	private UserService userService;
	
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
				uid, image, url,domain,title, province, city, district,description, start, end));
		return advertiseUnitFacadeService.createNewAdvertise(uid, image, url,domain, province, city, district,description,title, start, end);
	}

	@Override
	public RpcResponseDTO<Boolean> updateAdvertise(int uid,int advertiseId, String image,
			String url, String domain, String province, String city,
			String district, String description, String title, long start,
			long end) {
		logger.info(String.format("updateAdvertise with uid[%s] advertiseId[%s] image[%s] url[%s] domain[%s] province[%s] city[%s] district[%s] title[%s] description[%s] start[%s] end[%s]",
				uid,advertiseId, image, url,domain, province, city, district,title,description, start, end));
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

	@Override
	public RpcResponseDTO<AdvertiseListVTO> queryAdvertiseList(int uid, String province,
			String city, String district, String publishStartTime,
			String publishEndTime, int type, String createStartTime,
			String createEndTime, String userName,int state) {
		logger.info(String.format("queryAdvertiseList uid[%s] province[%s] city[%s] district[%s] publishStartTime[%s] publishEndTime[%s] type[%s] createStartTime[%s] createEndTime[%s] userName[%s] state[%s]",
				uid,province,city,district,publishStartTime,publishEndTime,type,createStartTime,createEndTime,userName,state));
		List<Map<String,Object>> maps=new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(province)){
			Map<String,Object> provinceMap=new HashMap<String,Object>();
			provinceMap.put("name", "province");
			provinceMap.put("value", province);
			maps.add(provinceMap);
		}
		if(StringUtils.isNotBlank(city)){
			Map<String,Object> cityMap=new HashMap<String,Object>();
			cityMap.put("name", "city");
			cityMap.put("value", city);
			maps.add(cityMap);
		}
		if(StringUtils.isNotBlank(district)){
			Map<String,Object> districtMap=new HashMap<String,Object>();
			districtMap.put("name", "district");
			districtMap.put("value", district);
		}
		if(state!=-1){
			Map<String,Object> districtMap=new HashMap<String,Object>();
			districtMap.put("name", "state");
			districtMap.put("value", state);
		}
		
		Map<String,Object> typeMap=new HashMap<String,Object>();
		typeMap.put("name", "type");
		typeMap.put("value", type);
		maps.add(typeMap);
		try{
			List<Advertise> advertises=advertiseUnitFacadeService.queryAdvertiseList(maps,publishStartTime,publishEndTime,createStartTime,createEndTime,userName);
			AdvertiseListVTO advertiseListVTO=new AdvertiseListVTO();
			List<AdvertiseVTO> advertiseVTOs=new ArrayList<AdvertiseVTO>();
			if(advertises!=null){
				for(Advertise ad:advertises){
					AdvertiseVTO singleAdvertise = ad.toVTO();
					//广告提交人信心
					User user=userService.getById(ad.getUid());
					singleAdvertise.setOwnerName(user.getNick());
					advertiseVTOs.add(singleAdvertise);
				}
			}
			advertiseListVTO.setAdvertiseList(advertiseVTOs);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(advertiseListVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
