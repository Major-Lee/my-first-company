package com.bhu.vas.di.op;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;

import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 统计操作系统分布图 根据mac地址区别重复数据 进行增加计数
 * 每个操作系统各个型号数量分布
 * @author Edmond
 *
 */
public class UserAccessStatisticAnaOp {
	public static void main(String[] argv) throws IOException{
		//操作系统
		//Map<String,Integer> opMap = new HashMap<String,Integer>();
		//设备型号 系统 型号 数量
		Map<String,Map<String,Integer>> optypeMap = new HashMap<String,Map<String,Integer>>();
		//出现过的设备mac地址
		Set<String> macSet = new HashSet<String>();
		
		InputStreamReader read = new InputStreamReader(new FileInputStream("/Users/Edmond/Msip.Test/logfile.log"));//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
        	if (!lineTxt.startsWith("0001") || !lineTxt.endsWith("0000")) continue;
        	DpiInfo dinfo = DpiInfo.fromTextLine(lineTxt);
        	System.out.println(String.format("%s %s", dinfo.getTmac(),dinfo.getUseragent()));
        	if(dinfo.getUseragent() != null){
	        	UserAgent useragent = new UserAgent(dinfo.getUseragent());
	            System.out.println(useragent.getOperatingSystem());
	            System.out.println(useragent.getBrowser());
	            System.out.println(useragent.getId());
	            System.out.println(useragent.getBrowserVersion());
	            String terminal = T.catchUserAgentTermianl(dinfo.getUseragent());
	            if(terminal == null){
	            	if(useragent.getOperatingSystem().toString().startsWith("WIN")){
	            		terminal = "PC";
	            	}
	            	if(useragent.getOperatingSystem() == OperatingSystem.MAC_OS){
	            		terminal = "Macintosh";
	            	}
	            	if(useragent.getOperatingSystem() == OperatingSystem.MAC_OS_X){
	            		terminal = "Macintosh";
	            	}
	            	if(useragent.getOperatingSystem() == OperatingSystem.MAC_OS_X_IPHONE){
	            		terminal = "iPhone";
	            	}
	            	if(useragent.getOperatingSystem() == OperatingSystem.MAC_OS_X_IPOD){
	            		terminal = "iPod";
	            	}
	            	if(useragent.getOperatingSystem() == OperatingSystem.MAC_OS_X_IPAD){
	            		terminal = "iPad";
	            	}
	            	
	            }
	            System.out.println(terminal);
        	}
        	/*String[] result = parserLine(lineTxt);
        	if(result == null) continue;
        	String handsetMac= StringHelper.removeWhiteSpace(result[1]);
        	//System.out.println(ArrayHelper.toSplitString(result, "|"));
        	if(!macSet.contains(handsetMac)){
        		macSet.add(handsetMac);
        		System.out.println(String.format("%s %s", handsetMac,result[7]));
        	}*/
        }
        bufferedReader.close();
        
        
	}
	
	
	public static String[] parserLine(String lineTxt){
		if (!lineTxt.startsWith("0001") || !lineTxt.endsWith("0000")) return null;
		String[] result = new String[8];
		int currentIndex = 0;
        String part0001 = lineTxt.substring(currentIndex, currentIndex + 4) +
                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
        result[0] = part0001;
        //System.out.println(" --- 任务id : " + part0001);
        currentIndex = part0001.length();

        String part0002 = lineTxt.substring(currentIndex, currentIndex + 4) +
                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)) + 1);
        result[1] = part0002.substring(8);
        //System.out.println(" --- 客户端MAC : " + part0002);
        currentIndex +=  part0002.length();

        String part0003 = lineTxt.substring(currentIndex, currentIndex + 4) +
                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
        //System.out.println(" --- 设备MAC : " + part0003);
        result[2] = part0003.substring(8);
        currentIndex +=  part0003.length();

        String part0004 = lineTxt.substring(currentIndex, currentIndex + 4) +
                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
        //System.out.println(" --- 访问IP : " + part0004);
        result[3] = part0004;
        currentIndex +=  part0004.length();

        String part0005 = lineTxt.substring(currentIndex, currentIndex + 4) +
                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
        //System.out.println(" --- HOST : " + part0005);
        result[4] = part0005;
        currentIndex += part0005.length();

        String part0006 = lineTxt.substring(currentIndex, currentIndex + 4) +
                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
        //System.out.println(" --- URI : " + part0006);
        result[5] = part0006;
        currentIndex +=  part0006.length();

        String part0007 = lineTxt.substring(currentIndex, currentIndex + 4) +
                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
        //System.out.println(" --- ACCEPT : " + part0007);
        result[6] = part0007.substring(8);
        currentIndex +=  part0007.length();

        String part0008 = lineTxt.substring(currentIndex, currentIndex + 4) +
                lineTxt.substring(currentIndex + 4, currentIndex + 8) +
                lineTxt.substring(currentIndex + 8, currentIndex + 8 + Integer.parseInt(lineTxt.substring(currentIndex + 4, currentIndex + 8)));
        //System.out.println(" --- USER_AGENT : " + part0008);
        result[7] = part0008.substring(8);
        //System.out.println(part0008.substring(8));
        currentIndex +=  part0008.length();
        
        return result;
	}
}
