package com.bhu.statistics.logic.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bhu.statistics.logic.IUMLogic;
import com.bhu.statistics.util.DataUtils;
import com.bhu.statistics.util.DateUtils;
import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.NotifyUtil;
import com.bhu.statistics.util.cache.BhuCache;
import com.bhu.statistics.util.enums.ErrorCodeEnum;
import com.bhu.statistics.util.file.FileHandling;
import com.bhu.statistics.util.um.OpenApiCnzzImpl;

public class UMLogicImpl implements IUMLogic{
	private static UMLogicImpl instance = null;
	
	private static Logger log = Logger.getLogger(UMLogicImpl.class);
	
	public static synchronized UMLogicImpl getInstance() {
		if (instance == null) {
			instance = new UMLogicImpl();
		}
		return instance;
	}
	
	/**
	 * ˽�л���������
	 * <p>
	 * Title: ˽�л���������
	 * </p>
	 * <p>
	 * Description:����Ĺ�����������˽�л�
	 * </p>
	 * <p>
	 * Author��gavin
	 * </p>
	 */
	private UMLogicImpl() {

	}
	@Override
	public String querySSIDStatistics(String data) {
		//���ؽ��
		String result = StringUtils.EMPTY;
		if(StringUtils.isBlank(data)){
			log.info("�������Ϊ��");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "�������Ϊ��", true);
			return result;
		} 
		//��ѯ����
		String date = StringUtils.EMPTY;
		//SSID
		String ssId = StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			date = object.getString("date");
			ssId = object.getString("ssId");
 		} catch (Exception e) {
			log.info("JSON��ʽת������");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON��ʽת������", true);
			return result;
		}
		if(StringUtils.isBlank(ssId)){
			String dayPv = BhuCache.getInstance().getDayPV(date, "dayPV");
			String dayUv = BhuCache.getInstance().getDayUV(date, "dayUV");
			Map<String,Object> body = new HashMap<String,Object>();
			body.put("uv", dayUv);
			body.put("pv", dayPv);
			result = NotifyUtil.success(body);
		}else{
			String num = BhuCache.getInstance().getSSID(date, ssId);
			JSONObject object = JSONObject.fromObject(num);
			Map<String,Object> body = new HashMap<String,Object>();
			body.put("uv", object.get("uv"));
			body.put("pv", object.get("pv"));
			result = NotifyUtil.success(body);
		}
		return result;
	}

	@Override
	public String queryAllPVAndUV(String data) {
		return null;
	}

	@Override
	public String queryDayPVAndUV(String data) {
		return null;
	}
	
	/**
	 * 查询SSID统计信息
	 * @author Jason
	 */
	@Override
	public String querySSIDInfoByType(String data) {
		//返回结果
		String result = StringUtils.EMPTY;
		if(StringUtils.isBlank(data)){
			log.info("请求参数为空");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "请求参数为空", true);
			return result;
		}
		//uv总数
		int totalUV = 0;
		//pv总数
		int totalPV = 0;
		//设备总数
		int totalDC = 0;
		//设备在线总数
		int totalDOC = 0;
		//单台订单总数
		double totalSingleOrderNum = 0;
		//单台收益总数
		double totalSingleGains = 0;
		//全部设备收益总金额
		double totalGains = 0;
		//每天uv总数
		int dayUV = 0 ;
		int dayPV = 0;
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
			return result;
		}
		//组装结果集
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;
		//查询最近七天SSID统计信息
		//获取时间列表
		if(StringUtils.isBlank(dateType)){
			log.info("时间类型为空");
			result =  NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "时间类型为空", true);
			return result;
		}
		List<String> dateList = DateUtils.getLastDay(Integer.parseInt(dateType));
		String date = StringUtils.EMPTY;
		for (int i = 0; i < dateList.size(); i++) {
			date = dateList.get(i);
			String dayPv = BhuCache.getInstance().getDayPV(date, "dayPV");
			String dayUv = BhuCache.getInstance().getDayUV(date, "dayUV");
			if(StringUtils.isNotBlank(dayPv)){
				dayPV = Integer.parseInt(dayPv);
			}
			if(StringUtils.isNotBlank(dayUv)){
				dayUV = Integer.parseInt(dayUv);
			}
			totalPV += dayPV;
			totalUV += dayUV;
			map = new HashMap<String,Object>();
			map.put("currDate", date);
			map.put("dayUV", dayUV);
			map.put("dayPV", dayPV);
			//获取设备总数以及设备在线数
			String equipment = StringUtils.EMPTY;
			equipment = BhuCache.getInstance().getEquipment(startTime, "equipment");
			JSONObject obj = JSONObject.fromObject(equipment);
			int dc = 0;
			int doc = 0;
			if(StringUtils.isBlank(equipment)){
				map.put("dc", dc);
				map.put("doc", doc);
			}else{
				//处理结果
				if(obj.get("dc") != null && obj.get("doc") != null){
					dc = (Integer)obj.get("dc");
					doc = (Integer)obj.get("doc");
					map.put("dc", dc);
					map.put("doc", doc);
				}else{
					map.put("dc", dc);
					map.put("doc", doc);
				}
			}
			totalDC += dc;
			totalDOC += doc;
			//获取当天订单统计数量
			String orderStatist = StringUtils.EMPTY; 
			orderStatist = BhuCache.getInstance().getStOrder(FileHandling.getNextDay(),"stOrder");
			double singleOrderNum = 0;
			double singleGains = 0;
			double dayGains = 0;
			if(StringUtils.isBlank(orderStatist)){
				map.put("singleOrderNum", singleOrderNum);
				map.put("singleGains", singleGains);
				map.put("dayGains", dayGains);
			}else{
				JSONObject orderObj = JSONObject.fromObject(orderStatist);
				if(orderObj.get("occ") != null && orderObj.get("ofc") != null && orderObj.get("ofa") != null){
					//单台订单
					int occ = (Integer)orderObj.get("occ");
					if(doc == 0){
						map.put("singleOrderNum", singleOrderNum);
						map.put("singleGains", singleGains);
					}else{
						singleOrderNum = (double) (occ/doc);
						map.put("singleOrderNum", singleOrderNum);
						//单台收益
						Double ofa = orderObj.getDouble("ofa");
						singleGains = ofa/doc;
						map.put("singleGains", singleGains);
					}
					dayGains = orderObj.getDouble("ofa");
					map.put("dayGains", dayGains);
				}else{
					map.put("singleOrderNum", singleOrderNum);
					map.put("singleGains", singleGains);
					map.put("dayGains", dayGains);
				}
			}
			totalSingleGains += singleGains;
			totalSingleOrderNum += singleOrderNum;
			totalGains += dayGains;
			listMap.add(map);
		}
		Map<String,Object> totalMap=new HashMap<String,Object>();
		totalMap.put("totalPV", totalPV);
		totalMap.put("totalUV", totalUV);
		totalMap.put("totalDC", totalDC);
		totalMap.put("totalDOC", totalDOC);
		totalMap.put("totalSingleGains", totalSingleGains);
		totalMap.put("totalSingleOrderNum", totalSingleOrderNum);
		totalMap.put("totalGains", totalGains);
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("ssidList", listMap);
		body.put("totalSSID", totalMap);
		result = NotifyUtil.success(body);
		return result;
	}

	@Override
	public String queryStatisticsByUM(String data) {
		String dataType=StringUtils.EMPTY;
		String beginTime=StringUtils.EMPTY;
		String endTime=StringUtils.EMPTY;
		
		String result=StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			dataType = object.getString("type");
			beginTime = object.getString("startTime");
			endTime = object.getString("endTime");
 		} catch (Exception e) {
			log.info("JSON转化错误");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON转化错误", true);
			return result;
		}
		List<String> daysList=new ArrayList<String>();
		if(StringUtils.isBlank(beginTime)||StringUtils.isBlank(endTime)){
			if(StringUtils.isNotBlank(dataType)){
				daysList=DateUtils.getLastDay(Integer.valueOf(dataType));
				beginTime=daysList.get(daysList.size()-1);
				endTime=DataUtils.beforeDay();
			}else{
				return NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "时间参数为空!");
			}
		}else{
			daysList=DateUtils.getDaysList(beginTime, endTime);
		}
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		String pcUv= apiCnzzImpl.queryCnzzStatistic("PC打赏页PV", beginTime, endTime, "date", "",1);
		String pcClick=apiCnzzImpl.queryCnzzStatistic("pc+赏", beginTime, endTime, "date", "",1);
		String mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", beginTime, endTime, "date,os", "os in ('android','ios')",2);
		String mobileClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", beginTime, endTime, "date,os", "os in ('android','ios')",2);
		JSONObject pcUvJson=JSONObject.fromObject(pcUv);
		JSONObject pcClickJson=JSONObject.fromObject(pcClick);
		JSONObject mobileUvJson=JSONObject.fromObject(mobileUv);
		JSONObject mobileClickJson=JSONObject.fromObject(mobileClick);
		
		String pcUvJsonStr=pcUvJson.getString("values");
		pcUvJsonStr=pcUvJsonStr.substring(1, pcUvJsonStr.length()-1);
		String[] pcUvArray=pcUvJsonStr.split("\\],");
		List<Map<String,Object>> pcUvList=new ArrayList<Map<String,Object>>();
		for(int i=0;i<pcUvArray.length;i++){
			Map<String,Object> singleMap=new HashMap<String,Object>();
			singleMap.put("date", pcUvArray[i].split("=")[0].trim());
			singleMap.put("uv", pcUvArray[i].split("=")[1].split(",")[1].replaceAll(".0", "").trim());
			pcUvList.add(singleMap);
		}
		String pcClickJsonStr=pcClickJson.getString("values");
		pcClickJsonStr=pcClickJsonStr.substring(1, pcClickJsonStr.length()-2);
		String[] pcClickArray=pcClickJsonStr.split("\\],");
		List<Map<String,Object>> pcClickList=new ArrayList<Map<String,Object>>();
		for(int i=0;i<pcClickArray.length;i++){
			Map<String,Object> singleMap=new HashMap<String,Object>();
			singleMap.put("date", pcClickArray[i].split("=")[0].trim());
			singleMap.put("pv", pcClickArray[i].split("=")[1].split(",")[0].replaceAll(".0", "").substring(1).trim());
			singleMap.put("average", pcClickArray[i].split("=")[1].split(",")[2].replaceAll(".0", "").trim());
			pcClickList.add(singleMap);
		}
		String mobileUvJsonStr=mobileUvJson.getString("values");
		mobileUvJsonStr=mobileUvJsonStr.substring(1, mobileUvJsonStr.length()-1);
		String[] mobileUvJsonStrArray=mobileUvJsonStr.split("\\},");
		List<Map<String,Object>> mobileUvJsonStrList=new ArrayList<Map<String,Object>>();
		for(int i=0;i<mobileUvJsonStrArray.length;i++){
			Map<String,Object> singleMap=new HashMap<String,Object>();
			singleMap.put("date", mobileUvJsonStrArray[i].split("=")[0].trim());
			List<Map<String,Object>> typeList=new ArrayList<Map<String,Object>>();
			String[] typeArray=mobileUvJsonStrArray[i].split("=\\{")[1].split("\\],");
			for(int j=0;j<typeArray.length;j++){
				Map<String,Object> typeMap=new HashMap<String,Object>();
				typeMap.put("type", typeArray[j].split("=")[0].trim());
				typeMap.put("uv", typeArray[j].split("=")[1].split(",")[1].trim().replace(".0", "").trim());
				typeList.add(typeMap);
			}
			singleMap.put("typeList", typeList);
			mobileUvJsonStrList.add(singleMap);
		}
		String mobileClickJsonStr=mobileClickJson.getString("values");
		mobileClickJsonStr=mobileClickJsonStr.substring(1, mobileClickJsonStr.length()-2);
		String[] mobileClickJsonStrArray=mobileClickJsonStr.split("\\},");
		List<Map<String,Object>> mobileClickJsonStrList=new ArrayList<Map<String,Object>>();
		for(int i=0;i<mobileClickJsonStrArray.length;i++){
			Map<String,Object> singleMap=new HashMap<String,Object>();
			singleMap.put("date", mobileClickJsonStrArray[i].split("=")[0].trim());
			List<Map<String,Object>> typeList=new ArrayList<Map<String,Object>>();
			String[] typeArray=mobileClickJsonStrArray[i].split("=\\{")[1].substring(0, mobileClickJsonStrArray[i].split("=\\{")[1].length()-1).split("\\],");
			for(int j=0;j<typeArray.length;j++){
				Map<String,Object> typeMap=new HashMap<String,Object>();
				typeMap.put("type", typeArray[j].split("=\\[")[0].trim());
				typeMap.put("pv", typeArray[j].split("=\\[")[1].split(",")[0].trim().replace(".0", ""));
				typeMap.put("average", typeArray[j].split("=\\[")[1].split(",")[2].trim().replace(".0", ""));
				typeList.add(typeMap);
			}
			singleMap.put("typeList", typeList);
			mobileClickJsonStrList.add(singleMap);
		}
		List<LinkedHashMap<String,Object>> resMaps=new ArrayList<LinkedHashMap<String,Object>>();
		int totalUv=0;
		int totalClickNum=0;
		
		int totalAndroidUV=0;
		int totalAndroidClickNum=0;
		int totalIosUV=0;
		int totalIosClickNum=0;
		int totalPcUV=0;
		int totalPcClickNum=0;
		
		int totalPcOrderNum=0;
		int totalPcOrderComplete=0;
		int totalPcOrderAmount=0;
		int totalMbOrderNum=0;
		int totalMbOrderComplete=0;
		int totalMbOrderAmount=0;
		for(int i=0;i<daysList.size();i++){
			LinkedHashMap<String,Object> singleMap=new LinkedHashMap<String,Object>();
			singleMap.put("date", daysList.get(i));
			String pcOrderNumStr=BhuCache.getInstance().getStOrder(daysList.get(i), "pc_occ");
			int pcOrderNum=0;
			if(StringUtils.isNotBlank(pcOrderNumStr)){
				pcOrderNum=Integer.valueOf(pcOrderNumStr);
			}
			String pcOrderCompleteStr=BhuCache.getInstance().getStOrder(daysList.get(i), "pc_ofc");
			int pcOrderComplete=0;
			if(StringUtils.isNotBlank(pcOrderCompleteStr)){
				pcOrderComplete=Integer.valueOf(pcOrderCompleteStr);
			}
			String pcOrderAmountStr=BhuCache.getInstance().getStOrder(daysList.get(i), "pc_ofa");
			int pcOrderAmount=0;
			if(StringUtils.isNotBlank(pcOrderAmountStr)){
				pcOrderAmount=Integer.valueOf(pcOrderAmountStr);
			}
			String mbOrderNumStr=BhuCache.getInstance().getStOrder(daysList.get(i), "mb_occ");
			int mbOrderNum=0;
			if(StringUtils.isNotBlank(mbOrderNumStr)){
				mbOrderNum=Integer.valueOf(mbOrderNumStr);
			}
			String mbOrderCompleteStr=BhuCache.getInstance().getStOrder(daysList.get(i), "mb_ofc");
			int mbOrderComplete=0;
			if(StringUtils.isNotBlank(mbOrderCompleteStr)){
				mbOrderComplete=Integer.valueOf(mbOrderCompleteStr);
			}
			String mbOrderAmountStr=BhuCache.getInstance().getStOrder(daysList.get(i), "mb_ofa");
			int mbOrderAmount=0;
			if(StringUtils.isNotBlank(mbOrderAmountStr)){
				mbOrderComplete=Integer.valueOf(mbOrderAmountStr);
			}
			totalPcOrderNum+=pcOrderNum;
			totalPcOrderComplete+=pcOrderComplete;
			totalPcOrderAmount+=pcOrderAmount;
			totalMbOrderNum+=mbOrderNum;
			totalMbOrderComplete+=mbOrderComplete;
			totalMbOrderAmount+=mbOrderAmount;
			
			int mobileUV=0;
			int mobileClickNum=0;
			int androidUV=0;
			int androidClickNum=0;
			int iosUV=0;
			int iosClickNum=0;
			
			for(int j=0;j<mobileUvJsonStrList.size();j++){
				if(mobileUvJsonStrList.get(j).get("date").equals(daysList.get(i))){
					List<Map<String,Object>> mobileSingleMap=new ArrayList<Map<String,Object>>();
					mobileSingleMap=(List<Map<String, Object>>) mobileUvJsonStrList.get(j).get("typeList");
					for(int n=0;n<mobileSingleMap.size();n++){
						if(mobileSingleMap.get(n).get("type").equals("android")){
							androidUV=Integer.valueOf( mobileSingleMap.get(n).get("uv").toString());
						}else{
							iosUV=Integer.valueOf(mobileSingleMap.get(n).get("uv").toString());
						}
					}
					mobileUV=androidUV+iosUV;
				}
			}
			for(int j=0;j<mobileClickJsonStrList.size();j++){
				if(mobileClickJsonStrList.get(j).get("date").equals(daysList.get(i))){
					List<Map<String,Object>> mobileSingleMap=new ArrayList<Map<String,Object>>();
					mobileSingleMap=(List<Map<String, Object>>) mobileClickJsonStrList.get(j).get("typeList");
					for(int n=0;n<mobileSingleMap.size();n++){
						if(mobileSingleMap.get(n).get("type").equals("android")){
							androidClickNum=Integer.valueOf(mobileSingleMap.get(n).get("pv").toString());
							iosClickNum=Integer.valueOf(mobileSingleMap.get(n).get("pv").toString());
						}
					}
					mobileClickNum=androidClickNum+iosClickNum;
				}
			}
			int pcUV=0;
			for(int j=0;j<pcUvList.size();j++){
				if(pcUvList.get(j).get("date").equals(daysList.get(i))){
					pcUV=Integer.valueOf( pcUvList.get(j).get("uv").toString());
				}
			}
			int pcClickNum=0;
			for(int j=0;j<pcClickList.size();j++){
				if(pcClickList.get(j).get("date").equals(daysList.get(i))){
					pcClickNum=Integer.valueOf(pcClickList.get(j).get("pv").toString());
				}
			}
			Map<String,Object> totalMap=new HashMap<String,Object>();
			
			totalMap.put("uv", pcUV+mobileUV);
			totalMap.put("clickNum", pcClickNum+mobileClickNum);
			totalMap.put("clickAverNum", 0);
			totalMap.put("orderConversion", 0);
			totalMap.put("orderComConversion", 0);
			if((pcUV+mobileUV)!=0){
			}
			totalMap.put("orderNum", pcOrderNum+mbOrderNum);
			totalMap.put("clickConversion", 0);
			if((pcClickNum+mobileClickNum)!=0){
				totalMap.put("clickConversion", (pcOrderNum+mbOrderNum)/(pcClickNum+mobileClickNum)*1.0);
			}
			totalMap.put("orderComplete", pcOrderComplete+mbOrderComplete);
			totalMap.put("orderAmount", pcOrderAmount+mbOrderAmount);
			singleMap.put("total", totalMap);
			
			
			Map<String,Object> pcMap=new HashMap<String,Object>();
			
			pcMap.put("uv", pcUV);
			pcMap.put("clickNum", pcClickNum);
			pcMap.put("clickAverNum", 0);
			pcMap.put("orderConversion", 0);
			pcMap.put("orderComConversion", 0);
			if(pcUV!=0){
				pcMap.put("clickAverNum", pcClickNum/pcUV*1.0);
				pcMap.put("orderConversion", pcOrderNum/pcUV*1.0);
				pcMap.put("orderComConversion", pcOrderComplete/pcUV*1.0);
			}
			pcMap.put("orderNum", pcOrderNum);
			pcMap.put("clickConversion", 0);
			if(pcClickNum!=0){
				pcMap.put("clickConversion", pcOrderNum/pcClickNum*1.0);
			}
			pcMap.put("orderComplete", pcOrderComplete);
			pcMap.put("orderAmount", pcOrderAmount);
			singleMap.put("PC", pcMap);
			
			Map<String,Object> mobileMap=new HashMap<String,Object>();
			
			mobileMap.put("uv", mobileUV);
			mobileMap.put("clickNum", mobileClickNum);
			mobileMap.put("clickAverNum", 0);
			mobileMap.put("orderConversion", 0);
			mobileMap.put("orderComConversion", 0);
			if(mobileUV!=0){
				mobileMap.put("clickAverNum", mobileClickNum/mobileUV*1.0);
				mobileMap.put("orderConversion", mbOrderNum/mobileUV*1.0);
				mobileMap.put("orderComConversion", mbOrderComplete/mobileUV*1.0);
			}
			mobileMap.put("orderNum", mbOrderNum);
			mobileMap.put("clickConversion", 0);
			if(mobileClickNum!=0){
				mobileMap.put("clickConversion", mbOrderNum/mobileClickNum*1.0);
			}
			mobileMap.put("orderComplete", mbOrderComplete);
			mobileMap.put("orderAmount", mbOrderAmount);
			singleMap.put("mobile", mobileMap);
			
			Map<String,Object> iosMap=new HashMap<String,Object>();
			
			iosMap.put("uv", iosUV);
			iosMap.put("clickNum", iosClickNum);
			iosMap.put("clickAverNum", 0);
			if(iosUV!=0){
				iosMap.put("clickAverNum", iosClickNum/iosUV*1.0);
			}
			iosMap.put("orderNum", "-");
			iosMap.put("clickConversion", "-");
			iosMap.put("orderConversion", "-");
			iosMap.put("orderComplete", "-");
			iosMap.put("orderAmount", "-");
			iosMap.put("orderComConversion", "-");
			singleMap.put("ios", iosMap);
			
			Map<String,Object> androidMap=new HashMap<String,Object>();
			
			androidMap.put("uv", androidUV);
			androidMap.put("clickNum", androidClickNum);
			androidMap.put("clickAverNum", 0);
			if(androidUV!=0){
				androidMap.put("clickAverNum", androidClickNum/androidUV*1.0);
			}
			androidMap.put("orderNum", "-");
			androidMap.put("clickConversion", "-");
			androidMap.put("orderConversion", "-");
			androidMap.put("orderComplete", "-");
			androidMap.put("orderAmount", "-");
			androidMap.put("orderComConversion", "-");
			singleMap.put("android", androidMap);
			
			resMaps.add(singleMap);
			totalAndroidUV+=androidUV;
			totalAndroidClickNum+=androidClickNum;
			
			totalIosUV+=iosUV;
			totalIosClickNum+=iosClickNum;
			
			totalPcUV+=pcUV;
			totalPcClickNum+=pcClickNum;
			
			totalUv+=pcUV+mobileUV;
			totalClickNum+=pcClickNum+mobileClickNum;
		}
		LinkedHashMap<String,Object> tMaps=new LinkedHashMap<String,Object>();
		Map<String,Object> totalMap=new HashMap<String,Object>();
		totalMap.put("uv", totalUv);
		totalMap.put("clickNum", totalClickNum);
		totalMap.put("clickAverNum", 0);
		totalMap.put("orderConversion", 0);
		totalMap.put("orderComConversion", 0);
		if(totalUv!=0){
			totalMap.put("clickAverNum", totalClickNum/totalUv*1.0);
			totalMap.put("orderConversion", (totalPcOrderNum+totalMbOrderNum)/totalUv*1.0);
			totalMap.put("orderComConversion", (totalPcOrderComplete+totalMbOrderComplete)/totalUv*1.0);
		}
		totalMap.put("orderNum", totalPcOrderNum+totalMbOrderNum);
		totalMap.put("clickConversion", 0);
		if(totalClickNum!=0){
			totalMap.put("clickConversion", (totalPcOrderNum+totalMbOrderNum)/totalClickNum*1.0);
		}
		totalMap.put("orderComplete", totalPcOrderComplete+totalMbOrderComplete);
		totalMap.put("orderAmount", totalPcOrderAmount+totalMbOrderAmount);
		tMaps.put("total",totalMap);
		Map<String,Object> pcMap=new HashMap<String,Object>();
		pcMap.put("uv", totalPcUV);
		pcMap.put("clickNum", totalPcClickNum);
		pcMap.put("clickAverNum", 0);
		pcMap.put("orderConversion", 0);
		pcMap.put("orderComConversion", 0);
		if(totalPcUV!=0){
			pcMap.put("clickAverNum", totalPcClickNum/totalPcUV*1.0);
			pcMap.put("orderConversion", totalPcOrderNum/totalPcUV*1.0);
			pcMap.put("orderComConversion", totalPcOrderComplete/totalPcUV*1.0);
		}
		pcMap.put("orderNum", totalPcOrderNum);
		pcMap.put("clickConversion", 0);
		if(totalPcClickNum!=0){
			pcMap.put("clickConversion", totalPcOrderNum/totalPcClickNum*1.0);
		}
		pcMap.put("orderComplete", totalPcOrderComplete);
		pcMap.put("orderAmount", totalPcOrderAmount);
		tMaps.put("PC", pcMap);
		
		Map<String,Object> mobileMap=new HashMap<String,Object>();
		mobileMap.put("uv", totalAndroidUV+totalIosUV);
		mobileMap.put("clickNum", totalAndroidClickNum+totalIosClickNum);
		mobileMap.put("clickAverNum", 0);
		mobileMap.put("orderConversion", 0);
		mobileMap.put("orderComConversion", 0);
		if((totalAndroidUV+totalIosUV)!=0){
			mobileMap.put("clickAverNum", (totalAndroidClickNum+totalIosClickNum)/(totalAndroidUV+totalIosUV)*1.0);
			mobileMap.put("orderConversion", totalMbOrderNum/(totalAndroidUV+totalIosUV)*1.0);
			mobileMap.put("orderComConversion", totalMbOrderAmount/(totalAndroidUV+totalIosUV)*1.0);
		}
		mobileMap.put("orderNum", totalMbOrderNum);
		mobileMap.put("clickConversion", 0);
		if((totalAndroidClickNum+totalIosClickNum)!=0){
			mobileMap.put("clickConversion", totalMbOrderNum/(totalAndroidClickNum+totalIosClickNum)*1.0);
		}
		mobileMap.put("orderComplete", totalMbOrderComplete);
		mobileMap.put("orderAmount", totalMbOrderAmount);
		tMaps.put("mobile", mobileMap);
		
		Map<String,Object> iosMap=new HashMap<String,Object>();
		
		iosMap.put("uv", totalIosUV);
		iosMap.put("clickNum", totalIosClickNum);
		iosMap.put("clickAverNum", 0);
		if(totalIosUV!=0){
			iosMap.put("clickAverNum", totalIosClickNum/totalIosUV*1.0);
		}
		iosMap.put("orderNum", "-");
		iosMap.put("clickConversion", "");
		iosMap.put("orderConversion", "-");
		iosMap.put("orderComplete", "-");
		iosMap.put("orderAmount", "-");
		iosMap.put("orderComConversion", "-");
		tMaps.put("ios", iosMap);
		tMaps.put("date", beginTime+" - "+endTime);
		
		Map<String,Object> androidMap=new HashMap<String,Object>();
		
		androidMap.put("uv", totalAndroidUV);
		androidMap.put("clickNum", totalAndroidClickNum);
		androidMap.put("clickAverNum", 0);
		if(totalAndroidUV!=0){
			androidMap.put("clickAverNum", totalAndroidClickNum/totalAndroidUV*1.0);
		}
		androidMap.put("orderNum", "-");
		androidMap.put("clickConversion", "-");
		androidMap.put("orderConversion", "-");
		androidMap.put("orderComplete", "-");
		androidMap.put("orderAmount", "-");
		androidMap.put("orderComConversion", "-");
		tMaps.put("android", androidMap);
		
		
		
		Map<String,Object> resMap=new HashMap<String,Object>();
		resMap.put("dataList", resMaps);
		resMap.put("total", tMaps);
		result=NotifyUtil.success(resMap);
		System.out.println(result);
		return result;
	}
	/**
	 * 根据时间间隔查询SSID统计信息
	 * @author Jason
	 */
	public String querySSIDInfoByTime(String data) {
		//返回结果
		String result = StringUtils.EMPTY;
		if(StringUtils.isBlank(data)){
			log.info("请求参数为空");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "请求参数为空", true);
			return result;
		}
		//uv总数
		int totalUV = 0;
		//pv总数
		int totalPV = 0;
		//设备总数
		int totalDC = 0;
		//设备在线总数
		int totalDOC = 0;
		//单台订单总数
		double totalSingleOrderNum = 0;
		//单台收益总数
		double totalSingleGains = 0;
		//全部设备收益总数
		double totalGains = 0;
		//uv总数
		int dayUV = 0;
		//pv总数
		int dayPV = 0;
		//开始时间
		String startTime = StringUtils.EMPTY;
		//结束时间
		String endTime = StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			startTime = object.getString("startTime");
			endTime = object.getString("endTime");
 		} catch (Exception e) {
			log.info("JSON格式转换错误");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON格式转换错误", true);
			return result;
		}
		if(StringUtils.isBlank(startTime)){
			log.info("开始时间为空");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "开始时间为空", true);
			return result;
		}
		if(StringUtils.isBlank(endTime)){
			log.info("结束时间为空");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "开始时间为空", true);
			return result;
		}
		//组装结果集
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;
		//时间格式化
		startTime = DateUtils.formatDate(startTime);
		endTime = DateUtils.formatDate(endTime);
		if(StringUtils.equals(startTime, endTime)){
			String dayPv = BhuCache.getInstance().getDayPV(startTime, "dayPV");
			String dayUv = BhuCache.getInstance().getDayUV(startTime, "dayUV");
			if(StringUtils.isNotBlank(dayPv)){
				dayPV = Integer.parseInt(dayPv);
			}
			if(StringUtils.isNotBlank(dayUv)){
				dayUV = Integer.parseInt(dayUv);
			}
			map = new HashMap<String,Object>();
			map.put("currDate", startTime);
			map.put("dayPV", dayPV);
			map.put("dayUV", dayUV);
			
			totalPV += dayPV;
			totalUV += dayUV;
			//获取设备总数以及设备在线数
			String equipment = StringUtils.EMPTY;
			equipment = BhuCache.getInstance().getEquipment(startTime, "equipment");
			//处理结果
			JSONObject obj = JSONObject.fromObject(equipment);
			int dc = 0;
			int doc = 0;
			if(StringUtils.isBlank(equipment)){
				map.put("dc", dc);
				map.put("doc", doc);
			}else{
				//处理结果
				if(obj.get("dc") != null && obj.get("doc") != null){
					dc = (Integer)obj.get("dc");
					doc = (Integer)obj.get("doc");
					map.put("dc", dc);
					map.put("doc", doc);
				}else{
					map.put("dc", dc);
					map.put("doc", doc);
				}
			}
			totalDC += dc;
			totalDOC += doc;
			
			//获取当天订单统计数量
			//获取当天订单统计数量
			String orderStatist = StringUtils.EMPTY; 
			orderStatist = BhuCache.getInstance().getStOrder(startTime,"stOrder");
			double singleOrderNum = 0;
			double singleGains = 0;
			double dayGains = 0;
			if(StringUtils.isBlank(orderStatist)){
				map.put("singleOrderNum", singleOrderNum);
				map.put("singleGains", singleGains);
				map.put("dayGains", dayGains);
			}else{
				JSONObject orderObj = JSONObject.fromObject(orderStatist);
				if(orderObj.get("occ") != null && orderObj.get("ofc") != null && orderObj.get("ofa") != null){
					//单台订单
					int occ = (Integer)orderObj.get("occ");
					if(doc == 0){
						map.put("singleOrderNum", singleOrderNum);
						map.put("singleGains", singleGains);
					}else{
						singleOrderNum = (double) (occ/doc);
						map.put("singleOrderNum", singleOrderNum);
						//单台收益
						Double ofa = orderObj.getDouble("ofa");
						singleGains = ofa/doc;
						map.put("singleGains", singleGains);
					}
					dayGains = orderObj.getDouble("ofa");;
					map.put("dayGains", dayGains);
				}else{
					map.put("singleOrderNum", singleOrderNum);
					map.put("singleGains", singleGains);
					map.put("dayGains", dayGains);
				}
			}
			totalSingleGains += singleGains;
			totalSingleOrderNum += singleOrderNum;
			totalGains += dayGains;
			listMap.add(map);
		}else{
			List<String> dateList = DateUtils.getDaysList(startTime, endTime);
			String currDate = StringUtils.EMPTY;
			for (int i = 0; i < dateList.size(); i++) {
				currDate = dateList.get(i);
				String dayPv = BhuCache.getInstance().getDayPV(currDate, "dayPV");
				String dayUv = BhuCache.getInstance().getDayUV(currDate, "dayUV");
				if(StringUtils.isNotBlank(dayPv)){
					dayPV = Integer.parseInt(dayPv);
				}
				if(StringUtils.isNotBlank(dayUv)){
					dayUV = Integer.parseInt(dayUv);
				}
				map = new HashMap<String,Object>();
				map.put("currDate", startTime);
				map.put("dayPV", dayPV);
				map.put("dayUV", dayUV);
				
				totalPV += dayPV;
				totalUV += dayUV;
				//获取设备总数以及设备在线数
				String equipment = StringUtils.EMPTY;
				equipment = BhuCache.getInstance().getEquipment(currDate, "equipment");
				//处理结果
				JSONObject obj = JSONObject.fromObject(equipment);
				int dc = 0;
				int doc = 0;
				if(StringUtils.isBlank(equipment)){
					map.put("dc", dc);
					map.put("doc", doc);
				}else{
					//处理结果
					if(obj.get("dc") != null && obj.get("doc") != null){
						dc = (Integer)obj.get("dc");
						doc = (Integer)obj.get("doc");
						map.put("dc", dc);
						map.put("doc", doc);
					}else{
						map.put("dc", dc);
						map.put("doc", doc);
					}
				}
				totalDC += dc;
				totalDOC += doc;
				
				//获取当天订单统计数量
				//获取当天订单统计数量
				String orderStatist = StringUtils.EMPTY; 
				orderStatist = BhuCache.getInstance().getStOrder(currDate,"stOrder");
				double singleOrderNum = 0;
				double singleGains = 0;
				double dayGains = 0;
				if(StringUtils.isBlank(orderStatist)){
					map.put("singleOrderNum", singleOrderNum);
					map.put("singleGains", singleGains);
					map.put("dayGains", dayGains);
				}else{
					JSONObject orderObj = JSONObject.fromObject(orderStatist);
					if(orderObj.get("occ") != null && orderObj.get("ofc") != null && orderObj.get("ofa") != null){
						//单台订单
						int occ = (Integer)orderObj.get("occ");
						if(doc == 0){
							map.put("singleOrderNum", singleOrderNum);
							map.put("singleGains", singleGains);
						}else{
							singleOrderNum = (double) (occ/doc);
							map.put("singleOrderNum", singleOrderNum);
							//单台收益
							Double ofa = orderObj.getDouble("ofa");
							singleGains = ofa/doc;
							map.put("singleGains", singleGains);
						}
						dayGains = orderObj.getDouble("ofa");
						map.put("dayGains", dayGains);
					}else{
						map.put("singleOrderNum", singleOrderNum);
						map.put("singleGains", singleGains);
						map.put("dayGains", dayGains);
					}
				}
				totalSingleGains += singleGains;
				totalSingleOrderNum += singleOrderNum;
				totalGains += dayGains;
				listMap.add(map);
			}
		}
		Map<String,Object> totalMap=new HashMap<String,Object>();
		totalMap.put("totalPV", totalPV);
		totalMap.put("totalUV", totalUV);
		totalMap.put("totalDC", totalDC);
		totalMap.put("totalDOC", totalDOC);
		totalMap.put("totalSingleGains", totalSingleGains);
		totalMap.put("totalSingleOrderNum", totalSingleOrderNum);
		totalMap.put("totalGains", totalGains);
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("ssidList", listMap);
		body.put("totalSSID", totalMap);
		result = NotifyUtil.success(body);
		return result;
	}
	
}
