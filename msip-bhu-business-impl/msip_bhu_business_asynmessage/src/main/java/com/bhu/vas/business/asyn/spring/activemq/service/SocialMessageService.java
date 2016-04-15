package com.bhu.vas.business.asyn.spring.activemq.service;



import com.bhu.vas.business.asyn.spring.activemq.queue.producer.SocialMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionSocialMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.social.HandsetMeetDTO;

import javax.annotation.Resource;


public class SocialMessageService {
	@Resource(name="socialMessageQueueProducer")
	private SocialMessageQueueProducer socialMessageQueueProducer;

	public void sendPureText(String message){
		socialMessageQueueProducer.sendPureText(message);
	}
//	/**
//	 * 订单支付完成的异步消息
//	 * @param orderid
//	 */
//	public void sendOrderPaySuccessedMessage(String orderid){
//		OrderPaySuccessedDTO dto = new OrderPaySuccessedDTO();
//		dto.setOrderid(orderid);
//		dto.setTs(System.currentTimeMillis());
//		socialMessageQueueProducer.sendPureText(ActionSocialMessageFactoryBuilder.toJsonHasPrefix(dto));
//	}


	public void sendHandsetMeetMessage(Long uid, String hd_macs, String bssid, String meet) {
		HandsetMeetDTO  dto = new HandsetMeetDTO();
		dto.setBssid(bssid);
		dto.setHd_macs(hd_macs);
		dto.setUid(uid.toString());
		dto.setMeet(meet);
		socialMessageQueueProducer.sendPureText(ActionSocialMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendHandsetMeetMessage(String hd_mac, String hd_macs, String bssid, String meet) {
		HandsetMeetDTO  dto = new HandsetMeetDTO();
		dto.setBssid(bssid);
		dto.setHd_macs(hd_macs);
		dto.setHd_mac(hd_mac);
		dto.setMeet(meet);
		socialMessageQueueProducer.sendPureText(ActionSocialMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
}
