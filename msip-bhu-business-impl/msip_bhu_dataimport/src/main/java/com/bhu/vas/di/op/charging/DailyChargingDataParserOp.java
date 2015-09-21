package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.ds.agent.mdto.LineRecords;
import com.bhu.vas.di.business.datainit.charging.Step00ParserLogService;
import com.bhu.vas.di.business.datainit.charging.Step01Result2FileService;
import com.bhu.vas.di.business.datainit.charging.Step02DeviceWholeDayRecordService;
import com.bhu.vas.di.business.datainit.charging.Step04AgentWholeDayRecordService;

/**
 * 每日必须进行状态保活，每天凌晨需要把在线的设备重新写入到日志中，作为模拟登录，模拟登录的时间缺省为当日的起始时间，防止漏算了长时间在线的设备
 * 对于终端则无需这样
 * @author Edmond
 *
 */
public class DailyChargingDataParserOp {

	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		String date = "2015-09-10";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		Step00ParserLogService step00ParserLogService = (Step00ParserLogService)ctx.getBean("step00ParserLogService");
		Step01Result2FileService step01Result2FileService = (Step01Result2FileService)ctx.getBean("step01Result2FileService");
		Step02DeviceWholeDayRecordService step02DeviceWholeDayRecordService = (Step02DeviceWholeDayRecordService)ctx.getBean("step02DeviceWholeDayRecordService");
		
		//Step03HandsetWholeDayRecordService step03HandsetWholeDayRecordService = (Step03HandsetWholeDayRecordService)ctx.getBean("step3HandsetWholeDayRecordService");
		Step04AgentWholeDayRecordService step04AgentWholeDayRecordService = (Step04AgentWholeDayRecordService)ctx.getBean("step04AgentWholeDayRecordService");
		//Step10AgentDeviceSimulateDateGenService step10AgentDeviceSimulateDateGenService = (Step10AgentDeviceSimulateDateGenService)ctx.getBean("step10AgentDeviceSimulateDateGenService");
		long ts1 = System.currentTimeMillis();
		
		//DailyChargingDataParserOp op = new DailyChargingDataParserOp();
		step00ParserLogService.parser(date);
		step00ParserLogService.processEnd(step00ParserLogService.getDevice_records());
		System.out.println("Device_records size:"+step00ParserLogService.getDevice_records().size());
		System.out.println("Device_handset_records size:"+step00ParserLogService.getDevice_handset_records().size());
		
		/*int handsets = 0;
		Iterator<Entry<String, Map<String, LineRecords>>> iterator = step00ParserLogService.getDevice_handset_records().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Map<String, LineRecords>> next = iterator.next();
			handsets+=next.getValue().size();
		}
		System.out.println(handsets);*/
		long ts2 = System.currentTimeMillis();
		//step10AgentDeviceSimulateDateGenService.deviceDataGen(date, step00ParserLogService.getDevice_records());
		System.out.println(String.format("Step1 Completed cost %s ms", ts2-ts1));
		//step01Result2FileService.records2File(date, step00ParserLogService.getDevice_records(), step00ParserLogService.getDevice_handset_records());
		long ts3 = System.currentTimeMillis();
		step02DeviceWholeDayRecordService.deviceRecord2Mongo(date, step00ParserLogService.getDevice_records(), step00ParserLogService.getDevice_handset_records());
		long ts4 = System.currentTimeMillis();
		System.out.println(String.format("Step2 Completed cost %s ms", ts4-ts3));
		/*step04AgentWholeDayRecordService.agentDailyRecord2Mongo(date,step00ParserLogService.getDevice_records(),step00ParserLogService.getDevice_handset_records());
		long ts5 = System.currentTimeMillis();
		System.out.println(String.format("Step4 Completed cost %s ms", ts5-ts4));*/
	}
}
