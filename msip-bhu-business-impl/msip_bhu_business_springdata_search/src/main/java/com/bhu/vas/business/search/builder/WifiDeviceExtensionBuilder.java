package com.bhu.vas.business.search.builder;

import org.elasticsearch.common.lang3.StringUtils;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 用于构建设备搜索记录的d_extension字段数据
 * @author tangzichao
 *
 */
public class WifiDeviceExtensionBuilder {
	//访客网络类型数据
	public static final String Extension_SharedNetwork_Type_Prefix = "snkt_";
	
	/**
	 * 构建共享网络业务相关的数据项的扩展字段字符串
	 * @param paramSharedNetworkDto
	 * @return
	 */
	public static String builderSharedNetworkExtension(WifiDeviceSharedNetwork wifiDeviceSharedNetwork){
		String sharedNetwork_type_item = null;
		if(wifiDeviceSharedNetwork != null){
			String sharedNetwork_type = wifiDeviceSharedNetwork.getSharednetwork_type();
			if(StringUtils.isNotEmpty(sharedNetwork_type)){
				sharedNetwork_type_item = Extension_SharedNetwork_Type_Prefix.concat(sharedNetwork_type);
			}
		}
		return generateExtensionString(sharedNetwork_type_item);
	}
	
	/**
	 * 生成扩展字段字符串格式
	 * 每项之间以空格分隔
	 * @param extensionItems
	 * @return
	 */
	public static String generateExtensionString(String... extensionItems){
		StringBuffer extensionStringBuffer = new StringBuffer();
		if(extensionItems != null){
			for(String extensionItem : extensionItems){
				if(StringUtils.isNotEmpty(extensionItem)){
					if(extensionStringBuffer.length() > 0){
						extensionStringBuffer.append(StringHelper.WHITESPACE_STRING_GAP);
					}
					extensionStringBuffer.append(extensionItem);
				}
			}
		}
		return extensionStringBuffer.toString();
	}
}
