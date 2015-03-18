package com.bhu.vas.business.asyn.spring.activemq.queue.producer;

import javax.jms.Queue;

import org.springframework.jms.core.JmsTemplate;

import com.bhu.vas.business.asyn.spring.builder.DeliverMessage;
import com.bhu.vas.business.asyn.spring.builder.DeliverMessageFactoryBuilder;

/**
 * Author：Edmond Lee
 * Date: 2008-8-28
 * Time: 17:14:23
 */
public class DeliverMessageQueueProducer {
	//private final Logger logger = LoggerFactory.getLogger(FeedService.class);
	
    private JmsTemplate template;

    private Queue destination;

    
    public void setTemplate(JmsTemplate template) {
        this.template = template;
    }

    public void setDestination(Queue destination) {
        this.destination = destination;
    }

    public void send(DeliverMessage message) {
        template.convertAndSend(this.destination, DeliverMessageFactoryBuilder.toJson(message));
    }
    
    public void sendPureText(String message) {
        template.convertAndSend(this.destination, message);
    }
}
