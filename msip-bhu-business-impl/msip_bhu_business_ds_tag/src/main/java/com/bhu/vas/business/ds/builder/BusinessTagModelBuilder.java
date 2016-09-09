package com.bhu.vas.business.ds.builder;

import java.util.Map;

import com.bhu.vas.api.rpc.tag.vto.TagGroupHandsetDetailVTO;
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
}
