package com.bhu.vas.business.search.builder.wifi;

import org.elasticsearch.search.sort.SortOrder;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.core.condition.component.SearchCondition;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPack;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPattern;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSort;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSortPattern;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionGeopointDistancePayload;
import com.smartwork.msip.cores.helper.JsonHelper;

public class SsidSearchMessageBuilder {
	
	public static SearchConditionMessage builderSearchMessageByBssidAndSsid(String bssid, String ssid){

		SearchCondition sc_s_bssid = SearchCondition.builderSearchCondition(BusinessIndexDefine.Ssid.
				Field.S_BSSID.getName(),  SearchConditionPattern.StringEqual.getPattern(), bssid);
		SearchCondition sc_s_ssid = SearchCondition.builderSearchCondition(BusinessIndexDefine.Ssid.
				Field.S_SSID.getName(),  SearchConditionPattern.StringEqual.getPattern(), ssid);
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_s_bssid, sc_s_ssid);
		
		SearchConditionSort sc_sortByCreated = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.Ssid.
				Field.S_CREATED_AT.getName(), SearchConditionSortPattern.Sort.getPattern(),
				SortOrder.DESC, null);
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		scm.addSorts(sc_sortByCreated);
		
		return scm;
	}
	
}
