package com.bhu.vas.business.user;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.business.ds.statistics.service.UserIncomeMonthRankService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeRankService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.smartwork.msip.localunit.BaseTest;

public class UserWalletlogFacadeServiceTest extends BaseTest{
	@Resource
	UserWalletLogService userWalletLogService;
	@Resource
	UserIncomeService userIncomeService;
	@Resource
	UserWalletFacadeService userWalletFacadeService;
	@Resource
	UserIncomeRankService userIncomeRankService;
	@Resource
	UserIncomeMonthRankService userIncomeMonthRankService;
	@Test
	public void test() {

		int x=0;
		Date date = new Date();  
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(date);  
		calendar.add(Calendar.MONTH, x-1);  
		date = calendar.getTime();  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
		String time =sdf.format(date);
		Date dateNow = new Date();  
		Calendar calendarNow = Calendar.getInstance();  
		calendarNow.setTime(dateNow);  
		calendarNow.add(Calendar.MONTH, x);  
		dateNow = calendarNow.getTime();  
		String timeNow =sdf.format(dateNow); 
		System.out.println(timeNow);
		//如果上月统计已执行，就跳过
		List<UserIncomeMonthRank> userIncomList = userIncomeMonthRankService.findByLimit(time+"%",0,1);
		System.out.println("User Income Month Rank size:"+userIncomList.size());
		if(userIncomList.size()<=0){
			System.out.println(time +"month rank list task statistics starting...");
			Date datebefore = new Date();  
			Calendar calendarBefore = Calendar.getInstance();  
			calendarBefore.setTime(datebefore);  
			calendarBefore.add(Calendar.MONTH, x-2);  
			datebefore = calendarBefore.getTime();  
			String timeBefore =sdf.format(datebefore); 
			//userIncomeRankService.deleteAllRank();
			List<UserIncome> userIncomes=userIncomeService.findMonthList(time+"%");
			System.out.println("month rank userIncomes size:"+userIncomes.size());
			if(userIncomes.size()>0){
				String beforeIncome="0";
				int beforeRankNum=0;
				int n=1;
				int m=1;
				for(int i=userIncomes.size()-1;i>=0;i--){
					UserIncomeMonthRank userIncomeMonthRank=new UserIncomeMonthRank();
					UserIncome userIncome=userIncomes.get(i);
					if(i==userIncomes.size()-1){
						beforeRankNum=n;
						beforeIncome=userIncome.getIncome();
					}else{
						if(!StringUtils.equals(beforeIncome, userIncome.getIncome())){
							beforeRankNum=m;
							beforeIncome=userIncome.getIncome();
							n=m;
						}
					}
					userIncomeMonthRank.setRank(beforeRankNum);
					userIncomeMonthRank.setIncome(userIncome.getIncome());
					UserIncomeMonthRank incomeRank=userIncomeMonthRankService.getByUid(userIncome.getUid(),timeBefore+"%");
					userIncomeMonthRank.setUid(userIncome.getUid());
					if(incomeRank!=null){
						userIncomeMonthRank.setBeforeIncome(incomeRank.getIncome());
						userIncomeMonthRank.setBeforeRank(incomeRank.getRank());
						userIncomeMonthRank.setCreated_at(date);
					}else{
						userIncomeMonthRank.setCreated_at(date);
						userIncomeMonthRank.setBeforeIncome(userIncomeMonthRank.getIncome());
						userIncomeMonthRank.setBeforeRank(9999999);
					}
					UserIncomeMonthRank incomeRankNow= userIncomeMonthRankService.getByUid(userIncome.getUid(),time+"%");
					System.out.println("insert month rank table"+incomeRankNow);
					if(incomeRankNow == null){
						//userIncomeMonthRankService.insert(userIncomeMonthRank);
					}
					m++;
				}
			}
		}else{
			System.out.println(time +"month rank list task statistics had finished.");
		}
	}
	
}
