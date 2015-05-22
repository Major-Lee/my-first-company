package com.bhu.vas.api.dto.ret.setting.param;

import com.smartwork.msip.cores.helper.JsonHelper;


/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamVapHttpRedirectDTO{
	
	private String enable;
	//重定向次数
	private int times = 1;
	//开始时间
	private String start_time;
	//结束时间
	private String end_time;
	//url 逗号,分割
	//private String urls;
	private String style;
	/*@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[2];
		properties[0] = enable;
		
		StringBuilder sb = new StringBuilder();
		sb.append(times).append(StringHelper.COMMA_STRING_GAP)
			.append(start_time).append(StringHelper.COMMA_STRING_GAP)
			.append(end_time).append(StringHelper.COMMA_STRING_GAP)
			.append(urls);
		properties[1] = sb.toString();
		return properties;
	}
	
	@Override
	public Object[] builderProperties(int type) {
		return builderProperties();
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}*/
	
	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}


	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}


	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	/*public String getUrls() {
		return urls;
	}

	public void setUrls(String urls) {
		this.urls = urls;
	}*/
//{"enable":"enable","times":10,"start_time":"20:00","end_time":"21:00","urls":"http://www.src1.com,http://www.dst1.com,http://src2.com,http://dst2.com"}
	public static void main(String[] argv){
		ParamVapHttpRedirectDTO dto = new ParamVapHttpRedirectDTO();
		dto.setTimes(10);
		dto.setStart_time("20:00");
		dto.setEnd_time("21:00");
		dto.setStyle("style0001");
		//dto.setUrls("http://www.src1.com,http://www.dst1.com,http://src2.com,http://dst2.com");
		dto.setEnable("enable");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
