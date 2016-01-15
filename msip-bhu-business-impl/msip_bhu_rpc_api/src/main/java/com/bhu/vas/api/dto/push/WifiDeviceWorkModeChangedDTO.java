package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;

/**
 * 设备切换工作模式上线后的push消息
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceWorkModeChangedDTO extends PushDTO{
	//设备工作模式
	private String wm;
	
	public WifiDeviceWorkModeChangedDTO(String mac, String workMode){
		super.setMac(mac);
		this.wm = workMode;
	}
	
	public String getWm() {
		return wm;
	}

	public void setWm(String wm) {
		this.wm = wm;
	}

	@Override
	public String getPushType() {
		return PushType.WifiDeviceWorkModeChanged.getType();
	}
	
//	public static void main(String[] args){
//		WifiDeviceRebootPushDTO dto = new WifiDeviceRebootPushDTO("aa:aa:aa:aa:aa:aa", "6");
//		dto.setTs(System.currentTimeMillis());
//		System.out.println(JsonHelper.getJSONString(dto));
//	}
}
