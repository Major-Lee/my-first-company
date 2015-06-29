package com.bhu.vas.business.ds.builder;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.DevicesSet;

@SuppressWarnings("serial")
public class WifiStasnifferHelper implements Serializable{
	public static final String Community_Loser = "屌丝社区";
	public static final String Community_Higher = "高端社区";
	/**
	 * 定位周边社区的类型
	 * 目前 如果苹果手机占比超过50% 则是高端社区
	 * 其他均为屌丝社区
	 * @param communityCountByType
	 * @return
	 */
	public static String communityType(Map<String,String> communityCountByType){
		try{
			long total = 0l;
			Collection<String> collections = communityCountByType.values();
			for(String countByType : collections){
				total = total + Long.parseLong(countByType);
			}
			String apple_scn = DevicesSet.Apple.getScn();
			String apple_count = communityCountByType.get(apple_scn);
			if(!StringUtils.isEmpty(apple_count)){
				 double apple_ratio = ArithHelper.div(Long.parseLong(apple_count),total,1);
				 if(apple_ratio >= 0.5d){
					 return Community_Higher;
				 }
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return Community_Loser;
	}
	
	
/*	public static final String Special_Detail_Outer_Gap = "$#";
	public static final String Split_Special_Detail_Outer_Gap = "\\$#";
	
	*//**
	 * 根据WifistasnifferItemRddto生成detail数据
	 * @param item_dto
	 * @return
	 *//*
	public static String generateDetailItemValue(WifistasnifferItemRddto item_dto){
		if(item_dto != null){
			String hd_mac = item_dto.getMac();
			if(!StringUtils.isEmpty(hd_mac)){
				StringBuffer item_value = new StringBuffer();
				item_value.append(hd_mac);
				item_value.append(Special_Detail_Outer_Gap);
				item_value.append(item_dto.getState());
				item_value.append(Special_Detail_Outer_Gap);
				item_value.append(item_dto.getSnifftime());
				if(!item_dto.isOnline()){
					item_value.append(Special_Detail_Outer_Gap);
					item_value.append(item_dto.getDuration());
				}
				return item_value.toString();
			}
		}
		return null;
	}
	
	*//**
	 * 根据WifistasnifferItemRddto生成上线detail数据
	 * @param item_dto
	 * @return
	 *//*
	public static String generateDetailItemOnlineValue(WifistasnifferItemRddto item_dto){
		if(item_dto != null){
			String hd_mac = item_dto.getMac();
			if(!StringUtils.isEmpty(hd_mac)){
				StringBuffer item_value = new StringBuffer();
				item_value.append(hd_mac);
				item_value.append(Special_Detail_Outer_Gap);
				item_value.append(WifistasnifferItemRddto.State_Online);
				item_value.append(Special_Detail_Outer_Gap);
				item_value.append(item_dto.getSnifftime());
				return item_value.toString();
			}
		}
		return null;
	}*/
}
