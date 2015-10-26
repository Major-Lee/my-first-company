package com.bhu.vas.api.rpc.user.model;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.user.dto.UserSettingDTO;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtStringModel;

/**
 * 记录用户登录的标识
 * 按月份分隔
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserSettingState extends KeyDtoMapJsonExtStringModel<String> {
	
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}
	
	/**
	 * put用户设置数据
	 * @param dto
	 */
	public void putUserSetting(UserSettingDTO dto){
		if(dto != null){
			super.putInnerModel(dto.getSettingKey(), JsonHelper.getJSONString(dto));
		}
	}
	
	/**
	 * 获取用户设置数据
	 * @param setting_key
	 * @param clazz
	 * @return
	 */
	public <T> T getUserSetting(String setting_key, Class<T> clazz){
		String setting_json = super.getInnerModel(setting_key);
		if(setting_json == null) return null;
		return JsonHelper.getDTO(setting_json, clazz);
		/*if(!StringUtils.isEmpty(setting_key)){
			return JsonHelper.getDTO(setting_json, clazz);
		}
		return null;*/
	}
	
}
