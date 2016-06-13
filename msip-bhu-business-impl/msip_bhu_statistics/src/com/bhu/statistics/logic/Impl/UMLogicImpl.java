package com.bhu.statistics.logic.Impl;

import java.math.BigDecimal;
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
			//每天uv总数
			int dayUV = 0 ;
			int dayPV = 0;
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
			equipment = BhuCache.getInstance().getEquipment(date, "equipment");
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
			orderStatist = BhuCache.getInstance().getStOrder(date,"stOrder");
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
						singleOrderNum = (double) occ/doc;
						BigDecimal b = new BigDecimal(singleOrderNum);
						singleOrderNum =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
						map.put("singleOrderNum", singleOrderNum);
						//单台收益
						Double ofa = orderObj.getDouble("ofa");
						singleGains = ofa/doc;
						BigDecimal b1 = new BigDecimal(singleGains);
						singleGains =  b1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
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
		//计算设备在线数/总数的平均值
		totalDC = totalDC/Integer.parseInt(dateType);
		totalMap.put("totalDC", totalDC);
		totalDOC = totalDOC/Integer.parseInt(dateType);
		totalMap.put("totalDOC", totalDOC);
		BigDecimal b = new BigDecimal(totalSingleGains);
		totalSingleGains =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
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
		
		List<LinkedHashMap<String,Object>> resMaps=new ArrayList<LinkedHashMap<String,Object>>();
		int totalUv=0;
		int totalClickNum=0;
		
		int totalAndroidUV=0;
		int totalAndroidClickNum=0;
		int totalIosUV=0;
		int totalIosClickNum=0;
		int totalPcUV=0;
		int totalPcClickNum=0;
		int totalMobileUV=0;
		int totalMobileClickNum=0;
		
		int totalPcOrderNum=0;
		int totalPcOrderComplete=0;
		int totalPcOrderAmount=0;
		int totalMbOrderNum=0;
		int totalMbOrderComplete=0;
		int totalMbOrderAmount=0;
		for(int i=0;i<daysList.size();i++){
			//获取每日PC端uv数据
			String pcUv= BhuCache.getInstance().getPCUV(daysList.get(i), "pcUv");
			int pcUV=0;
			if(StringUtils.isNotBlank(pcUv)){
				pcUV=Integer.valueOf(pcUv);
			}else{
				pcUv= apiCnzzImpl.queryCnzzStatistic("PC打赏页PV", daysList.get(i), daysList.get(i), "", "",1);
				JSONObject pcUvJson=JSONObject.fromObject(pcUv);
				String pcUvJsonStr=pcUvJson.getString("values");
				pcUvJsonStr=pcUvJsonStr.substring(1);
				pcUvJsonStr=pcUvJsonStr.substring(0, pcUvJsonStr.length()-1);
				pcUV=Integer.valueOf(pcUvJsonStr.split(",")[1].replace(".0", "").trim());
				BhuCache.getInstance().setPCUV(daysList.get(i), "pcUv", String.valueOf(pcUV));
			}
			//获取PC端点击事件发生数
			String pcClick=BhuCache.getInstance().getPcClickNum(daysList.get(i), "pcClickNum");
			int pcClickNum=0;
			if(StringUtils.isNotBlank(pcClick)){
				pcClickNum=Integer.valueOf(pcClick);
			}else{
				pcClick=apiCnzzImpl.queryCnzzStatistic("pc+赏", daysList.get(i), daysList.get(i), "", "",1);
				JSONObject pcClickJson=JSONObject.fromObject(pcClick);
				String pcClickJsonStr=pcClickJson.getString("values");
				pcClickJsonStr=pcClickJsonStr.substring(1);
				pcClickJsonStr=pcClickJsonStr.substring(0, pcClickJsonStr.length()-1);
				pcClickNum=Integer.valueOf(pcClickJsonStr.split(",")[0].replace(".0", "").trim());
				BhuCache.getInstance().setPcClickNum(daysList.get(i), "pcClickNum", String.valueOf(pcClickNum));
			}
			//获取手机端uv
			String mobileUv= BhuCache.getInstance().getMobileUv(daysList.get(i), "mobileUv");
			int mobileUV=0;
			if(StringUtils.isNotBlank(mobileUv)){
				mobileUV=Integer.valueOf(mobileUv);
			}else{
				mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", daysList.get(i), daysList.get(i), "", "",2);
				JSONObject mobileUvJson=JSONObject.fromObject(mobileUv);
				String mobileUvJsonStr=mobileUvJson.getString("values");
				mobileUvJsonStr=mobileUvJsonStr.substring(1);
				mobileUvJsonStr=mobileUvJsonStr.substring(0, mobileUvJsonStr.length()-1);
				mobileUV=Integer.valueOf(mobileUvJsonStr.split(",")[1].replace(".0", "").trim());
				BhuCache.getInstance().setMobileUv(daysList.get(i), "mobileUv", String.valueOf(mobileUV));
			}
			//获取手机ios端uv
			String iosUv= BhuCache.getInstance().getIosUv(daysList.get(i), "iosUv");
			int iosUV=0;
			if(StringUtils.isNotBlank(iosUv)){
				iosUV=Integer.valueOf(iosUv);
			}else{
				iosUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", daysList.get(i), daysList.get(i), "", "os = 'ios'",2);
				JSONObject iosUvJson=JSONObject.fromObject(iosUv);
				String iosUvJsonStr=iosUvJson.getString("values");
				iosUvJsonStr=iosUvJsonStr.substring(1);
				iosUvJsonStr=iosUvJsonStr.substring(0, iosUvJsonStr.length()-1);
				iosUV=Integer.valueOf(iosUvJsonStr.split(",")[1].replace(".0", "").trim());
				BhuCache.getInstance().setIosUv(daysList.get(i), "iosUv", String.valueOf(iosUV));
			}
			//获取手机android端uv
			String androidUv= BhuCache.getInstance().getAndroidUv(daysList.get(i), "androidUv");
			int androidUV=0;
			if(StringUtils.isNotBlank(androidUv)){
				androidUV=Integer.valueOf(androidUv);
			}else{
				androidUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", daysList.get(i), daysList.get(i), "", "os = 'android'",2);
				JSONObject androidUvJson=JSONObject.fromObject(androidUv);
				String androidUvJsonStr=androidUvJson.getString("values");
				androidUvJsonStr=androidUvJsonStr.substring(1);
				androidUvJsonStr=androidUvJsonStr.substring(0, androidUvJsonStr.length()-1);
				androidUV=Integer.valueOf(androidUvJsonStr.split(",")[1].replace(".0", "").trim());
				BhuCache.getInstance().setAndroidUv(daysList.get(i), "androidUv", String.valueOf(androidUV));
			}
			//获取手机端点击数
			String mobileClick=BhuCache.getInstance().getMobileClickNum(daysList.get(i), "mobileClickNum");
			int mobileClickNum=0;
			if(StringUtils.isNotBlank(mobileClick)){
				mobileClickNum=Integer.valueOf(mobileClick);
			}else{
				mobileClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", daysList.get(i), daysList.get(i), "", "",2);
				JSONObject mobileClickJson=JSONObject.fromObject(mobileClick);
				String mobileClickJsonStr=mobileClickJson.getString("values");
				mobileClickJsonStr=mobileClickJsonStr.substring(1);
				mobileClickJsonStr=mobileClickJsonStr.substring(0, mobileClickJsonStr.length()-1);
				mobileClickNum=Integer.valueOf(mobileClickJsonStr.split(",")[0].replace(".0", "").trim());
				BhuCache.getInstance().setMobileClickNum(daysList.get(i), "mobileClickNum", String.valueOf(mobileClickNum));
			}
			//获取手机ios端点击数
			String iosClick=BhuCache.getInstance().getIosClickNum(daysList.get(i), "iosClickNum");
			int iosClickNum=0;
			if(StringUtils.isNotBlank(iosClick)){
				iosClickNum=Integer.valueOf(iosClick);
			}else{
				iosClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", daysList.get(i), daysList.get(i), "", "os = 'ios'",2);
				JSONObject iosClickJson=JSONObject.fromObject(iosClick);
				String iosClickJsonStr=iosClickJson.getString("values");
				iosClickJsonStr=iosClickJsonStr.substring(1);
				iosClickJsonStr=iosClickJsonStr.substring(0, iosClickJsonStr.length()-1);
				iosClickNum=Integer.valueOf(iosClickJsonStr.split(",")[0].replace(".0", "").trim());
				BhuCache.getInstance().setIosClickNum(daysList.get(i), "iosClickNum", String.valueOf(iosClickNum));
			}
			
			//获取手机android端点击数
			int androidClickNum=0;
			String androidClick=BhuCache.getInstance().getAndroidClickNum(daysList.get(i), "androidClickNum");
			if(StringUtils.isNotBlank(androidClick)){
				androidClickNum=Integer.valueOf(androidClick);
			}else{
				androidClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", daysList.get(i), daysList.get(i), "", "os = 'android'",2);
				JSONObject androidClickJson=JSONObject.fromObject(androidClick);
				String androidClickJsonStr=androidClickJson.getString("values");
				androidClickJsonStr=androidClickJsonStr.substring(1);
				androidClickJsonStr=androidClickJsonStr.substring(0, androidClickJsonStr.length()-1);
				androidClickNum=Integer.valueOf(androidClickJsonStr.split(",")[0].replace(".0", "").trim());
				BhuCache.getInstance().setAndroidClickNum(daysList.get(i), "androidClickNum", String.valueOf(androidClickNum));
			}
			
			LinkedHashMap<String,Object> singleMap=new LinkedHashMap<String,Object>();
			singleMap.put("date", daysList.get(i));
			String orderRedius=BhuCache.getInstance().getStOrder(daysList.get(i), "stOrder");
			int pcOrderComplete=0;
			int pcOrderNum=0;
			int pcOrderAmount=0;
			int mbOrderComplete=0;
			int mbOrderAmount=0;
			int mbOrderNum=0;
			if(StringUtils.isNotBlank(orderRedius)){
				JSONObject orderJson=JSONObject.fromObject(orderRedius);
				String pcOrderNumStr=orderJson.getString("pc_occ");
				if(StringUtils.isNotBlank(pcOrderNumStr)){
					pcOrderNum=Integer.valueOf(pcOrderNumStr);
				}
				String pcOrderCompleteStr=orderJson.getString("pc_ofc");
				if(StringUtils.isNotBlank(pcOrderCompleteStr)){
					pcOrderComplete=Integer.valueOf(pcOrderCompleteStr);
				}
				String pcOrderAmountStr=orderJson.getString("pc_ofa");
				if(StringUtils.isNotBlank(pcOrderAmountStr)){
					pcOrderAmount=Integer.valueOf(pcOrderAmountStr);
				}
				String mbOrderNumStr=orderJson.getString("mb_occ");
				if(StringUtils.isNotBlank(mbOrderNumStr)){
					mbOrderNum=Integer.valueOf(mbOrderNumStr);
				}
				String mbOrderCompleteStr=orderJson.getString("mb_ofc");
				if(StringUtils.isNotBlank(mbOrderCompleteStr)){
					mbOrderComplete=Integer.valueOf(mbOrderCompleteStr);
				}
				String mbOrderAmountStr=orderJson.getString("mb_ofa");
				if(StringUtils.isNotBlank(mbOrderAmountStr)){
					mbOrderAmount=Integer.valueOf(mbOrderAmountStr);
				}
			}
			totalPcOrderNum+=pcOrderNum;
			totalPcOrderComplete+=pcOrderComplete;
			totalPcOrderAmount+=pcOrderAmount;
			totalMbOrderNum+=mbOrderNum;
			totalMbOrderComplete+=mbOrderComplete;
			totalMbOrderAmount+=mbOrderAmount;
			
			Map<String,Object> totalMap=new HashMap<String,Object>();
			
			totalMap.put("uv", pcUV+mobileUV);
			totalMap.put("clickNum", pcClickNum+mobileClickNum);
			totalMap.put("clickAverNum", 0);
			totalMap.put("orderConversion", 0);
			totalMap.put("orderComConversion", 0);
			if((pcUV+mobileUV)!=0){
				totalMap.put("clickAverNum", round((pcClickNum+mobileClickNum)*1.00/(pcUV+mobileUV),2));
				totalMap.put("orderConversion", round((pcOrderNum+mbOrderNum)*1.00/(pcUV+mobileUV),2));
				totalMap.put("orderComConversion", round((pcOrderComplete+mbOrderComplete)*1.00/(pcUV+mobileUV),2));
			}
			totalMap.put("orderNum", pcOrderNum+mbOrderNum);
			totalMap.put("clickConversion", 0);
			if((pcClickNum+mobileClickNum)!=0){
				totalMap.put("clickConversion", round((pcOrderNum+mbOrderNum)*1.00/(pcClickNum+mobileClickNum),2));
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
				pcMap.put("clickAverNum", round(pcClickNum*1.00/pcUV,2));
				pcMap.put("orderConversion", round(pcOrderNum*1.00/pcUV,2));
				pcMap.put("orderComConversion", round(pcOrderComplete*1.00/pcUV,2));
			}
			pcMap.put("orderNum", pcOrderNum);
			pcMap.put("clickConversion", 0);
			if(pcClickNum!=0){
				pcMap.put("clickConversion", round(pcOrderNum*1.00/pcClickNum,2));
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
				mobileMap.put("clickAverNum", round(mobileClickNum*1.00/mobileUV,2));
				mobileMap.put("orderConversion", round(mbOrderNum*1.00/mobileUV,2));
				mobileMap.put("orderComConversion", round(mbOrderComplete*1.00/mobileUV,2));
			}
			mobileMap.put("orderNum", mbOrderNum);
			mobileMap.put("clickConversion", 0);
			if(mobileClickNum!=0){
				mobileMap.put("clickConversion", round(mbOrderNum*1.00/mobileClickNum,2));
			}
			mobileMap.put("orderComplete", mbOrderComplete);
			mobileMap.put("orderAmount", mbOrderAmount);
			singleMap.put("mobile", mobileMap);
			
			Map<String,Object> iosMap=new HashMap<String,Object>();
			
			iosMap.put("uv", iosUV);
			iosMap.put("clickNum", iosClickNum);
			iosMap.put("clickAverNum", 0);
			if(iosUV!=0){
				iosMap.put("clickAverNum", round(iosClickNum*1.00/iosUV,2));
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
				androidMap.put("clickAverNum", round(androidClickNum*1.00/androidUV,2));
			}
			androidMap.put("orderNum", "-");
			androidMap.put("clickConversion", "-");
			androidMap.put("orderConversion", "-");
			androidMap.put("orderComplete", "-");
			androidMap.put("orderAmount", "-");
			androidMap.put("orderComConversion", "-");
			singleMap.put("android", androidMap);
			
//			Map<String,Object> otherMap=new HashMap<String,Object>();
//			
//			otherMap.put("uv", mobileUV-iosUV-androidUV);
//			otherMap.put("clickNum", mobileClickNum-iosClickNum-androidClickNum);
//			otherMap.put("clickAverNum", 0);
//			if((mobileUV-iosUV-androidUV)!=0){
//				otherMap.put("clickAverNum", round((mobileClickNum-androidClickNum-iosClickNum)*1.00/(mobileUV-iosUV-androidUV),2));
//			}
//			otherMap.put("orderNum", "-");
//			otherMap.put("clickConversion", "-");
//			otherMap.put("orderConversion", "-");
//			otherMap.put("orderComplete", "-");
//			otherMap.put("orderAmount", "-");
//			otherMap.put("orderComConversion", "-");
//			singleMap.put("other", otherMap);
			
			resMaps.add(singleMap);
			totalAndroidUV+=androidUV;
			totalAndroidClickNum+=androidClickNum;
			
			totalIosUV+=iosUV;
			totalIosClickNum+=iosClickNum;
			
			totalPcUV+=pcUV;
			totalPcClickNum+=pcClickNum;
			
			totalMobileUV+=mobileUV;
			totalMobileClickNum+=mobileClickNum;
			
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
			totalMap.put("clickAverNum", round(totalClickNum*1.00/totalUv,2));
			totalMap.put("orderConversion", round((totalPcOrderNum+totalMbOrderNum)*1.00/totalUv,2));
			totalMap.put("orderComConversion", round((totalPcOrderComplete+totalMbOrderComplete)*1.00/totalUv,2));
		}
		totalMap.put("orderNum", totalPcOrderNum+totalMbOrderNum);
		totalMap.put("clickConversion", 0);
		if(totalClickNum!=0){
			totalMap.put("clickConversion", round((totalPcOrderNum+totalMbOrderNum)*1.00/totalClickNum,2));
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
			pcMap.put("clickAverNum", round(totalPcClickNum*1.00/totalPcUV,2));
			pcMap.put("orderConversion", round(totalPcOrderNum*1.00/totalPcUV,2));
			pcMap.put("orderComConversion", round(totalPcOrderComplete*1.00/totalPcUV,2));
		}
		pcMap.put("orderNum", totalPcOrderNum);
		pcMap.put("clickConversion", 0);
		if(totalPcClickNum!=0){
			pcMap.put("clickConversion", round(totalPcOrderNum*1.00/totalPcClickNum,2));
		}
		pcMap.put("orderComplete", totalPcOrderComplete);
		pcMap.put("orderAmount", totalPcOrderAmount);
		tMaps.put("PC", pcMap);
		
		Map<String,Object> mobileMap=new HashMap<String,Object>();
		mobileMap.put("uv", totalMobileUV);
		mobileMap.put("clickNum", totalMobileClickNum);
		mobileMap.put("clickAverNum", 0);
		mobileMap.put("orderConversion", 0);
		mobileMap.put("orderComConversion", 0);
		if((totalAndroidUV+totalIosUV)!=0){
			mobileMap.put("clickAverNum", round(totalMobileClickNum*1.00/totalMobileUV,2));
			mobileMap.put("orderConversion", round(totalMbOrderNum*1.00/totalMobileUV,2));
			mobileMap.put("orderComConversion", round(totalMbOrderAmount*1.00/totalMobileUV,2));
		}
		mobileMap.put("orderNum", totalMbOrderNum);
		mobileMap.put("clickConversion", 0);
		if(totalMobileClickNum!=0){
			mobileMap.put("clickConversion", round(totalMbOrderNum*1.00/totalMobileClickNum,2));
		}
		mobileMap.put("orderComplete", totalMbOrderComplete);
		mobileMap.put("orderAmount", totalMbOrderAmount);
		tMaps.put("mobile", mobileMap);
		
		Map<String,Object> iosMap=new HashMap<String,Object>();
		
		iosMap.put("uv", totalIosUV);
		iosMap.put("clickNum", totalIosClickNum);
		iosMap.put("clickAverNum", 0);
		if(totalIosUV!=0){
			iosMap.put("clickAverNum", round(totalIosClickNum*1.00/totalIosUV,2));
		}
		iosMap.put("orderNum", "-");
		iosMap.put("clickConversion", "-");
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
			androidMap.put("clickAverNum", round(totalAndroidClickNum*1.00/totalAndroidUV,2));
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
			//uv总数
			int dayUV = 0;
			//pv总数
			int dayPV = 0;
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
						singleOrderNum = (double) occ/doc;
						BigDecimal b = new BigDecimal(singleOrderNum);
						singleOrderNum =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
						map.put("singleOrderNum", singleOrderNum);
						//单台收益
						Double ofa = orderObj.getDouble("ofa");
						singleGains = ofa/doc;
						BigDecimal b1 = new BigDecimal(singleGains);
						singleGains =  b1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
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
			//uv总数
			int dayUV = 0;
			//pv总数
			int dayPV = 0;
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
				map.put("currDate", currDate);
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
							singleOrderNum = (double) occ/doc;
							BigDecimal b = new BigDecimal(singleOrderNum);
							singleOrderNum =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
							map.put("singleOrderNum", singleOrderNum);
							//单台收益
							Double ofa = orderObj.getDouble("ofa");
							singleGains = ofa/doc;
							BigDecimal b1 = new BigDecimal(singleGains);
							singleGains =  b1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
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
		List<String> list = DateUtils.getDaysList(startTime, endTime);
		totalDC = totalDC/list.size();
		totalMap.put("totalDC", totalDC);
		totalDOC = totalDOC/list.size();
		totalMap.put("totalDOC", totalDOC);
		BigDecimal b = new BigDecimal(totalSingleGains);
		totalSingleGains =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
		totalMap.put("totalSingleGains", totalSingleGains);
		totalMap.put("totalSingleOrderNum", totalSingleOrderNum);
		totalMap.put("totalGains", totalGains);
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("ssidList", listMap);
		body.put("totalSSID", totalMap);
		result = NotifyUtil.success(body);
		return result;
	}
	public static void main(String[] args) {
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		/*String pcUv= apiCnzzImpl.queryCnzzStatistic("PC打赏页PV", "2016-06-01", "2016-06-01", "date", "",1);
		System.out.println(pcUv);*/
		//System.out.println(new java.text.DecimalFormat("0.00").format(4.025)); 
		//System.out.println(Math.round(4.024*100 + 0.5)/100.0); 
//		double s=3*1.00/2;
//		 BigDecimal b = new BigDecimal(Double.toString(s));         
//		 BigDecimal one = new BigDecimal("1");         
//		 System.out.println(b.divide(one,2,BigDecimal.ROUND_HALF_UP).doubleValue());        
		 String mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", "2016-06-07", "2016-06-07", "", " ",2);
		 //String mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", "2016-06-07", "2016-06-07", "date,os", "os in ('android','ios')",2);
			//String mobileClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", "2016-06-07", "2016-06-07", "date,os", "os in ('android','ios')",2);
			System.out.println(mobileUv);
			JSONObject jsonObject=JSONObject.fromObject(mobileUv);
			String ss=jsonObject.get("values").toString();
			ss=ss.substring(1);
			ss=ss.substring(0, ss.length()-1);
			System.out.println(ss);
			//System.out.println(mobileClick);
		//BhuCache.getInstance().setEquipment("2016-06-05", "equipment", "{\"dc\":10020,\"doc\":7998}");
		//BhuCache.getInstance().setStOrder("2016-06-05", "stOrder", "{\"mb_ofc\":833,\"mb_ofa\":\"594\",\"pc_ofc\":26,\"pc_ofa\":\"65\",\"pc_occ\":188,\"ofc\":859,\"mb_occ\":4210,\"ofa\":659.0,\"occ\":4398}");
	}
	/**      
	    * 提供精确的小数位四舍五入处理。      
	     * @param v 需要四舍五入的数字      
	     * @param scale 小数点后保留几位      
	     * @return 四舍五入后的结果      
	    */         
	public static double round(double v,int scale){         
		if(scale<0){         
	           throw new IllegalArgumentException("The scale must be a positive integer or zero");         
	    }         
	    BigDecimal b = new BigDecimal(Double.toString(v));         
	    BigDecimal one = new BigDecimal("1");         
	    return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();         
	}         
}
