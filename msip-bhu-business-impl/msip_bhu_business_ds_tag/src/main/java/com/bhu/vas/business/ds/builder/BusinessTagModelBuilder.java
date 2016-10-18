package com.bhu.vas.business.ds.builder;

import java.util.Map;

import com.bhu.vas.api.rpc.tag.dto.TagGroupHandsetDetailDTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupHandsetDetailVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupRankUsersVTO;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;
/**
 * 用于dto和model之间的转换builder
 * @author xiaowei
 *
 */
public class BusinessTagModelBuilder {
	
	public static TagGroupHandsetDetailVTO builderGroupUserDetailVTO(Map<String,Object> map){
		TagGroupHandsetDetailVTO vto = new TagGroupHandsetDetailVTO();
		vto.setHdMac((String)map.get("hdmac"));
		vto.setMobileno((String)map.get("mobileno"));
		vto.setCount((Long)map.get("count"));
		vto.setManu(MacDictParserFilterHelper.prefixMactch(vto.getHdMac(),true,false));
		vto.setFirstTime((String)map.get("min"));
		vto.setLastTime((String)map.get("max"));
		return vto;
	}
	
	public static TagGroupHandsetDetailDTO builderGroupUserDetailFilterVTO(Map<String,Object> map){
		TagGroupHandsetDetailDTO dto = new TagGroupHandsetDetailDTO();
		if(map.get("mobileno") == null){
			return null;
		}
		dto.setMobileno((String)map.get("mobileno"));
		return dto;
	}
	
	public static TagGroupRankUsersVTO builderGroupRankUsers(Map<String,String> map){
		TagGroupRankUsersVTO vto = new TagGroupRankUsersVTO();
		vto.setDate(map.get("date"));
		vto.setCount(map.get("count"));
		return vto;
	}
}
