package com.bhu.vas.api.helper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;

@SuppressWarnings("serial")
public class WifiStasnifferBuilder implements Serializable{
	
	public static final String Special_Detail_Outer_Gap = "$#";
	public static final String Split_Special_Detail_Outer_Gap = "\\$#";
	
	/**
	 * 根据WifistasnifferItemRddto生成detail数据
	 * @param item_dto
	 * @return
	 */
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
	
	/**
	 * 根据WifistasnifferItemRddto生成上线detail数据
	 * @param item_dto
	 * @return
	 */
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
	}
}
