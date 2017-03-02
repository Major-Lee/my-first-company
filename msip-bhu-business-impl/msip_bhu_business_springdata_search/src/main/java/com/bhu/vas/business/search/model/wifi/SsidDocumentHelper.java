package com.bhu.vas.business.search.model.wifi;

import com.bhu.vas.api.rpc.wifi.model.SsidInfo;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class SsidDocumentHelper {
	public static SsidDocument fromNormalSsid(SsidInfo ssid){
		SsidDocument doc = new SsidDocument();
		
		doc.setId(ssid.getId());
		doc.setS_bssid(ssid.getBssid());
		doc.setS_ssid(ssid.getSsid());
		doc.setS_pwd(ssid.getPwd());
		doc.setS_device(ssid.getDevice());
		doc.setS_geopoint(new double[]{ssid.getLon(), 
				ssid.getLat()});
		doc.setS_created_at(DateTimeHelper.getDateTime(ssid.getCreated_at(), DateTimeHelper.FormatPattern1));
		doc.setS_updated_at(DateTimeHelper.getDateTime(ssid.getUpdated_at(), DateTimeHelper.FormatPattern1));
		return doc;
	}
}
