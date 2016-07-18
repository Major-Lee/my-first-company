package com.bhu.vas.api.rpc.user.model;

import com.bhu.vas.api.rpc.user.dto.UserConfigsStateDTO;
import com.smartwork.msip.cores.orm.model.extjson.DtoJsonExtPKModel;

/**
   用户相关业务配置状态
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserConfigsState extends DtoJsonExtPKModel<Integer,UserConfigsStateDTO> {
	
	@Override
	public Class<UserConfigsStateDTO> getJsonParserModel() {
		return UserConfigsStateDTO.class;
	}

	@Override
	protected Class<Integer> getPKClass() {
		// TODO Auto-generated method stub
		return Integer.class;
	}

}
