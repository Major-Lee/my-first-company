package com.bhu.vas.business.ds.user.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserOAuthState;
import com.bhu.vas.api.rpc.user.model.pk.UserOAuthStatePK;
import com.bhu.vas.business.ds.user.dao.UserOAuthStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
@Transactional("coreTransactionManager")
public class UserOAuthStateService extends AbstractCoreService<UserOAuthStatePK,UserOAuthState,UserOAuthStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserOAuthStateDao userOAuthStateDao) {
		super.setEntityDao(userOAuthStateDao);
	}
		
	/*public List<UserSnsState> queryByAuidAndIdentify(String auid, String identify){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("auid", auid).andColumnEqualTo("identify", identify);
		return super.findModelByModelCriteria(mc);
	}*/
	
	/*public List<UserSnsState> queryByUidAndIdentify(int uid, String identify){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid).andColumnEqualTo("identify", identify);
		return super.findModelByModelCriteria(mc);
	}*/
	
	public List<UserOAuthState> queryByUid(Integer uid){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid);
		mc.setOrderByClause("updated_at desc");
		return super.findModelByModelCriteria(mc);
	}
	/*public void removeByIdAndIdentify(Integer id, String identify){
		if(id != null && !StringHelper.isNullorBlank(identify)){
			UserSnsState state = new UserSnsState(new UserSnsStatePK(id,identify));
			//state.setId(id);
			//state.setIdentify(identify);
			super.delete(state);
		}
	}*/
}
