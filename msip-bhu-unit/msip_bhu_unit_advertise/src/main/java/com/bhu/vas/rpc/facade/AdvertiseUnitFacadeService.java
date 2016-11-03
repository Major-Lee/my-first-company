package com.bhu.vas.rpc.facade;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.advertise.AdvertiseVTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePositionListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;

@Service
public class AdvertiseUnitFacadeService {
	@Resource
	private AdvertiseService advertiseService;
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	@Resource
	private UserService userService;
	
	
	public AdvertiseService getAdvertiseService() {
		return advertiseService;
	}

	public void setAdvertiseService(AdvertiseService advertiseService) {
		this.advertiseService = advertiseService;
	}

	public RpcResponseDTO<Boolean> createNewAdvertise(int uid,
			String image, String url,String domain, String province, String city,
			String district,String description,String title, long start, long end) {
		try{
			Advertise entity=new Advertise();
			entity.setCity(city);
			long count=wifiDeviceDataSearchService.searchCountByPosition(province, city, district);
			if(start>end){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIME_TIMEERROR);
			}
			entity.setCount(count);
			entity.setDistrict(district);
			Date endDate=new Date(end);
			entity.setEnd(endDate);
			entity.setImage(image);
			entity.setProvince(province);
			Date startDate=new Date(start);
			entity.setStart(startDate);
			entity.setState(AdvertiseType.UnVerified.getType());
			
			entity.setType(0);
			entity.setDomain(domain);
			entity.setDescription(description);
			entity.setTitle(title);
			entity.setUid(uid);
			Date date=new Date();
			entity.setCreated_at(date);
			//间隔天数
			long between_days = (end - start) / (1000 * 3600 * 24);
			int duration=Integer.parseInt(String.valueOf(between_days));
			entity.setDuration(duration);
			entity.setUrl(url);
			int n=advertiseService.getEntityDao().countByAdvertiseTime(startDate, endDate,province, city, district);
			if(n!=0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIMEFIELD_OVERLAY);
			}
			ModelCriteria mc=new ModelCriteria();
			List<Integer> stateList=new ArrayList<Integer>();
			stateList.add(AdvertiseType.UnPaid.getType());
			stateList.add(AdvertiseType.UnPublish.getType());
			stateList.add(AdvertiseType.UnVerified.getType());
			stateList.add(AdvertiseType.OnPublish.getType());
			mc.createCriteria().andColumnIn("state", stateList).andColumnEqualTo("uid", uid);
			
			int num=advertiseService.countByModelCriteria(mc);
			if(num>=2){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_NUMFIELD_BEYOND);
			}
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
	public List<String> fetchDevicePositionDistribution(String province,String city,String district){
		if(StringUtils.isNotBlank(district)){
			List<String> disableTime = new ArrayList<String>();
			ModelCriteria mc=new ModelCriteria();
			try {
				mc.createCriteria().andColumnEqualTo("province", province)
				.andColumnEqualTo("city", city).andColumnEqualTo("district", district)
				.andColumnBetween("start", DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 2),
						DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 17))
				.andColumnNotEqualTo("state", BusinessEnumType.AdvertiseType.EscapeOrder.getType())
				.andColumnNotEqualTo("state", BusinessEnumType.AdvertiseType.VerifyFailure.getType());
				List<Advertise> advertises = advertiseService.findModelByModelCriteria(mc);
				for(Advertise advertise : advertises){
					String startTime = DateTimeHelper.getDateTime(advertise.getStart(), DateTimeHelper.FormatPattern5);
					disableTime.add(startTime);
					if(advertise.getDuration() != 0){
						for(int i=1; i<=advertise.getDuration(); i++){
							disableTime.add(DateTimeHelper.getAfterDate(startTime, i));
						}
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return disableTime;
		}else if(StringUtils.isNoneBlank(city)){
			return WifiDevicePositionListService.getInstance().fetchCity(city);
		}else if(StringUtils.isNoneBlank(province)){
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
	public TailPage<AdvertiseVTO> queryAdvertiseList(Integer uid,List<Map<String,Object>> conditionMap,String publishStartTime,String publishEndTime,String createStartTime,String createEndTime,String mobileNo,int pn,int ps){
		List<Advertise> advertises=null;
		ModelCriteria mc=new ModelCriteria();
		Criteria criteria= mc.createCriteria();
		
		if(conditionMap!=null&&conditionMap.size()>0){
			for(Map<String,Object> singleMap:conditionMap){
				criteria.andColumnEqualTo(singleMap.get("name").toString(), singleMap.get("value"));
			}
		}
		
		if(uid != null){
			criteria.andColumnEqualTo("uid",uid);
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
		
		if(StringUtils.isNotBlank(mobileNo)){
			Integer uidM = UniqueFacadeService.fetchUidByAcc(86,mobileNo);
			if(uidM == null || uidM.intValue() == 0){
				return new CommonPage<AdvertiseVTO>(pn, ps, 0,null);
			}
			criteria.andColumnEqualTo("uid", uidM);
		}
		int total=advertiseService.countByModelCriteria(mc);
		mc.setPageNumber(pn);
		mc.setPageSize(ps);
		advertises=advertiseService.findModelByModelCriteria(mc);
		
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
		return new CommonPage<AdvertiseVTO>(pn, ps, total,advertiseVTOs);
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
			if(advertise.getState()==AdvertiseType.UnVerified.getType()){
				if(state==0){
					advertise.setState(AdvertiseType.UnPublish.getType());
				}else{
					advertise.setState(AdvertiseType.VerifyFailure.getType());
					advertise.setReject_reason(msg);
				}
				advertiseService.update(advertise);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
			}else{
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_VERIFY_TYPESUPPORT);
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
	public RpcResponseDTO<AdvertiseVTO> queryAdvertiseInfo(Integer uid,int advertiseId){
		try{
			Advertise advertise=advertiseService.getById(advertiseId);
			AdvertiseVTO advertiseVTO=advertise.toVTO();
			//广告提交人信心
			User user=userService.getById(advertise.getUid());
			advertiseVTO.setOwnerName(user.getNick());
			advertiseVTO.setEscapeFlag(false);
			if(advertise.getState()==AdvertiseType.UnPublish.getType()){
				Date date=new Date();
				if(advertise.getStart().getTime()>date.getTime()){
					long between_daysNow = (advertise.getStart().getTime() - date.getTime()) / (1000 * 3600 * 24);
					if(between_daysNow>2){
						advertiseVTO.setEscapeFlag(true);
					}
				}
			}
			if(uid!=null){
				if(uid!=advertise.getUid()){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.LOGIN_USER_NOT_OPERATOR);
				}
			}
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
			if(entity.getState()!=AdvertiseType.VerifyFailure.getType()){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_UPFIELD_TYPEERROR);
			}
			if(entity.getUid()!=uid){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_UPFIELD_UNSUPPORT);
			}
			if(start>end){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIME_TIMEERROR);
			}
			Date date=new Date();
			
			long between_daysNow = (start - date.getTime()) / (1000 * 3600 * 24);
			if(between_daysNow<2){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_UPFIELD_TYPEERROR);
			}
			
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
			entity.setState(AdvertiseType.UnVerified.getType());
			
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
			int n=advertiseService.getEntityDao().countByAdvertiseTime(startDate, endDate,province, city, district);
			if(n!=0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIMEFIELD_OVERLAY);
			}
			advertiseService.update(entity);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> escapeAdvertise(int uid, int advertiseId) {
		Advertise advertise=advertiseService.getById(advertiseId);
		if(advertise.getUid()!=uid){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_UPFIELD_UNSUPPORT);
		}
		Date date=new Date();
		long between_daysNow = (advertise.getStart().getTime() - date.getTime()) / (1000 * 3600 * 24);
		if(advertise.getState()==AdvertiseType.UnPublish.getType()&&advertise.getStart().getTime()>date.getTime()&&between_daysNow>2){
			advertise.setState(AdvertiseType.EscapeOrder.getType());
			advertiseService.update(advertise);
		}else{
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_UPFIELD_TYPEERROR);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
}
