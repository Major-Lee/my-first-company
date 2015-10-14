package com.bhu.vas.api.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

@Deprecated
public class OnlineOflineParserHelper {
	private static final String Login = "login";
	private static final String Logout = "logout";
	public static List<Fragment> parser(List<State> logs){
		List<Fragment> fragments = new ArrayList<>();
		Fragment fragment = null; 
		Iterator<State> iter = logs.iterator();
		long previous = 0l;
		boolean isLast = true;
		boolean isRepeat = true;
		boolean needLogin = false;
		while(iter.hasNext()){
			State next = iter.next();
			if(isRepeat){
				fragment = new Fragment();
				if(Login.equals(next.type)){
					if(isLast){
						fragment.setStart(DateTimeHelper.getDateTime(new Date(next.ts), DateTimeHelper.FormatPattern3));
						fragment.setEnd("Current");
						fragments.add(fragment);
						isRepeat = true;
						needLogin = false;
						previous = next.ts;
					}else{//两个login连续出现
						fragment.setStart(DateTimeHelper.getDateTime(new Date(next.ts), DateTimeHelper.FormatPattern3));
						fragment.setEnd(DateTimeHelper.getDateTime(new Date((next.ts+previous)/2), DateTimeHelper.FormatPattern3));
						fragments.add(fragment);
						isRepeat = true;
						needLogin = false;
						previous = next.ts;
					}
				}else{
					fragment.setEnd(DateTimeHelper.getDateTime(new Date(next.ts), DateTimeHelper.FormatPattern3));
					isRepeat = false;
					needLogin = true;
				}
			}else{
				if(Login.equals(next.type)){//login
					if(needLogin){
						fragment.setStart(DateTimeHelper.getDateTime(new Date(next.ts), DateTimeHelper.FormatPattern3));
						fragments.add(fragment);
						isRepeat = true;
						needLogin = false;
					}else{
						System.out.println("~~~~~~~~~~:"+next.type);
					}
					previous = next.ts;
				}else{//logout
					if(needLogin){
						//补齐缺失数据
						fragment.setStart(DateTimeHelper.getDateTime(new Date(next.ts), DateTimeHelper.FormatPattern3)+"~补齐");
						fragments.add(fragment);
						fragment = new Fragment();
						fragment.setEnd(DateTimeHelper.getDateTime(new Date(next.ts), DateTimeHelper.FormatPattern3)+"~补齐");
						isRepeat = false;
						needLogin = true;
					}else{
						System.out.println("~~~~~~~~~~:"+next.type);
					}
				}
			}
			
			isLast = false;
		}
		return fragments;
	}
	
	/**
	 * 合并连续记录里的不超过15分钟的记录
	 */
	public static void combine(){
		
	}
	
	
	public static void main(String[] argv) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File("/BHUData/data/logs.txt")));
        String str;
        StringBuffer content = new StringBuffer();
        while ((str = in.readLine()) != null) 
        {
        	content.append(str+"\n");
        }
        in.close();
        
        Logs dto = JsonHelper.getDTO(content.toString(), Logs.class);
        
        List<Fragment> parsers = OnlineOflineParserHelper.parser(dto.getLogs());
        System.out.println(parsers.size());
        for(Fragment fra:parsers){
        	System.out.println(fra);
        }
	}
	static class Fragment{
		private String start;
		private String end;
		public String getStart() {
			return start;
		}
		public void setStart(String start) {
			this.start = start;
		}
		public String getEnd() {
			return end;
		}
		public void setEnd(String end) {
			this.end = end;
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("start[%s]",start));
			sb.append(String.format("--end[%s]",end));
			return sb.toString(); 
		}
	}
	static class State{
		private long ts;
		private String type;
		public long getTs() {
			return ts;
		}
		public void setTs(long ts) {
			this.ts = ts;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
	}
	
	static class Logs{
		private List<State> logs;

		public List<State> getLogs() {
			return logs;
		}

		public void setLogs(List<State> logs) {
			this.logs = logs;
		}
		
	}
}
