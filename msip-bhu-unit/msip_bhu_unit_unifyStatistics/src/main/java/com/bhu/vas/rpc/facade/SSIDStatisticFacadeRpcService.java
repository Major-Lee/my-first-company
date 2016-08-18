package com.bhu.vas.rpc.facade;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.OnlineEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.DeviceStatisticsHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.UMStatisticsHashService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.rpc.util.DataUtils;
import com.bhu.vas.rpc.util.JSONObject;
import com.bhu.vas.rpc.util.um.OpenApiCnzzImpl;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;


/**
 * SSID统计信息
 * @author Jason
 *
 */
@Service
public class SSIDStatisticFacadeRpcService {
	@Resource
	private UserService userService;
	
//	@Resource
//	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	private static Logger log = Logger.getLogger(SSIDStatisticFacadeRpcService.class);
	public Map<String,Object> querySSIDStatisticInfo(Map<String,Object> map){
		Map<String,Object> result = new HashMap<String,Object>();
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
		int pn = 1;
		//每页显示条数
		int ps = 5;
		type = (String) map.get("type");
		deliveryChannel = (String) map.get("deliveryChannel");
		mobileNo = (String) map.get("mobileNo");
		deviceLable = (String) map.get("deviceLable");
		startTime = (String) map.get("startTime");
		endTime = (String) map.get("endTime");
		pn = (Integer) map.get("pn");
		ps = (Integer) map.get("ps");
		boolean flag=true;
		//mac List
		List<String> macList = null;
		if(StringUtils.isNotBlank(deliveryChannel)){
			//根据出货渠道查询
			macList = new ArrayList<String>();
			macList=queryMacListByDLabel(deliveryChannel);
			flag=false;
		}else if(StringUtils.isNotBlank(mobileNo)){
			//根据手机号查询
			macList = new ArrayList<String>();
			macList = queryMacListByMobileNo(mobileNo);
			flag=false;
		}else if(StringUtils.isNotBlank(deviceLable)){
			//根据设备标签查询
			macList = new ArrayList<String>();
			macList=queryMacListByDLabel(deviceLable);
			flag=false;
		}
		List<String> timeList = new ArrayList<String>();
		if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
			//根据时间间隔查询设备统计信息
			timeList = getDaysList(startTime,endTime);
		}else{
			//根据天数查询设备统计信息
			timeList = getLastDay(Integer.parseInt(type));
			startTime=timeList.get(timeList.size()-1);
			endTime=DataUtils.beforeDay();
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
		double totalPcOrderAmount=0;
		int totalMbOrderNum=0;
		int totalMbOrderComplete=0;
		double totalMbOrderAmount=0;
		
		//折线图组装数据
		List<Object> time=new ArrayList<Object>();
		List<List<Object>> equipments=new ArrayList<List<Object>>();
		List<Object> equipmentNums=new ArrayList<Object>();
		
		List<List<Object>> orders=new ArrayList<List<Object>>();
		List<Object> orderNums=new ArrayList<Object>();
		List<Object> orderComNums=new ArrayList<Object>();
		
		List<List<Object>> price=new ArrayList<List<Object>>();
		List<Object> totalPrice=new ArrayList<Object>();
		
		//组装结果集
		LinkedHashMap<String,Object> ssidMap = null;
		String date = StringUtils.EMPTY;
			for (int i = 0; i < timeList.size(); i++) {
				LinkedHashMap<String,Object> singleMap=new LinkedHashMap<String,Object>();
				//===================设备==============================
				//每天uv总数
				int dayUV = 0 ;
				int dayPV = 0;
				int dc = 0;
				int doc = 0;
				double singleOrderNum = 0;
				double singleGains = 0;
				double dayGains = 0;
				
				//订单创建数量
				long occ = 0;
				//订单支付总金额
				double ofa = 0;
				//获取每日PC端uv数据
				int pcUV=0;
				//获取PC端点击事件发生数
				int pcClickNum=0;
				//获取手机端uv
				int mobileUV=0;
				//获取手机ios端uv
				int iosUV=0;
				//获取手机android端uv
				int androidUV=0;
				//获取手机端点击数
				int mobileClickNum=0;
				//获取手机ios端点击数
				int iosClickNum=0;
				//获取手机android端点击数
				int androidClickNum=0;
				int pcOrderComplete=0;
				int pcOrderNum=0;
				double pcOrderAmount=0;
				int mbOrderComplete=0;
				double mbOrderAmount=0;
				int mbOrderNum=0;
				int ofc=0;
				date = timeList.get(i);
				if(flag){
					String dayPv = DeviceStatisticsHashService.getInstance().deviceMacHget(date, "dayPV");
					String dayUv = DeviceStatisticsHashService.getInstance().deviceMacHget(date, "dayUV");
					if(StringUtils.isNotBlank(dayPv)){
						dayPV = Integer.parseInt(dayPv);
					}
					if(StringUtils.isNotBlank(dayUv)){
						dayUV = Integer.parseInt(dayUv);
					}
					String equipment = StringUtils.EMPTY;
					equipment = DeviceStatisticsHashService.getInstance().deviceMacHget(date, "equipment");
					if(StringUtils.isNotBlank(equipment)){
						JSONObject obj = JSONObject.fromObject(equipment);
						//处理结果
						if(obj.get("dc ") != null){
							dc = (Integer)obj.get("dc ");
						}
						if( obj.get("doc") != null){
							doc = (Integer)obj.get("doc");
						}
					}
					//获取当天订单统计数量
					String orderStatist = StringUtils.EMPTY; 
					orderStatist = DeviceStatisticsHashService.getInstance().deviceMacHget(date,"stOrder");
					if(StringUtils.isNotBlank(orderStatist)){
						JSONObject orderObj = JSONObject.fromObject(orderStatist);
						if(orderObj.get("occ") != null){
							//单台订单
							occ = (Integer)orderObj.get("occ");
						}
						if(orderObj.get("ofa") != null){
							//单台收益
							ofa = orderObj.getDouble("ofa");
							dayGains = orderObj.getDouble("ofa");
						}
						if(orderObj.get("ofc") != null){
							//完成订单数
							ofc = (Integer)orderObj.get("ofc");
						}
						String pcOrderNumStr=orderObj.getString("pc_occ");
						if(StringUtils.isNotBlank(pcOrderNumStr)){
							pcOrderNum=Integer.valueOf(pcOrderNumStr);
						}
						String pcOrderCompleteStr=orderObj.getString("pc_ofc");
						if(StringUtils.isNotBlank(pcOrderCompleteStr)){
							pcOrderComplete=Integer.valueOf(pcOrderCompleteStr);
						}
						String pcOrderAmountStr=orderObj.getString("pc_ofa");
						if(StringUtils.isNotBlank(pcOrderAmountStr)){
							pcOrderAmount=Double.valueOf(pcOrderAmountStr);
						}
						String mbOrderNumStr=orderObj.getString("mb_occ");
						if(StringUtils.isNotBlank(mbOrderNumStr)){
							mbOrderNum=Integer.valueOf(mbOrderNumStr);
						}
						String mbOrderCompleteStr=orderObj.getString("mb_ofc");
						if(StringUtils.isNotBlank(mbOrderCompleteStr)){
							mbOrderComplete=Integer.valueOf(mbOrderCompleteStr);
						}
						String mbOrderAmountStr=orderObj.getString("mb_ofa");
						if(StringUtils.isNotBlank(mbOrderAmountStr)){
							mbOrderAmount=Double.valueOf(mbOrderAmountStr);
						}
					}
					String pcUv= UMStatisticsHashService.getInstance().umHget(timeList.get(i), "pcUv");
					if(StringUtils.isNotBlank(pcUv)){
						pcUV=Integer.valueOf(pcUv);
					}else{
						pcUv= apiCnzzImpl.queryCnzzStatistic("PC打赏页PV", timeList.get(i), timeList.get(i), "", "",1);
						JSONObject pcUvJson=JSONObject.fromObject(pcUv);
						String pcUvJsonStr=pcUvJson.getString("values");
						pcUvJsonStr=pcUvJsonStr.substring(1);
						pcUvJsonStr=pcUvJsonStr.substring(0, pcUvJsonStr.length()-1);
						pcUV=Integer.valueOf(pcUvJsonStr.split(",")[1].replace(".0", "").trim());
						UMStatisticsHashService.getInstance().umHset(timeList.get(i), "pcUv", String.valueOf(pcUV));
					}
					String pcClick=UMStatisticsHashService.getInstance().umHget(timeList.get(i), "pcClickNum");
					if(StringUtils.isNotBlank(pcClick)){
						pcClickNum=Integer.valueOf(pcClick);
					}else{
						pcClick=apiCnzzImpl.queryCnzzStatistic("pc+赏", timeList.get(i), timeList.get(i), "", "",1);
						JSONObject pcClickJson=JSONObject.fromObject(pcClick);
						String pcClickJsonStr=pcClickJson.getString("values");
						pcClickJsonStr=pcClickJsonStr.substring(1);
						pcClickJsonStr=pcClickJsonStr.substring(0, pcClickJsonStr.length()-1);
						pcClickNum=Integer.valueOf(pcClickJsonStr.split(",")[0].replace(".0", "").trim());
						UMStatisticsHashService.getInstance().umHset(timeList.get(i), "pcClickNum", String.valueOf(pcClickNum));
					}
					String mobileUv= UMStatisticsHashService.getInstance().umHget(timeList.get(i), "mobileUv");
					if(StringUtils.isNotBlank(mobileUv)){
						mobileUV=Integer.valueOf(mobileUv);
					}else{
						mobileUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", timeList.get(i), timeList.get(i), "", "",2);
						JSONObject mobileUvJson=JSONObject.fromObject(mobileUv);
						String mobileUvJsonStr=mobileUvJson.getString("values");
						mobileUvJsonStr=mobileUvJsonStr.substring(1);
						mobileUvJsonStr=mobileUvJsonStr.substring(0, mobileUvJsonStr.length()-1);
						mobileUV=Integer.valueOf(mobileUvJsonStr.split(",")[1].replace(".0", "").trim());
						UMStatisticsHashService.getInstance().umHset(timeList.get(i), "mobileUv", String.valueOf(mobileUV));
					}
					String iosUv= UMStatisticsHashService.getInstance().umHget(timeList.get(i), "iosUv");
					if(StringUtils.isNotBlank(iosUv)){
						iosUV=Integer.valueOf(iosUv);
					}else{
						iosUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", timeList.get(i), timeList.get(i), "", "os = 'ios'",2);
						JSONObject iosUvJson=JSONObject.fromObject(iosUv);
						String iosUvJsonStr=iosUvJson.getString("values");
						iosUvJsonStr=iosUvJsonStr.substring(1);
						iosUvJsonStr=iosUvJsonStr.substring(0, iosUvJsonStr.length()-1);
						iosUV=Integer.valueOf(iosUvJsonStr.split(",")[1].replace(".0", "").trim());
						UMStatisticsHashService.getInstance().umHset(timeList.get(i), "iosUv", String.valueOf(iosUV));
					}
					String androidUv= UMStatisticsHashService.getInstance().umHget(timeList.get(i), "androidUv");
					if(StringUtils.isNotBlank(androidUv)){
						androidUV=Integer.valueOf(androidUv);
					}else{
						androidUv= apiCnzzImpl.queryCnzzStatistic("mobile打赏页PV", timeList.get(i), timeList.get(i), "", "os = 'android'",2);
						JSONObject androidUvJson=JSONObject.fromObject(androidUv);
						String androidUvJsonStr=androidUvJson.getString("values");
						androidUvJsonStr=androidUvJsonStr.substring(1);
						androidUvJsonStr=androidUvJsonStr.substring(0, androidUvJsonStr.length()-1);
						androidUV=Integer.valueOf(androidUvJsonStr.split(",")[1].replace(".0", "").trim());
						UMStatisticsHashService.getInstance().umHset(timeList.get(i), "androidUv", String.valueOf(androidUV));
					}
					String mobileClick=UMStatisticsHashService.getInstance().umHget(timeList.get(i), "mobileClickNum");
					if(StringUtils.isNotBlank(mobileClick)){
						mobileClickNum=Integer.valueOf(mobileClick);
					}else{
						mobileClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", timeList.get(i), timeList.get(i), "", "",2);
						JSONObject mobileClickJson=JSONObject.fromObject(mobileClick);
						String mobileClickJsonStr=mobileClickJson.getString("values");
						mobileClickJsonStr=mobileClickJsonStr.substring(1);
						mobileClickJsonStr=mobileClickJsonStr.substring(0, mobileClickJsonStr.length()-1);
						mobileClickNum=Integer.valueOf(mobileClickJsonStr.split(",")[0].replace(".0", "").trim());
						UMStatisticsHashService.getInstance().umHset(timeList.get(i), "mobileClickNum", String.valueOf(mobileClickNum));
					}
					String iosClick=UMStatisticsHashService.getInstance().umHget(timeList.get(i), "iosClickNum");
					if(StringUtils.isNotBlank(iosClick)){
						iosClickNum=Integer.valueOf(iosClick);
					}else{
						iosClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", timeList.get(i), timeList.get(i), "", "os = 'ios'",2);
						JSONObject iosClickJson=JSONObject.fromObject(iosClick);
						String iosClickJsonStr=iosClickJson.getString("values");
						iosClickJsonStr=iosClickJsonStr.substring(1);
						iosClickJsonStr=iosClickJsonStr.substring(0, iosClickJsonStr.length()-1);
						iosClickNum=Integer.valueOf(iosClickJsonStr.split(",")[0].replace(".0", "").trim());
						UMStatisticsHashService.getInstance().umHset(timeList.get(i), "iosClickNum", String.valueOf(iosClickNum));
					}
					String androidClick=UMStatisticsHashService.getInstance().umHget(timeList.get(i), "androidClickNum");
					if(StringUtils.isNotBlank(androidClick)){
						androidClickNum=Integer.valueOf(androidClick);
					}else{
						androidClick=apiCnzzImpl.queryCnzzStatistic("mobile+赏+plus", timeList.get(i), timeList.get(i), "", "os = 'android'",2);
						JSONObject androidClickJson=JSONObject.fromObject(androidClick);
						String androidClickJsonStr=androidClickJson.getString("values");
						androidClickJsonStr=androidClickJsonStr.substring(1);
						androidClickJsonStr=androidClickJsonStr.substring(0, androidClickJsonStr.length()-1);
						androidClickNum=Integer.valueOf(androidClickJsonStr.split(",")[0].replace(".0", "").trim());
						UMStatisticsHashService.getInstance().umHset(timeList.get(i), "androidClickNum", String.valueOf(androidClickNum));
					}
				}else if(!flag&&macList != null &&macList.size()>0){
					dc=macList.size();
					for(String j:macList){
						String doC = DeviceStatisticsHashService.getInstance().deviceMacHget("MAC-DOC"+date, j);
						if(StringUtils.isNoneBlank(doC)){
							doc+=Integer.parseInt(doC);
						}
						String orderStatist=DeviceStatisticsHashService.getInstance().deviceMacHget("MAC-"+date, j);
						JSONObject orderObj = JSONObject.fromObject(orderStatist);
						if(orderObj.get("pv")!=null){
							dayPV += (Integer)orderObj.get("pv");
						}
						if(orderObj.get("uv")!=null){
							dayUV += (Integer)orderObj.get("uv");
						}
						if(orderObj.get("occ") != null){
							//单台订单
							occ += (Integer)orderObj.get("occ");
						}
						if(orderObj.get("ofa") != null){
							//单台收益
							ofa += orderObj.getDouble("ofa");
							dayGains += orderObj.getDouble("ofa");
						}
						if(orderObj.get("ofc") != null){
							//完成订单数
							ofc += (Integer)orderObj.get("ofc");
						}
						String pcOrderNumStr=orderObj.getString("pc_occ");
						if(StringUtils.isNotBlank(pcOrderNumStr)){
							pcOrderNum+=Integer.valueOf(pcOrderNumStr);
						}
						String pcOrderCompleteStr=orderObj.getString("pc_ofc");
						if(StringUtils.isNotBlank(pcOrderCompleteStr)){
							pcOrderComplete+=Integer.valueOf(pcOrderCompleteStr);
						}
						String pcOrderAmountStr=orderObj.getString("pc_ofa");
						if(StringUtils.isNotBlank(pcOrderAmountStr)){
							pcOrderAmount+=Double.valueOf(pcOrderAmountStr);
						}
						String mbOrderNumStr=orderObj.getString("mb_occ");
						if(StringUtils.isNotBlank(mbOrderNumStr)){
							mbOrderNum+=Integer.valueOf(mbOrderNumStr);
						}
						String mbOrderCompleteStr=orderObj.getString("mb_ofc");
						if(StringUtils.isNotBlank(mbOrderCompleteStr)){
							mbOrderComplete+=Integer.valueOf(mbOrderCompleteStr);
						}
						String mbOrderAmountStr=orderObj.getString("mb_ofa");
						if(StringUtils.isNotBlank(mbOrderAmountStr)){
							mbOrderAmount+=Double.valueOf(mbOrderAmountStr);
						}
						String pcUv= UMStatisticsHashService.getInstance().umHget("MacPcUv"+timeList.get(i), j);
						if(StringUtils.isNotBlank(pcUv)){
							pcUV+=Integer.valueOf(pcUv);
						}
						String pcClick=UMStatisticsHashService.getInstance().umHget("MacPcClickNum"+timeList.get(i), j);
						if(StringUtils.isNotBlank(pcClick)){
							pcClickNum+=Integer.valueOf(pcClick);
						}
						String mobileUv= UMStatisticsHashService.getInstance().umHget("MacMobileUv"+timeList.get(i), j);
						if(StringUtils.isNotBlank(mobileUv)){
							mobileUV+=Integer.valueOf(mobileUv);
						}
						String iosUv= UMStatisticsHashService.getInstance().umHget("MacIosUv"+timeList.get(i), j);
						if(StringUtils.isNotBlank(iosUv)){
							iosUV+=Integer.valueOf(iosUv);
						}
						String androidUv= UMStatisticsHashService.getInstance().umHget("MacAndroidUv"+timeList.get(i), j);
						if(StringUtils.isNotBlank(androidUv)){
							androidUV+=Integer.valueOf(androidUv);
						}
						String mobileClick=UMStatisticsHashService.getInstance().umHget("MacMobileClick"+timeList.get(i), j);
						if(StringUtils.isNotBlank(mobileClick)){
							mobileClickNum+=Integer.valueOf(mobileClick);
						}
						String iosClick=UMStatisticsHashService.getInstance().umHget("MacIosClick"+timeList.get(i), j);
						if(StringUtils.isNotBlank(iosClick)){
							iosClickNum+=Integer.valueOf(iosClick);
						}
						String androidClick=UMStatisticsHashService.getInstance().umHget("MacAndroidClick"+timeList.get(i), j);
						if(StringUtils.isNotBlank(androidClick)){
							androidClickNum+=Integer.valueOf(androidClick);
						}
					}
				}
				if(doc != 0){
					singleOrderNum = (double) occ/doc;
					BigDecimal b = new BigDecimal(singleOrderNum);
					singleOrderNum =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
					singleGains = ofa/doc;
					BigDecimal b1 = new BigDecimal(singleGains);
					singleGains =  b1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
				}
				totalPV += dayPV;
				totalUV += dayUV;
				ssidMap = new LinkedHashMap<String,Object>();
				ssidMap.put("currDate", date);
				ssidMap.put("dayUV", dayUV);
				ssidMap.put("dayPV", dayPV);
				//获取设备总数以及设备在线数
				ssidMap.put("dc", dc);
				ssidMap.put("doc", doc);
				ssidMap.put("singleOrderNum", singleOrderNum);
				ssidMap.put("singleGains", singleGains);
				ssidMap.put("dayGains", dayGains);
				totalDC += dc;
				totalDOC += doc;
				
				totalSingleGains += singleGains;
				totalSingleOrderNum += singleOrderNum;
				totalGains += dayGains;
				singleMap.put("ssid", ssidMap);
				
				
				//===================友盟==================================
				singleMap.put("date", timeList.get(i));
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
					totalMap.put("clickAverNum", round((pcClickNum+mobileClickNum)*1.00/(pcUV+mobileUV)*100,2)+"%");
					totalMap.put("orderConversion", round((pcOrderNum+mbOrderNum)*1.00/(pcUV+mobileUV)*100,2)+"%");
					totalMap.put("orderComConversion", round((pcOrderComplete+mbOrderComplete)*1.00/(pcUV+mobileUV)*100,2)+"%");
				}
				totalMap.put("orderNum", pcOrderNum+mbOrderNum);
				totalMap.put("clickConversion", 0);
				if((pcClickNum+mobileClickNum)!=0){
					totalMap.put("clickConversion", round((pcOrderNum+mbOrderNum)*1.00/(pcClickNum+mobileClickNum)*100,2)+"%");
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
					pcMap.put("clickAverNum", round(pcClickNum*1.00/pcUV*100,2)+"%");
					pcMap.put("orderConversion", round(pcOrderNum*1.00/pcUV*100,2)+"%");
					pcMap.put("orderComConversion", round(pcOrderComplete*1.00/pcUV*100,2)+"%");
				}
				pcMap.put("orderNum", pcOrderNum);
				pcMap.put("clickConversion", 0);
				if(pcClickNum!=0){
					pcMap.put("clickConversion", round(pcOrderNum*1.00/pcClickNum*100,2)+"%");
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
					mobileMap.put("clickAverNum", round(mobileClickNum*1.00/mobileUV*100,2)+"%");
					mobileMap.put("orderConversion", round(mbOrderNum*1.00/mobileUV*100,2)+"%");
					mobileMap.put("orderComConversion", round(mbOrderComplete*1.00/mobileUV*100,2)+"%");
				}
				mobileMap.put("orderNum", mbOrderNum);
				mobileMap.put("clickConversion", 0);
				if(mobileClickNum!=0){
					mobileMap.put("clickConversion", round(mbOrderNum*1.00/mobileClickNum*100,2)+"%");
				}
				mobileMap.put("orderComplete", mbOrderComplete);
				mobileMap.put("orderAmount", mbOrderAmount);
				singleMap.put("mobile", mobileMap);
				
				Map<String,Object> iosMap=new HashMap<String,Object>();
				
				iosMap.put("uv", iosUV);
				iosMap.put("clickNum", iosClickNum);
				iosMap.put("clickAverNum", 0);
				if(iosUV!=0){
					iosMap.put("clickAverNum", round(iosClickNum*1.00/iosUV*100,2)+"%");
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
					androidMap.put("clickAverNum", round(androidClickNum*1.00/androidUV*100,2)+"%");
				}
				androidMap.put("orderNum", "-");
				androidMap.put("clickConversion", "-");
				androidMap.put("orderConversion", "-");
				androidMap.put("orderComplete", "-");
				androidMap.put("orderAmount", "-");
				androidMap.put("orderComConversion", "-");
				singleMap.put("android", androidMap);
				
//				Map<String,Object> otherMap=new HashMap<String,Object>();
//				
//				otherMap.put("uv", mobileUV-iosUV-androidUV);
//				otherMap.put("clickNum", mobileClickNum-iosClickNum-androidClickNum);
//				otherMap.put("clickAverNum", 0);
//				if((mobileUV-iosUV-androidUV)!=0){
//					otherMap.put("clickAverNum", round((mobileClickNum-androidClickNum-iosClickNum)*1.00/(mobileUV-iosUV-androidUV),2));
//				}
//				otherMap.put("orderNum", "-");
//				otherMap.put("clickConversion", "-");
//				otherMap.put("orderConversion", "-");
//				otherMap.put("orderComplete", "-");
//				otherMap.put("orderAmount", "-");
//				otherMap.put("orderComConversion", "-");
//				singleMap.put("other", otherMap);
				
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
				
				equipmentNums.add(doc);
				
				orderNums.add(occ);
				orderComNums.add(ofc);
				
				totalPrice.add(ofa);
				time.add(date);
			}
			totalDC = totalDC/timeList.size();
			totalDOC = totalDOC/timeList.size();
			
		LinkedHashMap<String,Object> tMaps=new LinkedHashMap<String,Object>();
		Map<String,Object> totalMap=new HashMap<String,Object>();
		totalMap.put("totalPV", totalPV);
		totalMap.put("totalUV", totalUV);
		//计算设备在线数/总数的平均值
		totalMap.put("totalDC", totalDC);
		totalMap.put("totalDOC", totalDOC);
		BigDecimal b = new BigDecimal(totalSingleGains);
		totalSingleGains =  b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
		totalMap.put("totalSingleGains", totalSingleGains);
		totalMap.put("totalSingleOrderNum", totalSingleOrderNum);
		totalMap.put("totalGains", totalGains);
		tMaps.put("ssid", totalMap);
		
		Map<String,Object> totalUmMap=new HashMap<String,Object>();
		totalUmMap.put("uv", totalUv);
		totalUmMap.put("clickNum", totalClickNum);
		totalUmMap.put("clickAverNum", 0);
		totalUmMap.put("orderConversion", 0);
		totalUmMap.put("orderComConversion", 0);
		if(totalUv!=0){
			totalUmMap.put("clickAverNum", round(totalClickNum*1.00/totalUv*100,2)+"%");
			totalUmMap.put("orderConversion", round((totalPcOrderNum+totalMbOrderNum)*1.00/totalUv*100,2)+"%");
			totalUmMap.put("orderComConversion", round((totalPcOrderComplete+totalMbOrderComplete)*1.00/totalUv*100,2)+"%");
		}
		totalUmMap.put("orderNum", totalPcOrderNum+totalMbOrderNum);
		totalUmMap.put("clickConversion", 0);
		if(totalClickNum!=0){
			totalUmMap.put("clickConversion", round((totalPcOrderNum+totalMbOrderNum)*1.00/totalClickNum*100,2)+"%");
		}
		totalUmMap.put("orderComplete", totalPcOrderComplete+totalMbOrderComplete);
		totalUmMap.put("orderAmount", totalPcOrderAmount+totalMbOrderAmount);
		tMaps.put("total",totalUmMap);
		Map<String,Object> pcMap=new HashMap<String,Object>();
		pcMap.put("uv", totalPcUV);
		pcMap.put("clickNum", totalPcClickNum);
		pcMap.put("clickAverNum", 0);
		pcMap.put("orderConversion", 0);
		pcMap.put("orderComConversion", 0);
		if(totalPcUV!=0){
			pcMap.put("clickAverNum", round(totalPcClickNum*1.00/totalPcUV*100,2)+"%");
			pcMap.put("orderConversion", round(totalPcOrderNum*1.00/totalPcUV*100,2)+"%");
			pcMap.put("orderComConversion", round(totalPcOrderComplete*1.00/totalPcUV*100,2)+"%");
		}
		pcMap.put("orderNum", totalPcOrderNum);
		pcMap.put("clickConversion", 0);
		if(totalPcClickNum!=0){
			pcMap.put("clickConversion", round(totalPcOrderNum*1.00/totalPcClickNum*100,2)+"%");
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
			mobileMap.put("clickAverNum", round(totalMobileClickNum*1.00/totalMobileUV*100,2)+"%");
			mobileMap.put("orderConversion", round(totalMbOrderNum*1.00/totalMobileUV*100,2)+"%");
			mobileMap.put("orderComConversion", round(totalMbOrderComplete*1.00/totalMobileUV*100,2)+"%");
		}
		mobileMap.put("orderNum", totalMbOrderNum);
		mobileMap.put("clickConversion", 0);
		if(totalMobileClickNum!=0){
			mobileMap.put("clickConversion", round(totalMbOrderNum*1.00/totalMobileClickNum*100,2)+"%");
		}
		mobileMap.put("orderComplete", totalMbOrderComplete);
		mobileMap.put("orderAmount", totalMbOrderAmount);
		tMaps.put("mobile", mobileMap);
		
		Map<String,Object> iosMap=new HashMap<String,Object>();
		
		iosMap.put("uv", totalIosUV);
		iosMap.put("clickNum", totalIosClickNum);
		iosMap.put("clickAverNum", 0);
		if(totalIosUV!=0){
			iosMap.put("clickAverNum", round(totalIosClickNum*1.00/totalIosUV*100,2)+"%");
		}
		iosMap.put("orderNum", "-");
		iosMap.put("clickConversion", "-");
		iosMap.put("orderConversion", "-");
		iosMap.put("orderComplete", "-");
		iosMap.put("orderAmount", "-");
		iosMap.put("orderComConversion", "-");
		tMaps.put("ios", iosMap);
		tMaps.put("date", startTime+" - "+endTime);
		
		Map<String,Object> androidMap=new HashMap<String,Object>();
		
		androidMap.put("uv", totalAndroidUV);
		androidMap.put("clickNum", totalAndroidClickNum);
		androidMap.put("clickAverNum", 0);
		if(totalAndroidUV!=0){
			androidMap.put("clickAverNum", round(totalAndroidClickNum*1.00/totalAndroidUV*100,2)+"%");
		}
		androidMap.put("orderNum", "-");
		androidMap.put("clickConversion", "-");
		androidMap.put("orderConversion", "-");
		androidMap.put("orderComplete", "-");
		androidMap.put("orderAmount", "-");
		androidMap.put("orderComConversion", "-");
		tMaps.put("android", androidMap);
		
		Map<String,Object> resMap=new HashMap<String,Object>();
		
		List<LinkedHashMap<String,Object>> pageResMaps=new ArrayList<LinkedHashMap<String,Object>>();
		int num=timeList.size();
		if(num>(pn-1)*ps&&num<pn*ps){
			for(int i=(pn-1)*ps;i<=num-1;i++){
				pageResMaps.add(resMaps.get(i));
			}
		}else if(num>pn*ps){
			for(int i=(pn-1)*ps;i<=pn*ps;i++){
				pageResMaps.add(resMaps.get(i));
			}
		}
		resMap.put("dataList", pageResMaps);
		resMap.put("total", tMaps);
		
		//折线图组装数据
		List<Object> astime=new ArrayList<Object>();
		List<Object> asequipmentNums=new ArrayList<Object>();
		
		List<Object> asorderNums=new ArrayList<Object>();
		List<Object> asorderComNums=new ArrayList<Object>();
		
		List<Object> astotalPrice=new ArrayList<Object>();
		
		for(int i=time.size()-1;i>=0;i--){
			astime.add(time.get(i));
			asequipmentNums.add(equipmentNums.get(i));
			asorderNums.add(orderNums.get(i));
			asorderComNums.add(orderComNums.get(i));
			astotalPrice.add(totalPrice.get(i));
		}
		equipments.add(astime);
		equipments.add(asequipmentNums);
		
		resMap.put("equipment", equipments);
		
		orders.add(astime);
		orders.add(asorderNums);
		orders.add(asorderComNums);
		resMap.put("order", orders);
		
		price.add(astime);
		price.add(astotalPrice);
		resMap.put("price", price);
		
		resMap.put("pn", pn);
		int totalPage=1;
		if(timeList.size()%ps==0){
			totalPage=timeList.size()/ps;
		}else{
			totalPage=timeList.size()/ps+1;
		}
		resMap.put("totalpage", totalPage);
		result=success(resMap);
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
		final List<String> macList = new ArrayList<String>();
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
			for(User i:userList){
				//获取在线Mac集合
				wifiDeviceDataSearchService.iteratorAllByCommon(i.getId(), "", 
						"", "",  "","", "",
					 100, new IteratorNotify<Page<WifiDeviceDocument>>() {
				    @Override
				    public void notifyComming(Page<WifiDeviceDocument> pages) {
				    	for (WifiDeviceDocument doc : pages) {
				    		String mac = doc.getD_mac();
				    		macList.add(mac);
				    	}
				    }
				});
			}
			
			//根据用户Id查询mac列表
			//macList = userWifiDeviceFacadeService.findUserWifiDeviceIdsByUid(userList.get(0).getId());
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
		final List<String> macList = new ArrayList<String>();
		if(StringUtils.isBlank(deviceLabel)){
			log.info("电话号码为空");
			return macList;
		}
		try {
				//获取在线Mac集合
				wifiDeviceDataSearchService.iteratorAllByCommon(null, "", 
						"", "",  "","", deviceLabel,
					 100, new IteratorNotify<Page<WifiDeviceDocument>>() {
				    @Override
				    public void notifyComming(Page<WifiDeviceDocument> pages) {
				    	for (WifiDeviceDocument doc : pages) {
				    		String mac = doc.getD_mac();
				    		macList.add(mac);
				    	}
				    }
				});
			
			//根据用户Id查询mac列表
			//macList = userWifiDeviceFacadeService.findUserWifiDeviceIdsByUid(userList.get(0).getId());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		return macList;
	}
	
	/**
	 * 时间升序排列
	 * @param dateNum
	 * @return
	 */
	public static List<String> getLastDay(int dateNum){
		List<String> list = new ArrayList<String>();
		//获取当前日期
		for (int i = 1; i <= dateNum; i++) {
			Date date = new Date();  
			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(date); 
			calendar.add(Calendar.DAY_OF_MONTH, -i);
			date = calendar.getTime();  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
			String dateNowStr = sdf.format(date); 
			list.add(dateNowStr);
		}
		return list; 
	}
	
	public static List<String> getDaysList(String beginTime,String endTime){
		List<String> list = new ArrayList<String>();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		list.add(endTime);
		try {
			start.setTime(format.parse(beginTime));
			end.setTime(format.parse(endTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		while(end.after(start))
		{
			//System.out.println(format.format(start.getTime()));
			end.add(Calendar.DAY_OF_MONTH,-1);
			list.add(format.format(end.getTime()).toString());
		}
		return list; 
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
	
	public static void main(String[] args) {
		List<String> timeList=getDaysList("2016-08-11","2016-08-17");
		//List<String> timeList=getLastDay(7);
		for(String i:timeList){
			System.out.println(i);
		}
	}
	/**
	 * 返回成功结果集
	 * @Title success 
	 * @Description 返回成功结果集
	 * @author Alan
	 * @date 2015年9月5日 上午10:22:28
	 * @updateTime 2015年9月5日 上午10:22:28
	 * @udpateAuthor  Alan
	 * @param body 返回值中body参数结果 Object 
	 * @return String   {"code":"0","msg":"success","body":[{"id":"1","name":"测试"}]}
	 */
	public static Map<String, Object> success(Object body){
		Map<String, Object> map=new HashMap<String, Object>();
		String code="0";
		String message="success";
		map.put("code", code);
		map.put("msg", message);
		map.put("success", true);
		if(body==null){
			body="{}";
		}
		map.put("result", body);
		return map;
	}
}