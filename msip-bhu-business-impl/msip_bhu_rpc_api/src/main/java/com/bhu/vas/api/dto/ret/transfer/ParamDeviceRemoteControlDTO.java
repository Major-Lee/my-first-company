package com.bhu.vas.api.dto.ret.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 设备远程透传指令参数
 * @author edmond
 *
 */
public class ParamDeviceRemoteControlDTO{
	@JsonInclude(Include.NON_NULL)
	private String sender;
	@JsonInclude(Include.NON_NULL)
	private String load_path;
	@JsonInclude(Include.NON_NULL)
	private String device_path;
	@JsonInclude(Include.NON_NULL)
	private String file_type;
	@JsonInclude(Include.NON_NULL)
	private String param;
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getLoad_path() {
		return load_path;
	}
	public void setLoad_path(String load_path) {
		this.load_path = load_path;
	}
	public String getDevice_path() {
		return device_path;
	}
	public void setDevice_path(String device_path) {
		this.device_path = device_path;
	}
	public String getFile_type() {
		return file_type;
	}
	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}

	//{"sta_sniffer":"enable"}
	//{"sta_sniffer":"disable"}
	public static void main(String[] argv){
		ParamDeviceRemoteControlDTO dto = new ParamDeviceRemoteControlDTO();
		dto.setSender("BMS");
		dto.setFile_type("firmware");
		dto.setLoad_path("abcdefg");
		//dto.setSta_sniffer("enable");
		//dto.setUrls("http://www.src1.com,http://www.dst1.com,http://src2.com,http://dst2.com");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
