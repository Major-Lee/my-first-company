package com.wecite.toplines.business.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.localunit.BaseTest;
import com.wecite.toplines.business.bucache.redis.serviceimpl.statistics.SubjectStatisticsFragmentService;

public class SubjectClickCommingServiceTest extends BaseTest{
	
	@Test
	public void doClickTest(){
		int sid = 100011;
		long incr = 1;
		List<Integer> tags = new ArrayList<Integer>();
		tags.add(1001);
		tags.add(1002);
		tags.add(1003);
		List<String> fragments = DateTimeHelper.generateServalDateFormat(new Date());
		try{
			for(String fragment:fragments){
				SubjectStatisticsFragmentService.getInstance().subjectClickComming(fragment, String.valueOf(sid), incr,
						tags, HttpHelper.parseAllDomains("http://www.baidu.com"));	
			}
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
		
		for(String fragment:fragments){
			System.out.println(SubjectStatisticsFragmentService.getInstance().subjectClickScore(fragment, String.valueOf(tags.get(0)), String.valueOf(sid)));
		}
		
		HttpHelper.parseAllDomains("http://www.baidu.com");
		
	}

}
