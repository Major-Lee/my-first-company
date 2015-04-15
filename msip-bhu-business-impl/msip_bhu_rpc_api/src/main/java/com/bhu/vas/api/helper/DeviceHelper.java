package com.bhu.vas.api.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRateControlDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;

public class DeviceHelper {
	
	public static List<String> builderSettingVapNames(List<WifiDeviceSettingVapDTO> vaps){
		if(vaps == null || vaps.isEmpty()) return Collections.emptyList();
		List<String> vapnames = new ArrayList<String>();
		for(WifiDeviceSettingVapDTO vap : vaps){
			vapnames.add(vap.getName());
		}
		return vapnames;
	}
	
	/**
	 * 匹配终端mac是否有限速设置
	 * @param dto 设备的配置dto
	 * @param mac 终端mac
	 * @return
	 */
	public static WifiDeviceSettingRateControlDTO matchRateControl(WifiDeviceSettingDTO dto, String mac){
		if(dto == null || StringUtils.isEmpty(mac)) return null;
		List<WifiDeviceSettingRateControlDTO> rateControls = dto.getRatecontrols();
		if(rateControls == null || rateControls.isEmpty()) return null;
		for(WifiDeviceSettingRateControlDTO rcDto : rateControls){
			if(mac.endsWith(rcDto.getMac())){
				return rcDto;
			}
		}
		return null;
	}
}
