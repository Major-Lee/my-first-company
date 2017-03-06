package com.bhu.vas.business.search.service.wifi;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.wifi.model.SsidInfo;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.smartwork.msip.cores.helper.DateTimeHelper;
/**
 * ssid信息增量索引service
 * @author yetao
 *
 */
@Service
public class SsidIndexIncrementService implements ISsidIndexIncrement{
	private final Logger logger = LoggerFactory.getLogger(SsidIndexIncrementService.class);
	
	@Resource
	private SsidDataSearchService ssidDataSearchService;
	

	
	/**
	 * 目前应该暂时用不到
	 */
	@Override
	public void updateIncrement(final String id, final String ssid, final String mode, final String pwd){
		logger.info(String.format("updateIncrement Request id [%s] ssid [%s], mode[%s], pwd[%s]", id, ssid, mode, pwd));
		if(StringUtils.isEmpty(id) || StringUtils.isEmpty(ssid) || StringUtils.isEmpty(mode)) return;
		SsidInfo xx = null;
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put(BusinessIndexDefine.Ssid.Field.S_SSID.getName(), ssid);
		sourceMap.put(BusinessIndexDefine.Ssid.Field.UPDATED_AT.getName(), DateTimeHelper.getDateTime());
		ssidDataSearchService.updateIndex(id, sourceMap, false, true, true);
	}
}
