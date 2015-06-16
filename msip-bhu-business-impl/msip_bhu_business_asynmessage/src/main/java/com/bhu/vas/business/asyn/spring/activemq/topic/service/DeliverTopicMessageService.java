package com.bhu.vas.business.asyn.spring.activemq.topic.service;

import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.business.asyn.spring.activemq.topic.producer.TopicMessageProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.topic.CmJoinNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.topic.CmLeaveNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.topic.DeviceOfflineNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.topic.DeviceOnlineNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.topic.DevicesOnlineNotifyDTO;


public class DeliverTopicMessageService {
	@Resource(name="deliverMessageTopicProducer")
	private TopicMessageProducer deliverMessageTopicProducer;

	public void sendPureText(String message){
		deliverMessageTopicProducer.sendPureText(message);
	}

	public void sendCmJoinMessage(CmCtxInfo ctxInfo){
		CmJoinNotifyDTO dto = new CmJoinNotifyDTO();
		dto.setName(ctxInfo.getName());
		dto.setProcess_seq(ctxInfo.getProcess_seq());
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	
	public void sendCmLeaveMessage(CmCtxInfo ctxInfo){
		CmLeaveNotifyDTO dto = new CmLeaveNotifyDTO();
		dto.setName(ctxInfo.getName());
		dto.setProcess_seq(ctxInfo.getProcess_seq());
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendDeviceOnline(String ctx,String mac){
		DeviceOnlineNotifyDTO dto = new DeviceOnlineNotifyDTO();
		dto.setCtx(ctx);
		dto.setMac(mac);
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	public void sendDevicesOnline(String ctx,List<String> macs){
		DevicesOnlineNotifyDTO dto = new DevicesOnlineNotifyDTO();
		dto.setCtx(ctx);
		dto.setMacs(macs);
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	public void sendDeviceOffline(String ctx,String mac){
		DeviceOfflineNotifyDTO dto = new DeviceOfflineNotifyDTO();
		dto.setCtx(ctx);
		dto.setMac(mac);
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
}
