package com.bhu.vas.rpc.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CommdityServiceProvider {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUCommditySP");
		System.setProperty("provider.port", "20990");
		System.setProperty("deploy.conf.dir", "/Users/tangzichao/work/bhuspace/msip_bhu_business/msip-bhu-unit/msip_bhu_unit_commdity/deploy/conf/");
		//Class<?> classz = Class.forName("com.alibaba.dubbo.container.Main");
		//ReflectionHelper.invokeStaticMethod("com.alibaba.dubbo.container.Main", "main", args);
		System.out.println("CommdityServiceProvider~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:/springunit/appCtxUnit.xml" });
		context.start();
		Thread.currentThread().join();
		//System.out.println("Press any key to exit.");
		//System.in.read();
	}
}
