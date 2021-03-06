package com.bhu.vas.business.redis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineHandsetService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;

public class StatisticsFragmentMaxOnlineHandsetServiceTest extends BaseTest{

	//@Test
	public void doInitTest1(){
		Date current = DateTimeHelper.getDateDaysAgo(100);
		//int count = RandomData.intNumber(300,500);
		Calendar c = Calendar.getInstance();
		c.setTime(current);
		for(int j=0;j<100;j++){//模拟后100天的数据
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
			System.out.println(DateTimeExtHelper.generateServalDateFormat(c.getTime()));
			//System.out.println(DateTimeHelper.formatDate(c.getTime(), DateTimeHelper.FormatPattern9));
		}
	}
	
	@Test
	public void doInitTest(){
		Date current = DateTimeHelper.getDateDaysAgo(100);
		//int count = RandomData.intNumber(300,500);
		Calendar c = Calendar.getInstance();
		c.setTime(current);
		for(int j=0;j<100;j++){//模拟后100天的数据
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
			for(int i=0;i<24;i++){
				c.set(Calendar.HOUR_OF_DAY, i);
				StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentAllSet(c.getTime(), RandomData.intNumber(200,2000));
				//System.out.println(DateTimeHelper.formatDate(c.getTime(), DateTimeHelper.FormatPattern9));
			}
			System.out.println(DateTimeHelper.formatDate(c.getTime(), DateTimeHelper.FormatPattern9));
		}
	}
	
	@Test
	public void doGet(){
		Date current = DateTimeHelper.getDateDaysAgo(100);
		Calendar c = Calendar.getInstance();
		c.setTime(current);
		List<String> daily_keys = new ArrayList<String>();
		List<String> weekly_keys = new ArrayList<String>();
		List<String> monthy_keys = new ArrayList<String>();
		List<String> quarterly_keys = new ArrayList<String>();
		List<String> yearly_keys = new ArrayList<String>();
		for(int j=0;j<100;j++){//模拟后100天的数据
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
			List<String> fragments = DateTimeExtHelper.generateServalDateFormat(c.getTime());
			
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
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
			
		}
		
		sb_content.append("Weekly:").append("\n");
		for(String fragment:weekly_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
		}
		
		sb_content.append("Monthly:").append("\n");
		for(String fragment:monthy_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
		}
		
		
		sb_content.append("Quarterly:").append("\n");
		for(String fragment:quarterly_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey);
			Iterator<Entry<String, String>> iter = sorted.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				sb_content.append("====>").append(String.format("%s:%s", next.getKey(),next.getValue())).append("\n");
			}
		}
		
		sb_content.append("Yearly:").append("\n");
		for(String fragment:yearly_keys){
			sb_content.append("==>"+fragment).append("\n");
			Map<String,String> sorted = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey);
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
	
	//@Test
	public void testBuild4Chart(){
		System.out.println(JsonHelper.getJSONString(build4Chart(DateTimeExtHelper.YEAR_MONTH_DD,3)));
		System.out.println(JsonHelper.getJSONString(build4Chart(DateTimeExtHelper.YEAR_WHICH_WEEK,3)));
		System.out.println(JsonHelper.getJSONString(build4Chart(DateTimeExtHelper.YEAR_MONTH,3)));
		System.out.println(JsonHelper.getJSONString(build4Chart(DateTimeExtHelper.YEAR_QUARTER,3)));
		System.out.println(JsonHelper.getJSONString(build4Chart(DateTimeExtHelper.YEAR,3)));
	}
	
	/*private List<Map<String,String>> build4DailyChart(int type,int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<24;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put("key"+i, String.format("%02d", i));
			result.add(map);
		}
		Date current = new Date();
		//for(int i=ml;i>=0;i--){
		for(int i=1;i<ml+1;i++){
			Date current_ago = DateTimeHelper.getDateDaysAfter(current, i);//.getDateDaysAgo(current,1);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, type);
			System.out.println(fragment);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				int key = Integer.parseInt(next.getKey());//小时的key
				result.get(key).put(fragment, next.getValue());
			}
		}
		return result;
	}*/
	
	private List<Map<String,String>> build4Chart(int type,int ml){
		if(type == DateTimeExtHelper.YEAR_MONTH_DD){
			return build4DailyChart(ml);
		}else if(type == DateTimeExtHelper.YEAR_WHICH_WEEK){
			return build4WeeklyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR_MONTH){
			return build4MonthlyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR_QUARTER){
			return build4QuarterlyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR){
			return build4YearlyChart(ml);
		}
		return null;
	}
	
	private List<Map<String,String>> build4DailyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<24;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put("key"+i, String.format("%02d", i));
			result.add(map);
		}
		Date current = new Date();
		//for(int i=ml;i>=0;i--){
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH_DD);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String keyStr = next.getKey();
				if(keyStr.length()>2) continue;
				int key = Integer.parseInt(next.getKey());//小时的key
				result.get(key).put(fragment, next.getValue());
			}
		}
		return result;
	}
	
	private List<Map<String,String>> build4WeeklyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<7;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put("key"+i+1, String.format("%02d", i+1));
			result.add(map);
		}
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i*7);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_WHICH_WEEK);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 0;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				result.get(j).put(fragment, next.getValue());
				j++;
			}
		}
		return result;
	}
	
	private List<Map<String,String>> build4MonthlyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<31;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put(String.format("key%02d", i+1), String.format("%02d", i+1));
			result.add(map);
		}
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
			//System.out.println(fragment+"~~~~"+fragment_result+"    :"+fragment_result.size());
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 0;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				result.get(j).put(fragment, next.getValue());
				j++;
			}
		}
		return result;
	}
	
	private List<Map<String,String>> build4QuarterlyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<13;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put(String.format("key%02d", i+1), String.format("%02d", i+1));
			result.add(map);
		}
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i*3);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_QUARTER);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey);
			System.out.println(fragment+"~~~~"+fragment_result+"    :"+fragment_result.size());
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 0;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				result.get(j).put(fragment, next.getValue());
				j++;
			}
		}
		return result;
	}
	
	private List<Map<String,String>> build4YearlyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<12;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put(String.format("key%02d", i+1), String.format("%02d", i+1));
			result.add(map);
		}
		Date current = new Date();
		
		Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY); //设置每周的第一天为星期一  
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//每周从周一开始  
//      上面两句代码配合，才能实现，每年度的第一个周，是包含第一个星期一的那个周。  
        c.setMinimalDaysInFirstWeek(7);  //设置每周最少为7天  
		c.setTime(current);
		int year = c.get(Calendar.YEAR);
		
		for(int i=0;i<ml;i++){
			c.set(Calendar.YEAR, year-i);
			//Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i*3);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(c.getTime(), DateTimeExtHelper.YEAR);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey);
			System.out.println(fragment+"~~~~"+fragment_result+"    :"+fragment_result.size());
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 0;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				result.get(j).put(fragment, next.getValue());
				j++;
			}
		}
		return result;
	}
	/*public static Map<String, String> sortMapByKey(Map<String, String> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());  
        sortMap.putAll(map);  
        return sortMap;  
    } */
	
}
/*class MapKeyComparator implements Comparator<String>{  
    public int compare(String str1, String str2) {  
        return str1.compareTo(str2);  
    }  
} */
