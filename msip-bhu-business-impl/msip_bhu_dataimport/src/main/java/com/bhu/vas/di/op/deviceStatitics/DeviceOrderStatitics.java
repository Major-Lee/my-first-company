package com.bhu.vas.di.op.deviceStatitics;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.vto.statistics.DeviceOrderStatisticsVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DeviceStatisticsHashService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.di.op.repair.UserOAuthStateSpecialNickRepairEnvOp;
import com.smartwork.msip.cores.helper.JsonHelper;


public class DeviceOrderStatitics {
	
	//日志外网地址
	private static final String WAIWANG_LOG="/BHUData/bulogs/copylogs/"+getNextDay();
	//订单统计请求接口地址
	private static final String REQUEST_URL = "http://vap.bhunetworks.com/bhu_api/v1/dashboard/order/statistics";
	//PV【设备连接总数】前一天
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
	
	
	
	public static void main(String args[]){
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, DeviceOrderStatitics.class);
		ctx.start();
		OrderFacadeService orderUnitFacadeService = (OrderFacadeService) ctx.getBean("orderUnitFacadeService");
		try {
			readFile(WAIWANG_LOG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//存储当日设备的连接总数
		DeviceStatisticsHashService.getInstance().deviceMacHset(getNextDay(), "dayPV", String.valueOf(dayPVNum));
		//存储当日设备连接总人数
		DeviceStatisticsHashService.getInstance().deviceMacHset(getNextDay(), "dayUV", String.valueOf(dayUVNum));
		getDeviceOrder();
		//存储mac地址
		Iterator iterator=SSIDMacList.entrySet().iterator();
		//循环获取map的key和value
		while(iterator.hasNext()){//只遍历一次,速度快
			Entry entry=(Entry)iterator.next();
			//根据mac查询当前设备订单信息
			String startTime = StringUtils.EMPTY;
			String endTime = StringUtils.EMPTY;
			startTime = getNextDay()+" 00:00:00";
			endTime = getNextDay()+" 23:59:59";
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
			DeviceOrderStatisticsVTO deviceOrderStatisticsVTO = orderUnitFacadeService.deviceOrderStatisticsWithProcedure(startTime, endTime, entry.getKey().toString());
			pc_occ = deviceOrderStatisticsVTO.getMb_occ();
			pc_ofc = deviceOrderStatisticsVTO.getPc_ofc();
			pc_ofa = deviceOrderStatisticsVTO.getPc_ofa();
			
			mb_occ = deviceOrderStatisticsVTO.getMb_occ();
			mb_ofc = deviceOrderStatisticsVTO.getMb_ofc();
			mb_ofa = deviceOrderStatisticsVTO.getMb_ofa();
			double pc_ofa_money = 0;
			double mb_ofa_money = 0;
			if(StringUtils.isNotBlank(mb_ofa)){
				mb_ofa_money = Double.parseDouble(mb_ofa);
			}
			if(StringUtils.isNotBlank(pc_ofa)){
				pc_ofa_money = Double.parseDouble(pc_ofa);
			}
			occ = pc_occ+mb_occ;
			ofc = pc_ofc+mb_ofc;
			//存储到redis缓存
			String currJson =  entry.getValue().toString();
			Map<String, Object> map = JsonHelper.getMapFromJson(currJson);
			//JSONObject jsonobejct = JSONObject.fromObject(currJson);
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("occ", pc_occ+mb_occ);
			resultMap.put("ofc", pc_ofc+mb_ofc);
			resultMap.put("ofa", pc_ofa_money+mb_ofa_money);
			resultMap.put("pc_occ", pc_occ);
			resultMap.put("pc_ofc", pc_ofc);
			resultMap.put("pc_ofa", pc_ofa);
			resultMap.put("mb_occ", mb_occ);
			resultMap.put("mb_ofc", mb_ofc);
			resultMap.put("mb_ofa", mb_ofa);
			resultMap.put("pv", map.get("pv"));
			resultMap.put("uv", map.get("uv"));
			String json = JsonHelper.getJSONString(resultMap);
			DeviceStatisticsHashService.getInstance().deviceMacHset(getNextDay(), entry.getKey().toString(), json);
		}
		
	} 
	
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
										Map<String,Object> help =  JsonHelper.getMapFromJson(s);
										//JSONObject obj = JSONObject.fromObject(s);
										//获取vapname
										String vapname = (String) help.get("vapname");
										if(!StringUtils.equals(vapname, "wlan3")){
											continue;
										}
										//取出当前设备的mac地址
										String mac = (String) help.get("mac");
										//获取当前连接此设备的终端Mac地址
										String hmac = (String) help.get("hmac");
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
													macUVNumMap.put(mac, String.valueOf(uvNum));
											}else{
												macUVNumMap.put(mac, String.valueOf(uvNum+1));
											}
										}
										macMap.put(mac, hmac);
										Map<String,Object> jsonMap = new HashMap<String,Object>();
										jsonMap.put("pv", macPVMap.get(mac));
										jsonMap.put("uv", macUVNumMap.get(mac));
										SSIDMacList.put(mac, JsonHelper.getJSONString(jsonMap));
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
	
	public static void getDeviceOrder(){
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
		result = sendPost(REQUEST_URL, params);
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
		Map<String,Object> helper = JsonHelper.getMapFromJson(result);
		//JSONObject object = JSONObject.fromObject(result);
		if(StringUtils.equals((String) helper.get("code"), "200")){
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String, Object>) helper.get("result");
			
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
			DeviceStatisticsHashService.getInstance().deviceMacHset(getNextDay(), "stOrder",JsonHelper.getJSONString(resultMap));
		}
	}
	
	public static String sendPost(String url, String param) {
		 PrintWriter out = null;
			BufferedReader in = null;
			String result = "";
			try {
				URL realUrl = new URL(url);
				// 打开和URL之间的连接
				URLConnection conn = realUrl.openConnection();
				// 设置通用的请求属性
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("Accept-Encoding", "utf-8");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				// 发送POST请求必须设置如下两行
				conn.setDoOutput(true);
				conn.setDoInput(true);
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
				// 定义BufferedReader输入流来读取URL的响应
				in = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
			} catch (Exception e) {
				System.out.println("发送 POST 请求出现异常！" + e);
				e.printStackTrace();
			}
			// 使用finally块来关闭输出流、输入流
			finally {
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			return result;
	    } 
}