package com.bhu.vas.api.vto.device;

import java.io.Serializable;

/**
 * Created by bluesand on 1/25/16.
 *
 * 参照 com.bhu.vas.business.search.model.WifiDeviceDocument注释
 */
@SuppressWarnings("serial")
public class UserDeviceStatisticsVTO implements Serializable {
	//全部数量
	private long to;
	//在线数量
	private long on;
	//离线数量
	private long of;
	
	public long getTo() {
		return to;
	}
	public void setTo(long to) {
		this.to = to;
	}
	public long getOn() {
		return on;
	}
	public void setOn(long on) {
		this.on = on;
	}
	public long getOf() {
		return of;
	}
	public void setOf(long of) {
		this.of = of;
	}
}
