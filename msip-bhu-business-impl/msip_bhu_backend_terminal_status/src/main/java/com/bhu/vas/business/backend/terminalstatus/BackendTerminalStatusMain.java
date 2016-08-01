package com.bhu.vas.business.backend.terminalstatus;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service.BusinessHelper;
import com.bhu.vas.business.backend.terminalstatus.asyncprocessor.service.Step00ReadSimulateLogService;
import com.smartwork.msip.plugins.hook.ShutdownHookThread;

public class BackendTerminalStatusMain {
	public static void main(String[] args) throws InterruptedException {
		//String[] locations = {"classpath*:/springtest/testCtx.xml"};//,"classpath:springmq/applicationContext-activemq-server.xml", "classpath:springmq/applicationContext-activemq-message-consumer.xml"};
		//String[] locations = {"classpath*:/spring/appCtxBackend.xml"};
		//ApplicationContext ctx = new FileSystemXmlApplicationContext(locations);//("classpath*:/springtest/testCtx.xml");//"classpath*:springfeed/applicationContext-activemq-consumer.xml");//"classpath:springtest/testCtx.xml");
//		String mac = "84:82:f4:28:87:94";
//		String hdMac = "0c:77:1a:5c:b2:c2";
//		String newAddFields = UserOrderDetailsHashService.getInstance().fetchUserOrderDetail(mac, hdMac);
//		if(newAddFields.isEmpty()){
//			System.out.println(newAddFields);
//		}
//		OrderUserAgentDTO addMsg = JsonHelper.getDTO(newAddFields, OrderUserAgentDTO.class);
//		System.out.println(JsonHelper.getJSONString(addMsg));
//		System.out.println(addMsg.getWan_ip());
//		System.out.println(addMsg.getIp());
		
		String[] CONFIG = {"/spring/appCtxBackend.xml"};
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG, BackendTerminalStatusMain.class);
		context.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(context,"BackendTerminalStatusMain Server"));
		    
//		PortraitMemcachedCacheService portraitMemcachedCacheService = (PortraitMemcachedCacheService)context.getBean("portraitMemcachedCacheService");
		Step00ReadSimulateLogService step00ReadSimulateLogService = (Step00ReadSimulateLogService)context.getBean("step00ReadSimulateLogService");
//		System.out.println(portraitMemcachedCacheService.getPortraitOrderCacheByOrderId("Hello"));
		//123 147   
		//String path1 = "/BHUData/bulogs/reportinglogs";
//		String path2 = "/BHUData/bulogs/reportinglogsnew/i2";    
//		String path1 = "E:/onORoff/i1";
		String path2 = "E:/onORoff/i2";
//		
//		Vivi_portrait
//		String path1 = "/BHUData/bulogs/reporterlogsnew/i1";
//		String path2 = "/BHUData/bulogs/reporterlogsnew/i2";
		
//		SimpleDateFormat formatter = new SimpleDateFormat("ss");//初始化Formatter的转换格式。  
		String date = BusinessHelper.getCurrentPreviousMinuteString(1);
//		System.out.println(DateTimeHelper.getDateTime()+"   do   "+date+"   path1 parser log starting ......");
//		 long starTime=System.currentTimeMillis();
		//step00ReadSimulateLogService.parser(date, path1);
//		long endTime=System.currentTimeMillis();
//		long Time=endTime-starTime;
//		String hms = formatter.format(Time); 
//		System.out.println(hms+"second do "+date+"  path1 parser log ending ......");
//		System.out.println(DateTimeHelper.getDateTime()+"   do   "+date+"   path2 parser log starting ......");
//		starTime=System.currentTimeMillis();
		step00ReadSimulateLogService.parser(date, path2);
//		endTime=System.currentTimeMillis();
//		Time=endTime-starTime;
//		  
//		hms = formatter.format(Time); 
//		System.out.println(hms+"  do "+date+"  path2 parser log ending.....");
		System.exit(0);
		//context.close();
	}
}
