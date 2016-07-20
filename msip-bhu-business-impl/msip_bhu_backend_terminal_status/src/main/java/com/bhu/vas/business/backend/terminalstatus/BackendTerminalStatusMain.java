package com.bhu.vas.business.backend.terminalstatus;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service.BusinessHelper;
import com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service.PortraitMemcachedCacheService;
import com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service.Step00ReadSimulateLogService;
import com.smartwork.msip.plugins.hook.ShutdownHookThread;

public class BackendTerminalStatusMain {
	public static void main(String[] args) throws InterruptedException {
		//String[] locations = {"classpath*:/springtest/testCtx.xml"};//,"classpath:springmq/applicationContext-activemq-server.xml", "classpath:springmq/applicationContext-activemq-message-consumer.xml"};
		//String[] locations = {"classpath*:/spring/appCtxBackend.xml"};
		//ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
		String[] CONFIG = {"/spring/appCtxBackend.xml"};
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG, BackendTerminalStatusMain.class);
		context.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(context,"BackendTerminalStatusMain Server"));
		
		
		PortraitMemcachedCacheService portraitMemcachedCacheService = (PortraitMemcachedCacheService)context.getBean("portraitMemcachedCacheService");
		Step00ReadSimulateLogService step00ReadSimulateLogService = (Step00ReadSimulateLogService)context.getBean("step00ReadSimulateLogService");
		

		//upays
//		String path1 = "/BHUData/bulogs/reportinglogsnew/i1";
//		String path2 = "/BHUData/bulogs/reportinglogsnew/i2";
//		String path1 = "E:/onORoff/i1";
//		String path2 = "E:/onORoff/i2";
		
//		Vivi_portrait
		String path1 = "/BHUData/bulogs/reporterlogsnew/i1";
		String path2 = "/BHUData/bulogs/reporterlogsnew/i2";
		
		String date = BusinessHelper.getCurrentPreviousMinuteString(2);
		portraitMemcachedCacheService.storePortraitCacheResult("dddddddddddd", path1);
		String ddddddddddd = portraitMemcachedCacheService.getPortraitOrderCacheByOrderId("dddddddddddd");
		System.out.println(ddddddddddd);
		step00ReadSimulateLogService.parser(date, path1);
		step00ReadSimulateLogService.parser(date, path2);
		System.exit(0);
	}
}
