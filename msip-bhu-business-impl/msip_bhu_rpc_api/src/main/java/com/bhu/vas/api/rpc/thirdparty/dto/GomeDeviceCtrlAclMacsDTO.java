package com.bhu.vas.api.rpc.thirdparty.dto;

import java.util.List;

@SuppressWarnings("serial")
public class GomeDeviceCtrlAclMacsDTO implements java.io.Serializable{
	List<String> macs;

	public List<String> getMacs() {
		return macs;
	}

	public void setMacs(List<String> macs) {
		this.macs = macs;
	}
	
	public static GomeDeviceCtrlAclMacsDTO builder(List<String> macs){
		GomeDeviceCtrlAclMacsDTO dto = new GomeDeviceCtrlAclMacsDTO();
		dto.setMacs(macs);
		return dto;
	}
}
