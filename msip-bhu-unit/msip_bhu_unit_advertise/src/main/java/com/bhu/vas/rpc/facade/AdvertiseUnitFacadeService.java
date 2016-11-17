package com.bhu.vas.rpc.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseOccupiedVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.business.ds.advertise.facade.AdvertiseFacadeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
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
	@Resource
	private AdvertiseFacadeService advertiseFacadeService;
	
	
	public AdvertiseService getAdvertiseService() {
		return advertiseService;
	}

	public void setAdvertiseService(AdvertiseService advertiseService) {
		this.advertiseService = advertiseService;
	}

	public RpcResponseDTO<Boolean> createNewAdvertise(int uid,
			String image, String url,String domain, String province, String city,
			String district,String description,String title, long start, long end) {
			Advertise entity=new Advertise();
			
			entity.setCity(city);
			long count=wifiDeviceDataSearchService.searchCountByPosition(null,province, city, district);
			if(start>end){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIME_TIMEERROR);
			}
			entity.setCount(count);
			int displayNum=(int) (count*1.1);
			entity.setCash(displayNum*2);
			
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
//			if(num>=2){
//				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_NUMFIELD_BEYOND);
//			}
			advertiseService.insert(entity);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}

	/**
	 * 获取现有设备地理位置
	 * @param province
	 * @param city
	 * @return
	 * @throws ParseException 
	 */
	public AdDevicePositionVTO fetchDevicePositionDistribution(String province,String city,String district) throws ParseException{
		AdDevicePositionVTO vto = fetchAdvertiseOccupy(province, city, district);
		if(StringUtils.isNoneBlank(city)){
			vto.setPositions(WifiDevicePositionListService.getInstance().fetchCity(city));
		}else if(StringUtils.isNoneBlank(province)){
			vto.setPositions(WifiDevicePositionListService.getInstance().fetchProvince(province));
		}else{
			vto.setPositions(WifiDevicePositionListService.getInstance().fetchAllProvince());
		}
		
		if(vto != null && vto.getPositions() !=null){
				Iterator<String> iter = vto.getPositions().iterator();
				while (iter.hasNext()) {
					String rv = iter.next();
					if (AdDevicePositionVTO.isFilter(rv))
						iter.remove();
				}
		}
		
		return vto;
	}
	/**
	 * 根据条件查询广告列表
	 * @param conditionMap
	 * @return
	 */
	public TailPage<AdvertiseVTO> queryAdvertiseList(Integer uid,List<Map<String,Object>> conditionMap,String publishStartTime,String publishEndTime,String createStartTime,String createEndTime,String mobileNo,int pn,int ps){
		List<Advertise> advertises=null;
		ModelCriteria mc=new ModelCriteria();
		Criteria criteria2=mc.createCriteria();
		Criteria criteria3=mc.createCriteria();
		Criteria criteria4=mc.createCriteria();
		if(conditionMap!=null&&conditionMap.size()>0){
			for(Map<String,Object> singleMap:conditionMap){
				criteria2.andColumnEqualTo(singleMap.get("name").toString(), singleMap.get("value"));
				criteria3.andColumnEqualTo(singleMap.get("name").toString(), singleMap.get("value"));
				criteria4.andColumnEqualTo(singleMap.get("name").toString(), singleMap.get("value"));
			}
		}
		
		if(uid != null){
			criteria2.andColumnEqualTo("uid",uid);
			criteria3.andColumnEqualTo("uid",uid);
			criteria4.andColumnEqualTo("uid",uid);
		}
		
		if(StringUtils.isNotBlank(createStartTime)){
			if(StringUtils.isNotBlank(createEndTime)){
				criteria2.andColumnBetween("created_at", createStartTime, createEndTime);
				criteria3.andColumnBetween("created_at", createStartTime, createEndTime);
				criteria4.andColumnBetween("created_at", createStartTime, createEndTime);
			}else{
				criteria2.andColumnEqualTo("created_at", createStartTime);
				criteria3.andColumnEqualTo("created_at", createStartTime);
				criteria4.andColumnEqualTo("created_at", createStartTime);
			}
		}
		
		if(StringUtils.isNotBlank(mobileNo)){
			Integer uidM = UniqueFacadeService.fetchUidByAcc(86,mobileNo);
			if(uidM == null || uidM.intValue() == 0){
				return new CommonPage<AdvertiseVTO>(pn, ps, 0,null);
			}
			criteria2.andColumnEqualTo("uid", uidM);
			criteria3.andColumnEqualTo("uid", uidM);
			criteria4.andColumnEqualTo("uid", uidM);
		}
		
		if(StringUtils.isNotBlank(publishStartTime)){
			if(StringUtils.isNotBlank(publishEndTime)){
				criteria2.andColumnBetween("start", publishStartTime, publishEndTime);
				criteria3.andColumnBetween("end", publishStartTime, publishEndTime);
				criteria4.andColumnLessThanOrEqualTo("start", publishStartTime).andColumnGreaterThanOrEqualTo("end", publishEndTime);
				mc.or(criteria2);
				mc.or(criteria3);
				mc.or(criteria4);
			}else{
				criteria2.andColumnGreaterThanOrEqualTo("start", publishStartTime);
				mc.or(criteria2);
			}
		}
		int total=advertiseService.countByModelCriteria(mc);
		mc.setPageNumber(pn);
		mc.setPageSize(ps);
		mc.setOrderByClause("created_at desc");
		advertises=advertiseService.findModelByModelCriteria(mc);
		
		List<AdvertiseVTO> advertiseVTOs=new ArrayList<AdvertiseVTO>();
		if(advertises!=null){
			for(Advertise ad:advertises){
				AdvertiseVTO singleAdvertise = ad.toVTO();
				//广告提交人信心
				User user=userService.getById(ad.getUid());
				singleAdvertise.setOwnerName(user.getNick());
				singleAdvertise.setCount((int)(singleAdvertise.getCount()*1.1));
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
	public RpcResponseDTO<Boolean> verifyAdvertise(int verify_uid,String advertiseId,String msg,int state){
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
	}
	/**
	 * 查询广告详情
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<AdvertiseVTO> queryAdvertiseInfo(Integer uid,String advertiseId){
			Advertise advertise=advertiseService.getById(advertiseId);
			if(advertise==null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_EMPTY);
			}
			AdvertiseVTO advertiseVTO=advertise.toVTO();
			//广告提交人信心
			User user=userService.getById(advertise.getUid());
			advertiseVTO.setOwnerName(user.getNick());
			advertiseVTO.setEscapeFlag(false);
			advertiseVTO.setCount((int)(advertiseVTO.getCount()*1.1));
			if(advertise.getState()==AdvertiseType.UnPublish.getType()){
				Date date=new Date();
				if(advertise.getStart().getTime()>date.getTime()){
					double between_daysNow = (advertise.getStart().getTime() - date.getTime()) / (1000 * 3600 * 24*1.0);
					if(between_daysNow>2){
						advertiseVTO.setEscapeFlag(true);
					}
				}
			}
			if(uid!=null){
				if(uid!=advertise.getUid()){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_QUERY_UNSUPPORT);
				}
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(advertiseVTO);
	}

	public RpcResponseDTO<Boolean> updateAdvertise(int uid,String advertiseId, String image,
			String url, String domain, String province, String city,
			String district, String description, String title, long start,
			long end) {
			Advertise entity=advertiseService.getById(advertiseId);
			if(entity.getState()!=AdvertiseType.VerifyFailure.getType()){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_UPFIELD_TYPEERROR);
			}
			if(entity.getUid()!=uid){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_TYPEERROR);
			}
			if(start>end){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIME_TIMEERROR);
			}
			Date date=new Date();
			
			double between_daysNow = (start - date.getTime()) / (1000 * 3600 * 24*1.0);
			if(between_daysNow<2){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_UPFIELD_TYPEERROR);
			}
			
			entity.setCity(city);
			long count=wifiDeviceDataSearchService.searchCountByPosition(null,province, city, district);
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
	}

	public RpcResponseDTO<Boolean> escapeAdvertise(int uid, String advertiseId) {
		Advertise advertise=advertiseService.getById(advertiseId);
		if(advertise==null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_UNSUPPORT);
		}
		if(advertise.getUid()!=uid){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_UNSUPPORT);
		}
		Date date=new Date();
		double between_daysNow = (advertise.getStart().getTime() - date.getTime()) / (1000 * 3600 * 24*1.0);
		if(advertise.getState()==AdvertiseType.UnPublish.getType()){
			if(advertise.getStart().getTime()>date.getTime()&&between_daysNow>2){
				advertise.setState(AdvertiseType.EscapeOrder.getType());
				advertiseService.update(advertise);
			}else{
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_TIMEERROR);
			}
		}else{
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_TYPEERROR);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	private AdDevicePositionVTO fetchAdvertiseOccupy(String province,String city,String district) throws ParseException {
		AdDevicePositionVTO positionVto = new AdDevicePositionVTO();
		String start = null;
		String end = null;
		
		start = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 2);
		end = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 17);

		
		List<AdvertiseOccupiedVTO> occupiedVtos = new ArrayList<AdvertiseOccupiedVTO>();
		List<Advertise> advertises = advertiseService.getEntityDao().queryByAdvertiseTime(start, end, province, city, district);
		
		for(int i=2; i<=17; i++){
			
			AdvertiseOccupiedVTO occupiedVto = new AdvertiseOccupiedVTO();
			List<AdvertiseTrashPositionVTO> trashVtos = new ArrayList<AdvertiseTrashPositionVTO>();
			
			SimpleDateFormat sdf = new SimpleDateFormat(DateTimeHelper.FormatPattern5);  
			String time = null;
			Date nowDate = null;
			
			time = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), i);
			nowDate  = sdf.parse(time);
			
			for(Advertise ad : advertises){
				if(ad.getStart().getTime() <= nowDate.getTime()  &&  ad.getEnd().getTime() > nowDate.getTime()){
					AdvertiseTrashPositionVTO trashVto = new AdvertiseTrashPositionVTO();
					trashVto.setDistrict(ad.getDistrict());
					trashVto.setCity(ad.getCity());
					trashVto.setProvince(ad.getProvince());
					trashVtos.add(trashVto);
				}
			}
			occupiedVto.setTrashs(trashVtos);
			occupiedVto.setDate(time);
			occupiedVto.setCount(wifiDeviceDataSearchService.searchCountByPosition(trashVtos,province, city, district));
			occupiedVtos.add(occupiedVto);
		}
		positionVto.setOccupyAds(occupiedVtos);
		return positionVto;
	}
}
