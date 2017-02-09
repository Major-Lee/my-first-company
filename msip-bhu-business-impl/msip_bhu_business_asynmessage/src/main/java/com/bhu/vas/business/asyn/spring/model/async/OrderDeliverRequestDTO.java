package com.bhu.vas.business.asyn.spring.model.async;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

//商品中心支付成功，需要做发货处理。(noapp, 打赏可关闭等)
public class OrderDeliverRequestDTO extends AsyncDTO {
	private String orderid;
	
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	@Override
	public String getAsyncType() {
		return AsyncMessageType.OrderDeliverRequest.getPrefix();
	}
}
