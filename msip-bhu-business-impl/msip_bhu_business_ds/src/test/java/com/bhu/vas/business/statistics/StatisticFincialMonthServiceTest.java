package com.bhu.vas.business.statistics;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.charging.model.StatisticFincialMonth;
import com.bhu.vas.business.ds.statistics.service.StatisticFincialMonthService;
import com.smartwork.msip.localunit.BaseTest;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatisticFincialMonthServiceTest extends BaseTest{
	
	@Resource
	private StatisticFincialMonthService statisticFincialMonthService;
	
    //@Test
	public void test001BatchCreateDefault(){
		StatisticFincialMonth statisticFincialMonth = statisticFincialMonthService.getById(40);
		System.out.println(statisticFincialMonth.getMonthid());
	}
    
}
