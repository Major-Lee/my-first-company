package com.bhu.vas.rpc.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RestfulPaymentServiceProvider {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHURestfulPaymentSP");
		System.setProperty("provider.port", "8080");
		System.setProperty("deploy.conf.dir", "/Users/Edmond/Msip.smartwork.codespace/msip_bhu_business/msip-bhu-unit/msip_bhu_unit_restful_payment/conf/");
		//Class<?> classz = Class.forName("com.alibaba.dubbo.container.Main");
		//ReflectionHelper.invokeStaticMethod("com.alibaba.dubbo.container.Main", "main", args);
		System.out.println("BHURestfulPaymentSP~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:/springunit/appCtxUnit.xml" });
		context.start();
		Thread.currentThread().join();
		//System.out.println("Press any key to exit.");
		//System.in.read();
	}
}
