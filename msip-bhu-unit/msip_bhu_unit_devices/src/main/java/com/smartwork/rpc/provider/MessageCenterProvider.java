package com.smartwork.rpc.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.smartwork.msip.cores.helper.ReflectionHelper;

public class MessageCenterProvider {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "MessageCenterRpcProviderApp");
		System.setProperty("provider.port", "20882");
		System.setProperty("deploy.conf.dir", "/Users/Edmond/Msip.smartwork.codespace/msip_bhu_business/msip-bhu-unit/msip_bhu_unit_devices/conf/");
		
		//Class<?> classz = Class.forName("com.alibaba.dubbo.container.Main");
		//ReflectionHelper.invokeStaticMethod("com.alibaba.dubbo.container.Main", "main", args);
		System.out.println("~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:/springunit/appCtxUnit.xml" });
		context.start();
		Thread.sleep(10000000000l);
		//System.out.println("Press any key to exit.");
		//System.in.read();
	}
}
