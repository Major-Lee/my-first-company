package com.bhu.vas.rpc.consumer;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;

/**
 * 点对点直连
 * @author Edmond
 *
 */
public class MessageCenterConsumerNoSpringWithServiceUrl {
	public static void main(String[] argv){
		// 当前应用配置
		ApplicationConfig application = new ApplicationConfig();
		application.setName("yyy");
		ReferenceConfig<IDeviceMessageDispatchRpcService> reference = new ReferenceConfig<IDeviceMessageDispatchRpcService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		// 如果点对点直连，可以用reference.setUrl()指定目标地址，设置url后将绕过注册中心，
		// 其中，协议对应provider.setProtocol()的值，端口对应provider.setPort()的值，
		// 路径对应service.setPath()的值，如果未设置path，缺省path为接口名
		reference.setApplication(application);
		reference.setInterface(IDeviceMessageDispatchRpcService.class);
		reference.setUrl("dubbo://127.0.0.1:20880/com.bhu.vas.api.rpc.devices.iservice.IDeviceRpcService"); 
		// 和本地bean一样使用xxxService
		/*IDeviceRpcService deviceRpcService = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
		DeviceDTO dto = new DeviceDTO();
		dto.setMac("34:36:3b:d0:4b:ac");
		dto.setT(System.currentTimeMillis());
		boolean ret = deviceRpcService.deviceRegister(dto, null);//.generateUserAccessToken(200082, true, true);
		System.out.println(ret);*/
	}
}
