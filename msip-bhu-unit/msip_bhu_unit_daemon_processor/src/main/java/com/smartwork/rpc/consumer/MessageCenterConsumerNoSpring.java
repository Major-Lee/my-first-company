package com.smartwork.rpc.consumer;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRpcService;

public class MessageCenterConsumerNoSpring {
	public static void main(String[] argv){
		// 当前应用配置
		ApplicationConfig application = new ApplicationConfig();
		application.setName("yyy");
		 
		// 连接注册中心配置
		RegistryConfig registry = new RegistryConfig();
		registry.setAddress("192.168.66.7:2181");
		registry.setProtocol("zookeeper");
		//registry.setUsername("aaa");
		//registry.setPassword("bbb");
		 
		// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
		 
		// 引用远程服务
		ReferenceConfig<IDeviceRpcService> reference = new ReferenceConfig<IDeviceRpcService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		reference.setApplication(application);
		reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
		reference.setInterface(IDeviceRpcService.class);
		//reference.setVersion("1.0.0");
		 
		// 和本地bean一样使用xxxService
		/*IDeviceRpcService deviceRpcService = reference.get(); // 注意：此代理对象内部封装了所有通讯细节，对象较重，请缓存复用
		DeviceDTO dto = new DeviceDTO();
		dto.setMac("34:36:3b:d0:4b:ac");
		dto.setT(System.currentTimeMillis());
		boolean ret = deviceRpcService.deviceRegister(dto, null);//.generateUserAccessToken(200082, true, true);
		System.out.println(ret);*/
	}
}
