package com.bhu.vas.rpc.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.bhu.vas.api.vto.advertise.AdvertiseOccupiedVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseReportVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.business.ds.advertise.facade.AdvertiseFacadeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseDevicesIncomeService;
import com.bhu.vas.business.ds.advertise.service.AdvertiseService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
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
	@Resource
	private AdvertiseDevicesIncomeService advertiseDevicesIncomeService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	public AdvertiseService getAdvertiseService() {
		return advertiseService;
	}

	public void setAdvertiseService(AdvertiseService advertiseService) {
		this.advertiseService = advertiseService;
	}

	public RpcResponseDTO<AdvertiseVTO> createNewAdvertise(int uid,
			String image, String url,String domain, String province, String city,
			String district,String description,String title, long start, long end) {
			Advertise entity=new Advertise();
			
			if(StringUtils.isNotBlank(city)){
				entity.setCity(city);
			}
			
			if(StringUtils.isNotBlank(district)){
				entity.setDistrict(district);
			}
			
			if(StringUtils.isNotBlank(province)){
				entity.setProvince(province);
			}
			
			Date endDate=new Date(end);
			entity.setEnd(endDate);
			entity.setImage(image);

			Date startDate=new Date(start);
			entity.setStart(startDate);
			entity.setState(AdvertiseType.UnPaid.getType());
			
			
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			long count=0;
			try {
				AdDevicePositionVTO vto = fetchAdvertiseOccupy(0,format.format(startDate),format.format(endDate),DateTimeHelper.FormatPattern1,province, city, district,false);
				System.out.println("mark+++++++++++++++++++++++++"+format.format(startDate)+"  "+format.format(endDate)+" "+DateTimeHelper.FormatPattern1+" "+province+" "+city+" "+district );
				List<AdvertiseOccupiedVTO> advertiseOccupiedVTOs=vto.getOccupyAds();
				if(advertiseOccupiedVTOs!=null&&advertiseOccupiedVTOs.size()>0){
					for(AdvertiseOccupiedVTO i:advertiseOccupiedVTOs){
						count+=i.getCount();
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			System.out.println("count====================="+count);
			if(count==0){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIME_TIMEERROR);
			}
			if(start>end){
				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_TIME_TIMEERROR);
			}
			entity.setCount(count);
			int displayNum=(int) (count);
			entity.setCash(displayNum*BusinessRuntimeConfiguration.Advertise_Unit_Price+"");
			
			
			
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
			
			//int num=advertiseService.countByModelCriteria(mc);
//			if(num>=2){
//				return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_NUMFIELD_BEYOND);
//			}
			advertiseService.insert(entity);
			
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(entity.toVTO());
	}

	/**
	 * 获取现有设备地理位置
	 * @param province
	 * @param city
	 * @return
	 * @throws ParseException 
	 */
	public AdDevicePositionVTO fetchDevicePositionDistribution(String province,String city,String district) throws ParseException{
		
		String start = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 2);
		String end = DateTimeHelper.getAfterDate(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern5), 17);
		
		AdDevicePositionVTO vto = fetchAdvertiseOccupy(2,start,end,DateTimeHelper.FormatPattern5,province, city, district,true);
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
							String.format("Order Cash:%s, refund cash:%s", advertise.getCash(), advertise.getCash()));
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
			advertiseVTO.setCount(advertiseVTO.getCount());
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
	
	public AdDevicePositionVTO fetchAdvertiseOccupy(int index,String start,String end,String pattern,String province,String city,String district,boolean flag) throws ParseException {
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
		System.out.println("*********"+start+"|"+end);
		List<Advertise> advertises = advertiseService.getEntityDao().queryByAdvertiseTime(start, end, province, city, district,false);
		System.out.println("**********"+advertises.size());
		for(Advertise ad : advertises){
			System.out.println("***********"+ad.getProvince()+"|"+ad.getCity()+"|"+ad.getDistrict());
		}
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
			occupiedVto.setCash(occupiedVto.getCount() * BusinessRuntimeConfiguration.Advertise_Unit_Price +"");
			occupiedVtos.add(occupiedVto);
		}
		positionVto.setOccupyAds(occupiedVtos);
		return positionVto;
	}
	
	public RpcResponseDTO<AdvertiseReportVTO> fetchAdvertiseReport(int uid,String advertiseId){
		Advertise ad = advertiseService.getById(advertiseId);
		if(ad.getState() != BusinessEnumType.AdvertiseType.Published.getType()){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_REPOST_NOT_EXIST);
		}
		
		if(uid != ad.getUid()){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.ADVERTISE_QUERY_UNSUPPORT);
		}
		
		AdvertiseReportVTO report = new AdvertiseReportVTO();
		AdvertiseVTO adVto = ad.toVTO();
		
		Map<String, Integer> adResult = new HashMap<String, Integer>();
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("advertiseid", ad.getId());
		List<AdvertiseDetails> incomes  =  advertiseDevicesIncomeService.findModelByModelCriteria(mc);
		int sum = 0;
		int cashSum = 0;
		for(AdvertiseDetails income : incomes){
			adResult.put(income.getPublish_time(), (int) income.getActual_count());
			sum +=income.getActual_count();
			cashSum +=Integer.parseInt(income.getCash());
		}
		adResult.put("sum", sum);
		
		AdvertiseBillsVTO billVto =  new AdvertiseBillsVTO();
		billVto.setExpect(ad.getCash());
		billVto.setActual(Integer.parseInt(ad.getCash()) < cashSum ? Integer.parseInt(ad.getCash()) : cashSum);

		int balance = Integer.parseInt(ad.getCash()) - cashSum;
		billVto.setBalance(balance);
		report.setAdDetail(adVto);
		report.setAdResult(adResult);
		report.setAdBills(billVto);
		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(report);
	}
}
