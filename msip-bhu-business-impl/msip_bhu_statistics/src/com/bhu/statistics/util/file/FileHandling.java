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
import com.bhu.statistics.util.http.RequestPostUtils;
import com.bhu.statistics.util.um.OpenApiCnzzImpl;

/**
 * 文件处理类
 * @author Jason
 *
 */
public class FileHandling {
	//日志外网地址
	private static final String WAIWANG_LOG="/BHUData/bulogs/copylogs/"+getNextDay();
	//订单统计请求接口地址
	private static final String REQUEST_URL = "http://10.171.90.208/bhu_api/v1/dashboard/order/statistics";
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
			System.out.println("当前路径为:【"+filePath+"】");
			if(FileList == null || FileList.length<=0){
				System.out.println("当前路径下不存在文件");
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
		
		//存储当日设备的连接总数
		BhuCache.getInstance().setDayPV(getNextDay(), "dayPV", String.valueOf(dayPVNum));
		//存储当日设备连接总人数
		BhuCache.getInstance().setDayUV(getNextDay(), "dayUV", String.valueOf(dayUVNum));
		
		//BhuCache.getInstance().setDayPV("2016-05-20", "dayPV", String.valueOf(dayPVNum));
		//BhuCache.getInstance().setDayUV("2016-05-20", "dayUV", String.valueOf(dayUVNum));
		
		//存储mac地址
		Iterator iterator=SSIDMacList.entrySet().iterator();
		//循环获取map的key和value
		while(iterator.hasNext()){//只遍历一次,速度快
			Entry entry=(Entry)iterator.next();
			BhuCache.getInstance().setSSID(getNextDay(), entry.getKey().toString(), entry.getValue().toString());
			//BhuCache.getInstance().setSSID("2016-05-20", entry.getKey().toString(), entry.getValue().toString());
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
		                    		String[] array = line.split(" - ");
										String s = array[1].substring(2);
										JSONObject obj = JSONObject.fromObject(s);
										//获取vapname
										String vapname = obj.getString("vapname");
										if(!StringUtils.equals(vapname, "wlan3")){
											continue;
										}
										//取出当前设备的mac地址
										String mac = obj.getString("mac");
										//获取当前连接此设备的终端Mac地址
										String hmac = obj.getString("hmac");
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
		try {
			readFile(WAIWANG_LOG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//获取订单统计数量
		//开始时间
		String startTime = StringUtils.EMPTY;
		//结束时间
		String endTime = StringUtils.EMPTY;
		startTime = getNextDay()+" 00:00:00";
		//startTime = "2016-05-26 00:00:00";
		endTime = getNextDay()+" 23:59:59";
		//endTime  = "2016-05-26 23:59:59";
		String params = "start_date="+startTime+"&end_date="+endTime+"&sk=PzdfTFJSUEBHG0dcWFcLew==";
		String result = StringUtils.EMPTY;
		//请求接口获取设备总数以及设备总数
		result = RequestPostUtils.sendPost(REQUEST_URL, params);
		//订单创建数量
		long occ = 0;
		//订单支付数量
		long ofc = 0;
		//订单支付总金额
		String ofa = StringUtils.EMPTY;
		//pc端订单创建数量
		long pc_occ = 0;
		//pc端订单支付数量
		long pc_ofc = 0;
		//pc端订单支付总金额
		String pc_ofa = StringUtils.EMPTY;
		//移动端订单创建数量
		long mb_occ = 0;
		//移动端订单支付数量
		long mb_ofc = 0;
		//移动端订单支付总金额
		String mb_ofa = StringUtils.EMPTY;
		//解析参数
		JSONObject object = JSONObject.fromObject(result);
		if(object.getBoolean("success") == true){
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String, Object>) object.get("result");
			
			pc_occ = (Integer) map.get("pc_occ");
			pc_ofc = (Integer) map.get("pc_ofc");
			pc_ofa = (String)map.get("pc_ofa");
			
			mb_occ = (Integer) map.get("mb_occ");
			mb_ofc = (Integer) map.get("mb_ofc");
			mb_ofa = (String)map.get("mb_ofa");
			//存储当前日期的设备数以及在线设备数至redis
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("occ", pc_occ+mb_occ);
			resultMap.put("ofc", pc_ofc+mb_ofc);
			double pc_ofa_money = 0;
			double mb_ofa_money = 0;
			if(StringUtils.isNotBlank(mb_ofa)){
				mb_ofa_money = Double.parseDouble(mb_ofa);
			}
			if(StringUtils.isNotBlank(pc_ofa)){
				pc_ofa_money = Double.parseDouble(pc_ofa);
			}
			resultMap.put("ofa", pc_ofa_money+mb_ofa_money);
			resultMap.put("pc_occ", pc_occ);
			resultMap.put("pc_ofc", pc_ofc);
			resultMap.put("pc_ofa", pc_ofa);
			resultMap.put("mb_occ", mb_occ);
			resultMap.put("mb_ofc", mb_ofc);
			resultMap.put("mb_ofa", mb_ofa);
			BhuCache.getInstance().setStOrder(getNextDay(), "stOrder", JSONObject.toJsonString(resultMap));
			String str = BhuCache.getInstance().getStOrder(getNextDay(),"stOrder");
			System.out.println(str);
		}
		
		String dayPvNum = BhuCache.getInstance().getDayPV(getNextDay(), "dayPV");
		System.out.println("dayPvNum***************"+dayPvNum);
		String dayUvNum = BhuCache.getInstance().getDayUV(getNextDay(), "dayUV");
		System.out.println("dayUvNum***************"+dayUvNum);
		
		
		
	}
	
	public static void UmStorage(){
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		//获取每日PC端uv数据
		String pcUv= apiCnzzImpl.queryCnzzStatistic("PC打赏页PV", getNextDay(), getNextDay(), "", "",1);
		JSONObject pcUvJson=JSONObject.fromObject(pcUv);
		String pcUvJsonStr=pcUvJson.getString("values");
		pcUvJsonStr=pcUvJsonStr.substring(1);
		pcUvJsonStr=pcUvJsonStr.substring(0, pcUvJsonStr.length()-1);
		int pcUV=Integer.valueOf(pcUvJsonStr.split(",")[1].replace(".0", "").trim());
		
		//获取PC端点击事件发生数
		String pcClick=apiCnzzImpl.queryCnzzStatistic("pc+赏", getNextDay(), getNextDay(), "", "",1);
		JSONObject pcClickJson=JSONObject.fromObject(pcClick);
		String pcClickJsonStr=pcClickJson.getString("values");
		pcClickJsonStr=pcClickJsonStr.substring(1);
		pcClickJsonStr=pcClickJsonStr.substring(0, pcClickJsonStr.length()-1);
		int pcClickNum=Integer.valueOf(pcClickJsonStr.split(",")[0].replace(".0", "").trim());
		//获取手机端uv
		String mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "", "",2);
		JSONObject mobileUvJson=JSONObject.fromObject(mobileUv);
		String mobileUvJsonStr=mobileUvJson.getString("values");
		mobileUvJsonStr=mobileUvJsonStr.substring(1);
		mobileUvJsonStr=mobileUvJsonStr.substring(0, mobileUvJsonStr.length()-1);
		int mobileUV=Integer.valueOf(mobileUvJsonStr.split(",")[1].replace(".0", "").trim());
		//获取手机ios端uv
		String iosUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "", "os = 'ios'",2);
		JSONObject iosUvJson=JSONObject.fromObject(iosUv);
		String iosUvJsonStr=iosUvJson.getString("values");
		iosUvJsonStr=iosUvJsonStr.substring(1);
		iosUvJsonStr=iosUvJsonStr.substring(0, iosUvJsonStr.length()-1);
		int iosUV=Integer.valueOf(iosUvJsonStr.split(",")[1].replace(".0", "").trim());
		//获取手机android端uv
		String androidUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "", "os = 'android'",2);
		JSONObject androidUvJson=JSONObject.fromObject(androidUv);
		String androidUvJsonStr=androidUvJson.getString("values");
		androidUvJsonStr=androidUvJsonStr.substring(1);
		androidUvJsonStr=androidUvJsonStr.substring(0, androidUvJsonStr.length()-1);
		int androidUV=Integer.valueOf(androidUvJsonStr.split(",")[1].replace(".0", "").trim());
		//获取手机端点击数
		String mobileClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", getNextDay(), getNextDay(), "", "",2);
		JSONObject mobileClickJson=JSONObject.fromObject(mobileClick);
		String mobileClickJsonStr=mobileClickJson.getString("values");
		mobileClickJsonStr=mobileClickJsonStr.substring(1);
		mobileClickJsonStr=mobileClickJsonStr.substring(0, mobileClickJsonStr.length()-1);
		int mobileClickNum=Integer.valueOf(mobileClickJsonStr.split(",")[0].replace(".0", "").trim());
		//获取手机ios端点击数
		String iosClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", getNextDay(), getNextDay(), "", "os = 'ios'",2);
		JSONObject iosClickJson=JSONObject.fromObject(iosClick);
		String iosClickJsonStr=iosClickJson.getString("values");
		iosClickJsonStr=iosClickJsonStr.substring(1);
		iosClickJsonStr=iosClickJsonStr.substring(0, iosClickJsonStr.length()-1);
		int iosClickNum=Integer.valueOf(iosClickJsonStr.split(",")[0].replace(".0", "").trim());
		//获取手机android端点击数
		String androidClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", getNextDay(), getNextDay(), "", "os = 'android'",2);
		JSONObject androidClickJson=JSONObject.fromObject(androidClick);
		String androidClickJsonStr=androidClickJson.getString("values");
		androidClickJsonStr=androidClickJsonStr.substring(1);
		androidClickJsonStr=androidClickJsonStr.substring(0, androidClickJsonStr.length()-1);
		int androidClickNum=Integer.valueOf(androidClickJsonStr.split(",")[0].replace(".0", "").trim());
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
