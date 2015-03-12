package com.bhu.vas.business.mq.activemq.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.bhu.vas.business.mq.activemq.observer.QueueMsgObserverManager;

public class DynaMessageConsumerListener implements MessageListener{
	private String c_id_name;
	//private final Logger logger = LoggerFactory.getLogger(NotifyMessageConsumerListener.class);
	//private ConsumerContextInfo contextInfo;
	//private ExecutorService exec = Executors.newFixedThreadPool(30);
	public DynaMessageConsumerListener(String c_id_name){//ConsumerContextInfo contextInfo){///*String name,String key,*/MsgDispatcherServer server){//,ActiveMQConnectionManager manager){
		this.c_id_name = c_id_name;
	}
	public void onMessage(final Message m) {
		try {
			String message = ((TextMessage)m).getText();
			QueueMsgObserverManager.DynaMsgCommingObserver.notifyMsgComming(c_id_name, message);
		} catch (JMSException e) {
			e.printStackTrace(System.out);
		}
		
		//System.out.println("~~~~++++++++:"+this);
    	/*exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
		        	//System.out.println("~~~~++++++++:"+this);
		            //MessageProducer producer = session.createProducer(queue);
		            //System.out.println(name + " get:" + ((TextMessage)m).getText());
		            //回复一个消息
		            Message replyMessage = ActiveMQConnectionManager.getInstance().getSession(key).createTextMessage("Reply from " + name);
		            replyMessage.setStringProperty("selector-receiver", String.valueOf(new Random().nextInt(5)));
		            //设置JMSCorrelationID为刚才收到的消息的ID
		            //replyMessage.setJMSCorrelationID(m.getJMSMessageID());
		            server.getIMMessageProducer().getRandomDelivereProducer().send(replyMessage);
					
					String message = ((TextMessage)m).getText();
					processNotifyMessage(message);
				}catch(Exception ex){
					ex.printStackTrace();
					//logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));*/
    }
	public String getC_id_name() {
		return c_id_name;
	}
	public void setC_id_name(String c_id_name) {
		this.c_id_name = c_id_name;
	}
}
