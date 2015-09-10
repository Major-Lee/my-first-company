package com.bhu.vas.di.op.charging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.ds.agent.dto.RecordSummaryDTO;
import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayMDTO;
import com.bhu.vas.business.ds.agent.mservice.WifiDeviceWholeDayMService;

public class ChargingTest {
	public static void main(String[] argv) throws UnsupportedEncodingException, IOException{
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceWholeDayMService wifiDeviceWholeDayMService = (WifiDeviceWholeDayMService)ctx.getBean("wifiDeviceWholeDayMService");
		long count = wifiDeviceWholeDayMService.countAll();
		
		System.out.println(count);
		
		
		
		
		List<WifiDeviceWholeDayMDTO> all = wifiDeviceWholeDayMService.findAllByDevice("84:82:f4:1a:b4:b0");
		for(WifiDeviceWholeDayMDTO element:all){
			System.out.println(element.getId()+" "+element.getOnlinetime()+" "+element.getConnecttimes());
		}
		
		
		List<WifiDeviceWholeDayMDTO> allBetween = wifiDeviceWholeDayMService.findAllByDeviceBetween("84:82:f4:1a:b4:b0","2015-09-07", "2015-09-09");
		
		long total_onlinetimes = 0l;
		long total_connecttimes = 0l;
		for(WifiDeviceWholeDayMDTO element:allBetween){
			System.out.println(element.getId()+" "+element.getOnlinetime()+" "+element.getConnecttimes());
			total_onlinetimes += element.getOnlinetime();
			total_connecttimes += element.getConnecttimes();
		}
		System.out.println(String.format("A total_onlinetimes[%s] total_connecttimes[%s]", total_onlinetimes,total_connecttimes));
		
		
		List<String> macs = new ArrayList<String>();
		macs.add("84:82:f4:1a:b4:b0");
		macs.add("84:78:8b:6d:5a:b0");
		macs.add("84:82:f4:12:1a:34");
		List<RecordSummaryDTO> summaryAggregation = wifiDeviceWholeDayMService.summaryAggregation(macs,"2015-09-07", "2015-09-09");
		for(RecordSummaryDTO dto:summaryAggregation){
			System.out.println(String.format("B mac[%s] total_onlinetimes[%s] total_connecttimes[%s]",dto.getId(), dto.getTotal_onlinetime(),dto.getTotal_connecttimes()));
		}
	}
}
