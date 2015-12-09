package com.bhu.vas.api.rpc.user.model;

import java.util.List;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.user.dto.UserSearchConditionDTO;
import com.smartwork.msip.cores.orm.model.extjson.ListJsonExtIntModel;

/**
   用户保存的搜索条件记录
   最多保存10个
 * @author lawliet
 *
 */
@SuppressWarnings("serial")
public class UserSearchConditionState extends ListJsonExtIntModel<UserSearchConditionDTO> {
	
	@Override
	public Class<UserSearchConditionDTO> getJsonParserModel() {
		return UserSearchConditionDTO.class;
	}
	
	/**
	 * 验证是否存在重复条件
	 * @param message
	 * @return
	 */
	public boolean validateExist(String message){
		if(StringUtils.isEmpty(message)) return true;
		
		List<UserSearchConditionDTO> dtos = this.getInnerModels();
		if(dtos != null && !dtos.isEmpty()){
			for(UserSearchConditionDTO dto : dtos){
				if(dto.getMessage().equals(message)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int limitSize(){
		return 10;
	}
}
