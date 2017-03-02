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
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseCommentSortedSetService;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;
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
	
	public static AdvertiseVTO advertiseDocToVto(AdvertiseDocument doc){
		AdvertiseVTO vto = new AdvertiseVTO();
		vto.setId(doc.getId());
		vto.setTag(doc.getA_tag());
		vto.setUid(doc.getU_id());
		vto.setType(doc.getA_type());
		vto.setTitle(doc.getA_title());
		vto.setState(doc.getA_state());
		vto.setDescription(doc.getA_desc());
		vto.setProvince(doc.getA_province());
		vto.setCity(doc.getA_city());
		vto.setDistrict(doc.getA_district());
		if(doc.getA_geopoint() !=null){
			vto.setLat(doc.getA_geopoint()[0]);
			vto.setLon(doc.getA_geopoint()[1]);
		}
		vto.setDistance(doc.getA_distance());
		vto.setUrl(doc.getA_url());
		vto.setCount(doc.getA_count());
		vto.setCash(doc.getA_cash());
		SimpleDateFormat sdf = new SimpleDateFormat(DateTimeHelper.FormatPattern1);  
		try {
			if(doc.getA_start() !=null)
				vto.setStart(sdf.parse(doc.getA_start()));
			if(doc.getA_end() != null)
				vto.setEnd(sdf.parse(doc.getA_end()));
		} catch (ParseException e) {
		}
		vto.setDomain(doc.getA_domain());
		vto.setImage(doc.getA_image());
		vto.setExtparams(doc.getA_extparams());
		vto.setReject_reason(doc.getA_reject_reason());
		vto.setTop(doc.getA_top());
	}
}
