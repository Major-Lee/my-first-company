package com.bhu.vas.business.search.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class AdvertiseDocumentHelper {
	
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
		return vto;
	}
	
	public static AdvertiseDocument fromNormalAdvertise(Advertise advertise,boolean isShamUser){
		AdvertiseDocument doc = new AdvertiseDocument();
		
		doc.setId(advertise.getId());
		doc.setA_title(advertise.getTitle());
		doc.setA_type(advertise.getType());
		doc.setA_tag(advertise.getTag());
		doc.setA_top(advertise.getTop());
		doc.setA_desc(advertise.getDescription());
		doc.setA_image(advertise.getImage());
		doc.setA_url(advertise.getUrl());
		doc.setA_domain(advertise.getDomain());
		doc.setA_province(advertise.getProvince());
		doc.setA_city(advertise.getCity());
		doc.setA_district(advertise.getDistrict());
		if(isShamUser)
			doc.setA_adcode(advertise.getAdcode());

		doc.setA_geopoint(new double[]{advertise.getLon(), 
				advertise.getLat()});
		doc.setA_distance(advertise.getDistance());
		doc.setA_count(advertise.getCount());
		if(advertise.getStart() !=null){
			doc.setA_start(DateTimeHelper.getDateTime(advertise.getStart(), DateTimeHelper.FormatPattern1));
		}
		if(advertise.getEnd() !=null){
			doc.setA_end(DateTimeHelper.getDateTime(advertise.getEnd(), DateTimeHelper.FormatPattern1));
		}
		if(advertise.getCash() !=null){
			doc.setA_cash(advertise.getCash());
		}
		doc.setA_duration(advertise.getDuration());
		doc.setA_abledevices_num(advertise.getAbleDevicesNum());
		doc.setA_state(advertise.getState());
		doc.setA_reject_reason(advertise.getReject_reason());
		doc.setA_verify_uid(advertise.getVerify_uid());
		doc.setA_extparams(advertise.getExtparams());
		doc.setA_created_at(DateTimeHelper.getDateTime(advertise.getCreated_at(), DateTimeHelper.FormatPattern1));
		
		doc.setU_id(advertise.getUid()+"");
		
		return doc;
	}

}
