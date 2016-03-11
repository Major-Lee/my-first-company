package com.bhu.vas.business.asyn.spring.model.commdity;

import com.bhu.vas.business.asyn.spring.builder.ActionCommdityDTO;
import com.bhu.vas.business.asyn.spring.builder.ActionCommdityMessageType;

/**
 * 订单支付成功
 * @author tangzichao
 *
 */
public class OrderPaySuccessedDTO extends ActionCommdityDTO {
	//订单id
	private String orderid;

    public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	@Override
    public String getActionType() {
        return ActionCommdityMessageType.OrderPaySuccessed.getPrefix();
    }
}
