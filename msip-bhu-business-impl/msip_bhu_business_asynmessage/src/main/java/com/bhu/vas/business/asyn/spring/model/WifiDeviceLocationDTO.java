package com.bhu.vas.business.asyn.spring.model;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDeviceLocationDTO extends ActionDTO {
	private String lat;//连接的wifi mac
	private String lon;//移动设备的连接状态的持续时间
	private int loc_method;

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public int getLoc_method() {
		return loc_method;
	}

	public void setLoc_method(int loc_method) {
		this.loc_method = loc_method;
	}

	@Override
	public String getActionType() {
		return ActionMessageType.WifiDeviceLocation.getPrefix();
	}

}
