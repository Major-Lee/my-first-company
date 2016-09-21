package com.bhu.vas.pa;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.smartwork.msip.plugins.hook.ShutdownHookThread;

public class WanganMain {
    
	public static void main(String[] args) {
		String[] CONFIG = {"/spring/appCtxBackend.xml"};
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG, WanganMain.class);
		context.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(context,"WanganMain Server"));
	}
}