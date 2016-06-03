package com.bhu.vas.di.op.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.statistics.model.FincialStatistics;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;

public class FincialStatisticsBuilderOp {
	public static void main(String[] args) {
		System.out.println(0);
		ClassPathXmlApplicationContext context = null;
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
		try{
			String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
			context = new ClassPathXmlApplicationContext(CONFIG, FincialStatisticsBuilderOp.class);
			System.out.println(1);
			context.start();
			UserWalletFacadeService userWalletFacadeService = context.getBean("userWalletFacadeService",UserWalletFacadeService.class);
			float ctm=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",1,"Midas")))/100;
			float cpm=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",0,"Midas")))/100;
			float cta=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",1,"Alipay")))/100;
			float cpa=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",0,"Alipay")))/100;
			float ctw=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",1,"Weixin")))/100;
			float cpw=(float)(Math.round(100*userWalletFacadeService.fincialStatisticsWithProcedure(firstDay+" 00:00:00", lastDay+" 23:59:59",0,"Weixin")))/100;
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
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		System.exit(1);
	}
}
