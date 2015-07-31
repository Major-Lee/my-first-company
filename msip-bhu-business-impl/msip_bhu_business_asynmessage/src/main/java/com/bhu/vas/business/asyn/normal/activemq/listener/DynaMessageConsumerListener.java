package com.bhu.vas.business.asyn.normal.activemq.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.bhu.vas.business.logger.BusinessStatisticsLogger;
import com.bhu.vas.business.observer.QueueMsgObserverManager;

public class DynaMessageConsumerListener implements MessageListener{
	//private final Logger logger = LoggerFactory.getLogger(DynaMessageConsumerListener.class);
	private String cminfo;
	private String queue_name;
	//private ConsumerContextInfo contextInfo;
	//private ExecutorService exec = Executors.newFixedThreadPool(30);
	//private static final String Recv_Template = "ctx[%s] %s";
	public DynaMessageConsumerListener(String queue_name,String cminfo){//ConsumerContextInfo contextInfo){///*String name,String key,*/MsgDispatcherServer server){//,ActiveMQConnectionManager manager){
		this.queue_name = queue_name;
		this.cminfo = cminfo;
		//this.c_id_name = c_id_name;
	}
	public void onMessage(final Message m) {
		try {
			String message = ((TextMessage)m).getText();
			//logger.info(String.format(Recv_Template, cminfo,message));
			BusinessStatisticsLogger.doActionDynaQueueMessageLog(cminfo,message);
			QueueMsgObserverManager.DynaMsgCommingObserver.notifyMsgComming(cminfo, message);
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
	public String getCminfo() {
		return cminfo;
	}
	public void setCminfo(String cminfo) {
		this.cminfo = cminfo;
	}
	public String getQueue_name() {
		return queue_name;
	}
	public void setQueue_name(String queue_name) {
		this.queue_name = queue_name;
	}
	
	
	/*public String getC_id_name() {
		return c_id_name;
	}
	public void setC_id_name(String c_id_name) {
		this.c_id_name = c_id_name;
	}*/
}
