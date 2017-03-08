package com.bhu.vas.business.search.service.wifi;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
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
	
	public Iterable<SsidDocument> searchByIds(List<String> ids){
		return this.getRepository().findAll(ids);
	}
	
	
//	public Page<SsidDocument> searchPageByBssidAndSsidAndMode(String bssid, String ssid, String mode){
//		SearchConditionMessage scm = SsidSearchMessageBuilder.builderSearchMessageByBssidAndSsidAndMode(bssid, ssid, mode);
//		return super.searchByConditionMessage(scm, 0, 3);
//	}
//	
	
	/**
	 * 根据条件搜索数据
	 * 绑定设备的用户id
	 * 设备的业务线类型
	 * @param u_id
	 * @param d_dut
	 * @return
	 */
	public List<SsidDocument> searchPageByBssidAndSsidAndMode(String bssid, String ssid, String mode){
		SearchConditionMessage scm = SsidSearchMessageBuilder.builderSearchMessageByBssidAndSsidAndMode(bssid, ssid, mode);
		return super.searchListByConditionMessage(scm, 0, 100);
	}

	public void bulkIndexOne(SsidDocument doc){
		List<SsidDocument> list = new ArrayList<SsidDocument>();
		list.add(doc);
		super.bulkIndex(list, true, false);
	}
}
