/*package com.bhu.vas.api.rpc.user.model;

import com.bhu.vas.api.helper.BusinessEnumType.ThirdpartiesPaymentType;
import com.bhu.vas.api.rpc.user.dto.ThirdpartiesPaymentDTO;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtIntModel;

*//**
 * 存储 用户绑定的用户用于提现的帐号
 * 支付宝  	账户，名称
 * 微信   	账户
 * @author Edmond
 *
 *//*
@SuppressWarnings("serial")
public class UserThirdpartiesPayment extends KeyDtoMapJsonExtIntModel<ThirdpartiesPaymentDTO>{
	
	public boolean addOrReplace(ThirdpartiesPaymentType type,ThirdpartiesPaymentDTO payment){
		if(type == null || payment == null) return false;
		this.putInnerModel(type.getType(), payment);
		return true;
	}
	
	public boolean remove(ThirdpartiesPaymentType type){
		if(type == null) return false;
		if(this.containsKey(type.getType())){
			this.removeInnerModel(type.getType());
			return true;
		}
		return false;
	}
	
	@Override
	public Class<ThirdpartiesPaymentDTO> getJsonParserModel() {
		return ThirdpartiesPaymentDTO.class;
	}	
}
*/