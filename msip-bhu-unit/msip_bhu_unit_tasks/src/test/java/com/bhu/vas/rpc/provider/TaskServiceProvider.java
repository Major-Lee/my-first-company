package com.bhu.vas.rpc.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TaskServiceProvider {
	public static void main(String[] args) throws Exception {
		System.setProperty("appname", "BHUTasksSP");
		System.setProperty("provider.port", "20888");
		System.setProperty("deploy.conf.dir", "/Users/bluesand/Documents/bhu/msip_core/msip_core_unit_test/conf/");
		//Class<?> classz = Class.forName("com.alibaba.dubbo.container.Main");
		//ReflectionHelper.invokeStaticMethod("com.alibaba.dubbo.container.Main", "main", args);
		System.out.println("TaskServiceProvider~~~~~~~~~~~~~:"+System.getProperty("provider.port"));
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:/springunit/appCtxUnit.xml" });
		context.start();
		Thread.currentThread().join();
		//System.out.println("Press any key to exit.");
		//System.in.read();
	}
}
