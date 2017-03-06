package com.bhu.vas.business.statistics;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.charging.model.StatisticFincialIncome;
import com.bhu.vas.business.ds.statistics.service.StatisticFincialIncomeService;
import com.smartwork.msip.localunit.BaseTest;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatisticFincialIncomeServiceTest extends BaseTest{
	
	@Resource
	private StatisticFincialIncomeService statisticFincialIncomeService;
	
    //@Test
	public void test001BatchCreateDefault(){
		StatisticFincialIncome statisticFincialIncome = statisticFincialIncomeService.getById(78);
		System.out.println(statisticFincialIncome.getDayid());
	}
    
}
