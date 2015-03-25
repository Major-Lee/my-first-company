package com.bhu.vas.business.redis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineHandsetService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;

public class StatisticsFragmentMaxOnlineHandsetServiceTest extends BaseTest{

	@Test
	public void doInitTest(){
		Date current = new Date();
		//int count = RandomData.intNumber(300,500);
		Calendar c = Calendar.getInstance();
		c.setTime(current);
		for(int j=0;j<40;j++){//模拟后100天的数据
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
			for(int i=0;i<24;i++){
				c.set(Calendar.HOUR_OF_DAY, i);
				StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentAllSet(c.getTime(), RandomData.intNumber(300,500));
				//System.out.println(DateTimeHelper.formatDate(c.getTime(), DateTimeHelper.FormatPattern9));
			}
			System.out.println(DateTimeHelper.formatDate(c.getTime(), DateTimeHelper.FormatPattern9));
		}
	}
	
	@Test
	public void doGet(){
		Date current = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(current);
		List<String> daily_keys = new ArrayList<String>();
		List<String> weekly_keys = new ArrayList<String>();
		List<String> monthy_keys = new ArrayList<String>();
		List<String> quarterly_keys = new ArrayList<String>();
		List<String> yearly_keys = new ArrayList<String>();
		for(int j=0;j<40;j++){//模拟后100天的数据
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
			List<String> fragments = DateTimeExtHelper.generateServalDateFormat(c.getTime(), false);
			
			String value_ymd = fragments.get(DateTimeExtHelper.YEAR_MONTH_DD);
			if(!daily_keys.contains(value_ymd)){
				daily_keys.add(value_ymd);
			}
			
			String value_yw = fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK);
			if(!weekly_keys.contains(value_yw)){
				weekly_keys.add(value_yw);
			}
			
			String value_ym = fragments.get(DateTimeExtHelper.YEAR_MONTH);
			if(!monthy_keys.contains(value_ym)){
				monthy_keys.add(value_ym);
			}
			
			String value_yq = fragments.get(DateTimeExtHelper.YEAR_QUARTER);
			if(!quarterly_keys.contains(value_yq)){
				quarterly_keys.add(value_yq);
			}
			
			String value_yy = fragments.get(DateTimeExtHelper.YEAR);
			if(!yearly_keys.contains(value_yy)){
				yearly_keys.add(value_yy);
			}
		}
		StringBuilder sb_content = new StringBuilder();
		sb_content.append("Daily:").append("\n");
		for(String fragment:daily_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
			
		}
		
		sb_content.append("Weekly:").append("\n");
		for(String fragment:weekly_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
		}
		
		sb_content.append("Monthly:").append("\n");
		for(String fragment:monthy_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
		}
		
		
		sb_content.append("Quarterly:").append("\n");
		for(String fragment:quarterly_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
		}
		
		sb_content.append("Yearly:").append("\n");
		for(String fragment:yearly_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
		}
		
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter  bw = null;
		
		try{
			fos=new FileOutputStream(new File("/Users/Edmond/Msip.Test/output.txt"));
			osw=new OutputStreamWriter(fos, "UTF-8");
			bw=new BufferedWriter(osw);
			bw.write(sb_content.toString()+"\n");
		    bw.close();
		    osw.close();
		    fos.close();
		    
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try {
				if(bw != null)
					bw.close();
				if(osw != null)
					osw.close();
				if(fos != null)
					fos.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("dicpath:"+"/Users/Edmond/Msip.Test/output.txt");
		
	}
	
	
	public static Map<String, String> sortMapByKey(Map<String, String> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());  
        sortMap.putAll(map);  
        return sortMap;  
    } 
	
}
class MapKeyComparator implements Comparator<String>{  
    public int compare(String str1, String str2) {  
        return str1.compareTo(str2);  
    }  
} 
