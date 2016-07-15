package com.smartwork.msip.business.abstractmsd.dao;

import java.io.Serializable;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;

import com.smartwork.msip.cores.orm.jpa.ReadWriteEntityDao;
import com.smartwork.msip.cores.orm.logic.identifier.Identifier;

public abstract class AbstractPortraitDao<KEY extends Serializable, MODEL extends Identifier> extends ReadWriteEntityDao<KEY,MODEL>{
	
	@Resource(name = "sqlSessionTemplatePortraitMaster")
	@Override
	public void setSqlSessionMasterTemplate(
			SqlSessionTemplate sqlSessionMasterTemplate) {
		super.setSqlSessionMasterTemplate(sqlSessionMasterTemplate);
	}
	
	@Resource(name = "sqlSessionTemplatePortraitSlaver")
	@Override
	public void setSqlSessionSlaverTemplate(
			SqlSessionTemplate sqlSessionSlaverTemplate) {
		super.setSqlSessionSlaverTemplate(sqlSessionSlaverTemplate);
	}
}
