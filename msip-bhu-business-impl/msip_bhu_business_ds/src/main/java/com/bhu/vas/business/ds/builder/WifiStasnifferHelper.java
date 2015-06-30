package com.bhu.vas.business.ds.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.vto.URouterWSCommunityHDVTO;
import com.bhu.vas.api.vto.URouterWSCommunityVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.wifistasniffer.TerminalDeviceTypeCountHashService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.DevicesSet;

@SuppressWarnings("serial")
public class WifiStasnifferHelper implements Serializable{
	public static final String Community_Loser = "屌丝社区";
	public static final String Community_Higher = "高端社区";
	/**
	 * 定位周边社区的类型
	 * 目前 如果苹果手机占比超过30% 则是高端社区
	 * 其他均为屌丝社区
	 * @param communityCountByType
	 * @return
	 */
	public static URouterWSCommunityVTO communityType(Map<String,String> communityCountByTypes){
		try{
			//计算总数量
			long total = 0l;
			Collection<String> values = communityCountByTypes.values();
			for(String value : values){
				total = total + Long.parseLong(value);
			}
			//排序过程排除unknow数据
			String unknow_count = communityCountByTypes.get(DevicesSet.Unknow.getScn());
			if(StringUtils.isEmpty(unknow_count)){
				unknow_count = "0";
			}else{
				communityCountByTypes.remove(DevicesSet.Unknow.getScn());
			}

			//进行排序
			List<Map.Entry<String, String>> communityCountByTypeList = new ArrayList<Map.Entry<String, String>>(
					communityCountByTypes.entrySet());
			
			// 对HashMap中的 value desc 进行排序  
	        Collections.sort(communityCountByTypeList, new Comparator<Map.Entry<String, String>>() {  
	            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {  
	                return Integer.parseInt(o2.getValue()) - Integer.parseInt(o1.getValue());  
	            }
	        });  
	        
	        List<URouterWSCommunityHDVTO> hdts = new ArrayList<URouterWSCommunityHDVTO>();
	        // put the sorted map  
	        for (Entry<String, String> entry : communityCountByTypeList) {
	        	URouterWSCommunityHDVTO hdt_vto = new URouterWSCommunityHDVTO();
	        	hdt_vto.setHd_tn(entry.getKey());
	        	long hd_tc = Long.parseLong(entry.getValue());
	        	hdt_vto.setHd_tc(hd_tc);
	        	hdt_vto.setHd_tr(ArithHelper.percent(hd_tc, total, 0));
	        	hdts.add(hdt_vto);
	        }
	        //加入unknow数据在列表最后
        	URouterWSCommunityHDVTO unknow_hdt_vto = new URouterWSCommunityHDVTO();
        	unknow_hdt_vto.setHd_tn(DevicesSet.Unknow.getScn());
        	long unknow_hd_tc = Long.parseLong(unknow_count);
        	unknow_hdt_vto.setHd_tc(unknow_hd_tc);
        	unknow_hdt_vto.setHd_tr(ArithHelper.percent(unknow_hd_tc, total, 0));
        	hdts.add(unknow_hdt_vto);
	        
        	URouterWSCommunityVTO vto = new URouterWSCommunityVTO();
        	vto.setCt(Community_Higher);
        	vto.setHdts(hdts);
        	return vto;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
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
	
	public static void main(String[] args){
		Map<String, String> communityCountByTypes = TerminalDeviceTypeCountHashService.getInstance().getAll("84:82:f4:19:01:0c");
		communityType(communityCountByTypes);
	}
}
