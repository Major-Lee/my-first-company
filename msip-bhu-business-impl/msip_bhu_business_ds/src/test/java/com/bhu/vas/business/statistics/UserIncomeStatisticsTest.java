package com.bhu.vas.business.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.charging.model.StatisticFincialIncome;
import com.bhu.vas.api.rpc.charging.model.StatisticFincialMonth;
import com.bhu.vas.business.ds.statistics.service.StatisticFincialIncomeService;
import com.bhu.vas.business.ds.statistics.service.StatisticFincialMonthService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeService;
import com.smartwork.msip.localunit.BaseTest;

/**
 * Created by bluesand on 4/28/15.
 */
public class UserIncomeStatisticsTest extends BaseTest {

	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private StatisticFincialMonthService statisticFincialMonthService;
	@Resource
	private StatisticFincialIncomeService statisticFincialIncomeService;



    @Test
    public void find() {
//        UserBrandStatistics userBrandStatistics = userBrandStatisticsService.getById("2015-05-23");
//
//        System.out.println(userBrandStatistics.getExtension_content());
//
//        System.out.println(userBrandStatistics.getInnerModels());
////        List<UserBrandDTO> userBrandStatisticsDTOs  =
////                JsonHelper.getDTOList(userBrandStatistics.getExtension_content(),UserBrandDTO.class);
//
//        //System.out.println(userBrandStatisticsDTOs);

    	Date date = new Date();  
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(date);  
		calendar.add(Calendar.MONTH, 0-1);  
		date = calendar.getTime();  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
		String time =sdf.format(date);
		System.out.println(time);
		StatisticFincialMonth statisticFincialMonth = statisticFincialMonthService.getById(40);
		System.out.println("statisticFincialMonth："+statisticFincialMonth.getMonthid());
		
		StatisticFincialIncome statisticFincialIncome = statisticFincialIncomeService.getById(78);
		System.out.println("statisticFincialIncome："+statisticFincialIncome.getDayid());
//		UserIncome userIncome = userIncomeService.getById("219");
//		System.out.println(userIncome.getIncome());
//		List<UserIncome> userIncomList=userIncomeService.findMonthList(time+"%");
//		System.out.println("userIncomList size:"+userIncomList.size());
//		if(userIncomList.size()<=0){
//			
//			System.out.println(userIncomList.size()+"statring");
//		}else{
//			
//			System.out.println(userIncomList.size());
//		}


    }


}
