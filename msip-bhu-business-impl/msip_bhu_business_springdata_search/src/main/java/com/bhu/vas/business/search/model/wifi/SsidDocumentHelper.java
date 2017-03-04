package com.bhu.vas.business.search.model.wifi;

import com.bhu.vas.api.rpc.wifi.model.SsidInfo;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class SsidDocumentHelper {
	public static SsidDocument fromNormalSsid(SsidInfo ssid){
		SsidDocument doc = new SsidDocument();
		
		doc.setId(ssid.getId());
		doc.setS_ssid(ssid.getSsid());
		doc.setS_mode(ssid.getMode());
		doc.setS_pwd(ssid.getPwd());
		doc.setS_device(ssid.getDevice());
		doc.setS_geopoint(new double[]{ssid.getLon(), 
				ssid.getLat()});
		doc.setS_created_at(DateTimeHelper.getDateTime(ssid.getCreated_at(), DateTimeHelper.FormatPattern1));
		doc.setS_updated_at(DateTimeHelper.getDateTime(ssid.getUpdated_at(), DateTimeHelper.FormatPattern1));
		return doc;
	}
	
	public static SsidInfo toSsid(SsidDocument doc){
		SsidInfo ssid = new SsidInfo();
		ssid.setId(doc.getId());
		ssid.setSsid(doc.getS_ssid());
		ssid.setMode(doc.getS_mode());
		ssid.setPwd(doc.getS_pwd());
		ssid.setDevice(doc.getS_device());
		double[] geo = doc.getS_geopoint();
		if(geo != null && geo.length > 1){
			ssid.setLat(geo[1]);
			ssid.setLon(geo[0]);
		}
		ssid.setUpdated_at(DateTimeHelper.parseDate(doc.getS_updated_at(), DateTimeHelper.FormatPattern1));
		ssid.setCreated_at(DateTimeHelper.parseDate(doc.getS_created_at(), DateTimeHelper.FormatPattern1));
		return ssid;
	}

}
