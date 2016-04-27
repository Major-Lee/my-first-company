package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.dto.redis.DailyStatisticsDTO;
import com.bhu.vas.api.dto.redis.SystemStatisticsDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDetailDTO;
import com.bhu.vas.api.rpc.user.dto.UserSearchConditionDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserSearchConditionState;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO1;
import com.bhu.vas.api.vto.agent.UserAgentVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDevicePresentCtxService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DailyStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.SystemStatisticsHashService;
import com.bhu.vas.business.ds.builder.BusinessModelBuilder;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserSearchConditionStateService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.rpc.bucache.BusinessDeviceCacheService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.PageHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device Rest RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class DeviceRestBusinessFacadeService {
	//private final Logger logger = LoggerFactory.getLogger(DeviceRestBusinessFacadeService.class);

	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	//@Resource
	//private WifiHandsetDeviceLoginCountMService wifiHandsetDeviceLoginCountMService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	/*@Resource
	private HandsetDeviceService handsetDeviceService;*/
	
	/*@Resource
	private WifiDeviceSearchService wifiDeviceSearchService;*/
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
//	@Resource
//	private WifiDeviceDataSearchService1 wifiDeviceDataSearchService1;
	
	@Resource
	private BusinessDeviceCacheService businessDeviceCacheService;
	
	@Resource
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	
	@Resource
	private UserSearchConditionStateService userSearchConditionStateService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private DeliverMessageService deliverMessageService;
	
	/**
	 * 获取接入移动设备数量最多的wifi设备列表
	 * TODO：目前直接从mongodb中获取 后续改成后台程序定时从mongodb获取并放入指定的redis中 这边直接从redis提取数据
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize) {
		return Collections.emptyList();
		/*Pagination<WifiHandsetDeviceLoginCountMDTO> wifiDevices = wifiHandsetDeviceLoginCountMService.
				findWifiDevicesOrderMaxHandset(pageNo, pageSize);
		List<WifiHandsetDeviceLoginCountMDTO> mdtos = wifiDevices.getDatas();
		if(mdtos == null || mdtos.isEmpty()){
			return Collections.emptyList();
		}
		
		List<String> wifiIds = new ArrayList<String>();
		for(WifiHandsetDeviceLoginCountMDTO mdto : mdtos){
			wifiIds.add(mdto.getId());
		}
		
		List<WifiDevice> entitys = wifiDeviceService.findByIds(wifiIds, true, true);
		int cursor = 0;
		WifiDeviceMaxBusyVTO vto = null;
		WifiHandsetDeviceLoginCountMDTO mdto = null;
		List<WifiDeviceMaxBusyVTO> vtos = new ArrayList<WifiDeviceMaxBusyVTO>();
		for(WifiDevice entity : entitys){
			mdto = mdtos.get(cursor);
			vto = BusinessModelBuilder.toWifiDeviceMaxBusyVTO(mdto, entity);
			vtos.add(vto);
			cursor++;
		}
		return vtos;*/
	}
	
	/**
	   根据keyword来搜索wifi设备数据
	 * keyword 可以是 mac 或 地理名称
	 * 以当前在线和当前在线移动设备数量排序
	 * @param keyword 可以是 mac 或 地理名称
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ESQueryValidateException 
	 */
//	public TailPage<WifiDeviceVTO> fetchWDeviceByKeyword(String keyword, String region,
//			String region_excepts, int pageNo, int pageSize){
//		List<WifiDeviceVTO> vtos = null;
//		int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
//		Page<WifiDeviceDocument> searchResult = wifiDeviceDataSearchService.searchByKeyword(keyword, region, region_excepts, searchPageNo, pageNo);
//		
//		/*QueryResponse<List<WifiDeviceSearchDTO>> search_result = wifiDeviceSearchService.searchByKeyword(keyword, 
//				region, excepts, (pageNo*pageSize)-pageSize, pageSize);*/
//		
//		int total = (int)searchResult.getTotalElements();//.getTotal();
//		if(total == 0){
//			vtos = Collections.emptyList();
//		}
//		List<WifiDeviceDocument> searchDtos = searchResult.getContent();//search_result.getResult();
//		if(searchDtos.isEmpty()) {
//			vtos = Collections.emptyList();
//		}else{
//			List<String> wifiIds = new ArrayList<String>();
//			for(WifiDeviceDocument searchDto : searchDtos){
//				wifiIds.add(searchDto.getId());
//			}
//			List<WifiDevice> entitys = wifiDeviceService.findByIds(wifiIds, true, true);
//			vtos = new ArrayList<WifiDeviceVTO>();
//			WifiDeviceVTO vto = null;
//			WifiDeviceDocument searchDto = null;
//			int cursor = 0;
//			for(WifiDevice wifiDevice : entitys){
//				searchDto = searchDtos.get(cursor);
//				vto = WifiDeviceDocumentHelper.toWifiDeviceVTO(searchDto, wifiDevice);
//				//vto = BusinessModelBuilder.toWifiDeviceVTO(searchDto, entity);
//				vtos.add(vto);
//				cursor++;
//			}
//		}
//		return new CommonPage<WifiDeviceVTO>(pageNo, pageSize, total, vtos);
//		
//	}
	
	/**
	 * 根据多个条件来进行搜索wifi设备数据
	 * 以当前在线和当前在线移动设备数量排序
	 * @param mac 
	 * @param sn
	 * @param orig_swver 软件版本号
	 * @param adr 位置参数
	 * @param work_mode 工作模式
	 * @param config_mode 配置模式
	 * @param region 地区
	 * @param excepts 排除地区
	 * @param groupids 所属群组ids 空格分隔
	 * @param groupids_excepts 排序所属群组ids 空格分隔
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ESQueryValidateException
	 */
//	public TailPage<WifiDeviceVTO> fetchWDeviceByKeywords(
//			String mac, 
//			String sn, 
//			String orig_swver, 
//			String origvapmodule,
//			String adr, 
//			String work_mode,
//			String config_mode, 
//			String devicetype, 
//			Boolean online, 
//			Boolean moduleonline,
//			Boolean newVersionDevice, 
//			Boolean canOperateable,
//			String region, String region_excepts, String groupids, String groupids_excepts,
//			int pageNo, int pageSize){
//		List<WifiDeviceVTO> vtos = null;
//		int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
//		Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchByKeywords(
//				mac, sn, orig_swver,origvapmodule, 
//				adr, work_mode, config_mode, devicetype, 
//				online,moduleonline, newVersionDevice, canOperateable,
//				region, region_excepts, groupids, groupids_excepts, searchPageNo, pageSize);
//		/*QueryResponse<List<WifiDeviceSearchDTO>> search_result = wifiDeviceSearchService.searchByKeywords(mac, sn,
//				orig_swver, adr, work_mode, config_mode, devicetype, online, newVersionDevice, 
//				region, excepts, groupids, groupids_excepts, (pageNo*pageSize)-pageSize, pageSize);*/
//		
//		int total = (int)search_result.getTotalElements();//.getTotal();
//		if(total == 0){
//			vtos = Collections.emptyList();
//		}
//		List<WifiDeviceDocument> searchDtos = search_result.getContent();//.getResult();
//		if(searchDtos.isEmpty()) {
//			vtos = Collections.emptyList();
//		}else{
//			List<String> wifiIds = new ArrayList<String>();
//			for(WifiDeviceDocument searchDto : searchDtos){
//				wifiIds.add(searchDto.getId());
//			}
//			List<WifiDevice> entitys = wifiDeviceService.findByIds(wifiIds, true, true);
//			vtos = new ArrayList<WifiDeviceVTO>();
//			WifiDeviceVTO vto = null;
//			WifiDeviceDocument searchDto = null;
//			int cursor = 0;
//			for(WifiDevice wifiDevice : entitys){
//				searchDto = searchDtos.get(cursor);
//				vto = WifiDeviceDocumentHelper.toWifiDeviceVTO(searchDto, wifiDevice);
//				//vto = BusinessModelBuilder.toWifiDeviceVTO(searchDto, entity);
//				vtos.add(vto);
//				cursor++;
//			}
//			
//		}
//		return new CommonPage<WifiDeviceVTO>(pageNo, pageSize, total, vtos);
//		
//	}

	/**
	 * 获取统计数据的通用数据
		页面中统计数据体现：
		a、总设备数、总用户数、在线设备数、在线用户数、总接入次数、总用户访问时长
		b、今日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
		c、昨日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
	 * @return
	 */
	public StatisticsGeneralVTO fetchStatisticsGeneral(){
		StatisticsGeneralVTO vto = new StatisticsGeneralVTO();
		SystemStatisticsDTO systemStatisticsDto = SystemStatisticsHashService.getInstance().getStatistics();
		if(systemStatisticsDto == null) systemStatisticsDto = new SystemStatisticsDTO();
		deviceFacadeService.systemStatisticsArith(systemStatisticsDto);
		vto.setSystem(systemStatisticsDto);
		
		DailyStatisticsDTO daily_todayDto = DailyStatisticsHashService.getInstance().getStatistics(BusinessKeyDefine.
				Statistics.DailyStatisticsHandsetInnerPrefixKey);
		deviceFacadeService.dailyStatisticsArith(daily_todayDto);
		vto.setToday_daily(daily_todayDto);
		
		String yesterday_format = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1), DateTimeHelper.FormatPattern5);
		DailyStatisticsDTO daily_yesterdayDto = DailyStatisticsHashService.getInstance().getStatistics(BusinessKeyDefine.
				Statistics.DailyStatisticsHandsetInnerPrefixKey, yesterday_format);
		vto.setYesterday_daily(daily_yesterdayDto);
		return vto;
	}
	
	/**
	 * 获取wifi设备地域分布饼图
	 * @param regions 地域名称 按逗号分隔
	 * @return
	 * @throws ESQueryValidateException 
	 */
//	public List<RegionCountDTO> fetchWDeviceRegionCount(String regions){// throws ESQueryValidateException{
//		if(StringUtils.isEmpty(regions)) return null;
//		
//		String regionCountJson = WifiDeviceCountRegionStatisticsStringService.getInstance().getWifiDeviceCountRegion();
//		List<RegionCountDTO> dtos = null;
//		if(StringUtils.isEmpty(regionCountJson)){
//			//如果缓存失效 则从搜索引擎直接获取 (如果以后地域非常多,获取数据会相对耗时,可以放在定时程序去更新缓存)
//			//获取总共的wifi设备数量
//			long total_count = wifiDeviceDataSearchService.countByAddressMatchAll(null);//wifiDeviceSearchService.countByKeyword(null);
//			long total_region_count = 0;
//			dtos = new ArrayList<RegionCountDTO>();
//			String[] regions_array = regions.split(StringHelper.COMMA_STRING_GAP);
//			for(String region : regions_array){
//				RegionCountDTO region_dto = new RegionCountDTO();
//				//地域的wifi设备数量
//				long region_count = 0;
//				if(total_count > 0){
//					region_count = wifiDeviceDataSearchService.countByAddressMatchAll(region);//wifiDeviceSearchService.countByKeyword(region);
//				}
//				region_dto.setR(region);
//				region_dto.setV(region_count);
//				dtos.add(region_dto);
//				
//				total_region_count = total_region_count + region_count;
//			}
//			//其他地域的wifi设备数量
//			if(total_count > 0){
//				RegionCountDTO other_region_dto = new RegionCountDTO();
//				other_region_dto.setR("其他");
//				other_region_dto.setV(total_count-total_region_count);
//				dtos.add(other_region_dto);
//			}
//			regionCountJson = JsonHelper.getJSONString(dtos);
//			//存入redis中
//			WifiDeviceCountRegionStatisticsStringService.getInstance().setWifiDeviceCountRegion(regionCountJson);
//		}else{
//			dtos = JsonHelper.getDTOList(regionCountJson, RegionCountDTO.class);
//		}
//		return dtos;
//	}
	
	/**
	 * 获取近期接入的wifi设备列表
	 * 近期 (最近30天)
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ESQueryValidateException
	 */
//	public TailPage<WifiDeviceVTO> fetchRecentWDevice(int pageNo, int pageSize){//throws ESQueryValidateException{
//		List<WifiDeviceVTO> vtos = null;
//		
//		long minRegisterAt = System.currentTimeMillis() - (30 * 3600 * 24 * 1000l);
//		int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
//		Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.findByRegisteredatGreaterThan(minRegisterAt,searchPageNo,pageSize);
//		/*QueryResponse<List<WifiDeviceSearchDTO>> search_result = wifiDeviceSearchService.searchGtByRegisterAt(minRegisterAt, 
//				(pageNo*pageSize)-pageSize, pageSize);*/
//		
//		int total = (int)search_result.getTotalElements();//.getTotal();
//		if(total == 0){
//			vtos = Collections.emptyList();
//		}
//		List<WifiDeviceDocument> searchDtos = search_result.getContent();//.getResult();
//		if(searchDtos.isEmpty()) {
//			vtos = Collections.emptyList();
//		}else{
//			
//			List<String> wifiIds = new ArrayList<String>();
//			for(WifiDeviceDocument searchDto : searchDtos){
//				wifiIds.add(searchDto.getId());
//			}
//			List<WifiDevice> entitys = wifiDeviceService.findByIds(wifiIds, true, true);
//			vtos = new ArrayList<WifiDeviceVTO>();
//			WifiDeviceVTO vto = null;
//			WifiDeviceDocument searchDto = null;
//			int cursor = 0;
//			for(WifiDevice wifiDevice : entitys){
//				searchDto = searchDtos.get(cursor);
//				vto = WifiDeviceDocumentHelper.toWifiDeviceVTO(searchDto, wifiDevice);
//				//vto = BusinessModelBuilder.toWifiDeviceVTO(searchDto, entity);
//				vtos.add(vto);
//				cursor++;
//			}
//			/*List<String> wifiIds = new ArrayList<String>();
//			for(WifiDeviceSearchDTO searchDto : searchDtos){
//				wifiIds.add(searchDto.getId());
//			}
//			List<WifiDevice> entitys = wifiDeviceService.findByIds(wifiIds, true, true);
//			
//			vtos = new ArrayList<WifiDeviceVTO>();
//			WifiDeviceVTO vto = null;
//			WifiDeviceSearchDTO searchDto = null;
//			int cursor = 0;
//			for(WifiDevice entity : entitys){
//				searchDto = searchDtos.get(cursor);
//				vto = BusinessModelBuilder.toWifiDeviceVTO(searchDto, entity);
//				vtos.add(vto);
//				cursor++;
//			}*/
//		}
//		return new CommonPage<WifiDeviceVTO>(pageNo, pageSize, total, vtos);
//	}
	/**
	 * 根据wifi设备的id获取移动设备列表
	 * 如果终端的最后接入设备不是此设备 则不处理接入时间等数据
	 * @param wifiId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * modified by Edmond Lee for handset storage
	 */
	public TailPage<HandsetDeviceVTO> fetchHDevices(String wifiId, int pageNo, int pageSize){
		List<HandsetDeviceVTO> vtos = null;
		
		long total = WifiDeviceHandsetPresentSortedSetService.getInstance().presentSize(wifiId);
		if(total > 0){
			Set<Tuple> tuples = WifiDeviceHandsetPresentSortedSetService.getInstance().
					fetchPresents(wifiId, (pageNo*pageSize)-pageSize, pageSize);
			
			List<String> hd_macs = BusinessModelBuilder.toElementsList(tuples);
			
			if(!hd_macs.isEmpty()){
				vtos = new ArrayList<HandsetDeviceVTO>();
				List<HandsetDeviceDTO> handsets = HandsetStorageFacadeService.handsets(hd_macs);
				int cursor = 0;
				HandsetDeviceVTO vto = null;
				for(Tuple tuple : tuples){
					boolean online = WifiDeviceHandsetPresentSortedSetService.getInstance().isOnline(tuple.getScore());
					vto = BusinessModelBuilder.toHandsetDeviceVTO(wifiId, tuple.getElement(), online, handsets.get(cursor));
					vtos.add(vto);
					cursor++;
				}
				/*vtos = new ArrayList<HandsetDeviceVTO>();
				
				List<HandsetDevice> hd_entitys = handsetDeviceService.findByIds(hd_macs, true, true);
				HandsetDeviceVTO vto = null;
				int cursor = 0;
				for(Tuple tuple : tuples){
					boolean online = WifiDeviceHandsetPresentSortedSetService.getInstance().isOnline(tuple.getScore());
					vto = BusinessModelBuilder.toHandsetDeviceVTO(wifiId, tuple.getElement(), online, hd_entitys.get(cursor));
					vtos.add(vto);
					cursor++;
				}*/
			}
		}
		
		if(vtos == null)
			vtos = Collections.emptyList();
		
		return new CommonPage<HandsetDeviceVTO>(pageNo, pageSize, (int)total, vtos);
	}
	
	public RpcResponseDTO<List<PersistenceCMDDetailDTO>> fetchDevicePersistenceDetailCMD(String wifiId){
		try{
			List<PersistenceCMDDetailDTO> detailCMDs = wifiDevicePersistenceCMDStateService.fetchDevicePersistenceDetailCMD(wifiId);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(detailCMDs);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}
	
	public RpcResponseDTO<String> fetchDevicePresent(String wifiId) {
		try{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(WifiDevicePresentCtxService.getInstance().getPresent(wifiId));
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}
	}



	public RpcResponseDTO<TailPage<WifiDeviceVTO1>> fetchBySearchConditionMessage(String message, int pageNo, int pageSize){
		try{
			List<WifiDeviceVTO1> vtos = null;
			
			int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
			Page<WifiDeviceDocument> search_result = wifiDeviceDataSearchService.searchByConditionMessage(
					message, searchPageNo, pageSize);
			
			int total = 0;
			if(search_result != null){
				total = (int)search_result.getTotalElements();//.getTotal();
				if(total == 0){
					vtos = Collections.emptyList();
				}else{
					List<WifiDeviceDocument> searchDocuments = search_result.getContent();//.getResult();
					if(searchDocuments.isEmpty()) {
						vtos = Collections.emptyList();
					}else{
						vtos = new ArrayList<WifiDeviceVTO1>();
						WifiDeviceVTO1 vto = null;
						int startIndex = PageHelper.getStartIndexOfPage(pageNo, pageSize);
						for(WifiDeviceDocument wifiDeviceDocument : searchDocuments){
							vto = new WifiDeviceVTO1();
							vto.setIndex(++startIndex);
							BeanUtils.copyProperties(wifiDeviceDocument, vto);
							vtos.add(vto);
						}
					}
				}
			}else{
				vtos = Collections.emptyList();
			}
			TailPage<WifiDeviceVTO1> returnRet = new CommonPage<WifiDeviceVTO1>(pageNo, pageSize, total, vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		}catch(ElasticsearchIllegalArgumentException eiaex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.SEARCH_CONDITION_TYPE_NOTEXIST);
		}
	}
	
	public RpcResponseDTO<UserSearchConditionDTO> storeUserSearchCondition(int uid, String message, String desc){
		UserSearchConditionState entity = userSearchConditionStateService.getById(uid);
		boolean newed = false;
		if(entity == null){
			entity = new UserSearchConditionState();
			entity.setId(uid);
			newed = true;
		}
		
		UserSearchConditionDTO dto = new UserSearchConditionDTO(message, desc);
		if(entity.validateExist(message)){
			dto.setStored(false);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(dto);
		}
		
		dto.setTs(System.currentTimeMillis());
		entity.putInnerModel(dto, true, true);
		
		if(newed){
			userSearchConditionStateService.insert(entity);
		}else{
			userSearchConditionStateService.update(entity);
		}
		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(dto);
/*		Map<String,Object> payload = new HashMap<String,Object>();
		
		UserSearchConditionDTO dto = new UserSearchConditionDTO(message, desc);
		String dtojson = JsonHelper.getJSONString(dto);
		
		Double exist_ts = UserSearchConditionSortedSetService.getInstance().zscore(uid, dtojson);
		if(exist_ts != null && exist_ts > 0){
			payload.put("ts", exist_ts);
			payload.put("stored", false);
		}else{
			long ts = System.currentTimeMillis();
			Long ret = UserSearchConditionSortedSetService.getInstance().storeUserSearchCondition(uid, ts, dtojson);
			if(ret != null && ret > 0){
				payload.put("ts", ts);
				payload.put("stored", true);
			}
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);*/
	}
	
	public RpcResponseDTO<Boolean> removeUserSearchCondition(int uid, long ts){
		UserSearchConditionState entity = userSearchConditionStateService.getById(uid);
		if(entity != null){
			boolean removed = entity.removeInnerModel(new UserSearchConditionDTO(ts));
			if(removed){
				userSearchConditionStateService.update(entity);
				return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
			}
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(false);
/*		Long result = UserSearchConditionSortedSetService.getInstance().removeUserSearchCondition(uid, ts);
		if(result != null && result > 0){
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(false);*/
	}
	
	public RpcResponseDTO<Boolean> removeUserSearchConditions(int uid, String message_ts_splits){
		String[] message_ts_array = message_ts_splits.split(StringHelper.COMMA_STRING_GAP);
		if(message_ts_array != null && message_ts_array.length > 0){
			UserSearchConditionState entity = userSearchConditionStateService.getById(uid);
			if(entity != null){
				boolean needUpdate = false;
				for(String ts : message_ts_array){
					boolean removed = entity.removeInnerModel(new UserSearchConditionDTO(Long.parseLong(ts)));
					if(removed){
						needUpdate = true;
					}
				}
				
				if(needUpdate){
					userSearchConditionStateService.update(entity);
					return RpcResponseDTOBuilder.builderSuccessRpcResponse(true);
				}
			}
		}
		//UserSearchConditionSortedSetService.getInstance().removeUserSearchConditions(uid, message_ts_array);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(false);
	}
	
	public RpcResponseDTO<String> exportWifiDeviceResult(int uid, String message){
		String exportFileName = String.valueOf(uid).concat(StringHelper.MINUS_STRING_GAP).
				concat(DateTimeHelper.getDateTime());
		deliverMessageService.sendWifiDeviceSearchResultExportFileMessage(uid, message, exportFileName);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(exportFileName);
	}
	
	public RpcResponseDTO<String> exportOrderResult(int uid, String message, String start_date, String end_date){
		String exportFileName = String.valueOf(uid).concat(StringHelper.MINUS_STRING_GAP).
				concat(DateTimeHelper.getDateTime());
		deliverMessageService.sendOrderSearchResultExportFileMessage(uid, message, exportFileName, start_date, end_date);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(exportFileName);
	}
	
	public RpcResponseDTO<List<UserAgentVTO>> fetchAgents(int uid){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("utype", UserType.AgentNormal.getIndex());
		List<User> agents = userService.findModelByModelCriteria(mc);
		List<UserAgentVTO> vtos = null;
		if(agents != null && !agents.isEmpty()){
			vtos = new ArrayList<UserAgentVTO>();
			UserAgentVTO vto = null;
			for(User user : agents){
				vto = new UserAgentVTO();
				vto.setId(user.getId());
				vto.setN(user.getNick());
				vto.setOrg(user.getOrg());
				vtos.add(vto);
			}
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
	}
	
	public RpcResponseDTO<TailPage<UserSearchConditionDTO>> fetchUserSearchConditions(int uid, int pageNo, int pageSize) {
//		List<SearchConditionVTO> vtos = null;
//		int searchPageNo = pageNo>=1?(pageNo-1):pageNo;
		UserSearchConditionState entity = userSearchConditionStateService.getById(uid);
		if(entity == null){
			TailPage<UserSearchConditionDTO> emptyRet = new CommonPage<UserSearchConditionDTO>(pageNo, pageSize, 0, null);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(emptyRet);
		}
		List<UserSearchConditionDTO> dtos = entity.getInnerModels();
		if(dtos == null || dtos.isEmpty()){
			TailPage<UserSearchConditionDTO> emptyRet = new CommonPage<UserSearchConditionDTO>(pageNo, pageSize, 0, null);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(emptyRet);
		}
		int total = dtos.size();
		TailPage<UserSearchConditionDTO> returnRet = new CommonPage<UserSearchConditionDTO>(pageNo, pageSize, total, dtos);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);
		
		/*		long total = UserSearchConditionSortedSetService.getInstance().countUserSearchCondition(uid);
		if(total == 0){
			vtos = Collections.emptyList();
		}else{
			Set<Tuple> tuples = UserSearchConditionSortedSetService.getInstance().fetchUserSearchConditionsByPage(uid, searchPageNo, pageSize);
			if(tuples == null || tuples.isEmpty()){
				vtos = Collections.emptyList();
			}else{
				vtos = new ArrayList<SearchConditionVTO>();
				SearchConditionVTO vto = null;
				for(Tuple tuple : tuples){
					vto = new SearchConditionVTO();
					vto.setTs(Double.valueOf(tuple.getScore()).longValue());
					vto.setSdto(JsonHelper.getDTO(tuple.getElement(), StoreSearchConditionDTO.class));
					vtos.add(vto);
				}
			}
		}
		
		TailPage<SearchConditionVTO> returnRet = new CommonPage<SearchConditionVTO>(pageNo, pageSize, (int)total, vtos);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(returnRet);*/
	}
	
	
/*	public static final int GeoMap_Fetch_Count = 500;
	public Collection<GeoMapVTO> fetchGeoMap(){// throws ESQueryValidateException{
		Collection<GeoMapVTO> vtos = businessDeviceCacheService.getDeviceGeoMapCacheByQ();
		if(vtos == null){
			QueryResponse<List<WifiDeviceSearchDTO>> search_result = wifiDeviceSearchService.searchExistAddress(0, GeoMap_Fetch_Count);
			int total = search_result.getTotal();
			if(total == 0){
				return Collections.emptyList();
			}else{
				List<WifiDeviceSearchDTO> search_dtos = search_result.getResult();
				if(search_dtos.isEmpty()){
					return Collections.emptyList();
				}else{
					List<String> ids = new ArrayList<String>();
					for(WifiDeviceSearchDTO dto : search_dtos){
						ids.add(dto.getId());
					}
					List<WifiDevice> entitys = wifiDeviceService.findByIds(ids, true, true);
	
					//vtos = new ArrayList<GeoMapVTO>();
					Map<String,GeoMapVTO> mergeMap = new HashMap<String,GeoMapVTO>();
					int cursor = 0;
					for(WifiDeviceSearchDTO dto : search_dtos){
						//对坐标进行精度降维 合并坐标点 以便适应百度接口的50个坐标点的限制
						String lat_coarsness = String.valueOf(ArithHelper.round(dto.getLat(), 2));
						String lon_coarsness = String.valueOf(ArithHelper.round(dto.getLon(), 2));
						
						StringBuffer coordinate_sb = new StringBuffer();
						coordinate_sb.append(lat_coarsness);
						coordinate_sb.append(StringHelper.COMMA_STRING_GAP);
						coordinate_sb.append(lon_coarsness);
						
						GeoMapVTO vto = mergeMap.get(coordinate_sb.toString());
						if(vto == null)
							vto = new GeoMapVTO();
						
						vto.setLat(lat_coarsness);
						vto.setLng(lon_coarsness);
						WifiDevice entity = entitys.get(cursor);
						if(entity != null){
							List<GeoMapDeviceVTO> rows = vto.getRows();
							if(rows == null) 
								rows = new ArrayList<GeoMapDeviceVTO>();
							
							GeoMapDeviceVTO sub_vto = new GeoMapDeviceVTO();
							sub_vto.setName(entity.getOrig_model());
							sub_vto.setIp(entity.getWan_ip());
							sub_vto.setMac(entity.getId());
							sub_vto.setStatus(entity.isOnline() ? 1 : 0);
							rows.add(sub_vto);
							vto.setRows(rows);
						}
						mergeMap.put(coordinate_sb.toString(), vto);
						//vtos.add(vto);
						cursor++;
					}
					vtos = mergeMap.values();
					businessDeviceCacheService.storeDeviceGeoMapCacheResult(vtos);
				}
			}
		}
		return vtos;
	}*/
}
