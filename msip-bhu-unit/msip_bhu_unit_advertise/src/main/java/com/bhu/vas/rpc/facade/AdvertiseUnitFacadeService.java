
package com.bhu.vas.rpc.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.helper.AdvertiseHelper;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseStateType;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.helper.BusinessEnumType.UConsumptiveWalletTransType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserConsumptiveWalletLog;
import com.bhu.vas.api.vto.advertise.AdCommentVTO;
import com.bhu.vas.api.vto.advertise.AdCommentsVTO;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseOccupiedVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseResponseVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseUserDetailVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.api.vto.device.DeviceGEOPointCountVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseCPMListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseCommentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertisePortalHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseTipsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.UserMobilePositionRelationSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePositionListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.advertise.facade.AdvertiseFacadeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserConsumptiveWalletFacadeService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserConsumptiveWalletLogService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.helper.AdvertiseDocumentHelper;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;
import com.bhu.vas.business.search.service.advertise.AdvertiseDataSearchService;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.advertise.AdvertiseIndexIncrementService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class AdvertiseUnitFacadeService {
	@Resource
	private AdvertiseService advertiseService;
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	@Resource
	private AdvertiseDataSearchService advertiseDataSearchService;
	
	@Resource
	private AdvertiseIndexIncrementService advertiseIndexIncrementService;
	
	@Resource
	private UserService userService;
	@Resource
	private AdvertiseFacadeService advertiseFacadeService;
	@Resource
	private AdvertiseDevicesIncomeService advertiseDevicesIncomeService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	@Resource
	private UserFacadeService userFacadeService;
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	
	@Resource
	private UserConsumptiveWalletFacadeService userConsumptiveWalletFacadeService;
	
	@Resource
	private UserConsumptiveWalletLogService userConsumptiveWalletLogService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	public AdvertiseService getAdvertiseService() {
		return advertiseService;
	}

	public void setAdvertiseService(AdvertiseService advertiseService) {
		this.advertiseService = advertiseService;
	}

	/**
	 * 创建广告
	 * @param uid
	 * @param type
	 * @param image
	 * @param url
	 * @param domain
	 * @param province
	 * @param city
	 * @param district
	 * @param description
	 * @param title
	 * @param start
	 * @param end
	 * @param extparams
	 * @return
	 * @throws ParseException
	 */
	public RpcResponseDTO<AdvertiseVTO> createNewAdvertise(int uid,Integer vuid,String adid,int tag,
			int type,String image, String url,String domain, String province, String city,
			String district,String adcode,double lat,double lon,String distance,String description,String title, long start, long end,boolean isTop,String extparams) throws ParseException {
			
			Date endDate=new Date(end);
			Date startDate=new Date(start);
			
			long count=0;
			
			Advertise entity=new Advertise();
			
			AdvertiseType adType = BusinessEnumType.AdvertiseType.fromKey(type);
			double cash = 0d;
			
			boolean isAdmin = userFacadeService.isAdminByUid(uid);
			
			switch(adType){
				case HomeImage :
					AdDevicePositionVTO vto = fetchAdvertiseOccupy(uid,0,DateTimeHelper.formatDate(startDate,DateTimeHelper.FormatPattern1),
							DateTimeHelper.formatDate(endDate,DateTimeHelper.FormatPattern1),
							DateTimeHelper.FormatPattern1,province, city, district,false);
												List<AdvertiseOccupiedVTO> advertiseOccupiedVTOs=vto.getOccupyAds();
					if(advertiseOccupiedVTOs!=null&&advertiseOccupiedVTOs.size()>0){
						for(AdvertiseOccupiedVTO i:advertiseOccupiedVTOs){
							count+=i.getCount();
						}
					}
					
					int n=advertiseService.getEntityDao().countByAdvertiseTime(startDate, endDate,province, city, district);
					if(n!=0){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIMEFIELD_OVERLAY);
					}
					cash = count*BusinessRuntimeConfiguration.Advertise_Unit_Price;
					if(userFacadeService.checkOperatorByUid(uid)){
						entity.setCash(cash*BusinessRuntimeConfiguration.AdvertiseOperatorDiscount);
					}else{
						entity.setCash(cash*BusinessRuntimeConfiguration.AdvertiseCommonDiscount);
					}
					break;
					
				case SortMessage :
					count = UserMobilePositionRelationSortedSetService.getInstance().zcardPostionMobileno(province, city, district);
					int num = description.length()/Advertise.sortMessageLength +1;
					cash = count*BusinessRuntimeConfiguration.Advertise_Sm_Price*num;
					entity.setCash(cash);
					break;
					
				case HomeImage_SmallArea :
					
					StringBuilder sb = null;
					if(province !=null)
				        sb = new StringBuilder(province);
					if(city !=null)
						sb.append(city);
					if(district !=null)
						sb.append(district);
					String context = null;
					if(sb != null){
						context = sb.toString();
					}
					long sum = 0;
					if(context !=null && distance!=null ){
						 sum = wifiDeviceDataSearchService.searchCountByGeoPointDistance(context, lat, lon, distance);
					}
					
					count = sum > 500 ? 500 : sum;
					cash = BusinessRuntimeConfiguration.AdvertiseHandbill;
					entity.setCash(cash);
					entity.setLat(lat);
					entity.setLon(lon);
					entity.setDistance(distance);
					
					break;
					
				default:
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TYPE_ERROR);
			}
			
			if(StringUtils.isNotBlank(city))
				entity.setCity(city);
			if(StringUtils.isNotBlank(district))
				entity.setDistrict(district);
			if(StringUtils.isNotBlank(province))
				entity.setProvince(province);
			if(type != BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()){
				entity.setEnd(endDate);
				entity.setStart(startDate);
			}
			entity.setId(adid);
			entity.setAdcode(adcode);
			entity.setImage(image);
			entity.setDomain(domain);
			entity.setUrl(url);
			entity.setTag(tag);
			entity.setState(AdvertiseStateType.UnPaid.getType());
			entity.setCount(count);
			entity.setType(type);
			entity.setDescription(description);
			entity.setTitle(title);
			entity.setTop(isTop ? 1 : -1);
			if(isAdmin){
				entity.setUid(vuid);
			}else{
				entity.setUid(uid);
			}
			//间隔天数
			long between_days = (end - start) / (1000 * 3600 * 24);
			int duration=Integer.parseInt(String.valueOf(between_days));
			entity.setDuration(duration);
			entity.setExtparams(extparams);

			Advertise smAd= advertiseService.insert(entity);
			
			AdvertiseDocument adDoc = AdvertiseDocumentHelper.fromNormalAdvertise(smAd,userFacadeService.isShamUser(uid));
			advertiseDataSearchService.insertIndex(adDoc, false, false);
			advertiseDataSearchService.refresh(true);
			
			if(type == BusinessEnumType.AdvertiseType.SortMessage.getType()){
				UserMobilePositionRelationSortedSetService.getInstance().generateMobilenoSnapShot(smAd.getId(), province, city, district);
			}
			
			if(userFacadeService.isAdminByUid(uid)){
				smAd.setState(BusinessEnumType.AdvertiseStateType.OnPublish.getType());
				advertiseService.update(entity);
				advertiseIndexIncrementService.adStateUpdIncrement(adid, BusinessEnumType.AdvertiseStateType.OnPublish.getType(), null);
				asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(Arrays.asList(adid+""),IDTO.ACT_UPDATE,true);
			}
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(entity.toVTO());
	}
	
	public RpcResponseDTO<Boolean> confirmPay(int uid ,String adid){
		Advertise advertise = advertiseService.getById(adid);
		if(advertise !=null && advertise.getUid() == uid){
			if(advertise.getState() == BusinessEnumType.AdvertiseStateType.UnPaid.getType()){
				String balance = userConsumptiveWalletFacadeService.getUserCash(uid);	
				if(advertise.getTop() == 1){
					if(Double.valueOf(balance) < BusinessRuntimeConfiguration.AdvertiseCPMPrices + BusinessRuntimeConfiguration.AdvertiseHandbill){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
					}
				}else{
					if(Double.valueOf(balance) < BusinessRuntimeConfiguration.AdvertiseHandbill){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
					}
				}
				
				final int executeRet = userConsumptiveWalletFacadeService.userPurchaseGoods(uid, adid, Double.valueOf(advertise.getCash()), UConsumptiveWalletTransType.AdsPublish, 
						null, null, String.format("createNewAdvertise uid[%s]", uid), null, null);
				if(executeRet != 0){
					if(executeRet == 1){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
					}else{
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
					}
				}else{
					asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(Arrays.asList(adid+""),IDTO.ACT_UPDATE,true);
				}
			}else{
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TYPE_ERROR);
			}
		}else{
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_QUERY_UNSUPPORT);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}

	/**
	 * 获取现有设备地理位置
	 * @param province
	 * @param city
	 * @return
	 * @throws ParseException 
	 */
	public AdDevicePositionVTO fetchDevicePositionDistribution(int uid,int type ,String province,String city,String district) throws ParseException{
		
		AdDevicePositionVTO vto = new AdDevicePositionVTO();
		
		if(type == BusinessEnumType.AdvertiseType.SortMessage.getType()){
			vto.setMobilenos(UserMobilePositionRelationSortedSetService.getInstance().zcardPostionMobileno(province, city, district));
		}else{
			String start = null;
			String end = null;
			int startTime = 0;
			int endTime = 0;
			int index = 0;
			if(DateTimeHelper.getDateTime("HH").equals("23")){
				startTime = 2;
				endTime = 17;
				index = 2;
			}else{
				startTime = 1;
				endTime = 16;
				index = 1;
			}
			start = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), startTime);
			end = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), endTime);
			vto = fetchAdvertiseOccupy(uid,index,start,end,DateTimeHelper.FormatPattern5,province, city, district,true);
		}
		
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
	public TailPage<AdvertiseVTO> queryAdvertiseList(Integer uid,List<Map<String,Object>> conditionMap,String publishStartTime,String publishEndTime,String createStartTime,String createEndTime,String mobileNo,int pn,int ps,boolean isConsole){
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
				criteria2.andColumnLessThanOrEqualTo("created_at", createEndTime+" 23:59:59").andColumnGreaterThanOrEqualTo("created_at", createStartTime+" 00:00:00");
				criteria3.andColumnLessThanOrEqualTo("created_at", createEndTime+" 23:59:59").andColumnGreaterThanOrEqualTo("created_at", createStartTime+" 00:00:00");
				criteria4.andColumnLessThanOrEqualTo("created_at", createEndTime+" 23:59:59").andColumnGreaterThanOrEqualTo("created_at", createStartTime+" 00:00:00");
				
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
				if(isConsole){
					singleAdvertise.setMobileno(user.getMobileno());
				}
				singleAdvertise.setCount(singleAdvertise.getCount());
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
			if(advertise.getState()==AdvertiseStateType.UnVerified.getType() || (advertise.getType() == BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType() && advertise.getState() == BusinessEnumType.AdvertiseStateType.OnPublish.getType())){
				if(state==0){
					advertise.setState(AdvertiseStateType.UnPublish.getType());
					advertiseIndexIncrementService.adStateUpdIncrement(advertiseId, AdvertiseStateType.UnPublish.getType(),null);
				}else{
					advertise.setState(AdvertiseStateType.VerifyFailure.getType());
					advertise.setReject_reason(msg);
					AdvertiseTipsHashService.getInstance().adComment(advertise.getUid(), advertiseId, AdvertiseStateType.VerifyFailure.getDesc());
					advertiseIndexIncrementService.adStateUpdIncrement(advertiseId, AdvertiseStateType.VerifyFailure.getType(),msg);
					AdvertisePortalHashService.getInstance().advertiseVerifyFailure(advertiseId, msg);
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
			if(userFacadeService.isAdminByUid(uid)){
				advertiseVTO.setUid(advertise.getUid()+"");
			}
			advertiseVTO.setCount(advertiseVTO.getCount());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(advertiseVTO);
	}

	/**
	 * 更新广告
	 * @param uid
	 * @param advertiseId
	 * @param image
	 * @param url
	 * @param domain
	 * @param province
	 * @param city
	 * @param district
	 * @param description
	 * @param title
	 * @param start
	 * @param end
	 * @return
	 */
	public RpcResponseDTO<Boolean> updateAdvertise(int uid,String advertiseId, String image,
			String url, String domain, String province, String city,
			String district, String description, String title, long start,
			long end) {
			Advertise entity=advertiseService.getById(advertiseId);
			if(entity.getState()!=AdvertiseStateType.VerifyFailure.getType()){
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
			long count=wifiDeviceDataSearchService.searchCountByPosition(null,province, city, district,null);
			entity.setCount(count);
			entity.setDistrict(district);
			Date endDate=new Date(end);
			entity.setEnd(endDate);
			entity.setImage(image);
			entity.setProvince(province);
			Date startDate=new Date(start);
			entity.setStart(startDate);
			entity.setState(AdvertiseStateType.UnVerified.getType());
			
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

	/**
	 * 取消广告
	 * @param uid
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<Boolean> escapeAdvertise(int uid, String advertiseId) {
		Advertise advertise=advertiseService.getById(advertiseId);
		AdvertiseDocument doc = advertiseDataSearchService.searchById(advertiseId);
		
		if(userFacadeService.isAdminByUid(uid)){
			if(!userFacadeService.isShamUser(advertise.getUid())){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_UNSUPPORT);
			}
		}else{
			if(uid != advertise.getUid()){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_UNSUPPORT);
			}
		}
		
		if(advertise == null || doc == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_EMPTY);
		}
		
		advertise.setState(AdvertiseStateType.EscapeOrder.getType());
		advertiseService.update(advertise);
		advertiseIndexIncrementService.adStateUpdIncrement(advertiseId, AdvertiseStateType.EscapeOrder.getType(),null);

		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	/**
	 * 查看cpm记录
	 * @param uid
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<List<UserConsumptiveWalletLog>> fetchAdvertiseReport(int uid,String advertiseId,Long start,Long end, int pageNo ,int pageSize){
		
		String startDate = null;
		String endDate = null;
		if(start !=null && end !=null){
			startDate = DateTimeHelper.getDateTime(new Date(start), DateTimeHelper.FormatPattern1);
			endDate = DateTimeHelper.getDateTime(new Date(end), DateTimeHelper.FormatPattern1);
		}
		ModelCriteria mc = new ModelCriteria();
		Criteria cr = mc.createCriteria();
		cr.andColumnEqualTo("orderid", advertiseId).andColumnEqualTo("transtype", UConsumptiveWalletTransType.AdsCPM.getKey());
		if(StringHelper.isNotEmpty(startDate) && StringHelper.isNotEmpty(endDate))
			cr.andColumnBetween("updated_at", startDate, endDate);
		mc.setPageNumber(pageNo);
    	mc.setPageSize(pageSize);
		mc.setOrderByClause("updated_at desc");
		List<UserConsumptiveWalletLog> results = userConsumptiveWalletLogService.findModelByModelCriteria(mc);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(results);
	}
	
	public RpcResponseDTO<List<Map<String, Object>>> fetchAdvertiseChartReport(int uid,String advertiseId,int type,Long start,Long end,int pageNo ,int pageSize){
		String pattern = null;
		String startDate = null;
		String endDate = null;
		if(start !=null && end !=null){
			startDate = DateTimeHelper.getDateTime(new Date(start), DateTimeHelper.FormatPattern1);
			endDate = DateTimeHelper.getDateTime(new Date(end), DateTimeHelper.FormatPattern1);
		}

		switch (type) {
		case 0:
			pattern = "%Y-%m-%d %h";
			break;
		case 1:
			pattern = "%Y-%m-%d";
			break;
		case 2:
			pattern = "%Y-%m";
			break;
		default:
			pattern = "%Y-%m-%d %h";
			break;
		}
		
		List<Map<String, Object>> results = userConsumptiveWalletLogService.selectListBySumCash(pattern, advertiseId, UConsumptiveWalletTransType.AdsCPM.getKey(),startDate,endDate,pageNo,pageSize);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(results);
	}
	
	
	
	/**
	 * 根据经纬度和距离查看周围设备数量
	 * @param province
	 * @param city
	 * @param district
	 * @param lat
	 * @param lon
	 * @param distance
	 * @return
	 */
	public RpcResponseDTO<List<DeviceGEOPointCountVTO>> countDeviceCountByGEOPoint(String province, String city, String district,double lat,double lon,String distances){
		String[] distanceTemp = distances.split(StringHelper.COMMA_STRING_GAP);
		StringBuilder sb = null;
		
		if(province !=null)
	        sb = new StringBuilder(province);
		if(city!=null)
			sb.append(city);
		if(district!=null)
			sb.append(district);
		
		String contextId = null;
		if(sb !=null){
			contextId = sb.toString();
		}
		List<DeviceGEOPointCountVTO> vtos = new ArrayList<DeviceGEOPointCountVTO>();
		for(String distance : distanceTemp){
			DeviceGEOPointCountVTO vto = new DeviceGEOPointCountVTO();
			vto.setProvince(province);
			vto.setCity(city);
			vto.setDistrict(district);
			vto.setLat(lat);
			vto.setLon(lon);
			vto.setDistance(distance);
			vto.setCount(wifiDeviceDataSearchService.searchCountByGeoPointDistance(contextId, lat, lon, distance));
			vto.setCash(vto.getCount()*BusinessRuntimeConfiguration.Advertise_Unit_Price+"");
			vto.setSaledCash("1");
			vtos.add(vto);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
	}
	
	public List<TailPage<AdvertiseVTO>> fetchBySearchConditionMessages(String mac,String umac,int pageNo,int pageSize,boolean customize,String ... messages){
		List<TailPage<AdvertiseVTO>> resultList = null;
		if(messages == null || messages.length == 0){
			resultList = Collections.emptyList();
		}else{
			resultList = new ArrayList<TailPage<AdvertiseVTO>>();
			int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
			for(String message : messages){
				List<AdvertiseVTO> vtos = null;
				Page<AdvertiseDocument> search_result = advertiseDataSearchService.searchByConditionMessage(
						message, searchPageNo, pageSize);
				
				int total = 0;
				if(search_result != null){
					total = (int)search_result.getTotalElements();//.getTotal();
					if(total == 0){
						vtos = Collections.emptyList();
					}else{
						List<AdvertiseDocument> searchDocuments = search_result.getContent();//.getResult();
						if(searchDocuments.isEmpty()) {
							vtos = Collections.emptyList();
						}else{
							vtos = new ArrayList<AdvertiseVTO>();
							AdvertiseVTO vto = null;
							List<String> adids = new ArrayList<String>();
							List<String> topAds = new ArrayList<String>();
							for(AdvertiseDocument doc : searchDocuments){
								vto = AdvertiseDocumentHelper.advertiseDocToVto(doc);
								if(doc.getU_id() !=null){
									User user = userService.getById(Integer.valueOf(doc.getU_id()));
									if(user != null && user.getNick() !=null){
										vto.setNick(user.getNick());
									}
								}
								if(doc.getA_top() == 1){
									topAds.add(doc.getId());
								}
								vto.setComment_sum(AdvertiseCommentSortedSetService.getInstance().AdCommentCount(doc.getId()));
								adids.add(doc.getId());
								vtos.add(vto);
							}
							List<String> portalPv =  AdvertisePortalHashService.getInstance().queryAdvertisePV(adids);
							List<String> portalAct =  AdvertisePortalHashService.getInstance().queryAdvertiseAct(adids);
							int index = 0;
							for(AdvertiseVTO vto1 : vtos){
								vto1.setAct(portalAct.get(index));
								vto1.setPv(portalPv.get(index));
								index++;
							}
						}
					}
				}else{
					vtos = Collections.emptyList();
				}
				TailPage<AdvertiseVTO> returnRet = new CommonPage<AdvertiseVTO>(pageNo, pageSize, total, vtos);
				resultList.add(returnRet);
			}
		}
		return resultList;
	}
	
	/**
	 * 广告操作
	 * @param isTop 是否置顶
	 * @param isRefresh 是否刷新
	 * @return
	 */
	public RpcResponseDTO<Boolean> advertiseOperation(int uid, String adid , boolean isTop,boolean isRefresh){
		
		AdvertiseDocument doc = advertiseDataSearchService.searchById(adid);
		Advertise advertise = advertiseService.getById(adid);
		if(!userFacadeService.isAdminByUid(uid)){
			if(advertise.getUid() !=uid){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_UPFIELD_UNSUPPORT);
			}
		}
		if(advertise == null || advertise.getState() != BusinessEnumType.AdvertiseStateType.OnPublish.getType()){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_EMPTY);
		}
		
		long oldScore = doc.getA_score();
		long topScore = 100000000000000L;
		int topState = doc.getA_top();
		String balance = userConsumptiveWalletFacadeService.getUserCash(uid);
		if(isTop){//置顶
			
			if(!userFacadeService.isAdminByUid(uid)){
				if(Double.valueOf(balance) < BusinessRuntimeConfiguration.AdvertiseCPMPrices){
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
				}	
			}
			
			if(topState == 1)
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TYPE_ERROR);
			
			advertise.setTop(1);
			advertiseService.update(advertise);
			advertiseIndexIncrementService.adScoreUpdIncrement(adid, oldScore + topScore,1);
			
		}else if (isRefresh){//刷新
			
			if(!userFacadeService.isAdminByUid(uid)){
				final int executeRet = userConsumptiveWalletFacadeService.userPurchaseGoods(uid, adid, BusinessRuntimeConfiguration.AdvertiseHandbill, UConsumptiveWalletTransType.AdsRefresh, 
						null, null, String.format("createNewAdvertise uid[%s]", uid), null, null);
				if(executeRet != 0){
					if(Double.valueOf(balance) < BusinessRuntimeConfiguration.AdvertiseHandbill){
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ORDER_PAYMENT_VCURRENCY_NOTSUFFICIENT);
					}else{
						return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
					}
				}
			}

			if(topState == 1){
				advertiseIndexIncrementService.adScoreUpdIncrement(adid, topScore + Long.parseLong(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern16)),1);
			}else{
				advertiseIndexIncrementService.adScoreUpdIncrement(adid,  Long.parseLong(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern16)), topState);
			}
			
		}else if (!isTop){//取消置顶
			if(topState  != 1)
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TYPE_ERROR);
			
			advertise.setTop(0);
			advertiseService.update(advertise);
			advertiseIndexIncrementService.adScoreUpdIncrement(adid, oldScore - topScore,0);
			
		}else{
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TYPE_ERROR);
		}
		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	/**
	 * 广告评论、回复
	 * @param uid
	 * @param adid
	 * @param message
	 * @param type
	 * @param score
	 * @return 
	 */
	public RpcResponseDTO<Boolean> AdvertiseComment(int uid,Integer vuid, String adid,String message,int type,Double score){
		Advertise advertise = advertiseService.getById(adid);
		if(advertise == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_EMPTY);
		}
		if(uid == 2){
			uid = vuid;
		}
		User user = userService.getById(uid);
		switch(type){
			case 0 :
				AdvertiseCommentSortedSetService.getInstance().AdComment(uid,user.getNick(), adid, message);
				AdvertiseTipsHashService.getInstance().adComment(advertise.getUid(), adid, uid+"");
				break;
			case 1 :
				AdvertiseCommentSortedSetService.getInstance().AdReply(adid, score, message);
				break;
			default:
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	/**
	 * 获取评论
	 * @param uid
	 * @param adid
	 * @return
	 */
	public RpcResponseDTO<List<AdCommentsVTO>> fetchCommentDetail(String[] adids,int pn,int ps){
		if(adids == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		List<AdCommentsVTO> results = new ArrayList<AdCommentsVTO>();
		for(String adid : adids){
			Set<Tuple> tuples = AdvertiseCommentSortedSetService.getInstance().fetchAdComments(adid);
			List<AdCommentVTO> vtos = new ArrayList<AdCommentVTO>();
			AdCommentsVTO result = new AdCommentsVTO();
			for(Tuple tuple : tuples){
				AdCommentVTO vto = JsonHelper.getDTO(tuple.getElement(), AdCommentVTO.class);
				vto.setTime(tuple.getScore());
				vtos.add(vto);
			}
			if(vtos.size() > ps*pn){
				result.setSign(true);
			}
			result.setAdid(adid);
			result.setComments(PageHelper.pageList(vtos, pn, ps));
			result.setComment_sum(vtos.size());
			results.add(result);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(results);
	}
	
	/**
	 * 用户广告相关
	 * @param uid
	 * @return 
	 */
	public RpcResponseDTO<AdvertiseUserDetailVTO> userAdvertiseDetail(int uid){
		AdvertiseUserDetailVTO vto = new AdvertiseUserDetailVTO();
		vto.setTips(AdvertiseTipsHashService.getInstance().fetchAdTips(uid));
		//用户钱包余额
		vto.setBalance(userConsumptiveWalletFacadeService.getUserCash(uid));
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
	}
	/**
	 * 返回三个推荐小传单
	 * @param mac
	 * @param umac
	 * @return
	 */
	public RpcResponseDTO<TailPage<AdvertiseVTO>> queryRandomAdvertiseDetails(String mac,String umac,Double lat,Double lon,String adcode,String sourcetype ,String systype,int type){
		double real_lat = 0;
		double real_lon = 0;
		String real_adcode = null;
		switch (type) {
		case 0:
			WifiDevice device = wifiDeviceService.getById(mac);
			real_lat = Double.valueOf(device.getLat());
			real_lon = Double.valueOf(device.getLon());
			real_adcode = device.getAdcode();
			break;
		case 1:
			real_lat = lat;
			real_lon = lon;
			real_adcode = adcode;
			break;
		default:
			break;
		}
		TailPage<AdvertiseVTO> vtos = fetchAdvertise(null, real_lat, real_lon, real_adcode, 3, 1);
		List<String> ads = new ArrayList<String>();
		for(AdvertiseVTO vto : vtos){
			if(vto.getTop() == 1)
				ads.add(vto.getId());
		}
		AdvertiseCPMListService.getInstance().AdCPMPosh(ads,mac,umac,umac,sourcetype,systype);

		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
	}
	/**
	 * 清除小红点
	 * @param uid
	 * @param adid
	 * @return
	 */
	public RpcResponseDTO<Boolean> destoryTips(int uid,String adid){
		AdvertiseTipsHashService.getInstance().destoryTips(uid, adid);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	/**
	 * app Cpm通知
	 * @param adids
	 * @param userid
	 * @param sourcetype
	 * @param systype
	 * @return
	 */
	public RpcResponseDTO<Boolean> advertiseCPMNotify(String[] adids,String userid,String sourcetype ,String systype){
		List<Advertise> advertises = advertiseService.findByIds(ArrayHelper.toList(adids));
		List<String> ads = new ArrayList<String>();
		for(Advertise ad : advertises){
			if(ad.getTop() == 1)
				ads.add(ad.getId());
		}
		AdvertiseCPMListService.getInstance().AdCPMPosh(ads,null,null,userid,sourcetype,systype);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	/**
	 * portal获取小传单列表
	 * @param mac
	 * @param umac
	 * @param sourcetype
	 * @param systype
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public RpcResponseDTO<AdvertiseResponseVTO> fetchAdListByPortal(String mac ,String umac ,String sourcetype ,String systype, int pageSize , int pageNo){
		WifiDevice device = wifiDeviceService.getById(mac);
		TailPage<AdvertiseVTO> vtos = fetchAdvertise(null, Double.valueOf(device.getLat()), Double.valueOf(device.getLon()), device.getAdcode(), pageSize, pageNo);
		List<String> ads = new ArrayList<String>();
		for(AdvertiseVTO vto : vtos){
			if(vto.getTop() == 1)
				ads.add(vto.getId());
		}
		AdvertiseCPMListService.getInstance().AdCPMPosh(ads,mac,umac,umac,sourcetype,systype);

		AdvertiseResponseVTO results = new AdvertiseResponseVTO();
		results.setAdcode(device.getAdcode());
		results.setCity(device.getCity());
		results.setProvince(device.getProvince());
		results.setDistrict(device.getDistrict());
		results.setVtos(vtos);
		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(results);
	}
	
	/**
	 * app获取小传单列表
	 * @param lat
	 * @param lon
	 * @param adcode
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public RpcResponseDTO<TailPage<AdvertiseVTO>> fetchAdListByAPP(double lat ,double lon,String adcode, int pageSize , int pageNo){
		TailPage<AdvertiseVTO> vtos = fetchAdvertise(null, lat, lon, adcode, pageSize, pageNo);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
	}
	
	private TailPage<AdvertiseVTO> fetchAdvertise(String contextId, double lat, double lon,String adcode,int pageSize,int pageNo){
		Page<AdvertiseDocument> search_result= advertiseDataSearchService.searchByGeoPointDistanceAndAdcode(null, lat, lon, "5km", adcode, pageSize, pageNo);
		List<AdvertiseVTO> vtos = null;
		int total = 0;
		if(search_result != null){
			List<AdvertiseDocument> searchDocuments = search_result.getContent();//.getResult();
			total = (int)search_result.getTotalElements();//.getTotal();
			if(total == 0){
				vtos = Collections.emptyList();
			}else{
				vtos = new ArrayList<AdvertiseVTO>();
				AdvertiseVTO vto = null;
				List<String> adids = new ArrayList<String>();
				List<String> topAds = new ArrayList<String>();
				for(AdvertiseDocument doc : searchDocuments){
					vto = AdvertiseDocumentHelper.advertiseDocToVto(doc);
					if(doc.getA_top() == 1){
						topAds.add(doc.getId());
					}
					vto.setComment_sum(AdvertiseCommentSortedSetService.getInstance().AdCommentCount(doc.getId()));
					adids.add(doc.getId());
					vtos.add(vto);
				}
				List<String> portalPv =  AdvertisePortalHashService.getInstance().queryAdvertisePV(adids);
				List<String> portalAct =  AdvertisePortalHashService.getInstance().queryAdvertiseAct(adids);
				int index = 0;
				for(AdvertiseVTO vto1 : vtos){
					 vto1.setAct(portalAct.get(index));
					 vto1.setPv(portalPv.get(index));
					 index++;
				}
			}
		}else{
			vtos = Collections.emptyList();
		}
		TailPage<AdvertiseVTO> returnRet = new CommonPage<AdvertiseVTO>(pageNo, pageSize, total, vtos);
		return returnRet;
		
	}
	
	private AdDevicePositionVTO fetchAdvertiseOccupy(int uid,int index,String start,String end,String pattern,String province,String city,String district,boolean flag) throws ParseException {
		AdDevicePositionVTO positionVto = new AdDevicePositionVTO();
		SimpleDateFormat  format= new SimpleDateFormat(pattern);  
	    Date startDate = format.parse(start);
	    Date endDate = format.parse(end);
	    long startLong = startDate.getTime();  
	    long endLong = endDate.getTime(); 
	    int days = (int)(endLong-startLong)/(1000 * 60 * 60 * 24);
	    
		List<AdvertiseOccupiedVTO> occupiedVtos = new ArrayList<AdvertiseOccupiedVTO>();
		List<Advertise> advertises = advertiseService.getEntityDao().queryByAdvertiseTime(start, end, province, city, district,false);

		for(int i=index; i<=days; i++){
			String time = null;
			time = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), i);
			SimpleDateFormat  format2= new SimpleDateFormat(DateTimeHelper.FormatPattern5);
			Date times = format2.parse(time);
			AdvertiseOccupiedVTO occupiedVto = new AdvertiseOccupiedVTO();
			List<AdvertiseTrashPositionVTO> trashVtos = AdvertiseHelper.buildAdvertiseTrashs(advertises, times,flag);
			occupiedVto.setTrashs(trashVtos);
			occupiedVto.setDate(time);
			occupiedVto.setCount(wifiDeviceDataSearchService.searchCountByPosition(trashVtos,province, city, district,null));
			
			int cash = occupiedVto.getCount()*BusinessRuntimeConfiguration.Advertise_Unit_Price;
			if(userFacadeService.checkOperatorByUid(uid)){
				occupiedVto.setCash(cash*BusinessRuntimeConfiguration.AdvertiseOperatorDiscount);
				positionVto.setSale(BusinessRuntimeConfiguration.AdvertiseOperatorDiscount);
			}else{
				occupiedVto.setCash(cash*BusinessRuntimeConfiguration.AdvertiseCommonDiscount);
				positionVto.setSale(BusinessRuntimeConfiguration.AdvertiseCommonDiscount);
			}
			occupiedVtos.add(occupiedVto);
		}
		positionVto.setOccupyAds(occupiedVtos);
		return positionVto;
	}
}
