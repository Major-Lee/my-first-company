package com.bhu.vas.business.user;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.helper.CommonTimeHelper;
import com.bhu.vas.api.rpc.charging.model.GpathIncome;
import com.bhu.vas.api.rpc.charging.model.MacIncome;
import com.bhu.vas.api.rpc.charging.model.UserIncome;
import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.business.ds.statistics.service.UserIncomeMonthRankService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeRankService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.smartwork.msip.localunit.BaseTest;

public class UserWalletlogFacadeServiceTest extends BaseTest{
	@Resource
	UserWalletLogService userWalletLogService;
	@Resource
	UserWalletFacadeService userWalletFacadeService;
	@Resource
	UserIncomeRankService userIncomeRankService;
	@Resource
	UserIncomeMonthRankService userIncomeMonthRankService;
	@Test
	public void test() {
		String preDayDate =CommonTimeHelper.GetDateStr(-1);
        String today = CommonTimeHelper.GetDateStr(0);
//        System.out.println("preDate:"+preDayDate+"today:"+today);
//		List<UserIncome> userIncomesList =userWalletFacadeService.getUserIncomeService().findByLimit(today,0,1);
//    	System.out.println("select userIncom List size:"+userIncomesList.size());
//        if(userIncomesList.size() <= 0){
//        	//统计昨日用户收益userIncome
//        	List<Object> userIncomeList = userWalletFacadeService.getUserWalletLogService().findUserIncomeListByTime(preDayDate,today);
//        	for (Object object : userIncomeList) {
//        		UserIncome userIncomeVTO = (UserIncome) object;
//        		userIncomeVTO.setTime(preDayDate);
//        		userIncomeVTO.setUpdated_at(new Date());
//        		List<UserIncome> income=userWalletFacadeService.getUserIncomeService().findListByUid(userIncomeVTO.getUid(), preDayDate);
//        		if(income.size()<=0){
//        			System.out.println("userIncome");
//        			//userWalletFacadeService.getUserIncomeService().insert(userIncomeVTO);
//        		}
//        	}
//        }
//		
//        List<MacIncome> macIncomesList=userWalletFacadeService.getMacIncomeService().findByLimit(today,0,1);
//        System.out.println("select macIncomes List size:"+macIncomesList.size());
//        if(macIncomesList.size() <= 0){
//        	//统计昨日用户收益macIncome
//        	List<Object> macIncomeList = userWalletFacadeService.getUserWalletLogService().findMacIncomeListByTime(preDayDate,today);
//        	for (Object object : macIncomeList) {
//        		MacIncome macIncomeVTO = (MacIncome) object;
//        		macIncomeVTO.setTime(preDayDate);
//        		macIncomeVTO.setUpdated_at(new Date());
//        		List<MacIncome> macIncomes=userWalletFacadeService.getMacIncomeService().findListByMac(macIncomeVTO.getMac(), preDayDate);
//        		if(macIncomes.size()<=0){
//        			System.out.println("macIncomes");
//
//        			//userWalletFacadeService.getMacIncomeService().insert(macIncomeVTO);
//        		}
//        	}
//        }
//        
//        //统计昨日用户收益gpathIncome
//        List<GpathIncome> gpathIncomesList=userWalletFacadeService.getGpathIncomeService().findByLimit(today,0,1);
//        System.out.println("select gpathIncomes List size:"+gpathIncomesList.size());
//        if(gpathIncomesList.size() <= 0){
//    		List<Object> gpathIncomeList = userWalletFacadeService.getUserWalletLogService().findGpathIncomeListByTime(preDayDate,today);
//    		for (Object object : gpathIncomeList) {
//    			GpathIncome gpathIncomeVTO = (GpathIncome) object;
//    			System.out.println(gpathIncomeVTO.getGpath());
//    			gpathIncomeVTO.setTime(preDayDate);
//    			gpathIncomeVTO.setUpdated_at(new Date());
//    			List<GpathIncome> gpathIncomes=userWalletFacadeService.getGpathIncomeService().findListByGpath(gpathIncomeVTO.getGpath(), preDayDate);
//    			if(gpathIncomes.size()<=0){
//    				System.out.println("GpathIncomes");
////    				userWalletFacadeService.getGpathIncomeService().insert(gpathIncomeVTO);
//    			}
//    		}
//    	}
//		
        Date date = new Date();  
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(date);  
		calendar.add(Calendar.MONTH, -1);  
		date = calendar.getTime();  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
		String time =sdf.format(date); 
		UserIncomeMonthRank incomeRankNow= userIncomeMonthRankService.getByUid(1,time+"%");
		if(incomeRankNow == null){
			System.out.println("null");
		}
		
//		String preDayDate =CommonTimeHelper.GetDateStr(-1);
//        String today = CommonTimeHelper.GetDateStr(0);
//        System.out.println("preDate:"+preDayDate+"today:"+today);
//        List<Object> userIncomeMap = userWalletLogService.findUserIncomeListByTime(preDayDate, today);
//        for (Object object : userIncomeMap) {
//        	UserIncome userIncomeVTO = (UserIncome) object;
//        	System.out.println(userIncomeVTO.getUid());
//    		userIncomeVTO.setTime(preDayDate);
//    		userIncomeVTO.setUpdated_at(new Date());
//			List<UserIncome> income=userWalletFacadeService.getUserIncomeService().findListByUid(userIncomeVTO.getUid(), preDayDate);
//			if(income.size()<1){
//				userWalletFacadeService.getUserIncomeService().insert(userIncomeVTO);
//			}
//		}
//        if(userIncomeMap != null){
//        	for (int i = 0; i < userIncomeMap.size(); i++) {
//        		UserIncome userIncomeVTO = (UserIncome) userIncomeMap.get(i);
//        		userIncomeVTO.setTime(preDayDate);
//        		userIncomeVTO.setUpdated_at(new Date());
//    			List<UserIncome> income=userWalletFacadeService.getUserIncomeService().findListByUid(userIncomeVTO.getUid(), preDayDate);
//    			if(income.size()<1){
//    				userWalletFacadeService.getUserIncomeService().insert(userIncomeVTO);
//    			}
//    		}
//        }
        
//		for (UserIncomeVTO userIncomeVTO : userIncomeMap) {
//			UserIncome userIncome=new UserIncome();
//			userIncome.setTime(preDayDate);
//			userIncome.setUid(userIncomeVTO.getUid());
//			userIncome.setIncome(userIncomeVTO.getIncome());
//			userIncome.setTimes(userIncomeVTO.getTimes());
//			List<UserIncome> income=userWalletFacadeService.getUserIncomeService().findListByUid(userIncomeVTO.getUid(), preDayDate);
//			if(income.size()<1){
//				System.out.println("ss"+income.size());
//				//userWalletFacadeService.getUserIncomeService().insert(userIncome);
//			}
//		}
		//System.out.println(userIncomeList.size());
		/*
		String start_time = "2015-08-01 00:00:00";
		String end_time = "2016-08-11 23:59:59";
		Integer uid = 100153;
		String mac = "";
		String umac = "44:00:10:80:1f:6c";
		Map<String, Object> sum= userWalletLogService.getEntityDao().fetchCashSumAndCountByUid(uid, start_time, end_time, null,umac,10);
		Iterator<Map.Entry<String, Object>> it = sum.entrySet().iterator();
		while (it.hasNext()) {
			   Map.Entry<String, Object> entry = it.next();
			   System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
		}
		*/
	}
	
}
