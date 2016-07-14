package com.bhu.vas.business.asyn.spring.activemq.service;

import javax.annotation.Resource;

import com.bhu.vas.business.asyn.spring.activemq.queue.producer.CommdityMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionCommdityMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.commdity.OrderCreatedDTO;


public class CommdityMessageService {
	@Resource(name="commdityMessageQueueProducer")
	private CommdityMessageQueueProducer commdityMessageQueueProducer;

	public void sendPureText(String message){
		commdityMessageQueueProducer.sendPureText(message);
	}
	/**
	 * 订单支付完成的异步消息
	 * @param orderid
	 */
/*	public void sendOrderPaySuccessedMessage(String orderid){
		OrderPaySuccessedDTO dto = new OrderPaySuccessedDTO();
		dto.setOrderid(orderid);
		dto.setTs(System.currentTimeMillis());
		commdityMessageQueueProducer.sendPureText(ActionCommdityMessageFactoryBuilder.toJsonHasPrefix(dto));
	}*/
	/**
	 * 订单创建完成的异步消息
	 * @param orderid
	 */
	public void sendOrderCreatedMessage(String orderid){
		OrderCreatedDTO dto = new OrderCreatedDTO();
		dto.setOrderid(orderid);
		dto.setTs(System.currentTimeMillis());
		commdityMessageQueueProducer.sendPureText(ActionCommdityMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
}
