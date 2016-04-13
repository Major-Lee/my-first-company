package com.bhu.vas.business.search.builder;

import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.search.sort.SortOrder;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.core.condition.component.SearchCondition;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPack;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPattern;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSort;
import com.bhu.vas.business.search.core.condition.component.SearchConditionSortPattern;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 用于构建业务搜索条件的builder
 * @author tangzichao
 *
 */
public class WifiDeviceSearchMessageBuilder {
	
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
		scm.addSorts(sc_sortByOnine);
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
		SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		pack_must.addChildSearchCondtions(sc_u_id);
		//}
		SearchCondition sc_d_snk_type = null;
		if(StringUtils.isNotEmpty(sharedNetwork_type)){
			sc_d_snk_type = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), sharedNetwork_type);
		}else{
			sc_d_snk_type = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_SHAREDNETWORK_TYPE.getName(), SearchConditionPattern.Missing.getPattern(), sharedNetwork_type);
		}
		pack_must.addChildSearchCondtions(sc_d_snk_type);
		
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
	
	public static String builderSearchMessageString(SearchConditionMessage searchConditionMessage){
		if(searchConditionMessage == null) return null;
		return JsonHelper.getJSONString(searchConditionMessage);
	}
}
