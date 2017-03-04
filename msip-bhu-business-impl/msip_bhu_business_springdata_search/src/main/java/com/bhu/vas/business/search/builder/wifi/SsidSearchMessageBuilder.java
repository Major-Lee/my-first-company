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
	
	public static SearchConditionMessage builderSearchMessageByBssidAndSsidAndMode(String bssid, String ssid, String mode){

		SearchCondition sc_s_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.Ssid.
				Field.ID.getName(),  SearchConditionPattern.StringEqual.getPattern(), bssid);
		SearchCondition sc_s_ssid = SearchCondition.builderSearchCondition(BusinessIndexDefine.Ssid.
				Field.S_SSID.getName(),  SearchConditionPattern.StringEqual.getPattern(), ssid);
		SearchCondition sc_s_mode = SearchCondition.builderSearchCondition(BusinessIndexDefine.Ssid.
				Field.S_MODE.getName(),  SearchConditionPattern.StringEqual.getPattern(), mode);
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_s_id, sc_s_ssid, sc_s_mode);
		
		SearchConditionSort sc_sortByCreated = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.Ssid.
				Field.CREATED_AT.getName(), SearchConditionSortPattern.Sort.getPattern(),
				SortOrder.DESC, null);
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		scm.addSorts(sc_sortByCreated);
		
		return scm;
	}
	
}
