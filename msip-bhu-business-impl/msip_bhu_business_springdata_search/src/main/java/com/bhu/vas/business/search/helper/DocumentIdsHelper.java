package com.bhu.vas.business.search.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bhu.vas.business.search.model.WifiDeviceDocument;

public class DocumentIdsHelper {
	public static List<String> buildWifiDeviceDocumentIds(List<WifiDeviceDocument> documents){
		if(documents == null || documents.isEmpty()) return Collections.emptyList();
		
		List<String> ids = new ArrayList<String>();
		for(WifiDeviceDocument document : documents){
			if(document != null){
				ids.add(document.getId());
			}
		}
		
		return ids;
	}
}
