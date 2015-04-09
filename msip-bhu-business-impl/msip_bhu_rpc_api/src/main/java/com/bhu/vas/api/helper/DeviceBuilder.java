package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;

public class DeviceBuilder {
	
	public static List<String> builderSettingVapNames(List<WifiDeviceSettingVapDTO> vaps){
		if(vaps == null || vaps.isEmpty()) return Collections.emptyList();
		List<String> vapnames = new ArrayList<String>();
		for(WifiDeviceSettingVapDTO vap : vaps){
			vapnames.add(vap.getName());
		}
		return vapnames;
	}
}
