package com.bhu.vas.api.helper;

public interface IGenerateDeviceSetting {
	
	//DeviceFacadeService implements this
	/**
	 * 生成设备配置的广告配置数据
	 * @param mac
	 * @param ds_opt 修改设备配置的ds_opt
	 * @param extparams 修改配置具体的参数
	 * @return
	 * @throws Exception 
	 */
	public String generateDeviceSetting(String mac, OperationDS ods, String extparams) throws Exception;
}
