package com.wecite.toplines.business.subject;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.subject.model.Subject;
import com.bhu.vas.business.subject.service.SubjectService;
import com.bhu.was.business.bucache.redis.serviceimpl.statistics.SubjectStatisticsFragmentService;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;

public class SubjectServiceTest extends BaseTest{
	@Resource
	private SubjectService subjectService;
	
	//@Test
	public void doValidateUrlTest(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause(" id asc ");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<Integer, Subject> it = new KeyBasedEntityBatchIterator<Integer,Subject>(Integer.class,Subject.class, subjectService.getEntityDao(), mc);
		while(it.hasNext()){
			List<Subject> items = it.next();
			for(Subject item : items){
				Set<String> set = HttpHelper.parseAllDomains(item.getUrl());
				if(set.isEmpty()){
					System.out.println(item.getUrl());
				}
			}
		}
		
	}
	
	@Test
	public void doRedisTest(){
		SubjectStatisticsFragmentService.getInstance().clearAllSubjectClick("2015-02-12", "1", null, null);
	}
	
}
