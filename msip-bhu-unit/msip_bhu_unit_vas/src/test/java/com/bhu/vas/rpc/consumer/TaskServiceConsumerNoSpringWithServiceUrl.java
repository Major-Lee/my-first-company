package com.bhu.vas.rpc.consumer;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.api.rpc.task.iservice.ITaskRpcService;

/**
 * 点对点直连
 * @author Edmond
 *
 */
public class TaskServiceConsumerNoSpringWithServiceUrl {
	public static void main(String[] argv){
		// 当前应用配置
		ApplicationConfig application = new ApplicationConfig();
		application.setName("yyy");
		ReferenceConfig<ITaskRpcService> reference = new ReferenceConfig<ITaskRpcService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		// 如果点对点直连，可以用reference.setUrl()指定目标地址，设置url后将绕过注册中心，
		// 其中，协议对应provider.setProtocol()的值，端口对应provider.setPort()的值，
		// 路径对应service.setPath()的值，如果未设置path，缺省path为接口名
		reference.setApplication(application);
		reference.setInterface(ITaskRpcService.class);
		reference.setUrl("dubbo://192.168.1.4:20882/com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService"); 
		
		
		ITaskRpcService tokenRpcService = reference.get(); 
		ParserHeader parserHeader = new ParserHeader();
		parserHeader.setMt(0);
		parserHeader.setSt(1);
		parserHeader.setType(5);
		String message = "<join_req><ITEM orig_vendor=\"BHU\" hdtype=\"H104\" orig_model=\"BXO2000n(2S-Lite)\" orig_hdver=\"B1\" orig_swver=\"2015-03-11-18:27 Revision: 6855\" oem_vendor=\"BHU\" oem_model=\"BXO2000n(2S-Lite)\" oem_hdver=\"B1\" oem_swver=\"2015-03-11-18:27 Revision: 6855\" sn=\"AAA\" mac=\"62:68:75:02:00:06\" ip=\"192.168.66.176\" build_info=\"2015-03-11-18:27 Revision: 6855\" config_model_ver=\"V3\" config_mode=\"basic\" work_mode=\"router-ap\" config_sequence=\"14\" join_reason=\"0\" wan_ip=\"192.168.66.176\" /></join_req>";
		//CmCtxInfo info = new CmCtxInfo("cm001","1");
		//WifiDeviceContextDTO contextDto = new WifiDeviceContextDTO();
		//contextDto.setInfo(info);
		//contextDto.setMac("62:68:75:02:00:06");
		/*contextDto.setCmName("1");
		contextDto.setCmId("cm1");*/
		//tokenRpcService.messageDispatch("cm1", message, parserHeader);
	}
}
