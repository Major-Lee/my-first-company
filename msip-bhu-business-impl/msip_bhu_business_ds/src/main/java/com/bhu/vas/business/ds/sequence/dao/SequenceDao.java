package com.bhu.vas.business.ds.sequence.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.sequence.model.Sequence;
import com.smartwork.msip.cores.orm.jpa.GenericEntityDao;

@Repository
public class SequenceDao extends GenericEntityDao<String,Sequence>{

	@Resource(name = "sqlSessionTemplateCoreMaster")
	@Override
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		super.setSqlSessionTemplate(sqlSessionTemplate);
	}
	
	public void begin(){
		this.getSqlSessionTemplate().update(Sequence.class.getName()+".begin");
	}
	
	public void commit(){
		this.getSqlSessionTemplate().update(Sequence.class.getName()+".commit");
	}
/*	public synchronized int getNextId(String name) {
	    //Sequence sequence = new Sequence(name, -1);
	    this.getSqlMapClientTemplate().update(Sequence.class.getName()+".begin");
	    this.getById(name);
	    this.getSqlMapClientTemplate().update(Sequence.class.getName()+".commit");
	    //this.getSqlMapClientTemplate().getSqlMapClient()
	    sequence = (Sequence) queryForObject("getSequence", sequence);
	    if (sequence == null) {
	      throw new DaoException("Error: A null sequence was returned from the database (could not get next " + name + " sequence).");
	    }
	    Object parameterObject = new Sequence(name, sequence.getNextId() + 1);
	    update("updateSequence", parameterObject);

	    return sequence.getNextId();
	    return 0;
	}*/
	
/*	@Resource(name = "sqlMapClientTemplateCoreMaster")
	@Override
	public void setSqlMapClientMasterTemplate(
			SqlMapClientTemplate sqlMapClientMasterTemplate) {
		super.setSqlMapClientMasterTemplate(sqlMapClientMasterTemplate);
	}
	
	@Resource(name = "sqlMapClientTemplateCoreSlaver")
	@Override
	public void setSqlMapClientSlaverTemplate(
			SqlMapClientTemplate sqlMapClientSlaverTemplate) {
		super.setSqlMapClientSlaverTemplate(sqlMapClientSlaverTemplate);
	}*/
}
