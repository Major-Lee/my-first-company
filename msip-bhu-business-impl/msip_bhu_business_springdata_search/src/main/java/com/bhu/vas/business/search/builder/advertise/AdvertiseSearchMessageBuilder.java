package com.bhu.vas.business.search.builder.advertise;

import org.elasticsearch.search.sort.SortOrder;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.core.condition.component.SearchCondition;
import com.bhu.vas.business.search.core.condition.component.SearchConditionLogicEnumType;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPack;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPattern;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSort;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSortPattern;
import com.bhu.vas.business.search.core.condition.component.payload.SearchConditionGeopointDistancePayload;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

public class AdvertiseSearchMessageBuilder {
	
	/**
	 * 根据经纬度坐标和距离查询
	 *  根据距离正序排列
	 * @param contextId
	 * @param lat
	 * @param lon
	 * @param distance
	 * @return
	 */
	public static SearchConditionMessage builderSearchMessageWithGeoPointDistance(String contextId, double lat, double lon, String distance){
		SearchConditionGeopointDistancePayload geopointDistancePayload = SearchConditionGeopointDistancePayload.buildPayload(
				contextId, lat, lon, distance);
		SearchCondition sc_geopointDistance = SearchCondition.builderSearchCondition(BusinessIndexDefine.Advertise.
				Field.A_GEOPOINT.getName(), SearchConditionPattern.GeopointDistance.getPattern(), 
				JsonHelper.getJSONString(geopointDistancePayload));
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_geopointDistance);
		
		SearchConditionSort sc_sortByGeopoint = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.Advertise.
				Field.A_GEOPOINT.getName(), SearchConditionSortPattern.SortGeopointDistance.getPattern(),
				SortOrder.ASC, JsonHelper.getJSONString(sc_geopointDistance));
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		scm.addSorts(sc_sortByGeopoint);
		
		return scm;
	}
	
	public static SearchConditionMessage builderSearchMessageWithGeoPointDistanceAndAdcode(String contextId, double lat, double lon, String distance,String adcode){
		SearchConditionGeopointDistancePayload geopointDistancePayload = SearchConditionGeopointDistancePayload.buildPayload(
				contextId, lat, lon, distance);
		SearchCondition sc_geopointDistance = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,BusinessIndexDefine.Advertise.
				Field.A_GEOPOINT.getName(), SearchConditionPattern.GeopointDistance.getPattern(), 
				JsonHelper.getJSONString(geopointDistancePayload));
		
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_geopointDistance);


		
		
		SearchCondition sc_adcode = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,BusinessIndexDefine.Advertise.
				Field.A_ADCODE.getName(),  SearchConditionPattern.StringEqual.getPattern(), adcode);
		
		SearchCondition sc_adcode_all = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,BusinessIndexDefine.Advertise.
				Field.A_ADCODE.getName(),  SearchConditionPattern.StringEqual.getPattern(), "100000");
		
		SearchCondition sc_adcode_city = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,BusinessIndexDefine.Advertise.
				Field.A_ADCODE.getName(),  SearchConditionPattern.PrefixContain.getPattern(), adcode.substring(0, 4));
		
		SearchCondition sc_adcode_province = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,BusinessIndexDefine.Advertise.
				Field.A_ADCODE.getName(),  SearchConditionPattern.PrefixContain.getPattern(), adcode.substring(0, 2));
		
		SearchConditionSort sc_sortByScore = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.Advertise.
				Field.A_SCORE.getName(), SearchConditionSortPattern.Sort.getPattern(),
				SortOrder.DESC, null);
		
		
		
		SearchConditionPack pack_should = SearchConditionPack.builderSearchConditionPackWithConditions(sc_adcode);

		pack_should.addChildSearchCondtions(sc_adcode_all);
		pack_should.addChildSearchCondtions(sc_adcode_city);
		pack_should.addChildSearchCondtions(sc_adcode_province);
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		scm.addSorts(sc_sortByScore);
		
		return scm;
	}
	
	public static SearchConditionMessage builderSearchMessageByCreated(){

		SearchCondition sc_a_state = SearchCondition.builderSearchCondition(BusinessIndexDefine.Advertise.
				Field.A_STATE.getName(),  SearchConditionPattern.StringEqual.getPattern(), BusinessEnumType.AdvertiseStateType.OnPublish.getType()+"");
		
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_a_state);
		
		SearchConditionSort sc_sortByCreated = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.Advertise.
				Field.A_CREATED_AT.getName(), SearchConditionSortPattern.Sort.getPattern(),
				SortOrder.DESC, null);
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		scm.addSorts(sc_sortByCreated);
		
		return scm;
	}
}
