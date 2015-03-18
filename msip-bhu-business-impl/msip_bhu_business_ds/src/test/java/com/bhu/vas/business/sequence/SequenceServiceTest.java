package com.bhu.vas.business.sequence;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.business.ds.sequence.service.SequenceService;
import com.smartwork.msip.localunit.BaseTest;

public class SequenceServiceTest extends BaseTest{

	@Resource
	SequenceService sequenceService;

	public void buildAllData(){
		
	}
	
	@Test
	public void testRowLock(){
		//sequenceService.getEntityDao().getNextId("aaaa");
		for(int i=0;i<10;i++)
			System.out.println(sequenceService.getNextId("aaaa",1));
	}
}
