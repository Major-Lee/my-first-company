package com.bhu.vas.rpc.facade;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.advertise.AdvertiseVTO;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePositionListService;

@Service
public class AdvertiseUnitFacadeService {
	@Resource
	private AdvertiseService advertiseService;
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	@Resource
	private UserService userService;
	
	public RpcResponseDTO<Boolean> createNewAdvertise(int uid,
			String image, String url,String domain, String province, String city,
			String district,String description,String title, long start, long end) {
		try{
			Advertise entity=new Advertise();
			entity.setCity(city);
			long count=wifiDeviceDataSearchService.searchCountByPosition(province, city, district);
			entity.setCount(count);
			entity.setDistrict(district);
			Date endDate=new Date(end);
			entity.setEnd(endDate);
			entity.setImage(image);
			entity.setProvince(province);
			Date startDate=new Date(start);
			entity.setStart(startDate);
			entity.setState(AdvertiseType.UnPaid.getType());
			
			entity.setType(0);
			entity.setDomain(domain);
			entity.setDescription(description);
			entity.setTitle(title);
			entity.setUid(uid);
			//间隔天数
			long between_days = (end - start) / (1000 * 3600 * 24);
			int duration=Integer.parseInt(String.valueOf(between_days));
			entity.setDuration(duration);
			entity.setUrl(url);
			int n=advertiseService.getEntityDao().countByAdvertiseTime(startDate, endDate);
			if(n==0){
				advertiseService.insert(entity);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
			}else{
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(false);
			}
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
	/**
	 * 根据条件查询广告列表
	 * @param conditionMap
	 * @return
	 */
	public List<Advertise> queryAdvertiseList(List<Map<String,Object>> conditionMap,String publishStartTime,String publishEndTime,String createStartTime,String createEndTime,String userName){
		List<Advertise> advertises=null;
		ModelCriteria mc=new ModelCriteria();
		Criteria criteria= mc.createCriteria();
		if(conditionMap!=null&&conditionMap.size()>0){
			for(Map<String,Object> singleMap:conditionMap){
				criteria.andColumnEqualTo(singleMap.get("name").toString(), singleMap.get("value"));
			}
		}
		if(StringUtils.isNotBlank(publishStartTime)){
			if(StringUtils.isNotBlank(publishEndTime)){
				criteria.andColumnBetween("start", publishStartTime, publishEndTime);
			}else{
				criteria.andColumnEqualTo("start", publishStartTime);
			}
		}
		if(StringUtils.isNotBlank(createStartTime)){
			if(StringUtils.isNotBlank(createEndTime)){
				criteria.andColumnBetween("created_at", createStartTime, createStartTime);
			}else{
				criteria.andColumnEqualTo("created_at", createEndTime);
			}
		}
		if(StringUtils.isNotBlank(userName)){
			ModelCriteria userMc=new ModelCriteria();
			Criteria userCriteria= userMc.createCriteria();
			userCriteria.andColumnLike("nick", userName);
			List<Integer> userIds=userService.findIdsByModelCriteria(userMc);
			criteria.andColumnIn("uid", userIds);
		}
		advertises=advertiseService.findModelByModelCriteria(mc);
		return advertises;
	}
	/**
	 * 审核广告
	 * @param verify_uid
	 * @param advertiseId
	 * @param msg
	 * @param state
	 * @return
	 */
	public RpcResponseDTO<Boolean> verifyAdvertise(int verify_uid,int advertiseId,String msg,int state){
		try{ 
			Advertise advertise=advertiseService.getById(advertiseId);
			advertise.setVerify_uid(verify_uid);
			if(state==0){
				advertise.setState(AdvertiseType.UnPublish.getType());
			}else{
				advertise.setState(AdvertiseType.VerifyFailure.getType());
				advertise.setReject_reason(msg);
			}
			User user=userService.getById(verify_uid);
			if(verify_uid==2||user.getUtype()==13){
				advertiseService.update(advertise);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
			}else{
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(false);
			}
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	/**
	 * 查询广告详情
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<AdvertiseVTO> queryAdvertiseInfo(int advertiseId){
		try{
			Advertise advertise=advertiseService.getById(advertiseId);
			AdvertiseVTO advertiseVTO=advertise.toVTO();
			//广告提交人信心
			User user=userService.getById(advertise.getUid());
			advertiseVTO.setOwnerName(user.getNick());

			return RpcResponseDTOBuilder.builderSuccessRpcResponse(advertiseVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> updateAdvertise(int uid,int advertiseId, String image,
			String url, String domain, String province, String city,
			String district, String description, String title, long start,
			long end) {
		try{
			Advertise entity=advertiseService.getById(advertiseId);
			entity.setCity(city);
			long count=wifiDeviceDataSearchService.searchCountByPosition(province, city, district);
			entity.setCount(count);
			entity.setDistrict(district);
			Date endDate=new Date(end);
			entity.setEnd(endDate);
			entity.setImage(image);
			entity.setProvince(province);
			Date startDate=new Date(start);
			entity.setStart(startDate);
			entity.setState(AdvertiseType.UnPaid.getType());
			
			entity.setType(0);
			entity.setDomain(domain);
			entity.setDescription(description);
			entity.setTitle(title);
			entity.setUid(uid);
			//间隔天数
			long between_days = (end - start) / (1000 * 3600 * 24);
			int duration=Integer.parseInt(String.valueOf(between_days));
			entity.setDuration(duration);
			entity.setUrl(url);
			int n=advertiseService.getEntityDao().countByAdvertiseTime(startDate, endDate);
			if(n==0){
				advertiseService.update(entity);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
			}else{
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(false);
			}
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
