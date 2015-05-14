package com.bhu.vas.api.rpc.devices.model;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.smartwork.msip.cores.orm.model.extjson.SimpleJsonExtStringModel;

/**
 * 记录设备的配置信息
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceSetting extends SimpleJsonExtStringModel<WifiDeviceSettingDTO> {

	@Override
	public Class<WifiDeviceSettingDTO> getJsonParserModel() {
		return WifiDeviceSettingDTO.class;
	}
	
}
