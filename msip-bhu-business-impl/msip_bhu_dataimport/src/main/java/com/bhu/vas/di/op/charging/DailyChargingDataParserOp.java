package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.di.business.datainit.charging.Step0ParserLogService;
import com.bhu.vas.di.business.datainit.charging.Step1Result2FileService;
import com.bhu.vas.di.business.datainit.charging.Step2DeviceWholeDayRecordService;
import com.bhu.vas.di.business.datainit.charging.Step3HandsetWholeDayRecordService;

/**
 * 每日必须进行状态保活，每天凌晨需要把在线的设备重新写入到日志中，作为模拟登录，模拟登录的时间缺省为当日的起始时间，防止漏算了长时间在线的设备
 * 对于终端则无需这样
 * @author Edmond
 *
 */
public class DailyChargingDataParserOp {

	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		String date = "2015-09-11";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		Step0ParserLogService step0ParserLogService = (Step0ParserLogService)ctx.getBean("step0ParserLogService");
		Step1Result2FileService step1Result2FileService = (Step1Result2FileService)ctx.getBean("step1Result2FileService");
		Step2DeviceWholeDayRecordService step2DeviceWholeDayRecordService = (Step2DeviceWholeDayRecordService)ctx.getBean("step2DeviceWholeDayRecordService");
		
		Step3HandsetWholeDayRecordService step3HandsetWholeDayRecordService = (Step3HandsetWholeDayRecordService)ctx.getBean("step3HandsetWholeDayRecordService");
		
		//WifiDeviceWholeDayMService wifiDeviceWholeDayMService = (WifiDeviceWholeDayMService)ctx.getBean("wifiDeviceWholeDayMService");

		long ts1 = System.currentTimeMillis();
		
		//DailyChargingDataParserOp op = new DailyChargingDataParserOp();
		step0ParserLogService.parser(date);
		step0ParserLogService.processEnd(step0ParserLogService.getDevice_records());
		long ts2 = System.currentTimeMillis();
		System.out.println(String.format("Step1 Completed cost %s ms", ts2-ts1));
		step1Result2FileService.records2File(date, step0ParserLogService.getDevice_records(), step0ParserLogService.getDevice_handset_records());
		long ts3 = System.currentTimeMillis();
		System.out.println(String.format("Step2 Completed cost %s ms", ts3-ts2));
		
		step2DeviceWholeDayRecordService.deviceRecord2Mongo(date, step0ParserLogService.getDevice_records(), step0ParserLogService.getDevice_handset_records());
		
		long ts4 = System.currentTimeMillis();
		System.out.println(String.format("Step2 Completed cost %s ms", ts4-ts3));
	}
}
