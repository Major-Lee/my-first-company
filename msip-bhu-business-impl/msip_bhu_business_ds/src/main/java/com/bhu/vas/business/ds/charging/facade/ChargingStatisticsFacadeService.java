package com.bhu.vas.business.ds.charging.facade;

import java.util.ArrayList;
import java.util.Date;
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
import com.smartwork.msip.cores.helper.ArithHelper;
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
    	
    	String current_key = DeviceGroupPaymentStatistics.combineid(groupid, uid, current_date);
    	ShareDealDailyGroupSummaryProcedureVTO cache_entity = businessStatisticsCacheService.getDeviceGroupPaymentStatisticsDSCacheBy(current_key);
    	if(cache_entity == null){
    		cache_entity = userWalletFacadeService.sharedealDailyGroupSummaryWithProcedure(uid, group_path, current_date);
    		//businessStatisticsCacheService.storeDeviceGroupPaymentStatisticsDSCacheResult(current_key, cache_entity);
    	}
    	vto.setT_incoming_amount(String.valueOf(cache_entity.getTotal_cash()));
    	vto.setT_times(cache_entity.getTotal_nums());
    	
    	List<String> ids = new ArrayList<String>();
    	//ids.add(DeviceGroupPaymentStatistics.combineid(groupid, current_date));
    	ids.add(DeviceGroupPaymentStatistics.combineid(groupid, uid, yesterday_date));
    	ids.add(DeviceGroupPaymentStatistics.combineid(groupid, uid, DeviceGroupPaymentStatistics.TOTAL_DATE_STR));
    	List<DeviceGroupPaymentStatistics> statistics_entities = deviceGroupPaymentStatisticsService.findByIds(ids, true, true);
    	DeviceGroupPaymentStatistics yesterday_statistics_entity = statistics_entities.get(0);
    	if(yesterday_statistics_entity != null){
    		vto.setY_incoming_amount(yesterday_statistics_entity.getTotal_incoming_amount());
    		vto.setY_times(yesterday_statistics_entity.getTotal_times());
    	}
    	DeviceGroupPaymentStatistics total_statistics_entity = statistics_entities.get(1);
    	if(total_statistics_entity != null){
    		double total_incoming_amount = Double.parseDouble(total_statistics_entity.getTotal_incoming_amount());
    		int total_times = total_statistics_entity.getTotal_times();
    		double current_total_incoming_amount = ArithHelper.round(cache_entity.getTotal_cash() + total_incoming_amount, 2);
    		int current_total_times = cache_entity.getTotal_nums() + total_times;
    		vto.setT_incoming_amount(String.valueOf(current_total_incoming_amount));
    		vto.setT_times(current_total_times);
    	}
    	return vto;
    }
    
    /**
     * 增加一个设备分组的收益统计数据
     * @param uid
     * @param groupid 分组id
     * @param date 日期 yyyy-MM-dd
     * @param total_incoming_amount 分组收益
     * @param total_times 分组打赏次数
     */
    public void gainDeviceGroupPaymentStatistics(int uid, String groupid, String date, String total_incoming_amount,
    		int total_times){
    	if(StringUtils.isEmpty(groupid)){
    		groupid = DeviceGroupPaymentStatistics.DEFAULT_GROUP;
    	}
    	String id = DeviceGroupPaymentStatistics.combineid(groupid, uid, date);
    	DeviceGroupPaymentStatistics entity = deviceGroupPaymentStatisticsService.getById(id);
    	if(entity == null){
        	entity = new DeviceGroupPaymentStatistics();
        	entity.setId(id);
        	entity.setUid(uid);
        	entity.setGroupid(groupid);
        	entity.setTotal_payment_amount("0.00");
        	entity.setTotal_incoming_amount(total_incoming_amount);
        	entity.setTotal_times(total_times);
        	entity.setCreated_at(new Date());
        	deviceGroupPaymentStatisticsService.insert(entity);
    	}else{
        	entity.setUid(uid);
        	entity.setGroupid(groupid);
        	entity.setTotal_payment_amount("0.00");
        	entity.setTotal_incoming_amount(total_incoming_amount);
        	entity.setTotal_times(total_times);
        	deviceGroupPaymentStatisticsService.update(entity);
    	}
    }
    
    
    /**
     * 重新计算此设备分组下的收益总额/次数
     * 并且更新总额数据
     * @param uid
     * @param groupid
     */
	public void deviceGroupPaymentTotalWithProcedure(int uid, String gid){
		DeviceGroupPaymentTotalProcedureDTO procedureDTO = new DeviceGroupPaymentTotalProcedureDTO();
		procedureDTO.setUserid(uid);
		procedureDTO.setGid(gid);

		int executeRet = deviceGroupPaymentStatisticsService.executeProcedure(procedureDTO);
		if(executeRet == 0){
			;
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR,new String[]{procedureDTO.getName()});
		}
	}
}
