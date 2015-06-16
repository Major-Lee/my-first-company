package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;

/**
 * 设备配置变更的push消息
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceSettingChangedPushDTO extends PushDTO{

	public WifiDeviceSettingChangedPushDTO(String mac){
		super.setMac(mac);
	}

	@Override
	public String getPushType() {
		return PushType.WifiDeviceSettingChanged.getType();
	}
	
//	public static void main(String[] args){
//		WifiDeviceRebootPushDTO dto = new WifiDeviceRebootPushDTO("aa:aa:aa:aa:aa:aa", "6");
//		dto.setTs(System.currentTimeMillis());
//		System.out.println(JsonHelper.getJSONString(dto));
//	}
}
