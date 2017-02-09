package com.bhu.vas.api.dto.commdity;

import java.util.List;

@SuppressWarnings("serial")
public class TechServiceDataDTO implements java.io.Serializable{
	private List<String> macs;

	public List<String> getMacs() {
		return macs;
	}

	public void setMacs(List<String> macs) {
		this.macs = macs;
	}
	
	public static TechServiceDataDTO builder(List<String> macs){
		TechServiceDataDTO dto = new TechServiceDataDTO();
		dto.setMacs(macs);
		return dto;
	}
}
