package com.bhu.statistics.controller.statistics;


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.statistics.controller.BaseController;
import com.bhu.statistics.util.DataUtils;
import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.NotifyUtil;
import com.bhu.statistics.util.enums.ErrorCodeEnum;

@Controller
public class QuerySSIdStatisticsController extends BaseController{
	private static Logger log = Logger.getLogger(QuerySSIdStatisticsController.class);
	@ResponseBody
	@RequestMapping(value="/querySSIDStatist", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String querySSIDStatist(HttpServletRequest request,HttpServletResponse response,String data) throws IOException{
	String umRes=staticsService.queryStatisticsByUM(data);
		//返回结果
		String result = StringUtils.EMPTY;
		if(StringUtils.isBlank(data)){
			log.info("请求参数为空");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "请求参数为空", true);
			//response.getWriter().print(JSONObject.fromObject(result));
			return result;
		}
		//时间类型
		String dateType = StringUtils.EMPTY;
		//开始时间
		String startTime = StringUtils.EMPTY;
		//结束时间
		String endTime = StringUtils.EMPTY;
		//当前页数
		String pageIndex = StringUtils.EMPTY;
		//每页显示条数
		String pageSize = StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			dateType = object.getString("type");
			startTime = object.getString("startTime");
			endTime = object.getString("endTime");
			pageIndex = object.getString("pn");
			pageSize = object.getString("ps");
 		} catch (Exception e) {
			log.info("JSON格式转换错误");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON格式转换错误", true);
			//response.getWriter().print(JSONObject.fromObject(result));
			return result;
		} 
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)){
			//按时间间隔查询SSID统计信息
			result = staticsService.querySSIDInfoByTime(data);
		}else{
			//按时间类型查询SSID统计信息
			result=staticsService.querySSIDInfoByType(data);
		}
		JSONObject umResJson=JSONObject.fromObject(umRes);
		JSONObject resJson=JSONObject.fromObject(result);
		@SuppressWarnings("unchecked")
		Map<String, Object> umResResult=(Map<String, Object>)umResJson.get("result");
		@SuppressWarnings("unchecked")
		Map<String, Object> resResult=(Map<String, Object>)resJson.get("result");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> dateUmList=(List<Map<String, Object>>) umResResult.get("dataList");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> dateSsidList=(List<Map<String, Object>>) resResult.get("ssidList");
		for(Map<String,Object> i:dateUmList){
			for(Map<String,Object> j:dateSsidList){
				if(j.get("currDate").equals(i.get("date"))){
					i.put("ssid", j);
					break;
				}
			}
		}
		List<Map<String,Object>> pageMapList = new ArrayList<Map<String,Object>>();
		//总条数
		int totalCount = dateUmList.size();
		//总页数
		int pagecount=0; 
		//每页显示总数
		int ps = Integer.parseInt(pageSize);
		//当前页码
		int pn = Integer.parseInt(pageIndex);
		
		int m=totalCount%ps;
		if(m>0){
			pagecount=totalCount/ps+1; 
		}else{
			pagecount=totalCount/ps;  
		}
		//截取起始下标
		int fromIndex = (pn - 1) * ps;
		//截取截止下标
		int toIndex = pn * ps;
		if(totalCount <= ps){
			pageMapList = dateUmList.subList(0, totalCount);
		}else{
			if(totalCount<toIndex){
				pageMapList = dateUmList.subList(fromIndex, totalCount);
			}else{
				pageMapList = dateUmList.subList(fromIndex, toIndex);
			}
		}
		Map<String,Object> totalMap=(Map<String, Object>) umResResult.get("total");
		totalMap.put("ssid", resResult.get("totalSSID"));;
		Map<String,Object> resMap=new HashMap<String,Object>();
		resMap.put("total", umResResult.get("total"));
		resMap.put("totalPage", pagecount);
		resMap.put("pn", pn);
		//resMap.put("dateList", dateUmList);
		resMap.put("dateList", pageMapList);
		String lastResult = NotifyUtil.success(resMap);
		return lastResult;
	}
	@ResponseBody
	@RequestMapping(value="/queryLineChart", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String queryLineChart(HttpServletRequest request,HttpServletResponse response,String data) throws IOException{
		//返回结果
		String result = StringUtils.EMPTY;
		if(StringUtils.isBlank(data)){
			log.info("请求参数为空");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "请求参数为空", true);
			//response.getWriter().print(JSONObject.fromObject(result));
			return result;
		}
		//时间类型
		String dateType = StringUtils.EMPTY;
		//开始时间
		String startTime = StringUtils.EMPTY;
		//结束时间
		String endTime = StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			dateType = object.getString("type");
			startTime = object.getString("startTime");
			endTime = object.getString("endTime");
		} catch (Exception e) {
			log.info("JSON格式转换错误");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON格式转换错误", true);
			//response.getWriter().print(JSONObject.fromObject(result));
			return result;
		} 
		result=staticsService.queryLineChart(data);
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)){
			//按时间间隔查询SSID统计信息
			result = staticsService.querySSIDInfoByTime(data);
		}else{
			//按时间类型查询SSID统计信息
			result=staticsService.querySSIDInfoByType(data);
		}
		return result;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value="/exportCsvData", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String exportCsvData(String data,HttpServletRequest request,HttpServletResponse response){
		String result = StringUtils.EMPTY;
		
		String umRes=staticsService.queryStatisticsByUM(data);
		if(StringUtils.isBlank(data)){
			log.info("请求参数为空");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "请求参数为空", true);
			//response.getWriter().print(JSONObject.fromObject(result));
			return result;
		}
		//时间类型
		String dateType = StringUtils.EMPTY;
		//开始时间
		String startTime = StringUtils.EMPTY;
		//结束时间
		String endTime = StringUtils.EMPTY;
		//当前页数
		String pageIndex = StringUtils.EMPTY;
		//每页显示条数
		String pageSize = StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			dateType = object.getString("type");
			startTime = object.getString("startTime");
			endTime = object.getString("endTime");
			pageIndex = object.getString("pn");
			pageSize = object.getString("ps");
 		} catch (Exception e) {
			log.info("JSON格式转换错误");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON格式转换错误", true);
			//response.getWriter().print(JSONObject.fromObject(result));
			return result;
		} 
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)){
			//按时间间隔查询SSID统计信息
			result = staticsService.querySSIDInfoByTime(data);
		}else{
			//按时间类型查询SSID统计信息
			result=staticsService.querySSIDInfoByType(data);
		}
		JSONObject umResJson=JSONObject.fromObject(umRes);
		JSONObject resJson=JSONObject.fromObject(result);
		@SuppressWarnings("unchecked")
		Map<String, Object> umResResult=(Map<String, Object>)umResJson.get("result");
		@SuppressWarnings("unchecked")
		Map<String, Object> resResult=(Map<String, Object>)resJson.get("result");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> dateUmList=(List<Map<String, Object>>) umResResult.get("dataList");
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> dateSsidList=(List<Map<String, Object>>) resResult.get("ssidList");
		for(Map<String,Object> i:dateUmList){
			for(Map<String,Object> j:dateSsidList){
				if(j.get("currDate").equals(i.get("date"))){
					i.put("ssid", j);
					break;
				}
			}
		}
		Map<String,Object> totalMap=(Map<String, Object>) umResResult.get("total");
		totalMap.put("ssid", resResult.get("totalSSID"));;
		Map<String,Object> resMap=new HashMap<String,Object>();
		resMap.put("total", umResResult.get("total"));
		resMap.put("dateList", dateUmList);
		//获取当前tomcat路径
		HttpSession session = request.getSession(); 
		ServletContext application = session.getServletContext(); 
		String serverRealPath = application.getRealPath("/") ;
		
		//文件保存路径
		String filename = StringUtils.EMPTY;
		//文件下载地址
		String downFileName = StringUtils.EMPTY;
		try {
			//文件保存地址
			String saveFilePath = serverRealPath+"export/";
			File file = new File(saveFilePath);
			if(!file.exists()){
				file.mkdirs();
			}
			BufferedOutputStream bos = null;
			//OutputStream fos = null;
			//fos = response.getOutputStream();
			String time=DataUtils.getTimestamp().toString();
			time=time.substring(0,time.indexOf(".")).replaceAll(" ", "").replaceAll(":", "").replaceAll("-", "");
			downFileName = "statistics-"+time+".csv";
			filename = saveFilePath+"statistics-"+time+".csv";
			bos = new BufferedOutputStream(new FileOutputStream(filename));
			//response.setHeader("Content-disposition","attachment;filename="+URLEncoder.encode("statistics-"+time+".csv", "utf-8"));
			Map<String,Object> obj = (Map<String, Object>) umResResult.get("total");
			Map<String,Object> ssIdMap = (Map<String, Object>) obj.get("ssid");
			String firstHeader = (String) obj.get("date")+"                     总收益：￥"+ssIdMap.get("totalGains")+"\r\n";
			String headers="设备总数,SSID关联总次数,SSID连接总人数,在线/总数,单台订单(个),单台收益(元)\r\n";
			//这个就就是弹出下载对话框的关键代码
			bos.write(firstHeader.getBytes("GBK"));
			bos.write(headers.getBytes("GBK"));
			String ssIdStr = " ,"+ssIdMap.get("totalPV")+","+ssIdMap.get("totalUV")+","+ssIdMap.get("totalDOC")+"/"+ssIdMap.get("totalDC")+","+ssIdMap.get("totalSingleOrderNum")+","+ssIdMap.get("totalSingleGains")+""+"\r\n";
			bos.write(ssIdStr.getBytes("GBK"));
			String ymHeaders=",打赏认证页UV,'赏'字按钮点击数,'赏'字按钮人均点击次数,订单创建订单数,订单创建赏转化率,订单创建订单创建率,打赏成功打赏成功数,打赏成功金额合计,打赏成功订单成功率\r\n";
			bos.write(ymHeaders.getBytes("GBK"));
			//
			Map<String,Object> ymTotal = (Map<String, Object>) obj.get("total");
			String ymTotalStr = "总数,"+ymTotal.get("uv")+","+ymTotal.get("clickNum")+","+ymTotal.get("clickAverNum")+","+ymTotal.get("orderNum")+","+ymTotal.get("clickConversion")+","+ymTotal.get("orderConversion")+","+ymTotal.get("orderComplete")+","+ymTotal.get("orderAmount")+","+ymTotal.get("orderComConversion")+""+"\r\n";
			bos.write(ymTotalStr.getBytes("GBK"));
			Map<String,Object> pcTotal = (Map<String, Object>) obj.get("PC");
			String pcTotalStr = "PC端,"+pcTotal.get("uv")+","+pcTotal.get("clickNum")+","+pcTotal.get("clickAverNum")+","+pcTotal.get("orderNum")+","+pcTotal.get("clickConversion")+","+pcTotal.get("orderConversion")+","+pcTotal.get("orderComplete")+","+pcTotal.get("orderAmount")+","+pcTotal.get("orderComConversion")+""+"\r\n";
			bos.write(pcTotalStr.getBytes("GBK"));
			Map<String,Object> mobileTotal = (Map<String, Object>) obj.get("mobile");
			String mobileTotalStr = "Mobile,"+mobileTotal.get("uv")+","+mobileTotal.get("clickNum")+","+mobileTotal.get("clickAverNum")+","+mobileTotal.get("orderNum")+","+mobileTotal.get("clickConversion")+","+mobileTotal.get("orderConversion")+","+mobileTotal.get("orderComplete")+","+mobileTotal.get("orderAmount")+","+mobileTotal.get("orderComConversion")+""+"\r\n";
			bos.write(mobileTotalStr.getBytes("GBK"));
			Map<String,Object> iosTotal = (Map<String, Object>) obj.get("ios");
			String iosTotalStr = "iOS,"+iosTotal.get("uv")+","+iosTotal.get("clickNum")+","+iosTotal.get("clickAverNum")+","+iosTotal.get("orderNum")+","+iosTotal.get("clickConversion")+","+iosTotal.get("orderConversion")+","+iosTotal.get("orderComplete")+","+iosTotal.get("orderAmount")+","+iosTotal.get("orderComConversion")+""+"\r\n";
			bos.write(iosTotalStr.getBytes("GBK"));
			Map<String,Object> androidTotal = (Map<String, Object>) obj.get("android");
			String androidTotalStr = "Android,"+androidTotal.get("uv")+","+androidTotal.get("clickNum")+","+androidTotal.get("clickAverNum")+","+androidTotal.get("orderNum")+","+androidTotal.get("clickConversion")+","+androidTotal.get("orderConversion")+","+androidTotal.get("orderComplete")+","+androidTotal.get("orderAmount")+","+androidTotal.get("orderComConversion")+""+"\r\n";
			bos.write(androidTotalStr.getBytes("GBK"));
			for (int i = 0; i < dateUmList.size(); i++) {
				Map<String,Object> dataMap = dateUmList.get(i);
				Map<String,Object> currSSIdMap = (Map<String, Object>) dataMap.get("ssid");
				String currFirstHeader = (String) dataMap.get("date")+"                    总收益：￥"+currSSIdMap.get("dayGains")+"\r\n";
				String currHeaders="设备总数,SSID关联总次数,SSID连接总人数,在线/总数,单台订单(个),单台收益(元)\r\n";
				//这个就就是弹出下载对话框的关键代码
				bos.write(currFirstHeader.getBytes("GBK"));
				bos.write(currHeaders.getBytes("GBK"));
				String currSSIdStr = " ,"+currSSIdMap.get("dayPV")+","+currSSIdMap.get("dayUV")+","+currSSIdMap.get("doc")+"/"+currSSIdMap.get("dc")+","+currSSIdMap.get("singleOrderNum")+","+currSSIdMap.get("singleGains")+""+"\r\n";
				bos.write(currSSIdStr.getBytes("GBK"));
				String currYMHeaders=",打赏认证页UV,'赏'字按钮点击数,'赏'字按钮人均点击次数,订单创建订单数,订单创建赏转化率,订单创建订单创建率,打赏成功打赏成功数,打赏成功金额合计,打赏成功订单成功率\r\n";
				bos.write(currYMHeaders.getBytes("GBK"));
				//
				Map<String,Object> currYMTotal = (Map<String, Object>) dataMap.get("total");
				String currYMTotalStr = "总数,"+currYMTotal.get("uv")+","+currYMTotal.get("clickNum")+","+currYMTotal.get("clickAverNum")+","+currYMTotal.get("orderNum")+","+currYMTotal.get("clickConversion")+","+currYMTotal.get("orderConversion")+","+currYMTotal.get("orderComplete")+","+currYMTotal.get("orderAmount")+","+currYMTotal.get("orderComConversion")+""+"\r\n";
				bos.write(currYMTotalStr.getBytes("GBK"));
				Map<String,Object> currPCTotal = (Map<String, Object>) dataMap.get("PC");
				String currPCTotalStr = "PC端,"+currPCTotal.get("uv")+","+currPCTotal.get("clickNum")+","+currPCTotal.get("clickAverNum")+","+currPCTotal.get("orderNum")+","+currPCTotal.get("clickConversion")+","+currPCTotal.get("orderConversion")+","+currPCTotal.get("orderComplete")+","+currPCTotal.get("orderAmount")+","+currPCTotal.get("orderComConversion")+""+"\r\n";
				bos.write(currPCTotalStr.getBytes("GBK"));
				Map<String,Object> currMobileTotal = (Map<String, Object>) dataMap.get("mobile");
				String currMobileTotalStr = "Mobile,"+currMobileTotal.get("uv")+","+currMobileTotal.get("clickNum")+","+currMobileTotal.get("clickAverNum")+","+currMobileTotal.get("orderNum")+","+currMobileTotal.get("clickConversion")+","+currMobileTotal.get("orderConversion")+","+currMobileTotal.get("orderComplete")+","+currMobileTotal.get("orderAmount")+","+currMobileTotal.get("orderComConversion")+""+"\r\n";
				bos.write(currMobileTotalStr.getBytes("GBK"));
				Map<String,Object> currIOSTotal = (Map<String, Object>) dataMap.get("ios");
				String currIOSTotalStr = "iOS,"+currIOSTotal.get("uv")+","+currIOSTotal.get("clickNum")+","+currIOSTotal.get("clickAverNum")+","+currIOSTotal.get("orderNum")+","+currIOSTotal.get("clickConversion")+","+currIOSTotal.get("orderConversion")+","+currIOSTotal.get("orderComplete")+","+currIOSTotal.get("orderAmount")+","+currIOSTotal.get("orderComConversion")+""+"\r\n";
				bos.write(currIOSTotalStr.getBytes("GBK"));
				Map<String,Object> currAndroidTotal = (Map<String, Object>) dataMap.get("android");
				String currAndroidTotalStr = "Android,"+currAndroidTotal.get("uv")+","+currAndroidTotal.get("clickNum")+","+currAndroidTotal.get("clickAverNum")+","+currAndroidTotal.get("orderNum")+","+currAndroidTotal.get("clickConversion")+","+currAndroidTotal.get("orderConversion")+","+currAndroidTotal.get("orderComplete")+","+currAndroidTotal.get("orderAmount")+","+currAndroidTotal.get("orderComConversion")+""+"\r\n";
				bos.write(currAndroidTotalStr.getBytes("GBK"));
			}
		    bos.flush();
		    //fos.close();
		    bos.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String,Object> body = new HashMap<String,Object>();
		String responseUrl = "http://101.200.151.189:8080/msip_bhu_statistics/export/";
		body.put("path", responseUrl+downFileName);
		String responseStr = NotifyUtil.success(body);
		log.info("下载文件地址为："+responseStr);
		return responseStr;
	}
}
