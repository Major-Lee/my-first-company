package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.business.ds.user.dao.UserSettingStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserSettingStateService extends AbstractCoreService<Integer,UserSettingState,UserSettingStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserSettingStateDao userSettingStateDao) {
		super.setEntityDao(userSettingStateDao);
	}
	
	/**
	 * 初始化用户设置数据
	 * @param uid
	 */
	public void initUserSettingState(Integer uid){
		if(uid != null){
			UserSettingState entity = new UserSettingState();
			entity.setId(uid);
			entity.putUserSetting(new UserTerminalOnlineSettingDTO());
			super.insert(entity);
		}
	}
}
