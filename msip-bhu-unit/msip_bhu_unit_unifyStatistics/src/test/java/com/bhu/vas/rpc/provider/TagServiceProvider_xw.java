package com.bhu.vas.rpc.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TagServiceProvider_xw {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUStat");
		System.setProperty("provider.port", "20991");
		System.setProperty("deploy.conf.dir", "/Users/bluesand/Documents/bhu/msip_bhu_business/msip-bhu-unit/msip_bhu_unit_unifyStatistics/conf/");
		//Class<?> classz = Class.forName("com.alibaba.dubbo.container.Main");
		//ReflectionHelper.invokeStaticMethod("com.alibaba.dubbo.container.Main", "main", args);
		System.out.println("SocialServiceProvider~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:/springunit/appCtxUnit.xml" });
		context.start();
		Thread.currentThread().join();
		//System.out.println("Press any key to exit.");
		//System.in.read();
	}
}
