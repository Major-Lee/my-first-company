package com.bhu.vas.di.op;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.di.business.datainit.DataInitStatisticsFragmentService;
/**
 * @author Edmond Lee
 *
 */
public class BuilderWifiOnlineHandsetDataInitOp {
	
	public static void main(String[] argv){
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		DataInitStatisticsFragmentService dataInitStatisticsFragmentService = (DataInitStatisticsFragmentService)ctx.getBean("dataInitStatisticsFragmentService");
		dataInitStatisticsFragmentService.doInit(700);
		System.exit(1);
	}
}
