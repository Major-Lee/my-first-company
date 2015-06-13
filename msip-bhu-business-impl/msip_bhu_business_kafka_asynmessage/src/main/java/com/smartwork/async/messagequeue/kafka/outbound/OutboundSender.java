package com.smartwork.async.messagequeue.kafka.outbound;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import com.smartwork.async.messagequeue.kafka.model.CommonMessage;

public class OutboundSender {
	//@Resource(name="inputToKafka")
	private MessageChannel channel;
	
	public void send(CommonMessage cmessage,String topic,String messageKey){
		channel.send(
				MessageBuilder.withPayload(cmessage)
						.setHeader("messageKey", messageKey)
						.setHeader("topic", topic).build());
	}

	public MessageChannel getChannel() {
		return channel;
	}

	public void setChannel(MessageChannel channel) {
		this.channel = channel;
	}
	
	
}
