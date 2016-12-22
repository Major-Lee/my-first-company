package com.bhu.vas.plugins.quartz;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.statistics.model.FincialStatistics;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;


/**
 * 每天三点运行
 * @author dell
 *
 */
public class FincialStatisticsTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(FincialStatisticsTaskLoader.class);
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	public void execute() {
		logger.info("FincialStatisticsTaskLoader start....");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		Calendar cale = Calendar.getInstance();   
        cale.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天 
        String lastDay = format.format(cale.getTime());
        //String lastDay = "2016-04-30";
        
        cale.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
        String firstDay=format.format(cale.getTime());
        //String firstDay="2016-04-01";
        System.out.println("-----2------lastDay:"+lastDay);
        FincialStatistics fincialStatistics=new FincialStatistics();
		float ctm=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",1,"Midas")))/100;
		float cpm=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",0,"Midas")))/100;
		float cta=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",1,"%Alipay")))/100;
		float cpa=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",0,"%Alipay")))/100;
		float ctw=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",1,"%Weixin")))/100;
		float cpw=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",0,"%Weixin")))/100;
		fincialStatistics.setCpa(cpa);
		fincialStatistics.setCpm(cpm);
		fincialStatistics.setCpw(cpw);
		fincialStatistics.setCtw(ctw);
		fincialStatistics.setCta(cta);
		fincialStatistics.setCtm(ctm);
		fincialStatistics.setId(firstDay.substring(0, 7));
		try {
			userWalletFacadeService.getFincialStatisticsService().insert(fincialStatistics);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		logger.info("FincialStatisticsTaskLoader end....");
	}
	
}
