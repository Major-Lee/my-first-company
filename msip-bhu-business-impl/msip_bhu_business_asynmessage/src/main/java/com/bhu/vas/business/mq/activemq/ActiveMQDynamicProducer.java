package com.bhu.vas.business.mq.activemq;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.msip.localunit.RandomPicker;

public class ActiveMQDynamicProducer{
	private static final Logger logger = LoggerFactory.getLogger(ActiveMQDynamicProducer.class);
	//@Resource
	//private ActiveMQDynamicService activeMQDynamicService;
	@PostConstruct
	public void initialize(){
		//初始化ActiveMQConnectionManager
		ActiveMQConnectionManager.getInstance().initProducerQueues();
	}
	
	
	
	/*public void stop(){
		ActiveMQConnectionManager.getInstance().stop();
		ActiveMQConnectionManager.getInstance().destroy();
	}*/
	
	/**
	 * deliverMessage
	 * @param dsname node server name 也就是mds.xml中定义的queue name
	 * @param msg
	 */
	public void deliverMessage(String Sender_KEY,String message){
		Session des_session = ActiveMQConnectionManager.getInstance().getSession(Sender_KEY);
		MessageProducer des_producer = ActiveMQConnectionManager.getInstance().getProducer(Sender_KEY);
		if(des_session == null){
			String errorMsg = "ActiveMQConnectionManager could not fetch the session named:"+Sender_KEY;
			logger.error(errorMsg, new RuntimeException("errorMsg"));
			return;
		}
		
		if(des_producer == null){
			String errorMsg = "ActiveMQConnectionManager could not fetch the producer named:"+Sender_KEY;
			logger.error(errorMsg, new RuntimeException("errorMsg"));
			return;
		}
		//String jsonMsg = JsonHelper.getJSONString(message);
		logger.info(String.format("Destination[%s] Msg[%s]", Sender_KEY,message));
		try {
			Message replyMessage = des_session.createTextMessage(message);
			//replyMessage.setStringProperty("selector-receiver", String.valueOf(new Random().nextInt(5)));
			des_producer.send(replyMessage);
		} catch (JMSException ex) {
			String errorMsg = String.format("destination producer[%s] send msg[msg] error!",Sender_KEY,message);
			logger.error(errorMsg, ex);
		}
	}
	
	public boolean initTestProducers(){
		ActiveMQConnectionManager.getInstance().initConsumerTestProducers();
		return !ActiveMQConnectionManager.getInstance().currentTestProducerKeys().isEmpty();
	}
	
	public String randomTestProducerKey(){
		return RandomPicker.pick(ActiveMQConnectionManager.getInstance().currentTestProducerKeys());
	}
	
	public String randomProducerKey(){
		return RandomPicker.pick(ActiveMQConnectionManager.getInstance().currentProducerKeys());
	}
	
	
	public void deliverTestMessage(String Sender_KEY,String message){
		Session des_session = ActiveMQConnectionManager.getInstance().getSession(Sender_KEY);
		MessageProducer des_producer = ActiveMQConnectionManager.getInstance().getTestProducer(Sender_KEY);
		if(des_session == null){
			String errorMsg = "ActiveMQConnectionManager could not fetch the session named:"+Sender_KEY;
			logger.error(errorMsg, new RuntimeException("errorMsg"));
			return;
		}
		
		if(des_producer == null){
			String errorMsg = "ActiveMQConnectionManager could not fetch the producer named:"+Sender_KEY;
			logger.error(errorMsg, new RuntimeException("errorMsg"));
			return;
		}
		//String jsonMsg = JsonHelper.getJSONString(message);
		logger.info(String.format("Destination[%s] Msg[%s]", "Sender_KEY",message));
		try {
			Message replyMessage = des_session.createTextMessage(message);
			//replyMessage.setStringProperty("selector-receiver", String.valueOf(new Random().nextInt(5)));
			des_producer.send(replyMessage);
		} catch (JMSException ex) {
			String errorMsg = String.format("destination producer[%s] send msg[msg] error!",Sender_KEY,message);
			logger.error(errorMsg, ex);
		}
	}
}
