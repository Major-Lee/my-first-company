package com.smartwork.async.messagequeue.kafka.outbound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.springframework.integration.samples.kafka.user.User;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

import com.smartwork.async.messagequeue.kafka.model.CommonMessage;

/**
 * @author Soby Chacko
 */
public class CommonMessageTransformer implements Transformer {

    Log logger = LogFactory.getLog(getClass());

    @Override
    public Message<?> transform(Message<?> message) {
        if(message.getPayload().getClass().isAssignableFrom(CommonMessage.class)) {
        	CommonMessage cmessage = (CommonMessage) message.getPayload();
        	//cmessage.se
            //user.setFirstName(user.getFirstName().toString()+user.getFirstName());
            logger.info("CommonMessage transfer confirmed " + cmessage.getType() +" payload:"+cmessage.getPayload());
            return MessageBuilder.withPayload(cmessage).copyHeaders(message.getHeaders()).build();
        }
        return message;
    }
}
