
package com.bhu.vas.rpc.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.AdvertiseHelper;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseStateType;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.advertise.model.AdvertiseDetails;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.advertise.AdCommdityVTO;
import com.bhu.vas.api.vto.advertise.AdCommentVTO;
import com.bhu.vas.api.vto.advertise.AdCommentsVTO;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseBillsVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseDailyResultVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseOccupiedVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseReportVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseResultVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseUserDetailVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.api.vto.device.DeviceGEOPointCountVTO;
import com.bhu.vas.business.ds.advertise.facade.AdvertiseFacadeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocumentHelper;
import com.bhu.vas.business.search.service.advertise.AdvertiseDataSearchService;
import com.bhu.vas.business.search.service.device.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.advertise.AdvertiseIndexIncrementService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseErrorCode;

import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Tuple;

import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseCommentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertisePortalHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseSnapShotListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseTipsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.UserMobilePositionRelationSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.WifiDeviceAdvertiseSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePositionListService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;

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
			String district,double lat,double lon,String distance,String description,String title, long start, long end,String extparams) throws ParseException {
			
			Date endDate=new Date(end);
			Date startDate=new Date(start);
			
			long count=0;
			
			Advertise entity=new Advertise();
			
			AdvertiseType adType = BusinessEnumType.AdvertiseType.fromKey(type);
			double cash = 0d;
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
					
					long sum = wifiDeviceDataSearchService.searchCountByGeoPointDistance(context, lat, lon, distance);
					count = sum > 500 ? 500 : sum;
					cash = 1;
					entity.setCash(cash);
					entity.setLat(lat);
					entity.setLon(lon);
					entity.setDistance(distance);
					break;
					
				default:
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TYPE_ERROR);
			}
			
			if(count==0 && !userFacadeService.isAdminByUid(uid)){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIMEFIELD_OVERLAY);
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
			entity.setImage(image);
			entity.setDomain(domain);
			entity.setUrl(url);
			entity.setTag(tag);
			entity.setState(AdvertiseStateType.UnPaid.getType());
			entity.setCount(count);
			entity.setType(type);
			entity.setDescription(description);
			entity.setTitle(title);
			if(userFacadeService.isAdminByUid(uid)){
				entity.setUid(vuid);
			}else{
				entity.setUid(uid);
			}
			//间隔天数
			long between_days = (end - start) / (1000 * 3600 * 24);
			int duration=Integer.parseInt(String.valueOf(between_days));
			entity.setDuration(duration);
			entity.setExtparams(extparams);

			/*不限制发布次数
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
			}*/
			
			Advertise smAd= advertiseService.insert(entity);
			
			AdvertiseDocument adDoc = AdvertiseDocumentHelper.fromNormalAdvertise(smAd);
			advertiseDataSearchService.insertIndex(adDoc, false, false);
			advertiseDataSearchService.refresh(true);
			
			if(type == BusinessEnumType.AdvertiseType.SortMessage.getType()){
				UserMobilePositionRelationSortedSetService.getInstance().generateMobilenoSnapShot(smAd.getId(), province, city, district);
			}
			if(userFacadeService.isAdminByUid(uid)){
				//运营后台创建广告 直接发布
				asyncDeliverMessageService.sendBatchDeviceApplyAdvertiseActionMessage(Arrays.asList(adid+""),IDTO.ACT_UPDATE,true);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(entity.toVTO());
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
			AdvertiseDocument doc = advertiseDataSearchService.searchById(advertiseId);

			advertise.setVerify_uid(verify_uid);
			if(advertise.getState()==AdvertiseStateType.UnVerified.getType() || (advertise.getType() == BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType() && advertise.getState() == BusinessEnumType.AdvertiseStateType.OnPublish.getType())){
				if(state==0){
					advertise.setState(AdvertiseStateType.UnPublish.getType());
					advertiseIndexIncrementService.adStateUpdIncrement(advertiseId, AdvertiseStateType.UnPublish.getType(),null);
				}else{
					if(advertise.getType() == BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()){
						List<String> macList = AdvertiseSnapShotListService.getInstance().fetchAdvertiseSnapShot(advertiseId);
						WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdInvalid(macList, doc.getA_score());
						AdvertiseSnapShotListService.getInstance().destorySnapShot(advertiseId);
					}
					
					advertise.setState(AdvertiseStateType.VerifyFailure.getType());
					advertise.setReject_reason(msg);
					AdvertiseTipsHashService.getInstance().adComment(advertise.getUid(), advertiseId, AdvertiseStateType.VerifyFailure.getDesc());
					advertiseIndexIncrementService.adStateUpdIncrement(advertiseId, AdvertiseStateType.VerifyFailure.getType(),msg);
					AdvertisePortalHashService.getInstance().advertiseVerifyFailure(advertiseId, msg);
				}
				advertiseService.update(advertise);
				if(state!=0 && advertise.getType() != BusinessEnumType.AdvertiseType.HomeImage_SmallArea.getType()){//退费（个人热播暂不退费）
					userWalletFacadeService.advertiseRefundToUserWallet(advertise.getUid(), advertise.getOrderId(), Double.parseDouble(advertise.getCash()), 
							String.format("auditFail,OrderCash:%s,refundCash:%s", advertise.getCash(), advertise.getCash()));
				}
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
//			advertiseVTO.setCount(advertiseVTO.getCount());
			if(advertise.getState()==AdvertiseStateType.UnPublish.getType()){
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
			long count=wifiDeviceDataSearchService.searchCountByPosition(null,province, city, district);
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
		
		if(advertise == null || doc == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_EMPTY);
		}
		
		if(advertise.getState() == AdvertiseStateType.OnPublish.getType()){
			List<String> macList = AdvertiseSnapShotListService.getInstance().fetchAdvertiseSnapShot(advertiseId);
			WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdInvalid(macList, doc.getA_score());
			AdvertiseSnapShotListService.getInstance().destorySnapShot(advertiseId);
		}
		advertise.setState(AdvertiseStateType.EscapeOrder.getType());
		advertiseService.update(advertise);
		advertiseIndexIncrementService.adStateUpdIncrement(advertiseId, AdvertiseStateType.EscapeOrder.getType(),null);

//		if(advertise==null){
//			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_UNSUPPORT);
//		}
//		if(advertise.getUid()!=uid){
//			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_UNSUPPORT);
//		}
//		Date date=new Date();
//		double between_daysNow = (advertise.getStart().getTime() - date.getTime()) / (1000 * 3600 * 24*1.0);
//		if(advertise.getState()==AdvertiseStateType.UnPublish.getType()){
//			if(advertise.getStart().getTime()>date.getTime()&&between_daysNow>2){
//				advertise.setState(AdvertiseStateType.EscapeOrder.getType());
//				advertiseService.update(advertise);
//			}else{
//				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_TIMEERROR);
//			}
//		}else{
//			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_ESCFIELD_TYPEERROR);
//		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	/**
	 * 查看报表
	 * @param uid
	 * @param advertiseId
	 * @return
	 */
	public RpcResponseDTO<AdvertiseReportVTO> fetchAdvertiseReport(int uid,String advertiseId){
		Advertise ad = advertiseService.getById(advertiseId);
		
		if (ad.getType() == BusinessEnumType.AdvertiseType.SortMessage.getType()) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TYPE_ERROR);
		}
		
		if(ad.getState() != BusinessEnumType.AdvertiseStateType.Published.getType()){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_REPOST_NOT_EXIST);
		}
		
		if(uid != ad.getUid()){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_QUERY_UNSUPPORT);
		}
		
		
		AdvertiseReportVTO report = new AdvertiseReportVTO();
		AdvertiseVTO adVto = ad.toVTO();
		AdvertiseResultVTO resultVto = new AdvertiseResultVTO();
		List<AdvertiseDailyResultVTO> adResults = new ArrayList<AdvertiseDailyResultVTO>();
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("advertiseid", ad.getId());
		List<AdvertiseDetails> incomes  =  advertiseDevicesIncomeService.findModelByModelCriteria(mc);
		
		int adApplySum = 0;
		int adPVSum = 0;
		int adUvSum = 0;
		float cashSum = 0;
		for(AdvertiseDetails income : incomes){
			AdvertiseDailyResultVTO  dailyVto = new AdvertiseDailyResultVTO();
			dailyVto.setDate(income.getPublish_time());
			dailyVto.setAdApplyCount(income.getPublish_count());
			dailyVto.setAdPV(income.getPv());
			dailyVto.setAdUV(income.getUv());
			
			adApplySum +=income.getPublish_count();
			if(income.getCash() !=null || !income.getCash().isEmpty()){
				cashSum +=Double.parseDouble(income.getCash());
			}
			adPVSum+=dailyVto.getAdPV();
			adUvSum+=dailyVto.getAdUV();
			adResults.add(dailyVto);
		}
		resultVto.setAdApplySum(adApplySum);
		resultVto.setAdPVSum(adPVSum);
		resultVto.setAdUVSum(adUvSum);
		resultVto.setAdResult(adResults);
		
		AdvertiseBillsVTO billVto =  new AdvertiseBillsVTO();
		billVto.setExpect(Float.parseFloat(ad.getCash()));
		billVto.setActual(Float.parseFloat(ad.getCash()) < cashSum ? Float.parseFloat(ad.getCash()) : cashSum);
		
		float balance = Float.parseFloat(ad.getCash())*10000 - cashSum*10000;
		billVto.setBalance(balance > 0 ? balance/10000 : 0);
		
		report.setAdDetail(adVto);
		report.setAdResult(resultVto);
		report.setAdBills(billVto);
		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(report);
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
	
	private AdDevicePositionVTO fetchAdvertiseOccupy(int uid,int index,String start,String end,String pattern,String province,String city,String district,boolean flag) throws ParseException {
		AdDevicePositionVTO positionVto = new AdDevicePositionVTO();
//		String start = null;
//		String end = null;
//		
//		start = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 2);
//		end = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 17);
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
			occupiedVto.setCount(wifiDeviceDataSearchService.searchCountByPosition(trashVtos,province, city, district));
			
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
	
	public List<TailPage<AdvertiseVTO>> fetchBySearchConditionMessages(int pageNo,int pageSize,String ... messages){
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
							for(AdvertiseDocument doc : searchDocuments){
								vto = new AdvertiseVTO();
								vto.setId(doc.getId());
								vto.setType(doc.getA_type());
								vto.setTitle(doc.getA_title());
								vto.setState(doc.getA_state());
								vto.setDescription(doc.getA_desc());
								vto.setProvince(doc.getA_province());
								vto.setCity(doc.getA_city());
								vto.setDistrict(doc.getA_district());
								if(doc.getA_geopoint() !=null){
									vto.setLat(doc.getA_geopoint()[0]);
									vto.setLon(doc.getA_geopoint()[1]);
								}
								vto.setDistance(doc.getA_distance());
								vto.setUrl(doc.getA_url());
								vto.setCount(doc.getA_count());
								vto.setCash(doc.getA_cash());
								SimpleDateFormat sdf = new SimpleDateFormat(DateTimeHelper.FormatPattern1);  
								try {
									if(doc.getA_start() !=null)
										vto.setStart(sdf.parse(doc.getA_start()));
									if(doc.getA_end() != null)
										vto.setEnd(sdf.parse(doc.getA_end()));
								} catch (ParseException e) {
								}
								vto.setDomain(doc.getA_domain());
								vto.setImage(doc.getA_image());
								vto.setExtparams(doc.getA_extparams());
								vto.setReject_reason(doc.getA_reject_reason());
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
		
		if(advertise == null || advertise.getState() != BusinessEnumType.AdvertiseStateType.OnPublish.getType()){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_EMPTY);
		}
		
		long oldScore = doc.getA_score();
		long topScore = 100000000000000L;
		int topState = doc.getA_top();
		List<String> maclist = AdvertiseSnapShotListService.getInstance().fetchAdvertiseSnapShot(adid);

		if(isTop){//置顶
			if(topState == 1)
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_REPOST_NOT_EXIST);
			
			advertise.setTop(1);
			advertiseService.update(advertise);
			WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
					maclist, JsonHelper.getJSONString(advertise.toRedis()), oldScore + topScore);
			advertiseIndexIncrementService.adScoreUpdIncrement(adid, oldScore + topScore,1);
			
		}else if (isRefresh){//刷新
			if(topState == 1){
				WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
						maclist, JsonHelper.getJSONString(advertise.toRedis()),  topScore + Long.parseLong(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern16)));
				advertiseIndexIncrementService.adScoreUpdIncrement(adid, topScore + Long.parseLong(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern16)),1);
			}else{
				WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
						maclist, JsonHelper.getJSONString(advertise.toRedis()), Long.parseLong(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern16)));
				advertiseIndexIncrementService.adScoreUpdIncrement(adid,  Long.parseLong(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern16)), topState);
			}
			
		}else if (!isTop){//取消置顶
			if(topState  != 1)
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_REPOST_NOT_EXIST);
			
			advertise.setTop(0);
			advertiseService.update(advertise);
			WifiDeviceAdvertiseSortedSetService.getInstance().wifiDevicesAdApply(
					maclist, JsonHelper.getJSONString(advertise.toRedis()), oldScore - topScore);
			advertiseIndexIncrementService.adScoreUpdIncrement(adid, oldScore - topScore,0);
			
		}else{
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_REPOST_NOT_EXIST);
		}
		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
	}
	
	/**
	 * 广告评论、点赞
	 * @param uid
	 * @param adid
	 * @param message
	 * @param type
	 * @param score
	 * @return 
	 */
	public RpcResponseDTO<Boolean> AdvertiseComment(int uid,Integer vuid , String adid,String message,int type,Double score){
		Advertise advertise = advertiseService.getById(adid);
		if(advertise == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_EMPTY);
		}
		if(uid == 2){
			uid = vuid;
		}
		switch(type){
			case 0 :
				AdvertiseCommentSortedSetService.getInstance().AdComment(uid, adid, message);
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
	public RpcResponseDTO<List<AdCommentsVTO>> fetchCommentDetail(String[] adids){
		if(adids == null){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		List<AdCommentsVTO> results = new ArrayList<AdCommentsVTO>();
		for(String adid : adids){
			System.out.println(adid);
			Advertise advertise = advertiseService.getById(adid);
			if(advertise == null){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_EMPTY);
			}
			Set<Tuple> tuples = AdvertiseCommentSortedSetService.getInstance().fetchAdComments(adid);
			List<AdCommentVTO> vtos = new ArrayList<AdCommentVTO>();
			AdCommentsVTO result = new AdCommentsVTO();
			for(Tuple tuple : tuples){
				AdCommentVTO vto = JsonHelper.getDTO(tuple.getElement(), AdCommentVTO.class);
				vto.setTime(tuple.getScore());
				vtos.add(vto);
			}
			result.setAdid(adid);
			result.setComments(vtos);
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
		vto.setBalance(0);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
	}
	
	public RpcResponseDTO<List<AdvertiseVTO>> queryRandomAdvertiseDetails(){
		List<AdvertiseVTO> vtos = null;
		Page<AdvertiseDocument> search_result = advertiseDataSearchService.searchPageByCreated();
		if(search_result != null){
				List<AdvertiseDocument> searchDocuments = search_result.getContent();//.getResult();
				if(searchDocuments.isEmpty()) {
					vtos = Collections.emptyList();
				}else{
					vtos = new ArrayList<AdvertiseVTO>();
					AdvertiseVTO vto = null;
					List<String> adids = new ArrayList<String>();
					for(AdvertiseDocument doc : searchDocuments){
						vto = new AdvertiseVTO();
						vto.setId(doc.getId());
						vto.setType(doc.getA_type());
						vto.setTitle(doc.getA_title());
						vto.setState(doc.getA_state());
						vto.setDescription(doc.getA_desc());
						vto.setProvince(doc.getA_province());
						vto.setCity(doc.getA_city());
						vto.setDistrict(doc.getA_district());
						if(doc.getA_geopoint() !=null){
							vto.setLat(doc.getA_geopoint()[0]);
							vto.setLon(doc.getA_geopoint()[1]);
						}
						vto.setDistance(doc.getA_distance());
						vto.setUrl(doc.getA_url());
						vto.setCount(doc.getA_count());
						vto.setCash(doc.getA_cash());
						SimpleDateFormat sdf = new SimpleDateFormat(DateTimeHelper.FormatPattern1);  
						try {
							if(doc.getA_start() !=null)
								vto.setStart(sdf.parse(doc.getA_start()));
							if(doc.getA_end() != null)
								vto.setEnd(sdf.parse(doc.getA_end()));
						} catch (ParseException e) {
						}
						vto.setDomain(doc.getA_domain());
						vto.setImage(doc.getA_image());
						vto.setExtparams(doc.getA_extparams());
						vto.setReject_reason(doc.getA_reject_reason());
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
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
	}
}
