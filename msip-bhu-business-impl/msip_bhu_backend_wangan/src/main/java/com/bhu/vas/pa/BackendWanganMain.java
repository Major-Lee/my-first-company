package com.bhu.vas.pa;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.smartwork.msip.plugins.hook.ShutdownHookThread;

public class BackendWanganMain {
    
	public static void main(String[] args) {
		String[] CONFIG = {"/spring/appCtxBackend.xml"};
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG, BackendWanganMain.class);
		context.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(context,"WanganMain Server"));
	}
}