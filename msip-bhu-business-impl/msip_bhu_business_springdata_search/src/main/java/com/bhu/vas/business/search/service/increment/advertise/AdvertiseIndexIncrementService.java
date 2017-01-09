package com.bhu.vas.business.search.service.increment.advertise;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.service.advertise.AdvertiseDataSearchService;
import com.smartwork.msip.cores.helper.DateTimeHelper;

@Service
public class AdvertiseIndexIncrementService {
	private final Logger logger = LoggerFactory.getLogger(AdvertiseIndexIncrementService.class);

	@Resource
	private AdvertiseDataSearchService advertiseDataSearchService;
	
	//广告状态发生变更
	public void adStateUpdIncrement(String id , int state , String msg){
		logger.info(String.format("adStateUpdIncrement Request id [%s] state [%s]", id, state));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.Advertise.Field.A_TYPE.getName(), state);
		sourceMap.put(BusinessIndexDefine.Advertise.Field.A_UPDATED_AT.getName(), DateTimeHelper.getDateTime());
		if(msg != null){
			sourceMap.put(BusinessIndexDefine.Advertise.Field.A_REJECT_REASON.getName(), msg);
		}
		advertiseDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
	
	//广告状态发生变更(批量)
	public void adStateBulkUpdIncrement(List<String> ids , int state , String msg){
		logger.info(String.format("adStateUpdIncrement Request id [%s] state [%s]", ids, state));
		if(ids.isEmpty()) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.Advertise.Field.A_TYPE.getName(), state);
		sourceMap.put(BusinessIndexDefine.Advertise.Field.A_UPDATED_AT.getName(), DateTimeHelper.getDateTime());
		if(!msg.isEmpty()){
			sourceMap.put(BusinessIndexDefine.Advertise.Field.A_REJECT_REASON.getName(), msg);
		}
		advertiseDataSearchService.bulkUpdate(ids, sourceMap, false, true, true);
	}
	
	//广告持续时间发生变更
	public void adStartAndEndUpdIncrement(String id ,String start,String end){
		logger.info(String.format("adStartAndEndUpdIncrement Request id [%s] start [%s] end [%s]", id, start,end));
		if(StringUtils.isEmpty(id)) return;
		
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.Advertise.Field.A_START.getName(), start);
		sourceMap.put(BusinessIndexDefine.Advertise.Field.A_END.getName(), end);
		sourceMap.put(BusinessIndexDefine.Advertise.Field.A_UPDATED_AT.getName(), DateTimeHelper.getDateTime());

		advertiseDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
}
