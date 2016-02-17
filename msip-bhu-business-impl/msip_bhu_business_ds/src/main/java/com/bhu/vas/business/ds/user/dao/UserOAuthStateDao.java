package com.bhu.vas.business.ds.user.dao;

import org.springframework.stereotype.Repository;

import com.bhu.vas.api.rpc.user.model.UserOAuthState;
import com.bhu.vas.api.rpc.user.model.pk.UserOAuthStatePK;
import com.smartwork.msip.business.abstractmsd.dao.AbstractCoreDao;
@Repository
public class UserOAuthStateDao extends AbstractCoreDao<UserOAuthStatePK,UserOAuthState>{
	
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
