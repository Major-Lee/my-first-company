package com.bhu.vas.api.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.vto.advertise.AdvertiseTrashPositionVTO;


public class AdvertiseHelper {
	
	public static List<AdvertiseTrashPositionVTO> buildAdvertiseTrashs(List<Advertise> advertises, Date nowDate) throws ParseException{
		
		List<AdvertiseTrashPositionVTO> trashVtos = new ArrayList<AdvertiseTrashPositionVTO>();
		
		for(Advertise ad : advertises){
			if(ad.getStart().getTime() <= nowDate.getTime()  &&  ad.getEnd().getTime() > nowDate.getTime()){
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
	
}
