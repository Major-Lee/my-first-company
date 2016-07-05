package com.bhu.vas.di.op.task;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.helper.ArithHelper;

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
				userIncome.setIncome(i.getCash().substring(1));
				userIncome.setType("uid");
				userIncome.setId(time+"-"+i.getUid());
				UserIncome income=userWalletFacadeService.getUserIncomeService().getById(userIncome.getId());
				if(income==null){
					userWalletFacadeService.getUserIncomeService().insert(userIncome);
				}else{
					//ArithHelper.round(v, scale);
					userIncome.setIncome(String.valueOf(round(Double.valueOf(income.getIncome())+Double.valueOf(userIncome.getIncome()),2)));
					userWalletFacadeService.getUserIncomeService().update(userIncome);
				}
				if(StringUtils.isNoneBlank(i.getMac())&&!StringUtils.equals("-", i.getMac())){
					userIncome.setId(time+"-"+i.getMac());
					userIncome.setType("mac");
					income=userWalletFacadeService.getUserIncomeService().getById(userIncome.getId());
					if(income==null){
						userWalletFacadeService.getUserIncomeService().insert(userIncome);
					}else{
						userIncome.setIncome(String.valueOf(round(Double.valueOf(income.getIncome())+Double.valueOf(userIncome.getIncome()),2)));
						userWalletFacadeService.getUserIncomeService().update(userIncome);
					}
				}else if(StringUtils.isNoneBlank(i.getCurrent_gpath())&&!StringUtils.equals("-", i.getCurrent_gpath())){
					userIncome.setId(time+"-"+i.getCurrent_gpath());
					userIncome.setType("gpath");
					income=userWalletFacadeService.getUserIncomeService().getById(userIncome.getId());
					if(income==null){
						userWalletFacadeService.getUserIncomeService().insert(userIncome);
					}else{
						userIncome.setIncome(String.valueOf(round(Double.valueOf(income.getIncome())+Double.valueOf(userIncome.getIncome()),2)));
						userWalletFacadeService.getUserIncomeService().update(userIncome);
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
