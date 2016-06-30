package com.bhu.vas.di.op.task;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
	        time="2016-06-24";
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
					userIncome.setIncome(String.valueOf(Double.valueOf(income.getIncome())+Double.valueOf(userIncome.getIncome())));
					userWalletFacadeService.getUserIncomeService().update(userIncome);
				}
				if(StringUtils.isNoneBlank(i.getMac())&&!StringUtils.equals("-", i.getMac())){
					userIncome.setId(time+"-"+i.getMac());
					userIncome.setType("mac");
					income=userWalletFacadeService.getUserIncomeService().getById(userIncome.getId());
					if(income==null){
						userWalletFacadeService.getUserIncomeService().insert(userIncome);
					}else{
						userIncome.setIncome(String.valueOf(Double.valueOf(income.getIncome())+Double.valueOf(userIncome.getIncome())));
						userWalletFacadeService.getUserIncomeService().update(userIncome);
					}
				}else if(StringUtils.isNoneBlank(i.getCurrent_gpath())&&!StringUtils.equals("-", i.getCurrent_gpath())){
					userIncome.setId(time+"-"+i.getCurrent_gpath());
					userIncome.setType("gpath");
					income=userWalletFacadeService.getUserIncomeService().getById(userIncome.getId());
					if(income==null){
						userWalletFacadeService.getUserIncomeService().insert(userIncome);
					}else{
						userIncome.setIncome(String.valueOf(Double.valueOf(income.getIncome())+Double.valueOf(userIncome.getIncome())));
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
}
