package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;

/**
 * 设备重启成功后的push消息
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceRebootPushDTO extends PushDTO{
	//设备加入原因 
	private String join_reason;
	
	public WifiDeviceRebootPushDTO(String mac, String join_reason){
		super.setMac(mac);
		this.join_reason = join_reason;
	}
	
	public String getJoin_reason() {
		return join_reason;
	}

	public void setJoin_reason(String join_reason) {
		this.join_reason = join_reason;
	}

	@Override
	public String getPushType() {
		return PushType.WifiDeviceReboot.getType();
	}
	
}
