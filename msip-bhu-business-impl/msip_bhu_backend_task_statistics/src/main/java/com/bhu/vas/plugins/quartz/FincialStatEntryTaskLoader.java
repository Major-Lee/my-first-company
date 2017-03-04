package com.bhu.vas.plugins.quartz;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import com.bhu.vas.api.rpc.statistics.model.FincialStatistics;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;


/**
 * 每天0点0分1秒运行
 * @author dell
 *
 */
public class FincialStatEntryTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(FincialStatEntryTaskLoader.class);
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	
	public void execute() {
		logger.info("FincialStatEntryTaskLoader start....");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		Calendar cale = Calendar.getInstance();   
        cale.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天 
        String lastDay = format.format(cale.getTime());
        
        cale.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
        String firstDay=format.format(cale.getTime());

        System.out.println("-----2------lastDay:"+lastDay);
        
        FincialStatistics fincialStatistics=new FincialStatistics();
		try {
			userWalletFacadeService.getFincialStatisticsService().insert(fincialStatistics);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		logger.info("FincialStatEntryTaskLoader end....");
	}
	
}
