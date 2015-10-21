package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.di.business.datainit.charging.ParserLog;
import com.bhu.vas.di.business.datainit.charging.Step00ReadAndMergeLogService;
import com.bhu.vas.di.business.datainit.charging.Step02DeviceWholeDayRecordService;
import com.bhu.vas.di.business.datainit.charging.Step04DeviceWholeMonthRecordService;
import com.bhu.vas.di.business.datainit.charging.Step05AgentWholeDayRecordService;
import com.bhu.vas.di.business.datainit.charging.Step10AgentDeviceSimulateDateGenService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 每日必须进行状态保活，每天凌晨需要把在线的设备重新写入到日志中，作为模拟登录，模拟登录的时间缺省为当日的起始时间，防止漏算了长时间在线的设备
 * 对于终端则无需这样
 * @author Edmond
 *
 */
public class DailyChargingDataParserOper {

	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		//String date = "2015-09-10";
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		//Step00ParserLogService step00ParserLogService = (Step00ParserLogService)ctx.getBean("step00ParserLogService");
		Step00ReadAndMergeLogService step00ReadAndMergeLogService = (Step00ReadAndMergeLogService)ctx.getBean("step00ReadAndMergeLogService");
		//Step01Result2FileService step01Result2FileService = (Step01Result2FileService)ctx.getBean("step01Result2FileService");
		Step02DeviceWholeDayRecordService step02DeviceWholeDayRecordService = (Step02DeviceWholeDayRecordService)ctx.getBean("step02DeviceWholeDayRecordService");
		
		Step04DeviceWholeMonthRecordService step04DeviceWholeMonthRecordService = (Step04DeviceWholeMonthRecordService)ctx.getBean("step04DeviceWholeMonthRecordService");
		Step05AgentWholeDayRecordService step05AgentWholeDayRecordService = (Step05AgentWholeDayRecordService)ctx.getBean("step05AgentWholeDayRecordService");
		Step10AgentDeviceSimulateDateGenService step10AgentDeviceSimulateDateGenService = (Step10AgentDeviceSimulateDateGenService)ctx.getBean("step10AgentDeviceSimulateDateGenService");
		long ts1 = System.currentTimeMillis();
		String[] dates = new String[]{"2015-10-19"};
		//String[] dates = new String[]{"2015-09-10","2015-09-11","2015-09-12","2015-09-13"};
		//String[] dates = new String[]{"2015-09-14","2015-09-15","2015-09-16","2015-09-17"};
		//String[] dates = new String[]{"2015-09-18","2015-09-19","2015-09-20","2015-09-21"};
		//String[] dates = new String[]{"2015-09-22"};
		
		for(String date:dates){
			final ParserLog parser = new ParserLog();
			parser.setCurrentDate(DateTimeHelper.parseDate(date, DateTimeHelper.shortDateFormat));
			//DailyChargingDataParserOp op = new DailyChargingDataParserOp();
			//step00ParserLogService.reset();
			step00ReadAndMergeLogService.parser(date,new IteratorNotify<String>(){
				@Override
				public void notifyComming(String line) {
					//System.out.println(line);
					try {
						parser.processBackendActionMessage(line);
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
					/*String[] split = line.split(" - ");
					if(split.length == 2){
						try {
							parser.processBackendActionMessage(split[1]);
						} catch (Exception e) {
							e.printStackTrace(System.out);
						}
					}*/
				}
			},"/BHUData/bulogs/charginglogs-a/");
			System.out.println("Device_records size:"+parser.getDevice_records().size());
			System.out.println("Device_handset_records size:"+parser.getDevice_handset_records().size());
			
			/*int handsets = 0;
			Iterator<Entry<String, Map<String, LineRecords>>> iterator = step00ParserLogService.getDevice_handset_records().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Map<String, LineRecords>> next = iterator.next();
				handsets+=next.getValue().size();
			}
			System.out.println(handsets);*/
			
			/*long ts2 = System.currentTimeMillis();
			//step10AgentDeviceSimulateDateGenService.deviceDataGen(date, step00ParserLogService.getDevice_records());
			System.out.println(String.format("Step1 Completed cost %s ms", ts2-ts1));
			//step01Result2FileService.records2File(date, step00ParserLogService.getDevice_records(), step00ParserLogService.getDevice_handset_records());
			long ts3 = System.currentTimeMillis();
			step02DeviceWholeDayRecordService.deviceRecord2Mongo(date, parser.getDevice_records(), parser.getDevice_handset_records());
			long ts4 = System.currentTimeMillis();
			System.out.println(String.format("Step2 Completed cost %s ms", ts4-ts3));
			step04DeviceWholeMonthRecordService.deviceMonthlyRecord2Mongo(date, parser.getDevice_records());
			long ts5 = System.currentTimeMillis();
			System.out.println(String.format("Step4 Completed cost %s ms", ts5-ts4));
			
			step05AgentWholeDayRecordService.agentDailyRecord2Mongo(date,parser.getDevice_records(),parser.getDevice_handset_records());
			long ts6 = System.currentTimeMillis();
			System.out.println(String.format("Step5 Completed cost %s ms", ts6-ts5));*/
		}
	}
}
