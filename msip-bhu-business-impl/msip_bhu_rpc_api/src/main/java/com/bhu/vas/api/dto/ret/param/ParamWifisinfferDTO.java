package com.bhu.vas.api.dto.ret.param;

import com.smartwork.msip.cores.helper.JsonHelper;



/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamWifisinfferDTO{
	
	public static final String Start_Sta_Sniffer = "enable";
	public static final String Stop_Sta_Sniffer  = "disable";
	
	private String sta_sniffer;
	public String getSta_sniffer() {
		return sta_sniffer;
	}
	public void setSta_sniffer(String sta_sniffer) {
		this.sta_sniffer = sta_sniffer;
	}

	
	//{"sta_sniffer":"enable"}
	//{"sta_sniffer":"disable"}
	public static void main(String[] argv){
		ParamWifisinfferDTO dto = new ParamWifisinfferDTO();
		dto.setSta_sniffer("enable");
		//dto.setUrls("http://www.src1.com,http://www.dst1.com,http://src2.com,http://dst2.com");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
