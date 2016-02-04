package com.bhu.vas.rpc.provider;


public class VasServiceProviderNoSpring {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUDevicesRpcProviderApp");
		System.setProperty("provider.port", "20882");
		System.setProperty("deploy.conf.dir", "/Users/Edmond/Msip.smartwork.codespace/msip_bhu_business/msip-bhu-unit/msip_bhu_unit_devices/conf/");
		
		// 服务实现
		/*ITaskRpcService taskRpcService = new TaskRpcService();
		 
		// 当前应用配置
		ApplicationConfig application = new ApplicationConfig();
		application.setName("xxx");
		 
		// 连接注册中心配置
		RegistryConfig registry = new RegistryConfig();
		registry.setAddress("192.168.66.7:2181");
		registry.setProtocol("zookeeper");
		//registry.setUsername("aaa");
		//registry.setPassword("bbb");
		 
		// 服务提供者协议配置
		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setName("dubbo");
		protocol.setPort(20882);
		protocol.setThreads(50);
		 
		// 注意：ServiceConfig为重对象，内部封装了与注册中心的连接，以及开启服务端口
		 
		// 服务提供者暴露服务配置
		ServiceConfig<ITaskRpcService> service = new ServiceConfig<ITaskRpcService>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
		service.setApplication(application);
		service.setRegistry(registry); // 多个注册中心可以用setRegistries()
		service.setProtocol(protocol); // 多个协议可以用setProtocols()
		service.setInterface(ITaskRpcService.class);
		service.setRef(taskRpcService);
		//service.setVersion("1.0.0");
		 
		// 暴露及注册服务
		service.export();*/
		
		Thread.sleep(1000000000l);
	}
}
