package com.bhu.jorion.mq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.JOrion;
import com.bhu.jorion.util.StringHelper;

public class MqMessageListener implements MessageListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(MqMessageListener.class);
	private String id;
	private JOrion orion;
	
	public MqMessageListener(JOrion orion, String id){
		this.orion = orion;
		this.id = id;
	}
	
	@Override
	public void onMessage(Message arg0) {
		TextMessage msg = (TextMessage)arg0;
		try {
			LOGGER.debug("Got mq message: \n" + msg.getText());
		} catch (JMSException e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
		orion.onMqMessage(id, arg0);
	}

}
