package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.rpc.user.dto.UserWifiSinfferSettingDTO;
import com.bhu.vas.api.rpc.user.dto.UserWifiTimerSettingDTO;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.business.ds.user.dao.UserSettingStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserSettingStateService extends AbstractCoreService<String,UserSettingState,UserSettingStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(UserSettingStateDao userSettingStateDao) {
		super.setEntityDao(userSettingStateDao);
	}
	
	/**
	 * 初始化用户设置数据
	 * @param uid
	 */
	public UserSettingState initUserSettingState(String mac){
		if(!StringUtils.isEmpty(mac)){
			UserSettingState entity = new UserSettingState();
			entity.setId(mac);
			entity.putUserSetting(new UserTerminalOnlineSettingDTO());
			entity.putUserSetting(new UserWifiTimerSettingDTO());
			entity.putUserSetting(new UserWifiSinfferSettingDTO());
			return super.insert(entity);
		}
		return null;
	}
	
	/**
	 * 重新封装
	 * 如果获取的数据不存在 直接初始化数据
	 */
	@Override
	public UserSettingState getById(String mac){
		UserSettingState entity = super.getById(mac);
		if(entity == null){
			return initUserSettingState(mac);
		}
		return entity;
	}
	
	public void updateUserSetting(String mac, String setting_key,String data){
		UserSettingState entity = getById(mac);
		entity.putInnerModel(setting_key, data);
		this.update(entity);
	}
}
