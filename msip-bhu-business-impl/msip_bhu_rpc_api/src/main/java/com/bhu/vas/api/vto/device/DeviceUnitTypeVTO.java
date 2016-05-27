package com.bhu.vas.api.vto.device;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class DeviceUnitTypeVTO implements java.io.Serializable{
	private String index;
	//name
	private String n;
	
	//children
	@JsonInclude(Include.NON_NULL)
	private List<DeviceUnitTypeVTO> c;
	
	public DeviceUnitTypeVTO(String index, String name) {
		super();
		this.index = index;
		this.n = name;
	}
	
	public DeviceUnitTypeVTO(String index, String name,
			List<DeviceUnitTypeVTO> children) {
		super();
		this.index = index;
		this.n = name;
		this.c = children;
	}

	public String getIndex() {
		return index;
	}
	
	public void setIndex(String index) {
		this.index = index;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public List<DeviceUnitTypeVTO> getC() {
		return c;
	}

	public void setC(List<DeviceUnitTypeVTO> c) {
		this.c = c;
	}
	
}
