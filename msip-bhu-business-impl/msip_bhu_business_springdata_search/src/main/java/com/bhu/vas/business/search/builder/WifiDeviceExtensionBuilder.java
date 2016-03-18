package com.bhu.vas.business.search.builder;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.core.condition.component.SearchCondition;
import com.bhu.vas.business.search.core.condition.component.SearchConditionLogicEnumType;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPack;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPattern;

/**
 * 用于构建设备搜索记录的d_extension字段数据
 * @author tangzichao
 *
 */
public class WifiDeviceExtensionBuilder {
	//访客网络类型数据
	public static final String Extension_SharedNetwork_Type_Prefix = "snkt_";
	/**
	 * 根据绑定的用户ID，在线状态，输入框条件进行构建搜索message对象
	 * @param u_id
	 * @param d_online
	 * @param s_content
	 * @return
	 */
	public static SearchConditionMessage builderSearchTCMessage(Integer u_id, String d_online, String s_content){
		List<SearchConditionPack> packs = new ArrayList<SearchConditionPack>();
		
		SearchConditionPack pack_must_1 = new SearchConditionPack();
		
		SearchCondition sc_d_dut = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), VapEnumType.DUT_CWifi);
		pack_must_1.addChildSearchCondtions(sc_d_dut);
		
		if(u_id != null){
			SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
			pack_must_1.addChildSearchCondtions(sc_u_id);
		}
		
		if(StringUtils.isNotEmpty(d_online)){
			SearchCondition sc_d_online = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_ONLINE.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(d_online));
			pack_must_1.addChildSearchCondtions(sc_d_online);
		}
		packs.add(pack_must_1);
		
		if(StringUtils.isNotEmpty(s_content)){
			SearchCondition sc_u_dnick = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
					BusinessIndexDefine.WifiDevice.Field.U_DNICK.getName(), SearchConditionPattern.Contain.getPattern(),
					s_content);
			SearchCondition sc_d_mac = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
					BusinessIndexDefine.WifiDevice.Field.D_MAC.getName(), SearchConditionPattern.Contain.getPattern(),
					s_content);
			SearchCondition sc_d_ip = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
					BusinessIndexDefine.WifiDevice.Field.D_WANIP.getName(), SearchConditionPattern.Contain.getPattern(),
					s_content);
			SearchCondition sc_d_origmodel = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
					BusinessIndexDefine.WifiDevice.Field.D_ORIGMODEL.getName(), SearchConditionPattern.Contain.getPattern(),
					s_content);
			SearchCondition sc_d_origswver = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
					BusinessIndexDefine.WifiDevice.Field.D_ORIGSWVER.getName(), SearchConditionPattern.Contain.getPattern(),
					s_content);
			SearchCondition sc_d_workmodel = SearchCondition.builderSearchCondition(SearchConditionLogicEnumType.Should,
					BusinessIndexDefine.WifiDevice.Field.D_WORKMODEL.getName(), SearchConditionPattern.StringEqual.getPattern(),
					s_content);
			SearchConditionPack pack_must_2 = SearchConditionPack.builderSearchConditionPackWithConditions(
					sc_u_dnick, sc_d_mac, sc_d_ip, sc_d_origmodel, sc_d_origswver, sc_d_workmodel);
			packs.add(pack_must_2);
		}

		return SearchConditionMessage.builderSearchConditionMessage(packs.toArray(new SearchConditionPack[]{}));
	}
}
