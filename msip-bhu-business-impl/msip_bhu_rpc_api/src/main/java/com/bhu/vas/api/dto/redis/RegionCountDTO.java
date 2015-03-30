package com.bhu.vas.api.dto.redis;


/**
 * 地域对应的wifi设备数量DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class RegionCountDTO implements java.io.Serializable{
	private String r;//地域名称
	private long v;//wifi设备数量
	
	public String getR() {
		return r;
	}
	public void setR(String r) {
		this.r = r;
	}
	public long getV() {
		return v;
	}
	public void setV(long v) {
		this.v = v;
	}
}
