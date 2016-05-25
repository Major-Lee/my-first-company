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
	 * ���ʱ�����Ͳ�ѯSSID���ͳ����Ϣ
	 * @author Jason
	 */
	@Override
	public String querySSIDInfoByType(String data) {
		//返回结果
		String result = StringUtils.EMPTY;
		if(StringUtils.isBlank(data)){
			log.info("�������Ϊ��");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "�������Ϊ��", true);
			return result;
		}
		//uv总数
		int totalUV = 0;
		//pv总数
		int totalPV = 0;
		String dateType = StringUtils.EMPTY;
		//当前页数
		String pageIndex = StringUtils.EMPTY;
		//分页条数
		String pageSize = StringUtils.EMPTY;
		try {
			JSONObject object = JSONObject.fromObject(data);
			dateType = object.getString("type");
			pageIndex = object.getString("pn");
			pageSize = object.getString("ps");
 		} catch (Exception e) {
			log.info("JSON��ʽת������");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "JSON��ʽת������", true);
			return result;
		}
		if(StringUtils.isBlank(dateType)){
			log.info("��ȡʱ������Ϊ��");
			result = NotifyUtil.error(ErrorCodeEnum.NULLPARAM, "ʱ������Ϊ��", true);
			return result;
		}
		//��װ���
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = null;
		if(StringUtils.equals(dateType, "1")){
			//��ѯ��������SSIDͳ����Ϣ
			//��ȡ�����������
			List<String> dateList = DateUtils.getLastDay(7);
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
				//TODO ��ȡ����������� 
				map = new HashMap<String,Object>();
				map.put("currDate", date);
				map.put("totalUV", totalUV);
				map.put("totalPV", totalPV);
				listMap.add(map);
			}
		}else if(StringUtils.equals(dateType, "2")){
			//��ȡ�����ʮ��SSIDͳ����Ϣ
			//��ȡ�����ʮ������
			List<String> dateList = DateUtils.getLastDay(30);
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
		}else if(StringUtils.equals(dateType, "3")){ 
		 	//��ȡ�����ʧ��SSIDͳ����Ϣ
			//��ȡ�����ʮ������
			List<String> dateList = DateUtils.getLastDay(60);
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
		}
		Map<String,Object> body = new HashMap<String,Object>();
		for (int i = 0; i < listMap.size(); i++) {
			
		} 
		body.put("ssidList", listMap);
		result = NotifyUtil.success(body);
		return result;
	}
	
}
