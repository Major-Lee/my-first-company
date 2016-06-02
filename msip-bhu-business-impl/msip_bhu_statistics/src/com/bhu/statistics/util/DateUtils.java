package com.bhu.statistics.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 日期操作转换类
 * @author Jason
 *
 */
public class DateUtils {
	
	/**
	 * 获取当前日期的前n天日期
	 * @param data
	 * @return
	 */
	public static List<String> getLastDay(int dateNum){
		List<String> list = new ArrayList<String>();
		//获取当前日期
        for (int i = 1; i <= dateNum; i++) {
        	Date date = new Date();  
            Calendar calendar = Calendar.getInstance();  
            calendar.setTime(date); 
        	calendar.add(Calendar.DAY_OF_MONTH, -i);
            date = calendar.getTime();  
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
            String dateNowStr = sdf.format(date); 
            list.add(dateNowStr);
		}
       return list; 
	}
	
	public static List<String> getDaysList(String beginTime,String endTime){
		List<String> list = new ArrayList<String>();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        //list.add(beginTime);
        try {
            start.setTime(format.parse(beginTime));
            end.setTime(format.parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while(start.before(end))
        {
            System.out.println(format.format(start.getTime()));
            list.add(format.format(start.getTime()).toString());
            start.add(Calendar.DAY_OF_MONTH,1);
        }
        if(!StringUtils.endsWith(beginTime, endTime)){
        	list.add(endTime);
        }
       return list; 
	}
	/**
	 * 格式化时间 YY-MM-dd
	 * @param date
	 * @return
	 */
	public static String formatDate(String date){
		String dateString = StringUtils.EMPTY;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt=formatter.parse(date);
			dateString = formatter.format(dt);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateString;
	}
	
	public static long getDaySub(String startTime,String endTime){
		Date beginDate; 
		Date endDate; 
		long day=0; 
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
			beginDate = format.parse(startTime); 
			endDate = format.parse(endTime);
			day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return day;
	}
	public static void main(String[] args) {
		/*List<String> list = getLastDay(30);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}*/
		//System.out.println(formatDate("2016-05-23 15:15"));
		System.out.println(getDaysList("2016-05-19 15:15","2016-05-21 15:15"));
	}
}
