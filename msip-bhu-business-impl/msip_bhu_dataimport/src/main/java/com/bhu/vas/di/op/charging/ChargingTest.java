package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ChargingTest {
	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		/*ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceWholeDayMService wifiDeviceWholeDayMService = (WifiDeviceWholeDayMService)ctx.getBean("wifiDeviceWholeDayMService");
		AgentWholeDayMService agentWholeDayMService = (AgentWholeDayMService)ctx.getBean("agentWholeDayMService");
		
		AgentSettlementsRecordMService agentSettlementsRecordMService = (AgentSettlementsRecordMService)ctx.getBean("agentSettlementsRecordMService");
		long count = wifiDeviceWholeDayMService.countAll();*/
		
		
		/*List<AgentSettlementsRecordMDTO> bills = agentSettlementsRecordMService.fetchBillsByAgent(100084, AgentSettlementsRecordMDTO.Settlement_Created);
		for(AgentSettlementsRecordMDTO dto:bills){
			System.out.println(dto.getAgent()+"  "+dto.getDate()+"  "+dto.getId()+" "+dto.getiSVPrice()+" "+dto.getSdPrice());
		}
		
		String result = agentSettlementsRecordMService.iterateSettleBills(3,100084,2000);//ArithHelper.sub(1891.85,80.49));
		System.out.println(result);
		
		bills = agentSettlementsRecordMService.fetchBillsByAgent(100084, AgentSettlementsRecordMDTO.Settlement_Created);
		for(AgentSettlementsRecordMDTO dto:bills){
			System.out.println(dto.getAgent()+"  "+dto.getDate()+"  "+dto.getId()+" "+dto.getiSVPrice()+" "+dto.getSdPrice());
		}*/
		
		/*System.out.println(count);
		
		List<WifiDeviceWholeDayMDTO> all = wifiDeviceWholeDayMService.findAllByDevice("84:82:f4:1b:52:8c");
		for(WifiDeviceWholeDayMDTO element:all){
			System.out.println(element.getId()+" "+element.getDod()+" "+element.getDct());
		}
		
		
		List<WifiDeviceWholeDayMDTO> allBetween = wifiDeviceWholeDayMService.findAllByDeviceBetween("84:82:f4:1b:52:8c","2015-09-08", "2015-09-10");
		
		long total_onlinetimes = 0l;
		long total_connecttimes = 0l;
		for(WifiDeviceWholeDayMDTO element:allBetween){
			System.out.println(element.getId()+" "+element.getDod()+" "+element.getDct());
			total_onlinetimes += element.getDod();
			total_connecttimes += element.getDct();
		}
		System.out.println(String.format("A total_onlinetimes[%s] total_connecttimes[%s]", total_onlinetimes,total_connecttimes));
		*/
		
		/*List<String> macs = new ArrayList<String>();
		macs.add("84:82:f4:1a:b4:b0");
		macs.add("84:82:f4:1a:c5:1c");
		macs.add("84:78:8b:6d:5a:b0");
		macs.add("84:82:f4:1a:4b:93");
		List<RecordSummaryDTO> summaryAggregation = wifiDeviceWholeDayMService.summaryAggregationWith(macs,"2015-09-18");
		for(RecordSummaryDTO dto:summaryAggregation){
			System.out.println(String.format("B id[%s] total_onlineduration[%s] total_connecttimes[%s]",dto.getId(), dto.getT_dod(),dto.getT_dct()));
		}*/
		
/*		List<String> macs = new ArrayList<String>();
		macs.add("84:82:f4:1a:b4:b0");
		macs.add("84:78:8b:6d:5a:b0");
		macs.add("84:82:f4:12:1a:34");
		List<RecordSummaryDTO> summaryAggregation = wifiDeviceWholeDayMService.summaryAggregation(macs,"2015-09-07", "2015-09-09");
		for(RecordSummaryDTO dto:summaryAggregation){
			System.out.println(String.format("B mac[%s] total_onlinetimes[%s] total_connecttimes[%s]",dto.getId(), dto.getTotal_onlineduration(),dto.getTotal_connecttimes()));
		}
		
		//WifiDeviceWholeDayMDTO result = wifiDeviceWholeDayMService.findAndModifyFlowBytes("2015-09-09", "84:82:f4:1a:c5:1c", 78645123l, 1278645123l);
		//System.out.println(result.getId());
		
		WriteResult upsertFlowBytes = wifiDeviceWholeDayMService.upsertFlowBytes("2015-09-09", "84:82:f4:1a:c5:1c:aa", 78645123l, 1278645123l);
		System.out.println(upsertFlowBytes.getUpsertedId()+"   "+upsertFlowBytes.getN() +" "+upsertFlowBytes.isUpdateOfExisting());
		
		upsertFlowBytes = wifiDeviceWholeDayMService.upsertFlowBytes("2015-09-09", "84:82:f4:1a:c5:1c:bb", 78645023l, 1278645023l);
		System.out.println(upsertFlowBytes.getUpsertedId()+"   "+upsertFlowBytes.getN() +" "+upsertFlowBytes.isUpdateOfExisting());*/
	
		/*List<Integer> users = new ArrayList<Integer>();
		users.add(100083);
		users.add(100084);
		List<RecordSummaryDTO> summaryAggregationBetween = agentWholeDayMService.summaryAggregationBetween(users, "2015-09-09", "2015-09-17");
	
		for(RecordSummaryDTO dto:summaryAggregationBetween){
			System.out.println(dto);
		}*/
	}
}
