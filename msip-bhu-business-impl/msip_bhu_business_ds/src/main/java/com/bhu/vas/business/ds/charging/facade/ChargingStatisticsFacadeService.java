package com.bhu.vas.business.ds.charging.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.procedure.DeviceGroupPaymentTotalProcedureDTO;
import com.bhu.vas.api.rpc.charging.model.DeviceGroupPaymentStatistics;
import com.bhu.vas.api.rpc.charging.vto.DeviceGroupPaymentStatisticsVTO;
import com.bhu.vas.api.rpc.user.dto.ShareDealDailyGroupSummaryProcedureVTO;
import com.bhu.vas.business.bucache.local.serviceimpl.statistics.BusinessStatisticsCacheService;
import com.bhu.vas.business.ds.charging.service.DeviceGroupPaymentStatisticsService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class ChargingStatisticsFacadeService {

    @Resource
    private DeviceGroupPaymentStatisticsService deviceGroupPaymentStatisticsService;
    
    @Resource
    private BusinessStatisticsCacheService businessStatisticsCacheService;
    
    @Resource
    private UserWalletFacadeService userWalletFacadeService;
    
    /**
     * 获取某一个用户分组下的今日/昨日打赏收益/次数, 总打赏收益/次数
     * @return
     */
    public DeviceGroupPaymentStatisticsVTO fetchDeviceGroupPaymentStatistics(int uid, String groupid, 
    		String group_path){
    	DeviceGroupPaymentStatisticsVTO vto = new DeviceGroupPaymentStatisticsVTO();
    	if(StringUtils.isEmpty(groupid)){
    		groupid = DeviceGroupPaymentStatistics.DEFAULT_GROUP;
    	}
    	//当日日期 yyyy-MM-dd
    	String current_date = DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5);
    	//昨日日期 yyyy-MM-dd
    	String yesterday_date = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1), 
    			DateTimeHelper.FormatPattern5);
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
    	String current_key = DeviceGroupPaymentStatistics.combineid(groupid, current_date);
    	ShareDealDailyGroupSummaryProcedureVTO cache_entity = businessStatisticsCacheService.getDeviceGroupPaymentStatisticsDSCacheBy(current_key);
    	if(cache_entity == null){
    		cache_entity = userWalletFacadeService.sharedealDailyGroupSummaryWithProcedure(uid, group_path, current_date);
    		businessStatisticsCacheService.storeDeviceGroupPaymentStatisticsDSCacheResult(current_key, cache_entity);
    	}
    	vto.setT_incoming_amount(String.valueOf(cache_entity.getTotal_cash()));
    	vto.setT_times(cache_entity.getTotal_nums());
    	return vto;
    }
    
    /**
     * 重新计算此设备分组下的收益总额/次数
     * 并且更新总额数据
     * @param uid
     * @param groupid
     */
	public void deviceGroupPaymentTotalWithProcedure(int uid, String groupid){
		DeviceGroupPaymentTotalProcedureDTO procedureDTO = new DeviceGroupPaymentTotalProcedureDTO();
		procedureDTO.setUserid(uid);
		procedureDTO.setGroupid(groupid);

		int executeRet = deviceGroupPaymentStatisticsService.executeProcedure(procedureDTO);
		if(executeRet == 0){
			;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{procedureDTO.getName()});
		}
	}
}
