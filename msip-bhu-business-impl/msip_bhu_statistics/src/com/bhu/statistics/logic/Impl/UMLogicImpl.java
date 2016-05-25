package com.bhu.statistics.logic.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bhu.statistics.logic.IUMLogic;
import com.bhu.statistics.util.DateUtils;
import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.NotifyUtil;
import com.bhu.statistics.util.cache.BhuCache;
import com.bhu.statistics.util.enums.ErrorCodeEnum;
import com.bhu.statistics.util.um.OpenApiCnzzImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;

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
				totalPV = Integer.parseInt(dayPv);
			}
			if(StringUtils.isNotBlank(dayUv)){
				totalUV = Integer.parseInt(dayUv);
			}
			map = new HashMap<String,Object>();
			map.put("currDate", date);
			map.put("totalUV", totalUV);
			map.put("totalPV", totalPV);
			listMap.add(map);
		}
		Map<String,Object> body = new HashMap<String,Object>();
		body.put("ssidList", listMap);
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
			beginTime = object.getString("beginTime");
			endTime = object.getString("endTime");
 		} catch (Exception e) {
			log.info("JSON转化错误");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON转化错误", true);
			return result;
		}
		OpenApiCnzzImpl apiCnzzImpl=new OpenApiCnzzImpl();
		String pcUv= apiCnzzImpl.queryCnzzStatistic("PC打赏页PV", beginTime, endTime, "date", "",1);
		String pcClick=apiCnzzImpl.queryCnzzStatistic("pc+赏", beginTime, endTime, "date", "",1);
		String mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", beginTime, endTime, "date,os", "os in ('android','ios')",2);
		String mobileClick=apiCnzzImpl.queryCnzzStatistic("pc+赏", beginTime, endTime, "date,os", "os in ('android','ios')",2);
		Map<String,Object> allmap=new HashMap<String,Object>();
		JSONObject pcUvJson=JSONObject.fromObject(pcUv);
		JSONObject pcClickJson=JSONObject.fromObject(pcClick);
		JSONObject mobileUvJson=JSONObject.fromObject(mobileUv);
		JSONObject mobileClickJson=JSONObject.fromObject(mobileClick);
		
		List<String> daysList=DateUtils.getDaysList(beginTime, endTime);
		List<Map<String,Object>> resMaps=new ArrayList<Map<String,Object>>();
		for(int i=0;i<daysList.size();i++){
			Map<String,Object> singleMap=new HashMap<String,Object>();
			
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("", "");
		return result;
	}
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
		/*if(){
			
		}*/
		return null;
	}
	
}
