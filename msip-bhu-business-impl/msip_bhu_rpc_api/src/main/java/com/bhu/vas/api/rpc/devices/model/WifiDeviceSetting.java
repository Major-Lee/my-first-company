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
	public static final String VAPNAME_WLAN10 = "wlan10";
	public static final String VAPNAME_WLAN0 = "wlan0";
	
	public static final String VAPNAME_WLAN3 = "wlan3";
	public static final String VAPNAME_WLAN13 = "wlan13";
	public static final String AUTH_MODE_OPEN = "open";
	public static final String AUTH_MODE_WPA2 = "WPA2-PSK";
	
        //设置是否从设备上查询过配置(用户识别数据库中的记录是否系统生成，接着用户忽略设备的的首次恢复出厂)
        private boolean query_from_device = false;


        public boolean getQuery_from_device() {
                return query_from_device;
        }



        public void setQuery_from_device(boolean query_from_device) {
                this.query_from_device = query_from_device;
        }


	@Override
	public Class<WifiDeviceSettingDTO> getJsonParserModel() {
		return WifiDeviceSettingDTO.class;
	}
	
}
