package com.bhu.vas.di.op.task;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.charging.model.GpathIncome;
import com.bhu.vas.api.rpc.charging.model.MacIncome;
import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;


public class UserIncomeBuilderOp {
	public static void main(String[] args) {
		System.out.println(0);
		ClassPathXmlApplicationContext context = null;
		try{
			String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
			context = new ClassPathXmlApplicationContext(CONFIG, UserIncomeBuilderOp.class);
			System.out.println(1);
			context.start();
			Date date = new Date();  
	        Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(date);  
	        calendar.add(Calendar.DAY_OF_MONTH, -1);  
	        date = calendar.getTime();  
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	        String time = sdf.format(date); 
	        	UserWalletFacadeService userWalletFacadeService = context.getBean("userWalletFacadeService",UserWalletFacadeService.class);
	        	List<UserWalletLog> userWalletLogs= userWalletFacadeService.getUserWalletLogService().findListByTime(time);
	        	for(UserWalletLog i:userWalletLogs){
	 				UserIncome userIncome=new UserIncome();
	 				userIncome.setTime(time);
	 				userIncome.setUid(i.getUid());
	 				userIncome.setIncome(i.getCash().substring(1));
	 				userIncome.setTimes(1);
	 				List<UserIncome> income=userWalletFacadeService.getUserIncomeService().findListByUid(userIncome.getUid(), time);
	 				if(income==null||income.size()<1){
	 					userWalletFacadeService.getUserIncomeService().insert(userIncome);
	 				}else{
	 					int num=income.get(0).getTimes();
	 					num++;
	 					userIncome.setTimes(num);
	 					//ArithHelper.round(v, scale);
	 					userIncome.setIncome(String.valueOf(round(Double.valueOf(income.get(0).getIncome())+Double.valueOf(userIncome.getIncome()),2)));
	 					userIncome.setId(income.get(0).getId());
	 					userWalletFacadeService.getUserIncomeService().update(userIncome);
	 				}
	 				if(StringUtils.isNoneBlank(i.getMac())&&!StringUtils.equals("-", i.getMac())){
	 					MacIncome macIncome=new MacIncome();
	 					macIncome.setTime(time);
	 					macIncome.setMac(i.getMac());
	 					macIncome.setUid(i.getUid());
	 					macIncome.setTimes(1);
	 					macIncome.setIncome(i.getCash().substring(1));
	 					List<MacIncome> macIncomes=userWalletFacadeService.getMacIncomeService().findListByMac(i.getMac(), time);
	 					if(macIncomes==null||macIncomes.size()<1){
	 						userWalletFacadeService.getMacIncomeService().insert(macIncome);
	 					}else{
	 						int num=macIncomes.get(0).getTimes();
	 	 					num++;
	 	 					macIncome.setTimes(num);
	 						macIncome.setId(macIncomes.get(0).getId());
	 						macIncome.setIncome(String.valueOf(round(Double.valueOf(macIncomes.get(0).getIncome())+Double.valueOf(macIncome.getIncome()),2)));
	 						userWalletFacadeService.getMacIncomeService().update(macIncome);
	 					}
	 				}
	 				if(StringUtils.isNoneBlank(i.getCurrent_gpath())&&!StringUtils.equals("-", i.getCurrent_gpath())){
	 					GpathIncome gpathIncome=new GpathIncome();
	 					gpathIncome.setGpath(i.getCurrent_gpath());
	 					gpathIncome.setTime(time);
	 					gpathIncome.setIncome(i.getCash().substring(1));
	 					gpathIncome.setTimes(1);
	 					List<GpathIncome> gpathIncomes=userWalletFacadeService.getGpathIncomeService().findListByGpath(i.getCurrent_gpath(), time);
	 					if(gpathIncomes==null||gpathIncomes.size()<1){
	 						userWalletFacadeService.getGpathIncomeService().insert(gpathIncome);
	 					}else{
	 						int num=gpathIncomes.get(0).getTimes();
	 	 					num++;
	 	 					gpathIncome.setTimes(num);
	 						gpathIncome.setId(gpathIncomes.get(0).getId());
	 						gpathIncome.setIncome(String.valueOf(round(Double.valueOf(gpathIncomes.get(0).getIncome())+Double.valueOf(gpathIncome.getIncome()),2)));
	 						userWalletFacadeService.getGpathIncomeService().update(gpathIncome);
	 					}
	 				}
	        	}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{

		}
		System.exit(1);
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
}
