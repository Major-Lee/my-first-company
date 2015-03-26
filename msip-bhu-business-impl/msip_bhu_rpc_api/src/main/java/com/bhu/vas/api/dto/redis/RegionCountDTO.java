package com.bhu.vas.api.dto.redis;

import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 地域对应的wifi设备数量DTO
 * @author tangzichao
 *
 */
public class RegionCountDTO {
	private String r;//地域名称
	private String v;//显示内容 格式是 2000 15%
	
	public String getR() {
		return r;
	}
	public void setR(String r) {
		this.r = r;
	}
	public String getV() {
		return v;
	}
	public void setV(String v) {
		this.v = v;
	}
	
	public static String builderValue(long region_count, long count){
		StringBuffer v_sb = new StringBuffer();
		v_sb.append(region_count);
		v_sb.append(StringHelper.WHITESPACE_STRING_GAP);
		v_sb.append(ArithHelper.percent(region_count, count, 2));
		return v_sb.toString();
	}
}
