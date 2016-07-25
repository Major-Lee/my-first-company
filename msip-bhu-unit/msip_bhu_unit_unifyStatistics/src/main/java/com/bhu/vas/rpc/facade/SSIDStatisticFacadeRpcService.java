package com.bhu.vas.rpc.facade;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.elasticsearch.common.lang3.StringUtils;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DeviceStatisticsHashService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;


/**
 * SSID统计信息
 * @author Jason
 *
 */
public class SSIDStatisticFacadeRpcService {
	@Resource
	private UserService userService;
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	private static Logger log = Logger.getLogger(SSIDStatisticFacadeRpcService.class);
	public String querySSIDStatisticInfo(Map<String,Object> map){
		String result = StringUtils.EMPTY;
		//查询日期类型
		String type = StringUtils.EMPTY;
		//出货渠道
		String deliveryChannel = StringUtils.EMPTY;
		//手机号
		String mobileNo = StringUtils.EMPTY;
		//设备标签
		String deviceLable = StringUtils.EMPTY;
		//查询起始时间
		String startTime = StringUtils.EMPTY;
		//查询结束时间
		String endTime = StringUtils.EMPTY;
		//当前页数
		String pn = StringUtils.EMPTY;
		//每页显示条数
		String ps = StringUtils.EMPTY;
		type = (String) map.get("type");
		deliveryChannel = (String) map.get("deliveryChannel");
		mobileNo = (String) map.get("mobileNo");
		deviceLable = (String) map.get("deviceLable");
		startTime = (String) map.get("startTime");
		endTime = (String) map.get("endTime");
		pn = (String) map.get("pn");
		ps = (String) map.get("ps");
		//mac List
		List<String> macList = null;
		if(StringUtils.isNotBlank(deliveryChannel)){
			//根据出货渠道查询
			macList = new ArrayList<String>();
		}else if(StringUtils.isNotBlank(mobileNo)){
			//根据手机号查询
			macList = new ArrayList<String>();
			macList = queryMacListByMobileNo(mobileNo);
			if(macList == null || macList.size()<=0){
				
			}
		}else if(StringUtils.isNotBlank(deviceLable)){
			//根据设备标签查询
			macList = new ArrayList<String>();
		}
		List<String> timeList = new ArrayList<String>();
		if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
			//根据时间间隔查询设备统计信息
			timeList = getDaysListAsc(startTime,endTime);
		}else{
			//根据天数查询设备统计信息
			timeList = getLastDayAsc(Integer.parseInt(type));
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
		for (int i = 0; i < timeList.size(); i++) {
			String date = StringUtils.EMPTY;
			if(macList == null && macList.size()<=0){
				//查询全部设备信息
				//每天uv总数
				int dayUV = 0 ;
				int dayPV = 0;
				date = timeList.get(i);
				String dayPv = DeviceStatisticsHashService.getInstance().deviceMacHget(date, "dayPV");
				String dayUv = DeviceStatisticsHashService.getInstance().deviceMacHget(date, "dayUV");
				if(StringUtils.isNotBlank(dayPv)){
					dayPV = Integer.parseInt(dayPv);
				}
				if(StringUtils.isNotBlank(dayUv)){
					dayUV = Integer.parseInt(dayUv);
				}
				totalPV += dayPV;
				totalUV += dayUV;
				//map = new HashMap<String,Object>();
				//map.put("currDate", date);
				//map.put("dayUV", dayUV);
				//map.put("dayPV", dayPV);
				//获取设备总数以及设备在线数
				String equipment = StringUtils.EMPTY;
				equipment =DeviceStatisticsHashService.getInstance().deviceMacHget(date, "equipment");
				Map<String,Object> equMap= JsonHelper.getMapFromJson(equipment);
				int dc = 0;
				int doc = 0;
				if(StringUtils.isBlank(equipment)){
					//map.put("dc", dc);
					//map.put("doc", doc);
				}else{
					//处理结果
					if(equMap.get("dc") != null && equMap.get("doc") != null){
						dc = (Integer)equMap.get("dc");
						doc = (Integer)equMap.get("doc");
						//map.put("dc", dc);
						//map.put("doc", doc);
					}else{
						//map.put("dc", dc);
						//map.put("doc", doc);
					}
				}
				totalDC += dc;
				totalDOC += doc;
				//获取当天订单统计数量
				String orderStatist = StringUtils.EMPTY; 
				orderStatist = DeviceStatisticsHashService.getInstance().deviceMacHget(date,"stOrder");
				double singleOrderNum = 0;
				double singleGains = 0;
				double dayGains = 0;
				if(StringUtils.isBlank(orderStatist)){
					//map.put("singleOrderNum", singleOrderNum);
					//map.put("singleGains", singleGains);
					//map.put("dayGains", dayGains);
				}else{
					Map<String,Object> orderMap = JsonHelper.getMapFromJson(orderStatist);
					if(orderMap.get("occ") != null && orderMap.get("ofc") != null && orderMap.get("ofa") != null){
						//单台订单
						int occ = (Integer)orderMap.get("occ");
						if(doc == 0){
							//map.put("singleOrderNum", singleOrderNum);
							//map.put("singleGains", singleGains);
						}else{
							singleOrderNum = (double) occ/doc;
							BigDecimal b = new BigDecimal(singleOrderNum);
							singleOrderNum =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
							//map.put("singleOrderNum", singleOrderNum);
							//单台收益
							Double ofa = (Double) orderMap.get("ofa");
							singleGains = ofa/doc;
							BigDecimal b1 = new BigDecimal(singleGains);
							singleGains =  b1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
							//map.put("singleGains", singleGains);
						}
						dayGains = (Double) orderMap.get("ofa");
						//map.put("dayGains", dayGains);
					}else{
						//map.put("singleOrderNum", singleOrderNum);
						//map.put("singleGains", singleGains);
						//map.put("dayGains", dayGains);
					}
				}
				totalSingleGains += singleGains;
				totalSingleOrderNum += singleOrderNum;
				totalGains += dayGains;
				//listMap.add(map);
			}else{
				//根据mac地址查询设备统计信息
				date = timeList.get(i);
				String currMac = StringUtils.EMPTY;
				for (int j = 0; j < macList.size(); j++) {
					currMac = macList.get(j);
					//根据mac地址查询缓存中的mac数据
					if(StringUtils.isNotBlank(currMac)){
						String macJson = DeviceStatisticsHashService.getInstance().deviceMacHget(date,currMac);
						if(StringUtils.isNotBlank(macJson)){
							Map<String,Object> macMap = JsonHelper.getMapFromJson(macJson);
							//查询全部设备信息
							//每天uv总数
							int dayUV = 0 ;
							int dayPV = 0;
							date = timeList.get(i);
							String dayPv = (String) macMap.get("pv");
							String dayUv = (String) macMap.get("uv");
							if(StringUtils.isNotBlank(dayPv)){
								dayPV = Integer.parseInt(dayPv);
							}
							if(StringUtils.isNotBlank(dayUv)){
								dayUV = Integer.parseInt(dayUv);
							}
							totalPV += dayPV;
							totalUV += dayUV;
							//map = new HashMap<String,Object>();
							//map.put("currDate", date);
							//map.put("dayUV", dayUV);
							//map.put("dayPV", dayPV);
							//获取设备总数以及设备在线数  TODO  待处理 需要查询Service获取当前mac状态
							String equipment = StringUtils.EMPTY;
							equipment =DeviceStatisticsHashService.getInstance().deviceMacHget(date, "equipment");
							Map<String,Object> equMap= JsonHelper.getMapFromJson(equipment);
							int dc = 0;
							int doc = 0;
							if(StringUtils.isBlank(equipment)){
								//map.put("dc", dc);
								//map.put("doc", doc);
							}else{
								//处理结果
								if(equMap.get("dc") != null && equMap.get("doc") != null){
									dc = (Integer)equMap.get("dc");
									doc = (Integer)equMap.get("doc");
									//map.put("dc", dc);
									//map.put("doc", doc);
								}else{
									//map.put("dc", dc);
									//map.put("doc", doc);
								}
							}
							totalDC += dc;
							totalDOC += doc;
							//获取当天订单统计数量
							String orderStatist = StringUtils.EMPTY; 
							orderStatist = DeviceStatisticsHashService.getInstance().deviceMacHget(date,"stOrder");
							double singleOrderNum = 0;
							double singleGains = 0;
							double dayGains = 0;
							if(StringUtils.isBlank(orderStatist)){
								//map.put("singleOrderNum", singleOrderNum);
								//map.put("singleGains", singleGains);
								//map.put("dayGains", dayGains);
							}else{
								//Map<String,Object> orderMap = JsonHelper.getMapFromJson(orderStatist);
								if(macMap.get("occ") != null && macMap.get("ofc") != null && macMap.get("ofa") != null){
									//单台订单
									int occ = (Integer)macMap.get("occ");
									if(doc == 0){
										//map.put("singleOrderNum", singleOrderNum);
										//map.put("singleGains", singleGains);
									}else{
										singleOrderNum = (double) occ/doc;
										BigDecimal b = new BigDecimal(singleOrderNum);
										singleOrderNum =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
										//map.put("singleOrderNum", singleOrderNum);
										//单台收益
										Double ofa = (Double) macMap.get("ofa");
										singleGains = ofa/doc;
										BigDecimal b1 = new BigDecimal(singleGains);
										singleGains =  b1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
										//map.put("singleGains", singleGains);
									}
									dayGains = (Double) macMap.get("ofa");
									//map.put("dayGains", dayGains);
								}else{
									//map.put("singleOrderNum", singleOrderNum);
									//map.put("singleGains", singleGains);
									//map.put("dayGains", dayGains);
								}
							}
							totalSingleGains += singleGains;
							totalSingleOrderNum += singleOrderNum;
							totalGains += dayGains;
						}
					}
					
					
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 根据出货渠道获取mac集合
	 * @param deliveryChannel
	 * @return
	 */
	public List<String> queryMacListByDChannel(String deliveryChannel){
		List<String> macList = new ArrayList<String>();
		return macList;
	}
	
	/**
	 * 根据用户手机号获取mac集合
	 * @param deliveryChannel
	 * @return
	 */
	public List<String> queryMacListByMobileNo(String mobileNo){
		List<String> macList = new ArrayList<String>();
		if(StringUtils.isBlank(mobileNo)){
			log.info("电话号码为空");
			return macList;
		}
		try {
			ModelCriteria mc = new ModelCriteria();
			Criteria cri = mc.createCriteria();
			if(StringUtils.isNotBlank(mobileNo)){
				cri.andColumnEqualTo("mobileno", mobileNo);
			}
			cri.andSimpleCaulse(" 1=1 ");
			mc.setOrderByClause(" id desc ");
			List<User> userList = this.userService.findModelByModelCriteria(mc);
			if(userList == null || userList.size()<=0){
				return macList;
			}
			//根据用户Id查询mac列表
			macList = userWifiDeviceFacadeService.findUserWifiDeviceIdsByUid(userList.get(0).getId());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		return macList;
	}
	
	/**
	 * 根据设备标签获取mac集合
	 * @param deliveryChannel
	 * @return
	 */
	public List<String> queryMacListByDLabel(String deviceLabel){
		List<String> macList = new ArrayList<String>();
		return macList;
	}
	
	/**
	 * 时间升序排列
	 * @param dateNum
	 * @return
	 */
	public static List<String> getLastDayAsc(int dateNum){
		List<String> list = new ArrayList<String>();
		//获取当前日期
		for (int i = 1; i <= dateNum; i++) {
			Date date = new Date();  
			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(date); 
			calendar.add(Calendar.DAY_OF_MONTH, -(dateNum-i));
			date = calendar.getTime();  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
			String dateNowStr = sdf.format(date); 
			list.add(dateNowStr);
		}
		return list; 
	}
	
	public static List<String> getDaysListAsc(String beginTime,String endTime){
		List<String> list = new ArrayList<String>();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		list.add(beginTime);
		try {
			start.setTime(format.parse(beginTime));
			end.setTime(format.parse(endTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		while(end.after(start))
		{
			System.out.println(format.format(start.getTime()));
			start.add(Calendar.DAY_OF_MONTH,1);
			list.add(format.format(start.getTime()).toString());
		}
		return list; 
	}
}
