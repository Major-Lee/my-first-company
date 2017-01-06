package com.bhu.vas.business.search.model.advertise;

import com.bhu.vas.api.rpc.advertise.model.Advertise;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class AdvertiseDocumentHelper {
	public static AdvertiseDocument fromNormalAdvertise(Advertise advertise){
		AdvertiseDocument doc = new AdvertiseDocument();
		
		doc.setId(advertise.getId());
		doc.setA_title(advertise.getTitle());
		doc.setA_type(advertise.getType());
		doc.setA_desc(advertise.getDescription());
		doc.setA_image(advertise.getImage());
		doc.setA_url(advertise.getUrl());
		doc.setA_domain(advertise.getDomain());
		doc.setA_province(advertise.getProvince());
		doc.setA_city(advertise.getCity());
		doc.setA_district(advertise.getDistrict());
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
