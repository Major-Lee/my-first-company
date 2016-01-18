package com.bhu.vas.api.dto.ret.param;

import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamVasSwitchWorkmodeDTO{
	//切换名称
	private int wmode;
	public int getWmode() {
		return wmode;
	}
	public void setWmode(int wmode) {
		this.wmode = wmode;
	}
	/*public Object[] builderProperties() {
		Object[] properties = new Object[1];
		properties[0] = (WifiDeviceHelper.SwitchMode_Router2Bridge == wmode)?WifiDeviceHelper.WorkMode_Bridge:WifiDeviceHelper.WorkMode_Router;
		return properties;
	}*/
	
	
	
	public static void main(String[] argv){
		//http://7xpatx.dl1.z0.glb.clouddn.com/uRouter/smb.package.tar.gz
		ParamVasSwitchWorkmodeDTO dto = new ParamVasSwitchWorkmodeDTO();
		dto.setWmode(WifiDeviceHelper.SwitchMode_Router2Bridge_Act);
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
