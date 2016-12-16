package com.bhu.vas.rpc.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import com.bhu.vas.api.helper.AdvertiseHelper;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.AdvertiseType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.rpc.advertise.model.AdvertiseDetails;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseBillsVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseDailyResultVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseOccupiedVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseReportVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseResultVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.business.ds.advertise.facade.AdvertiseFacadeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.sms.SmsSenderFactory;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseErrorCode;

import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.UserMobilePositionRelationSortedSetService;
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
	@Resource
	private AdvertiseDevicesIncomeService advertiseDevicesIncomeService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	@Resource
	private UserFacadeService userFacadeService;
	
	public AdvertiseService getAdvertiseService() {
		return advertiseService;
	}

	public void setAdvertiseService(AdvertiseService advertiseService) {
		this.advertiseService = advertiseService;
	}

	public RpcResponseDTO<AdvertiseVTO> createNewAdvertise(int uid,
			int type,String image, String url,String domain, String province, String city,
			String district,String description,String title, long start, long end) throws ParseException {
			
			Date endDate=new Date(end);
			Date startDate=new Date(start);
			
			long count=0;
			
			Advertise entity=new Advertise();

			switch(type){
				case Advertise.homeImage :
					
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
					float cash = count*BusinessRuntimeConfiguration.Advertise_Unit_Price;
					if(userFacadeService.checkOperatorByUid(uid)){
						entity.setCash(cash*BusinessRuntimeConfiguration.AdvertiseOperatorDiscount);
					}else{
						entity.setCash(cash*BusinessRuntimeConfiguration.AdvertiseCommonDiscount);
					}
					entity.setType(Advertise.homeImage);
					
					break;
				case Advertise.sortMessage :
					count = UserMobilePositionRelationSortedSetService.getInstance().zcardPostionMobileno(province, city, district);
					int num = description.length()/Advertise.sortMessageLength +1;
					entity.setCash(count*BusinessRuntimeConfiguration.Advertise_Sm_Price*num);
					entity.setType(Advertise.sortMessage);
					break;
				default:
					break;
			}
			
			if(count==0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIMEFIELD_OVERLAY);
			}
			
			if(StringUtils.isNotBlank(city))
				entity.setCity(city);
			if(StringUtils.isNotBlank(district))
				entity.setDistrict(district);
			if(StringUtils.isNotBlank(province))
				entity.setProvince(province);
			
			entity.setEnd(endDate);
			entity.setStart(startDate);
			entity.setImage(image);
			entity.setDomain(domain);
			entity.setUrl(url);
			entity.setState(AdvertiseType.UnPaid.getType());
			entity.setCount(count);
//			entity.setType(type);
			entity.setDescription(description);
			entity.setTitle(title);
			entity.setUid(uid);
			//间隔天数
			long between_days = (end - start) / (1000 * 3600 * 24);
			int duration=Integer.parseInt(String.valueOf(between_days));
			entity.setDuration(duration);

			
			
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
			if(type == Advertise.sortMessage){
				UserMobilePositionRelationSortedSetService.getInstance().generateMobilenoSnapShot(smAd.getId(), province, city, district);
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
		
		if(type == Advertise.sortMessage){
			vto.setMobilenos(UserMobilePositionRelationSortedSetService.getInstance().zcardPostionMobileno(province, city, district));
		}else{
			String start = null;
			String end = null;
			int startTime = 0;
			int endTime = 0;
			int index = 0;
			if(DateTimeHelper.getDateTime("HH").equals("23")){
				startTime = 2;
				endTime = 18;
				index = 2;
			}else{
				startTime = 1;
				endTime = 17;
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
			if(advertise.getState()==AdvertiseType.UnVerified.getType()){
				if(state==0){
					advertise.setState(AdvertiseType.UnPublish.getType());
				}else{
					advertise.setState(AdvertiseType.VerifyFailure.getType());
					advertise.setReject_reason(msg);
				}
				advertiseService.update(advertise);
				if(state!=0){//退费
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
	
	public AdDevicePositionVTO fetchAdvertiseOccupy(int uid,int index,String start,String end,String pattern,String province,String city,String district,boolean flag) throws ParseException {
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
	
	public RpcResponseDTO<AdvertiseReportVTO> fetchAdvertiseReport(int uid,String advertiseId){
		Advertise ad = advertiseService.getById(advertiseId);
		
		if (ad.getType() != Advertise.homeImage) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TYPE_ERROR);
		}
		
		if(ad.getState() != BusinessEnumType.AdvertiseType.Published.getType()){
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
}
