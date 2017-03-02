package com.bhu.vas.business.search.service.wifi;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.builder.wifi.SsidSearchMessageBuilder;
import com.bhu.vas.business.search.core.condition.AbstractDataSearchConditionService;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.field.FieldDefine;
import com.bhu.vas.business.search.model.wifi.SsidDocument;
import com.bhu.vas.business.search.repository.wifi.SsidDocumentRepository;


@Service
public class SsidDataSearchService extends AbstractDataSearchConditionService<SsidDocument>{
    @Resource
    private SsidDocumentRepository ssidDocumentRepository;
	
	public SsidDocumentRepository getRepository(){
		return ssidDocumentRepository;
	}
	
	@Override
	public FieldDefine getFieldByName(String fieldName) {
		if(StringUtils.isEmpty(fieldName)) return null;
		return BusinessIndexDefine.Ssid.Field.getByName(fieldName);	}

	@Override
	public int retryOnConflict() {
		return BusinessIndexDefine.Ssid.RetryOnConflict;
	}

	public SsidDocument searchById(String id){
		return this.getRepository().findOne(id);
	}
	
	
	public Page<SsidDocument> searchPageByBssidAndSsid(String bssid, String ssid){
		SearchConditionMessage scm = SsidSearchMessageBuilder.builderSearchMessageByBssidAndSsid(bssid, ssid);
		return super.searchByConditionMessage(scm, 0, 3);
	}
}
