package com.bhu.vas.business.asyn.kafka.producer;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;


/**
 * Date: 2008-8-28
 * Time: 17:10:34
 */
public class KafkaMessageQueueProducer {
	
	private QueueChannel channel;
	private String queue;
	
	@PostConstruct
	public void initialize(){
    	
	}
	public QueueChannel getChannel() {
		return channel;
	}
	public void setChannel(QueueChannel channel) {
		this.channel = channel;
	}
	
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	
	public void send(String index){
		channel.send(
				MessageBuilder.withPayload("Message你好-" + RandomUtils.nextInt())
					.setHeader("messageKey", index)
					.setHeader("topic", queue).build());
	}
}
