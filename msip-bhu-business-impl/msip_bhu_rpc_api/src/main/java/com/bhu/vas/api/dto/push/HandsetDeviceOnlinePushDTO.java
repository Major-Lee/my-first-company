package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 终端上线push dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class HandsetDeviceOnlinePushDTO extends PushDTO{
	//上线的终端的mac
	private String hd_mac;
	//终端别名或主机名
	private String n;
	//此终端是否第一次连接到此设备
	@JsonIgnore
	private boolean newed;
	
	public String getHd_mac() {
		return hd_mac;
	}

	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}

	public boolean isNewed() {
		return newed;
	}

	public void setNewed(boolean newed) {
		this.newed = newed;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	@Override
	public String getPushType() {
		return PushType.HandsetDeviceOnline.getType();
	}
	
//	public static void main(String[] args){
//		HandsetDeviceOnlinePushDTO dto = new HandsetDeviceOnlinePushDTO();
//		dto.setMac("aa:aa:aa:aa:aa:aa");
//		dto.setHd_mac("11:11:11:11:11:11");
//		dto.setN("law");
//		dto.setTs(System.currentTimeMillis());
//		System.out.println(JsonHelper.getJSONString(dto));
//	}
}
