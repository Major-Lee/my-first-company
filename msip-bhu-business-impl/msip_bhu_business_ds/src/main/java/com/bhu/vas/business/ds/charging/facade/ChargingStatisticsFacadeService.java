package com.bhu.vas.business.ds.charging.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.charging.model.DeviceGroupPaymentStatistics;
import com.bhu.vas.api.rpc.charging.vto.DeviceGroupPaymentStatisticsVTO;
import com.bhu.vas.business.ds.charging.service.DeviceGroupPaymentStatisticsService;
import com.smartwork.msip.cores.helper.DateTimeHelper;

@Service
public class ChargingStatisticsFacadeService {

    @Resource
    private DeviceGroupPaymentStatisticsService deviceGroupPaymentStatisticsService;
    
    /**
     * 获取某一个用户分组下的今日/昨日打赏收益/次数, 总打赏收益/次数
     * @return
     */
    public DeviceGroupPaymentStatisticsVTO fetchDeviceGroupPaymentStatistics(String groupid){
    	DeviceGroupPaymentStatisticsVTO vto = new DeviceGroupPaymentStatisticsVTO();
    	if(StringUtils.isEmpty(groupid)){
    		groupid = DeviceGroupPaymentStatistics.DEFAULT_GROUP;
    	}
    	//当日日期 yyyyMMdd
    	String current_date = DateTimeHelper.formatDate(DateTimeHelper.FormatPattern7);
    	//昨日日期 yyyyMMdd
    	String yesterday_date = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1), 
    			DateTimeHelper.FormatPattern7);
    	List<String> ids = new ArrayList<String>();
    	//ids.add(DeviceGroupPaymentStatistics.combineid(groupid, current_date));
    	ids.add(DeviceGroupPaymentStatistics.combineid(groupid, yesterday_date));
    	ids.add(DeviceGroupPaymentStatistics.combineid(groupid, DeviceGroupPaymentStatistics.TOTAL_DATE_STR));
    	List<DeviceGroupPaymentStatistics> statistics_entities = deviceGroupPaymentStatisticsService.findByIds(ids, true, true);
    	DeviceGroupPaymentStatistics yesterday_statistics_entity = statistics_entities.get(0);
    	if(yesterday_statistics_entity != null){
    		vto.setY_incoming_amount(yesterday_statistics_entity.getTotal_incoming_amount());
    		vto.setY_times(yesterday_statistics_entity.getTotal_times());
    	}
    	DeviceGroupPaymentStatistics total_statistics_entity = statistics_entities.get(1);
    	if(total_statistics_entity != null){
    		vto.setT_incoming_amount(total_statistics_entity.getTotal_incoming_amount());
    		vto.setT_times(total_statistics_entity.getTotal_times());
    	}
    	return vto;
    }
}
