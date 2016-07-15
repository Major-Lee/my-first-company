package com.bhu.vas.api.rpc.user.model;

import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtIntModel;

/**
   用户相关业务配置状态
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserConfigsState extends KeyDtoMapJsonExtIntModel<String> {
	
	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}

}
