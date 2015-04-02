package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;

import org.elasticsearch.ElasticsearchException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.di.business.datainit.DataInitStatisticsFragmentService;
import com.smartwork.msip.es.exception.ESException;
/**
 * @author Edmond Lee
 *
 */
public class BuilderWifiOnlineHandsetDataInitOp {
	
	public static void main(String[] argv) throws ElasticsearchException, ESException, IOException, ParseException{
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		DataInitStatisticsFragmentService dataInitStatisticsFragmentService = (DataInitStatisticsFragmentService)ctx.getBean("dataInitStatisticsFragmentService");
		dataInitStatisticsFragmentService.doInit(700);
		System.exit(1);
	}
}
