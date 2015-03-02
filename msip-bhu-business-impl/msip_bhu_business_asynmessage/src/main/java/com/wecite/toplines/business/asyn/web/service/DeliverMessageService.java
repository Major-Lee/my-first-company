package com.wecite.toplines.business.asyn.web.service;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wecite.toplines.business.asyn.web.activemq.queue.producer.DeliverMessageQueueProducer;
import com.wecite.toplines.business.asyn.web.builder.ActionMessageFactoryBuilder;
import com.wecite.toplines.business.asyn.web.builder.DeliverMessage;
import com.wecite.toplines.business.asyn.web.builder.DeliverMessageFactoryBuilder;
import com.wecite.toplines.business.asyn.web.model.SubjectWeixinShareDTO;
import com.wecite.toplines.business.asyn.web.model.UserBlackDomainDTO;
import com.wecite.toplines.business.asyn.web.model.UserRegisteredDTO;
import com.wecite.toplines.business.asyn.web.model.UserSubjectAbstractClickDTO;
import com.wecite.toplines.business.asyn.web.model.UserSubjectClickDTO;
import com.wecite.toplines.business.asyn.web.model.UserSubjectEstimateDTO;
import com.wecite.toplines.business.asyn.web.model.UserSubjectShareDTO;
import com.wecite.toplines.business.asyn.web.model.UserSubjectTaggingDTO;

@Service
public class DeliverMessageService {
	@Resource(name="deliverMessageQueueProducer")
	private DeliverMessageQueueProducer deliverMessageQueueProducer;
	
	public void sendDeliverMessage(char type,int uid,String messagedata){
		DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, messagedata);
		deliverMessageQueueProducer.send(message);
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
	}
}
