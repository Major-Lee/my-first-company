package com.bhu.vas.business.search.builder;

import java.util.List;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.search.sort.SortOrder;

import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
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

/**
 * 用于构建业务搜索条件的builder
 * @author tangzichao
 *
 */
public class WifiDeviceSearchMessageBuilder {
	
	/**
	 * 通用构建搜索message对象
	 * @param u_id 用户id
	 * @param sharedNetwork_type 访客网络类型
	 * @param d_dut 业务线
	 * @param t_uc_extension 设备分组
	 * @param d_online 设备在线状态
	 * @param d_snk_turnstate 访客网络是否开启
	 * @return
	 */
	public static SearchConditionMessage builderSearchMessageCommon(Integer u_id, String sharedNetwork_type, 
			String d_dut, String t_uc_extension, String d_online, String d_snk_turnstate ,String d_tags){
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionMustPack();
		
		if(u_id != null){
			SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
			pack_must.addChildSearchCondtions(sc_u_id);
		}

		if(StringUtils.isNotEmpty(sharedNetwork_type)){
			SearchCondition sc_d_snk_type = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), sharedNetwork_type);
			pack_must.addChildSearchCondtions(sc_d_snk_type);
		}
		
		if(StringUtils.isNotEmpty(d_dut)){
			SearchCondition sc_d_dut = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_dut);
			pack_must.addChildSearchCondtions(sc_d_dut);
		}
		
		if(StringUtils.isNotEmpty(t_uc_extension)){
			SearchCondition sc_uc_extension = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.T_UC_EXTENSION.getName(), SearchConditionPattern.StringEqual.getPattern(), t_uc_extension);
			pack_must.addChildSearchCondtions(sc_uc_extension);
		}
		
		if(StringUtils.isNotEmpty(d_online)){
			SearchCondition sc_d_online = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_online);
			pack_must.addChildSearchCondtions(sc_d_online);
		}
		
		if(StringUtils.isNotEmpty(d_snk_turnstate)){
			SearchCondition sc_d_snk_turnstate = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TURNSTATE.getName(), SearchConditionPattern.StringEqual.getPattern(), 
					d_snk_turnstate);
			pack_must.addChildSearchCondtions(sc_d_snk_turnstate);
		}

		if(StringUtils.isNotEmpty(d_tags)){
			SearchCondition sc_d_tags_turnstate = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_TAGS.getName(), SearchConditionPattern.StringEqual.getPattern(), 
					d_tags);
			pack_must.addChildSearchCondtions(sc_d_tags_turnstate);
		}
		
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		
		SearchConditionSort sc_sortByOnine = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.WifiDevice.
				Field.D_ONLINE.getName(), SearchConditionSortPattern.Sort.getPattern(),
				SortOrder.DESC, null);
		scm.addSorts(sc_sortByOnine);
		return scm;
	}
	
	/**
	 * 根据uid和设备业务线构建搜索message对象
	 * @param u_id 用户uid
	 * @param d_dut 设备业务线
	 * @return
	 */
	public static SearchConditionMessage builderSearchMessageWithDut(Integer u_id, String d_dut){
		SearchConditionPack pack_must = null;
		
		SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		
		if(StringUtils.isNotEmpty(d_dut)){
			SearchCondition sc_d_dut = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_dut);
			pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_d_dut, sc_u_id);
		}else{
			pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_u_id);
		}
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		
		SearchConditionSort sc_sortByOnine = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.WifiDevice.
				Field.D_ONLINE.getName(), SearchConditionSortPattern.Sort.getPattern(),
				SortOrder.DESC, null);
		SearchConditionSort sc_sortByDnick = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.WifiDevice.
				Field.U_DNICK.getName(), SearchConditionSortPattern.Sort.getPattern(),
				SortOrder.ASC, null);
		scm.addSorts(sc_sortByOnine);
		scm.addSorts(sc_sortByDnick);
		return scm;
	}
	
	/**
	 * 根据uid和设备共享网络类型构建搜索message对象
	 * @param u_id 用户id
	 * @param sharedNetwork_type 设备共享网络类型
	 * @param d_snk_template
	 * @return
	 */
	public static SearchConditionMessage builderSearchMessageWithSharedNetwork(Integer u_id, String sharedNetwork_type, String d_snk_template,
			String d_dut){
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionMustPack();
		//if(u_id != null){
//		SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_SHAREDNETWORK_OWNER.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		pack_must.addChildSearchCondtions(sc_u_id);
		//}

		if(StringUtils.isNotEmpty(sharedNetwork_type)){
			SearchCondition sc_d_snk_type = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), sharedNetwork_type);
			pack_must.addChildSearchCondtions(sc_d_snk_type);
		}else{
			if(StringHelper.EMPTY_STRING_GAP.equals(sharedNetwork_type)){
				SearchCondition sc_d_snk_type = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
						Field.D_SHAREDNETWORK_TYPE.getName(), SearchConditionPattern.Missing.getPattern(), sharedNetwork_type);
				pack_must.addChildSearchCondtions(sc_d_snk_type);
			}
		}
		
		
		if(StringUtils.isNotEmpty(d_dut)){
			SearchCondition sc_d_dut = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_dut);
			pack_must.addChildSearchCondtions(sc_d_dut);
		}
		
		if(StringUtils.isNotEmpty(d_snk_template)){
			SearchCondition sc_d_snk_template = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TEMPLATE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_snk_template);
			pack_must.addChildSearchCondtions(sc_d_snk_template);
		}else{
			if(StringHelper.EMPTY_STRING_GAP.equals(d_snk_template)){
				SearchCondition sc_d_snk_template = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
						Field.D_SHAREDNETWORK_TEMPLATE.getName(), SearchConditionPattern.Missing.getPattern(), d_snk_template);
				pack_must.addChildSearchCondtions(sc_d_snk_template);
			}
		}

		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		
		SearchConditionSort sc_sortByOnine = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.WifiDevice.
				Field.D_ONLINE.getName(), SearchConditionSortPattern.Sort.getPattern(),
				SortOrder.DESC, null);
		scm.addSorts(sc_sortByOnine);
		return scm;
	}
	
	
	public static SearchConditionMessage builderSearchMessageWithUserGroup(Integer u_id, String t_uc_extension, String d_online){
		SearchConditionPack pack_must = null;
		
		SearchCondition sc_uc_extension = null;
		if(StringHelper.isEmpty(t_uc_extension)){
			sc_uc_extension = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.T_UC_EXTENSION.getName(), SearchConditionPattern.Missing.getPattern(), null);
		}else{
			sc_uc_extension = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.T_UC_EXTENSION.getName(), SearchConditionPattern.StringEqual.getPattern(), t_uc_extension);
		}

		SearchCondition sc_d_uid = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		
		pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_uc_extension, sc_d_uid);
		
		if(StringUtils.isNotEmpty(d_online)){
			SearchCondition sc_d_online = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(), 
					WifiDeviceDocumentEnumType.OnlineEnum.Online.getType());
			pack_must.addChildSearchCondtions(sc_d_online);
		}

//		pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_uc_extension, sc_d_uid, sc_d_online);

		return SearchConditionMessage.builderSearchConditionMessage(pack_must);
	}
	
	public static SearchConditionMessage builderSearchMessageWithDeviceStatistics(String d_snk_turnstate,
			String d_snk_type, String d_online, String d_tags, String u_id){
		
		SearchCondition sc_d_snk_turnstate = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_SHAREDNETWORK_TURNSTATE.getName(), SearchConditionPattern.StringEqual.getPattern(), 
				d_snk_turnstate);
		
		SearchCondition sc_d_snk_type = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_SHAREDNETWORK_TYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), 
				d_snk_type);
		
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_d_snk_turnstate, sc_d_snk_type);
		
		if(StringHelper.isNotEmpty(d_online)){
			SearchCondition sc_d_online = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_online);
			pack_must.addChildSearchCondtions(sc_d_online);
		}
		if(StringHelper.isNotEmpty(d_tags)){
			SearchCondition sc_d_tags = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_TAGS.getName(), SearchConditionPattern.StringEqual.getPattern(), d_tags);
			pack_must.addChildSearchCondtions(sc_d_tags);
		}
		if(StringHelper.isNotEmpty(u_id)){
			SearchCondition sc_d_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), u_id);
			pack_must.addChildSearchCondtions(sc_d_u_id);
		}

		return SearchConditionMessage.builderSearchConditionMessage(SearchType.COUNT.id(), pack_must);
	}
	
	public static SearchConditionMessage builderSearchMessageWithSnkType(Integer u_id, 
			String d_snk_type, String d_snk_turnstate, String d_snk_template){
//		SearchCondition sc_d_uid = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		SearchCondition sc_d_uid = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_SHAREDNETWORK_OWNER.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		
		SearchCondition sc_d_snk_type = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_SHAREDNETWORK_TYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_snk_type);

		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_d_uid, sc_d_snk_type);
		
		if(StringUtils.isNotEmpty(d_snk_turnstate)){
			SearchCondition sc_d_snk_turnstate = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TURNSTATE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_snk_turnstate);
			pack_must.addChildSearchCondtions(sc_d_snk_turnstate);
		}
		
		if(StringUtils.isNotEmpty(d_snk_template)){
			SearchCondition sc_d_snk_template = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TEMPLATE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_snk_template);
			pack_must.addChildSearchCondtions(sc_d_snk_template);
		}
		return SearchConditionMessage.builderSearchConditionMessage(SearchType.COUNT.id(), pack_must);
	}
	
	/**
	 * 根据设备地理位置(省市区)构建搜索message对象
	 * @param d_province 省
	 * @param d_city 市
	 * @param d_distrcy 区
	 * @return
	 */
	public static SearchConditionMessage builderSearchMessageWithPosition(List<AdvertiseTrashPositionVTO> must_not_positions, String d_province,String d_city,String d_distrcy,boolean snkTurnOn){
		SearchConditionPack pack = SearchConditionPack.builderSearchConditionMustPack();
		
		if(StringUtils.isNotEmpty(d_province)){
			SearchCondition sc_d_province = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_PROVINCE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_province);
			pack.addChildSearchCondtions(sc_d_province);
		}
		if(StringUtils.isNotEmpty(d_city)){
			SearchCondition sc_d_city = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_CITY.getName(), SearchConditionPattern.StringEqual.getPattern(), d_city);
			pack.addChildSearchCondtions(sc_d_city);
		}
		if(StringUtils.isNotEmpty(d_distrcy)){
			SearchCondition sc_d_distrcy = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_DISTRICT.getName(), SearchConditionPattern.StringEqual.getPattern(), d_distrcy);
			pack.addChildSearchCondtions(sc_d_distrcy);
		}
		
		if(must_not_positions != null && !must_not_positions.isEmpty()){
			for(AdvertiseTrashPositionVTO dto: must_not_positions){
				if(StringUtils.isNotEmpty(dto.getDistrict())){
					SearchCondition sc_d_ms_distrcy = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.MustNot,BusinessIndexDefine.WifiDevice.
							Field.D_DISTRICT.getName(), SearchConditionPattern.StringEqual.getPattern(), dto.getDistrict());
					pack.addChildSearchCondtions(sc_d_ms_distrcy);
					
				}else if(StringUtils.isNotEmpty(dto.getCity())){
					SearchCondition sc_d_ms_city = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.MustNot,BusinessIndexDefine.WifiDevice.
							Field.D_CITY.getName(), SearchConditionPattern.StringEqual.getPattern(), dto.getCity());
					pack.addChildSearchCondtions(sc_d_ms_city);
					
				}else if(StringUtils.isNotEmpty(dto.getProvince())){
					SearchCondition sc_d_ms_province = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.MustNot,BusinessIndexDefine.WifiDevice.
							Field.D_PROVINCE.getName(), SearchConditionPattern.StringEqual.getPattern(), dto.getProvince());
					pack.addChildSearchCondtions(sc_d_ms_province);
				}else{
					return null;
				}
			}
		}
		
		if(snkTurnOn){
			SearchCondition sc_d_online = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(), WifiDeviceDocumentEnumType.OnlineEnum.Online.getType());
			pack.addChildSearchCondtions(sc_d_online);
			
			SearchCondition sc_d_snk_turnOn = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TURNSTATE.getName(), SearchConditionPattern.StringEqual.getPattern(), WifiDeviceDocumentEnumType.SnkTurnStateEnum.On.getType());
			pack.addChildSearchCondtions(sc_d_snk_turnOn);
		}
		
		if(pack.getChildSearchCondtions() == null || pack.getChildSearchCondtions().isEmpty()){
			SearchCondition sc_all = SearchCondition.builderSearchConditionWithAll();
			pack.addChildSearchCondtions(sc_all);
		}
		
		SearchCondition sc_d_dut = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), "TU");
		pack.addChildSearchCondtions(sc_d_dut);
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack);
		return scm;
	}
	
	
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
		SearchCondition sc_geopointDistance = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_GEOPOINT.getName(), SearchConditionPattern.GeopointDistance.getPattern(), 
				JsonHelper.getJSONString(geopointDistancePayload));
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_geopointDistance);
		
		SearchConditionSort sc_sortByGeopoint = SearchConditionSort.builderSearchConditionSort(BusinessIndexDefine.WifiDevice.
				Field.D_GEOPOINT.getName(), SearchConditionSortPattern.SortGeopointDistance.getPattern(),
				SortOrder.ASC, JsonHelper.getJSONString(sc_geopointDistance));
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		scm.addSorts(sc_sortByGeopoint);
		
		return scm;
	}
	
	public static String builderSearchMessageString(SearchConditionMessage searchConditionMessage){
		if(searchConditionMessage == null) return null;
		return JsonHelper.getJSONString(searchConditionMessage);
	}
}
