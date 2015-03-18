package com.bhu.vas.business.asyn.spring.activemq.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.activemq.queue.producer.DeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.builder.DeliverMessage;
import com.bhu.vas.business.asyn.spring.builder.DeliverMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.UserRegisteredDTO;

@Service
public class DeliverMessageService {
	@Resource(name="deliverMessageQueueProducer")
	private DeliverMessageQueueProducer deliverMessageQueueProducer;
	
	public void sendDeliverMessage(char type,int uid,String messagedata){
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, messagedata);
		deliverMessageQueueProducer.send(message);
	}
	
	public void sendPureText(String message){
		deliverMessageQueueProducer.sendPureText(message);
	}
	
	public void sendUserRegisteredActionMessage(char type,Integer uid,String channel,String device,String remoteip){
		UserRegisteredDTO dto = new UserRegisteredDTO();
		dto.setUid(uid);
		dto.setChannel(channel);
		//dto.setInviteuid(inviteuid);
		//dto.setInvitetoken(invitetoken);
		dto.setRemoteip(remoteip);
		dto.setD(device);
		dto.setTs(System.currentTimeMillis());
		
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		deliverMessageQueueProducer.send(message);
	}
	/*
	public void sendUserSubjectClickActionMessage(char type,int uid,int sid,String act,long incr){
		UserSubjectClickDTO dto = new UserSubjectClickDTO();
		dto.setUid(uid);
		dto.setSid(sid);
		dto.setAct(act);
		dto.setIncr(incr);
		dto.setTs(System.currentTimeMillis());
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserSubjectAbstractClickActionMessage(char type,int uid,int aid,String act){
		UserSubjectAbstractClickDTO dto = new UserSubjectAbstractClickDTO();
		dto.setUid(uid);
		dto.setAid(aid);
		dto.setAct(act);
		dto.setTs(System.currentTimeMillis());
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserSubjectShareActionMessage(char type,int uid,int sid,String custom_abstract){
		UserSubjectShareDTO dto = new UserSubjectShareDTO();
		dto.setUid(uid);
		dto.setSid(sid);
		dto.setCustom_abstract(custom_abstract);
		dto.setTs(System.currentTimeMillis());
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserSubjectEstimateActionMessage(char type,int uid,int sid,String estimate){
		UserSubjectEstimateDTO dto = new UserSubjectEstimateDTO();
		dto.setUid(uid);
		dto.setSid(sid);
		dto.setEstimate(estimate);
		dto.setTs(System.currentTimeMillis());
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserSubjectTaggingActionMessage(char type,int uid,int sid,Set<Integer> otags,Set<Integer> ntags){
		UserSubjectTaggingDTO dto = new UserSubjectTaggingDTO();
		dto.setUid(uid);
		dto.setSid(sid);
		dto.setOtags(otags);
		dto.setNtags(ntags);
		dto.setTs(System.currentTimeMillis());
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserBlackDomainActionMessage(char type,int uid, String url, String domain, boolean add, boolean single){
		UserBlackDomainDTO dto = new UserBlackDomainDTO();
		dto.setUid(uid);
		dto.setAdd(add);
		dto.setDomain(domain);
		dto.setSingle(single);
		dto.setUrl(url);
		dto.setTs(System.currentTimeMillis());
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		deliverMessageQueueProducer.send(message);
	}
	
	public void sendSubjectWeixinShareActionMessage(char type,int uid, String url){
		SubjectWeixinShareDTO dto = new SubjectWeixinShareDTO();
		dto.setUid(uid);
		dto.setUrl(url);
		dto.setTs(System.currentTimeMillis());
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		deliverMessageQueueProducer.send(message);
	}*/
}
