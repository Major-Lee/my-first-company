package com.bhu.vas.api.rpc.user.model;

import com.bhu.vas.api.helper.BusinessEnumType.ThirdpartiesPaymentMode;
import com.bhu.vas.api.rpc.user.dto.ThirdpartiesPaymentDTO;
import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtIntModel;

/**
 * 存储 用户绑定的用户用于提现的帐号
 * 支付宝  	账户，名称
 * 微信   	账户
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserThirdpartiesPayment extends KeyDtoMapJsonExtIntModel<ThirdpartiesPaymentDTO>{
	
	public boolean addOrReplace(ThirdpartiesPaymentMode mode,ThirdpartiesPaymentDTO payment){
		if(mode == null || payment == null) return false;
		this.putInnerModel(mode.getMode(), payment);
		return true;
	}
	
	public boolean remove(ThirdpartiesPaymentMode mode){
		if(mode == null) return false;
		if(this.containsKey(mode.getMode())){
			this.removeInnerModel(mode.getMode());
			return true;
		}
		return false;
	}
	
	@Override
	public Class<ThirdpartiesPaymentDTO> getJsonParserModel() {
		return ThirdpartiesPaymentDTO.class;
	}	
}
