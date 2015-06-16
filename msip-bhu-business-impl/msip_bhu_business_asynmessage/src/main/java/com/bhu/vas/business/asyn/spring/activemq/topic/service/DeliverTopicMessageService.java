package com.bhu.vas.business.asyn.spring.activemq.topic.service;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.business.asyn.spring.activemq.topic.producer.TopicMessageProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.WifiCmdNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceAsynCmdGenerateDTO;


public class DeliverTopicMessageService {
	@Resource(name="deliverMessageTopicProducer")
	private TopicMessageProducer deliverMessageTopicProducer;

	public void sendPureText(String message){
		deliverMessageTopicProducer.sendPureText(message);
	}

	public void sendCmJoinMessage(CmCtxInfo ctxInfo){
		/*WifiDeviceAsynCmdGenerateDTO dto = new WifiDeviceAsynCmdGenerateDTO();
		dto.setUid(uid);
		dto.setGid(gid);
		dto.setDependency(dependency);
		dto.setMac(mac);
		dto.setOpt(opt);
		dto.setSubopt(subopt);
		dto.setExtparams(extparams);
		dto.setChannel(channel);
		dto.setChannel_taskid(channel_taskid);
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));*/
	}
	
	
	public void sendCmLeaveMessage(CmCtxInfo ctxInfo){
		/*WifiDeviceAsynCmdGenerateDTO dto = new WifiDeviceAsynCmdGenerateDTO();
		dto.setUid(uid);
		dto.setGid(gid);
		dto.setDependency(dependency);
		dto.setMac(mac);
		dto.setOpt(opt);
		dto.setSubopt(subopt);
		dto.setExtparams(extparams);
		dto.setChannel(channel);
		dto.setChannel_taskid(channel_taskid);
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));*/
	}
	public void sendDeviceOnline(String ctx,String mac){
		/*WifiCmdNotifyDTO dto = new WifiCmdNotifyDTO();
		dto.setMac(mac);
		dto.setTaskid(taskid);
		dto.setOpt(opt);
		dto.setPayload(payload);
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));*/
	}
	public void sendDeviceOffline(String ctx,String mac){
		/*WifiCmdNotifyDTO dto = new WifiCmdNotifyDTO();
		dto.setMac(mac);
		dto.setTaskid(taskid);
		dto.setOpt(opt);
		dto.setPayload(payload);
		dto.setTs(System.currentTimeMillis());
		deliverMessageTopicProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));*/
	}
}
