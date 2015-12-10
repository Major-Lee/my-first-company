package com.bhu.vas.business.asyn.spring.model;

import java.util.List;

import com.bhu.vas.business.asyn.spring.builder.ActionDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

public class WifiDevicesGrayChangedDTO extends ActionDTO {
	private String dut;
	private int gl;
	private List<String> macs;

	public String getDut() {
		return dut;
	}

	public void setDut(String dut) {
		this.dut = dut;
	}

	public int getGl() {
		return gl;
	}

	public void setGl(int gl) {
		this.gl = gl;
	}

	public List<String> getMacs() {
		return macs;
	}

	public void setMacs(List<String> macs) {
		this.macs = macs;
	}


	@Override
	public String getActionType() {
		return ActionMessageType.WifiDevicesGrayChanged.getPrefix();
	}
}
