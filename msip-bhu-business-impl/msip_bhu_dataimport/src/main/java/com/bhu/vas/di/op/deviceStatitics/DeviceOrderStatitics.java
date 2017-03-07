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

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DeviceStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.UMStatisticsHashService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.di.op.umUtil.JSONObject;
import com.bhu.vas.di.op.umUtil.OpenApiCnzzImpl;
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
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		
		String pcUv= apiCnzzImpl.queryCnzzStatistic("PC打赏页PV", getNextDay(), getNextDay(), "", "",2);
		JSONObject pcUvJson=JSONObject.fromObject(pcUv);
		String pcUvJsonStr=pcUvJson.getString("values");
		pcUvJsonStr=pcUvJsonStr.substring(1);
		pcUvJsonStr=pcUvJsonStr.substring(0, pcUvJsonStr.length()-1);
		int pcUV=Integer.valueOf(pcUvJsonStr.split(",")[1].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "pcUv", String.valueOf(pcUV));
		
		String pcMacUv= apiCnzzImpl.queryCnzzStatistic("PC打赏页PV", getNextDay(), getNextDay(), "wlanapmac", "",2);
		JSONObject pcMac=JSONObject.fromObject(pcMacUv);
		String pcMacData=pcMac.getString("values");
		pcMacData=pcMacData.substring(1);
		pcMacData=pcMacData.substring(0,pcMacData.length()-1);
		if(!pcMacData.equals("0,0,0")){
			String[] pcMacDataArray=pcMacData.split("],");
			if(pcMacDataArray.length>0){
				for(String i:pcMacDataArray){
					UMStatisticsHashService.getInstance().umHset("MacPcUv"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		String freePcClick=apiCnzzImpl.queryCnzzStatistic("pc+我要免费上网", getNextDay(), getNextDay(), "", "",2);
		JSONObject freePcClickJson=JSONObject.fromObject(freePcClick);
		String freePcClickJsonStr=freePcClickJson.getString("values");
		freePcClickJsonStr=freePcClickJsonStr.substring(1);
		freePcClickJsonStr=freePcClickJsonStr.substring(0, freePcClickJsonStr.length()-1);
		int freePcClickNum=Integer.valueOf(freePcClickJsonStr.split(",")[0].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "freePcClickNum", String.valueOf(freePcClickNum));
		
		String freePcClickMac= apiCnzzImpl.queryCnzzStatistic("pc+我要免费上网", getNextDay(), getNextDay(), "wlanapmac", "",2);
		JSONObject freePcMac=JSONObject.fromObject(freePcClickMac);
		String freePcMacData=freePcMac.getString("values");
		freePcMacData=freePcMacData.substring(1);
		freePcMacData=freePcMacData.substring(0,freePcMacData.length()-1);
		if(!freePcMacData.equals("0,0,0")){
			String[] freePcMacDataArray=freePcMacData.split("],");
			if(freePcMacDataArray.length>0){
				for(String i:freePcMacDataArray){
					UMStatisticsHashService.getInstance().umHset("MacFreePcClickNum"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		String freeMobileClick=apiCnzzImpl.queryCnzzStatistic("mobile+我要免费上网", getNextDay(), getNextDay(), "", "",2);
		JSONObject freeMobileClickJson=JSONObject.fromObject(freeMobileClick);
		String freeMobileClickJsonStr=freeMobileClickJson.getString("values");
		freeMobileClickJsonStr=freeMobileClickJsonStr.substring(1);
		freeMobileClickJsonStr=freeMobileClickJsonStr.substring(0, freeMobileClickJsonStr.length()-1);
		int freeMobileClickNum=Integer.valueOf(freeMobileClickJsonStr.split(",")[0].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "freeMobileClickNum", String.valueOf(freeMobileClickNum));
		
		String freeMobileClickMac= apiCnzzImpl.queryCnzzStatistic("pc+我要免费上网", getNextDay(), getNextDay(), "wlanapmac", "",2);
		JSONObject freeMobileMac=JSONObject.fromObject(freeMobileClickMac);
		String freeMobileMacData=freeMobileMac.getString("values");
		freeMobileMacData=freeMobileMacData.substring(1);
		freeMobileMacData=freeMobileMacData.substring(0,freeMobileMacData.length()-1);
		if(!freeMobileMacData.equals("0,0,0")){
			String[] freeMobileMacDataArray=freeMobileMacData.split("],");
			if(freeMobileMacDataArray.length>0){
				for(String i:freeMobileMacDataArray){
					UMStatisticsHashService.getInstance().umHset("MacFreeMobileClickNum"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		
		
		String pcClick=apiCnzzImpl.queryCnzzStatistic("pc+赏", getNextDay(), getNextDay(), "", "",2);
		JSONObject pcClickJson=JSONObject.fromObject(pcClick);
		String pcClickJsonStr=pcClickJson.getString("values");
		pcClickJsonStr=pcClickJsonStr.substring(1);
		pcClickJsonStr=pcClickJsonStr.substring(0, pcClickJsonStr.length()-1);
		int pcClickNum=Integer.valueOf(pcClickJsonStr.split(",")[0].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "pcClickNum", String.valueOf(pcClickNum));

		
		String pcMacClick=apiCnzzImpl.queryCnzzStatistic("pc+赏", getNextDay(), getNextDay(), "wlanapmac", "",2);
		JSONObject pcMacClickJson=JSONObject.fromObject(pcMacClick);
		String pcMacClickData=pcMacClickJson.getString("values");
		pcMacClickData=pcMacClickData.substring(1);
		pcMacClickData=pcMacClickData.substring(0,pcMacClickData.length()-1);
		if(!pcMacClickData.equals("0,0,0")){
			String[] pcMacClickDataArray=pcMacClickData.split("],");
			if(pcMacClickDataArray.length>0){
				for(String i:pcMacClickDataArray){
					UMStatisticsHashService.getInstance().umHset("MacPcClickNum"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		String mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "", "",2);
		JSONObject mobileUvJson=JSONObject.fromObject(mobileUv);
		String mobileUvJsonStr=mobileUvJson.getString("values");
		mobileUvJsonStr=mobileUvJsonStr.substring(1);
		mobileUvJsonStr=mobileUvJsonStr.substring(0, mobileUvJsonStr.length()-1);
		int mobileUV=Integer.valueOf(mobileUvJsonStr.split(",")[1].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "mobileUv", String.valueOf(mobileUV));

		
		String mobileMacUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "wlanapmac", "",2);
		JSONObject mobileMacUvJson=JSONObject.fromObject(mobileMacUv);
		String mobileMacUvData=mobileMacUvJson.getString("values");
		mobileMacUvData=mobileMacUvData.substring(1);
		mobileMacUvData=mobileMacUvData.substring(0,mobileMacUvData.length()-1);
		if(!mobileMacUvData.equals("0,0,0")){
			String[] mobileMacUvDataArray=mobileMacUvData.split("],");
			if(mobileMacUvDataArray.length>0){
				for(String i:mobileMacUvDataArray){
					UMStatisticsHashService.getInstance().umHset("MacMobileUv"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		String iosUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "", "os = 'ios'",2);
		JSONObject iosUvJson=JSONObject.fromObject(iosUv);
		String iosUvJsonStr=iosUvJson.getString("values");
		iosUvJsonStr=iosUvJsonStr.substring(1);
		iosUvJsonStr=iosUvJsonStr.substring(0, iosUvJsonStr.length()-1);
		int iosUV=Integer.valueOf(iosUvJsonStr.split(",")[1].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "iosUv", String.valueOf(iosUV));

		
		String iosMacUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "wlanapmac", "os = 'ios'",2);
		JSONObject iosMacUvJson=JSONObject.fromObject(iosMacUv);
		String iosMacUvData=iosMacUvJson.getString("values");
		iosMacUvData=iosMacUvData.substring(1);
		iosMacUvData=iosMacUvData.substring(0,iosMacUvData.length()-1);
		if(!iosMacUvData.equals("0,0,0")){
			String[] iosMacUvDataArray=iosMacUvData.split("],");
			if(iosMacUvDataArray.length>0){
				for(String i:iosMacUvDataArray){
					UMStatisticsHashService.getInstance().umHset("MacIosUv"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		String androidUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "", "os = 'android'",2);
		JSONObject androidUvJson=JSONObject.fromObject(androidUv);
		String androidUvJsonStr=androidUvJson.getString("values");
		androidUvJsonStr=androidUvJsonStr.substring(1);
		androidUvJsonStr=androidUvJsonStr.substring(0, androidUvJsonStr.length()-1);
		int androidUV=Integer.valueOf(androidUvJsonStr.split(",")[1].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "androidUv", String.valueOf(androidUV));

	
		String androidMacUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", getNextDay(), getNextDay(), "wlanapmac", "os = 'android'",2);
		JSONObject androidMacUvJson=JSONObject.fromObject(androidMacUv);
		String androidMacUvData=androidMacUvJson.getString("values");
		androidMacUvData=androidMacUvData.substring(1);
		androidMacUvData=androidMacUvData.substring(0,androidMacUvData.length()-1);
		if(!androidMacUvData.equals("0,0,0")){
			String[] androidMacUvDataArray=androidMacUvData.split("],");
			if(androidMacUvDataArray.length>0){
				for(String i:androidMacUvDataArray){
					UMStatisticsHashService.getInstance().umHset("MacAndroidUv"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		
		String mobileClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏", getNextDay(), getNextDay(), "", "",2);
		JSONObject mobileClickJson=JSONObject.fromObject(mobileClick);
		String mobileClickJsonStr=mobileClickJson.getString("values");
		mobileClickJsonStr=mobileClickJsonStr.substring(1);
		mobileClickJsonStr=mobileClickJsonStr.substring(0, mobileClickJsonStr.length()-1);
		int mobileClickNum=Integer.valueOf(mobileClickJsonStr.split(",")[0].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "mobileClickNum", String.valueOf(mobileClickNum));

		
		String mobileMacClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏", getNextDay(), getNextDay(), "wlanapmac", "",2);
		JSONObject mobileMacClickJson=JSONObject.fromObject(mobileMacClick);
		String mobileMacClickData=mobileMacClickJson.getString("values");
		mobileMacClickData=mobileMacClickData.substring(1);
		mobileMacClickData=mobileMacClickData.substring(0,mobileMacClickData.length()-1);
		if(!mobileMacClickData.equals("0,0,0")){
			String[] mobileMacClickDataArray=mobileMacClickData.split("],");
			if(mobileMacClickDataArray.length>0){
				for(String i:mobileMacClickDataArray){
					UMStatisticsHashService.getInstance().umHset("MacMobileClick"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		String iosClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏", getNextDay(), getNextDay(), "", "os = 'ios'",2);
		JSONObject iosClickJson=JSONObject.fromObject(iosClick);
		String iosClickJsonStr=iosClickJson.getString("values");
		iosClickJsonStr=iosClickJsonStr.substring(1);
		iosClickJsonStr=iosClickJsonStr.substring(0, iosClickJsonStr.length()-1);
		int iosClickNum=Integer.valueOf(iosClickJsonStr.split(",")[0].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "iosClickNum", String.valueOf(iosClickNum));

	
		String iosMacClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏", getNextDay(), getNextDay(), "wlanapmac", "os = 'ios'",2);
		JSONObject iosMacClickJson=JSONObject.fromObject(iosMacClick);
		String iosMacClickData=iosMacClickJson.getString("values");
		iosMacClickData=iosMacClickData.substring(1);
		iosMacClickData=iosMacClickData.substring(0,iosMacClickData.length()-1);
		if(!iosMacClickData.equals("0,0,0")){
			String[] iosMacClickDataArray=iosMacClickData.split("],");
			if(iosMacClickDataArray.length>0){
				for(String i:iosMacClickDataArray){
					UMStatisticsHashService.getInstance().umHset("MacIosClick"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		
		String androidClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏", getNextDay(), getNextDay(), "", "os = 'android'",2);
		JSONObject androidClickJson=JSONObject.fromObject(androidClick);
		String androidClickJsonStr=androidClickJson.getString("values");
		androidClickJsonStr=androidClickJsonStr.substring(1);
		androidClickJsonStr=androidClickJsonStr.substring(0, androidClickJsonStr.length()-1);
		int androidClickNum=Integer.valueOf(androidClickJsonStr.split(",")[0].replace(".0", "").trim());
		UMStatisticsHashService.getInstance().umHset(getNextDay(), "androidClickNum", String.valueOf(androidClickNum));

	
		String androidMacClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏", getNextDay(), getNextDay(), "wlanapmac", "os = 'android'",2);
		JSONObject androidMacClickJson=JSONObject.fromObject(androidMacClick);
		String androidMacClickData=androidMacClickJson.getString("values");
		androidMacClickData=androidMacClickData.substring(1);
		androidMacClickData=androidMacClickData.substring(0,androidMacClickData.length()-1);
		if(!androidMacClickData.equals("0,0,0")){
			String[] androidMacClickDataArray=androidMacClickData.split("],");
			if(androidMacClickDataArray.length>0){
				for(String i:androidMacClickDataArray){
					UMStatisticsHashService.getInstance().umHset("MacAndroidClick"+getNextDay(), i.split("=")[0].replace("%", ":").trim(), i.split(",")[1].replace(".0", "").trim());
				}
			}
		}
		OrderFacadeService orderUnitFacadeService = (OrderFacadeService) ctx.getBean("orderFacadeService",OrderFacadeService.class);
		try {
			readFile(WAIWANG_LOG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//存储当日设备的连接总数
		DeviceStatisticsHashService.getInstance().deviceMacHset(getNextDay(), "dayPV", String.valueOf(dayPVNum));
		//存储当日设备连接总人数
		DeviceStatisticsHashService.getInstance().deviceMacHset(getNextDay(), "dayUV", String.valueOf(dayUVNum));
		List<Order> orders=orderUnitFacadeService.findOrdersByTime(getNextDay()+" 00:00:00", getNextDay()+" 23:59:59");
		
		//订单创建数量
		long occ = 0;
		//订单支付数量
		long ofc = 0;
		//pc端订单创建数量
		long pc_occ = 0;
		//pc端订单支付数量
		long pc_ofc = 0;
		//移动端订单创建数量
		long mb_occ = 0;
		//移动端订单支付数量
		long mb_ofc = 0;
		//免费上网完成订单数
		long free_ofc=0;
		
		
		float ofaF=0;
		float pc_ofaF=0;
		float mb_ofaF=0;
		List<String> macList=new ArrayList<String>();
		if(orders!=null&&orders.size()>0){
			for(int i=0;i<orders.size();i++){
				
				if(orders.get(i).getType()==0 || orders.get(i).getType()==1){
					if(orders.get(i).getStatus()==10){
						ofaF+=Float.valueOf(orders.get(i).getAmount());
						ofc++;
						if(orders.get(i).getUmactype()==1){
							pc_ofaF+=Float.valueOf(orders.get(i).getAmount());
							pc_ofc++;
						}else{
							mb_ofaF+=Float.valueOf(orders.get(i).getAmount());
							mb_ofc++;
						}
					}
					if(orders.get(i).getUmactype()==1){
						pc_occ++;
					}else{
						mb_occ++;
					}
					occ++;
					if(macList!=null&&macList.size()>0){
						if(!macList.contains(orders.get(i).getMac())){  
							macList.add(orders.get(i).getMac());  
							//System.out.println(orders.get(i).getMac());
						}  
					}else{
						macList.add(orders.get(i).getMac());
					}
				}else if(orders.get(i).getType()==6){
					if(orders.get(i).getStatus()==10){
						free_ofc++;
					}
				}
				
			}
		}
		for(int i=0;i<macList.size();i++){
			//订单创建数量
			long socc = 0;
			//订单支付数量
			long sofc = 0;
			//pc端订单创建数量
			long spc_occ = 0;
			//pc端订单支付数量
			long spc_ofc = 0;
			//移动端订单创建数量
			long smb_occ = 0;
			//移动端订单支付数量
			long smb_ofc = 0;
			//免费上网订单数量
			long sfree_ofc=0;
			
			float sofaF=0;
			float spc_ofaF=0;
			float smb_ofaF=0;
			Map<String,Map<String,Object>> singleMapF=new HashMap<String,Map<String,Object>>();
			Map<String,Object> singleMapS=new HashMap<String,Object>();
			for(int j=0;j<orders.size();j++){
				if(macList.get(i)!=null){
					if(orders.get(j).getType()==0){
						if(macList.get(i).equals(orders.get(j).getMac())){
							if(orders.get(j).getStatus()==10){
								sofc++;
								sofaF+=(float)(Math.round(Float.valueOf(orders.get(j).getAmount())*100/100));
								if(orders.get(j).getUmactype()==1){
									spc_ofaF+=(float)(Math.round(Float.valueOf(orders.get(j).getAmount())*100/100));
									spc_ofc++;
								}else{
									smb_ofaF+=(float)(Math.round(Float.valueOf(orders.get(j).getAmount())*100/100));
									smb_ofc++;
								}
							}else{
								if(orders.get(j).getUmactype()==1){
									spc_occ++;
								}else{
									smb_occ++;
								}
							}
							socc++;
						}
					}else if(orders.get(j).getType()==6){
						if(macList.get(i).equals(orders.get(j).getMac())){
							if(orders.get(j).getStatus()==10){
								sfree_ofc++;
							}
						}
					}
				}
			}
			singleMapS.put("occ", socc);
			singleMapS.put("ofc", sofc);
			singleMapS.put("ofa", sofaF);
			singleMapS.put("pc_occ", spc_occ);
			singleMapS.put("pc_ofc", spc_ofc);
			singleMapS.put("pc_ofa", spc_ofaF);
			singleMapS.put("mb_occ", smb_occ);
			singleMapS.put("mb_ofc", smb_ofc);
			singleMapS.put("mb_ofa", smb_ofaF);
			singleMapS.put("free_ofc", sfree_ofc);
			DeviceStatisticsHashService.getInstance().deviceMacHset("MAC-"+getNextDay(), macList.get(i), JsonHelper.getJSONString(singleMapS));
		}
		//存储到redis缓存
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("occ", occ);
		resultMap.put("ofc", ofc);
		resultMap.put("ofa", ofaF);
		resultMap.put("pc_occ", pc_occ);
		resultMap.put("pc_ofc", pc_ofc);
		resultMap.put("pc_ofa", pc_ofaF);
		resultMap.put("mb_occ", mb_occ);
		resultMap.put("mb_ofc", mb_ofc);
		resultMap.put("mb_ofa", mb_ofaF);
		resultMap.put("free_ofc", free_ofc);
		DeviceStatisticsHashService.getInstance().deviceMacHset(getNextDay(), "stOrder",JsonHelper.getJSONString(resultMap));
		//getDeviceOrder();
		//存储mac地址
		Iterator iterator=SSIDMacList.entrySet().iterator();
		//循环获取map的key和value
		while(iterator.hasNext()){//只遍历一次,速度快
			Entry entry=(Entry)iterator.next();
			String currJson =  entry.getValue().toString();
			Map<String, Object> map = JsonHelper.getMapFromJson(currJson);
			String pv="0";
			String uv="0";
			if(map.get("pv")!=null){
				pv=(String) map.get("pv");
			}
			if(map.get("uv")!=null){
				uv=(String) map.get("uv");
			}
			DeviceStatisticsHashService.getInstance().deviceMacHset("MAC-PV-"+getNextDay(), entry.getKey().toString(),pv);
			DeviceStatisticsHashService.getInstance().deviceMacHset("MAC-UV-"+getNextDay(), entry.getKey().toString(), uv);
		}
		ctx.stop();
		ctx.close();
		System.exit(0);
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
		        in.close();
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
	
	/*public static void getDeviceOrder(){
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
		//result="{\"code\":\"200\",\"result\":{\"pc_occ\":294,\"pc_ofc\":127,\"pc_ofa\":\"296\",\"mb_occ\":10108,\"mb_ofc\":3931,\"mb_ofa\":\"1955\"}}";
		System.out.println(result);
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
			//System.out.println(resultMap.toString());
			DeviceStatisticsHashService.getInstance().deviceMacHset(getNextDay(), "stOrder",JsonHelper.getJSONString(resultMap));
		}
	}*/
	
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
