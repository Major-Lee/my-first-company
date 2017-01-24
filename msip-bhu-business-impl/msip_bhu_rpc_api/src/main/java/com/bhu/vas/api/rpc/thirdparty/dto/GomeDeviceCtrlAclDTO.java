package com.bhu.vas.api.rpc.thirdparty.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class GomeDeviceCtrlAclDTO implements java.io.Serializable{
	@JsonInclude(Include.NON_NULL)
	private GomeDeviceCtrlAclMacsDTO incr;
	@JsonInclude(Include.NON_NULL)
	private GomeDeviceCtrlAclMacsDTO del;
	public GomeDeviceCtrlAclMacsDTO getIncr() {
		return incr;
	}
	public void setIncr(GomeDeviceCtrlAclMacsDTO incr) {
		this.incr = incr;
	}
	public GomeDeviceCtrlAclMacsDTO getDel() {
		return del;
	}
	public void setDel(GomeDeviceCtrlAclMacsDTO del) {
		this.del = del;
	}
	
	public static GomeDeviceCtrlAclDTO builder(String cmd, List<String> macs){
		GomeDeviceCtrlAclDTO dto = new GomeDeviceCtrlAclDTO();
		if (cmd.equals("incr")){
			dto.setIncr(GomeDeviceCtrlAclMacsDTO.builder(macs));
		}
		if (cmd.equals("del")){
			dto.setDel(GomeDeviceCtrlAclMacsDTO.builder(macs));
		}
		return dto;
		
	}
}
