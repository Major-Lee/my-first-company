package com.bhu.vas.api.vto.device;

import java.util.List;

@SuppressWarnings("serial")
public class CurrentGrayUsageVTO implements java.io.Serializable{
	//当前的灰度列表
	private List<GrayUsageVTO> guvs;
	//当前有效的固件版本列表
	private List<VersionVTO> fws;
	//当前有效的增值运营组件版本
	private List<VersionVTO> oms;
	public List<GrayUsageVTO> getGuvs() {
		return guvs;
	}
	public void setGuvs(List<GrayUsageVTO> guvs) {
		this.guvs = guvs;
	}
	public List<VersionVTO> getFws() {
		return fws;
	}
	public void setFws(List<VersionVTO> fws) {
		this.fws = fws;
	}
	public List<VersionVTO> getOms() {
		return oms;
	}
	public void setOms(List<VersionVTO> oms) {
		this.oms = oms;
	}
	
}
