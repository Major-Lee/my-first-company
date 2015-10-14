package com.bhu.vas.api.dto.ret;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.dto.vap.ModuleDTO;
import com.bhu.vas.api.dto.vap.RegisterDTO;

@SuppressWarnings("serial")
public class WifiDeviceVapReturnDTO implements Serializable{
	private RegisterDTO register;
	private List<ModuleDTO>  modules;
	public RegisterDTO getRegister() {
		return register;
	}
	public void setRegister(RegisterDTO register) {
		this.register = register;
	}
	public List<ModuleDTO> getModules() {
		return modules;
	}
	public void setModules(List<ModuleDTO> modules) {
		this.modules = modules;
	}
}
