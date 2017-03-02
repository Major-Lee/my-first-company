package com.bhu.vas.api.helper;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;


public class AdvertiseHelper {
	
	public static List<AdvertiseTrashPositionVTO> buildAdvertiseTrashs(List<Advertise> advertises, Date nowDate,boolean flag) throws ParseException{
		
		List<AdvertiseTrashPositionVTO> trashVtos = new ArrayList<AdvertiseTrashPositionVTO>();
		
		for(Advertise ad : advertises){
			if(flag){
				if(ad.getStart().getTime() <= nowDate.getTime()  &&  ad.getEnd().getTime() > nowDate.getTime()){
					AdvertiseTrashPositionVTO trashVto = new AdvertiseTrashPositionVTO();
					trashVto.setDistrict(ad.getDistrict());
					trashVto.setCity(ad.getCity());
					trashVto.setProvince(ad.getProvince());
					trashVtos.add(trashVto);
					System.out.println("trash:" +ad.getProvince()+ad.getCity()+ad.getDistrict());
				}
			}else{
				AdvertiseTrashPositionVTO trashVto = new AdvertiseTrashPositionVTO();
				trashVto.setDistrict(ad.getDistrict());
				trashVto.setCity(ad.getCity());
				trashVto.setProvince(ad.getProvince());
				trashVtos.add(trashVto);
				System.out.println("trash:" +ad.getProvince()+ad.getCity()+ad.getDistrict());
			}
		}
		return trashVtos;
	}
	
	public static List<String> chineseInitialSort(List<String> list){
		  Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
		  String [] arrStrings = ArrayHelper.toStringArray(list);
		  // 使根据指定比较器产生的顺序对指定对象数组进行排序。
		  Arrays.sort(arrStrings, comparator);
		  return ArrayHelper.toList(arrStrings);
	}
	
}
