package com.bhu.vas.business.ds.sequence.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.sequence.helper.ISequenceGenable;
import com.bhu.vas.api.rpc.sequence.model.Sequence;
import com.bhu.vas.business.ds.sequence.dao.SequenceDao;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.orm.service.EntityService;
import com.smartwork.msip.localunit.RandomData;
/**
 * Sequence管理业务
 *
 * @author Edmond Lee
 */
@Service
@Transactional("coreTransactionManager")
public class SequenceService extends EntityService<String,Sequence,SequenceDao>{//EntityCacheableService<String,Sequence,SequenceDao>{//
	private final Logger logger = LoggerFactory.getLogger(SequenceService.class);
	//设定初始值 在没有记录的前提下会设定，有值的情况下继续递增
	private static Map<String,Integer> tableSequenceStart = new HashMap<String,Integer>();
	
	@Resource
	@Override
	public void setEntityDao(SequenceDao sequenceDao) {
		super.setEntityDao(sequenceDao);
	}
		
	/*@Resource(name = "coreCacheService")
    @Override
	public void setCacheService(CacheService cacheService) {
		super.setCacheService(cacheService);
	}*/
	
	@PostConstruct
	protected void init() {
		tableSequenceStart.put("com.bhu.vas.api.user.model.User", 200000);
		tableSequenceStart.put("com.bhu.vas.api.subject.model.Subject",10000);
		super.init();
	}
	
/*	public void test(){
		this.getEntityDao().getNextId("aaaa");
	}*/
	
	public <T extends ISequenceGenable> void onCreateSequenceKey(T model,boolean israndomincr) {
		//model.getClass().getName();
		if(israndomincr){
			if(RuntimeConfiguration.AccountCreateContinuous){
				model.setSequenceKey(getNextId(model.getClass().getName()));
			}else{
				model.setSequenceKey(getNextId(model.getClass().getName(),RandomData.intNumber(1,5)));
			}
		}else
			model.setSequenceKey(getNextId(model.getClass().getName()));
    }
	
	//todo:如果慢的话通过存储过程实现
	@Transactional("coreTransactionManager")
    public Integer getNextId(String name, int incrnum) {
		logger.info("SequenceService getNextID By {}",name);
    	Sequence o = new Sequence(name, -1);
    	try{
    		this.getEntityDao().begin();
    		o = this.getById(name);
        	if(o == null){
        		Integer start = tableSequenceStart.get(name);
        		int ret = 0;
        		if(start == null)
        			ret = incrnum+1;//o = new Sequence(name, incrnum+1);
        		else 
        			ret = start.intValue()+incrnum+1;//o = new Sequence(name, start.intValue()+incrnum+1);
        		o = new Sequence(name, ret);
        		o.setIncr(0);
        		this.insert(o);//.insert(o);//没有数据则在表中增加
        		logger.info("SequenceService getNextID is{} By {}",ret,name);
        		return ret;
        	}else{
        		o.setNextid(o.getNextid() + incrnum);
        		o.setIncr(incrnum);
        		this.update(o);
        		logger.info("SequenceService getNextID is{} By {}",o.getNextid(),name);
        		//this.getEntityDao().commit();
        		return o.getNextid();
        	}    		
    	}finally{
    		this.getEntityDao().commit();
    	}
    }
	
	@Transactional("coreTransactionManager")
    public Integer getNextId(String name) {
		logger.info("SequenceService getNextID By {}",name);
		return this.getNextId(name,1);
/*    	  //this.getSqlMapClient().startTransaction();
    	  Sequence o = new Sequence(name, -1);
    	  o = this.getById(name);//.findUniqueBy("name", name);//this..get(name);
    	  //o = this.findUniqueBy(name, value)
    	  if(o == null){
    		  o = new Sequence(name, 2);
    		  this.insert(o);//.insert(o);//没有数据则在表中增加
    		  logger.info("SequenceService getNextID is{} By {}",1,name);
    		  //return new Long(1);
    		  return 1;
    	  }else{
    		  //Object newObj = new Sequence(name, o.getNextid() + 1);
    		  o.setNextid(o.getNextid() + 1);
    		  this.update(o);
    		  logger.info("SequenceService getNextID is{} By {}",o.getNextid(),name);
    		  //Sequence oo = sequenceDao.get(name);
    		  //System.out.println("----------------------"+((Sequence)newObj).getNextid());
    		  //throw new RuntimeException("111111111111");
    		  //return new Long(o.getNextid());
    		  return o.getNextid();
    	  }*/
    }
}
