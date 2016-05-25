package com.bhu.statistics.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.cache.BhuCache;

/**
 * 文件处理类
 * @author Jason
 *
 */
public class FileHandling {
	//PV【设备连接总数】前一天
	private static long dayPVNum = 0;
	//UV【设备连接总人数】前一天
	private static long dayUVNum = 0;
	
	//存储当日hmac列表
	private static List<String> hmacList = new ArrayList<String>();
	//存储当日mac列表
	private static Map<String,Object> macMap = new HashMap<String,Object>();
	//存储当期mac与uv的个数
	private static Map<String,Object> macUVNumMap = new HashMap<String,Object>();
	//存储当前mac的PV数量
	private static Map<String,Object> macPVMap = new HashMap<String,Object>();
	//存储所有设备SSID地址列表
	private static Map<String,Object> SSIDMacList = new HashMap<String,Object>();
	private static int l = 0;
	/**
	 * 读取文件
	 * @author Jason
	 * @param filePath 文件路径
	 * @return
	 */
	public static String readFile(String filePath) {
		if(StringUtils.isBlank(filePath) || StringUtils.isEmpty(filePath)){
			System.out.println("文件路径为空");
			return null;
		}
		try {
			//文件读取
			File file = new File(filePath);
			//获取当前路径下所有文件以及文件夹
			File[] FileList = file.listFiles();
			if(FileList == null || FileList.length<=0){
				return null;
			}
			for (int i = 0; i < FileList.length; i++) {
				File currFile = FileList[i];
				if(currFile.getName().contains(".DS_Store")){
					continue;
				}
				/*if(currFile.getName().contains("chargingsimulogs")){
					continue;
				}*/
				if(currFile.isDirectory()){
					readFile(currFile.getAbsolutePath());
				}else{
					readZipFile(currFile.getAbsolutePath());
				}
			}         
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		
		/*//存储当日设备的连接总数
		BhuCache.getInstance().setDayPV(getNextDay(), "dayPV", String.valueOf(dayPVNum));
		//存储当日设备连接总人数
		BhuCache.getInstance().setDayUV(getNextDay(), "dayUV", String.valueOf(dayUVNum));*/
		
		//存储mac地址
		Iterator iterator=SSIDMacList.entrySet().iterator();
		//循环获取map的key和value
		while(iterator.hasNext()){//只遍历一次,速度快
			Entry entry=(Entry)iterator.next();
			System.out.println("macList大小为【"+SSIDMacList.size()+"】");
			System.out.println("macList对应的key为【"+entry.getKey()+"】");
			System.out.println("macList对应的value为【"+entry.getValue()+"】");
			BhuCache.getInstance().setSSID(getNextDay(), entry.getKey().toString(), entry.getValue().toString());
		}
		return null;
	}
	
	/**
	 * 读取压缩包内容
	 * @param filePath
	 * @return 
	 */
	public static String readZipFile(String filePath){
		//返回结果
		String result = StringUtils.EMPTY;
		/*//统计每个SSID的uv数量和PV数量
		int macPVNum = 0;
		//SSID连接UV总数
		int macUVNum = 0;
		//SSID连接PV总数*/
		try {
				ZipFile zipFile = new ZipFile(filePath);
				InputStream in = new BufferedInputStream(new FileInputStream(filePath)); 
				ZipInputStream zin = new ZipInputStream(in);
		        ZipEntry ze;  
		        while ((ze = zin.getNextEntry()) != null) {  
		        	zin.closeEntry();
		            if (!ze.isDirectory()) {
		            	//String name = ze.getName();
		                long size = ze.getSize();  
		                if (size > 0) {  
		                    BufferedReader br = new BufferedReader(  
		                            new InputStreamReader(zipFile.getInputStream(ze)));  
		                    //读取内容
		                    String line = StringUtils.EMPTY;
		                    while ((line = br.readLine()) != null) {
		                    	if(StringUtils.contains(line, "HO{")){
		                    		l++;
		                    		String[] array = line.split(" - ");
										String s = array[1].substring(2);
										JSONObject obj = JSONObject.fromObject(s);
										//取出当前设备的mac地址
										String mac = obj.getString("mac");
										System.out.println("mac:【"+mac+"】,当前是第【"+l+"】个mac");
										//获取当前连接此设备的终端Mac地址
										String hmac = obj.getString("hmac");
										System.out.println("hmac:【"+hmac+"】，当前是第【"+l+"】个hmac");
										//设备连接总次数++
										dayPVNum++;
										//uv连接人数
										if(!hmacList.contains(hmac)){
											//如果当前hmac不存在则添加当前hmac到list
											hmacList.add(hmac);
											//设备连接总人数
											dayUVNum++;
										}
										if(macMap.containsKey(mac)){
											String m = (String) macPVMap.get(mac);
											int pvNum = 0;
											if(StringUtils.isBlank(m)){
												pvNum = 0;
											}else{
												pvNum =Integer.parseInt((String) macPVMap.get(mac));
											}
											macPVMap.put(mac, String.valueOf(pvNum+1));
											//macPVNum++;
											//根据key获取value
											String k = (String) macUVNumMap.get(mac);
											int uvNum = 0;
											if(StringUtils.isBlank(k)){
												 uvNum = 0;
											}else{
												uvNum = Integer.parseInt((String) macUVNumMap.get(mac));
											}
											String currHMac = (String) macMap.get(mac);
											System.out.println("当前currHMac为"+currHMac);
											if(StringUtils.equals(currHMac, hmac)){
													System.out.println("=======HMAC已存在======");
													macUVNumMap.put(mac, String.valueOf(uvNum));
											}else{
												System.out.println("=======HMAC不存在======");
												macUVNumMap.put(mac, String.valueOf(uvNum+1));
												System.out.println("**********"+macUVNumMap.get(mac)+"********");
											}
										}
										macMap.put(mac, hmac);
										//TODO
										Map<String,Object> jsonMap = new HashMap<String,Object>();
										jsonMap.put("pv", macPVMap.get(mac));
										jsonMap.put("uv", macUVNumMap.get(mac));
										SSIDMacList.put(mac, JSONObject.toJsonString(jsonMap));
		                    	}
		                    }  
		                    br.close(); 
		                }
		            } 
		        }
		        zipFile.close();
		        zin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			return null;
		}
		return result;
	} 
	
	public static void main(String args[]){
		String filePath = "D:\\2016-04-22";
		try {
			readFile(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//deleteAllData("2016-05-18");
		/*String dayPvNum = BhuCache.getInstance().getDayPV("2016-05-18", "dayPV");
		System.out.println("dayPvNum***************"+dayPvNum);
		String dayUvNum = BhuCache.getInstance().getDayUV("2016-05-18", "dayUV");
		System.out.println("dayUvNum***************"+dayUvNum);*/
	}
	
	/**
	 * 获取当前日期前一天【格式：xxxx-xx-xx】
	 * @return
	 */
	public static String getNextDay() {
		Date date = new Date();  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        date = calendar.getTime();  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String dateNowStr = sdf.format(date); 
        return dateNowStr;
    }
	
	public static void deleteAllData(String date){
		
		//删除TotalPV
		long pv = BhuCache.getInstance().delTotalPV("totalPV");
		System.out.println("删除TotalPV返回值为【"+pv+"】");
		
		//删除TotalUV
		long uv = BhuCache.getInstance().delTotalUV("totalUV");
		System.out.println("删除TotalUV返回值为【"+uv+"】");
		
		//删除DayPV
		Set<String> pvSet = BhuCache.getInstance().getdayPVFiled(date);
		for(String value : pvSet){  
            //删除hash
            long n = BhuCache.getInstance().deletedayPVHash(date,value);
            System.out.println("返回值为【"+n+"】");
        }
		
		//删除DayUV
		Set<String> uvSet = BhuCache.getInstance().getdayUVFiled(date);
		for(String value : uvSet){  
            //删除hash
            long n = BhuCache.getInstance().deletedayUVHash(date,value);
            System.out.println("返回值为【"+n+"】");
        }
		 
		//删除SSID
		Set<String> ssIdSet = BhuCache.getInstance().getSSIDFiled(date);
		for(String value : ssIdSet){  
            //删除hash
            long n = BhuCache.getInstance().deleteSSIDHash(date,value);
            System.out.println("返回值为【"+n+"】");
        }
	}
}
