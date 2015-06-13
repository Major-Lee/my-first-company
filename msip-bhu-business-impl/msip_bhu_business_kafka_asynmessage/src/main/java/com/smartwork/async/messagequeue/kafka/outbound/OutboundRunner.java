package com.smartwork.async.messagequeue.kafka.outbound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;

import com.smartwork.async.messagequeue.builder.MessageFactoryBuilder;
import com.smartwork.async.messagequeue.builder.PayloadModelBuilder;
import com.smartwork.async.messagequeue.kafka.model.CommonMessage;
import com.smartwork.async.messagequeue.type.MessageType;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

public class OutboundRunner {
	private static final String CONFIG = "applicationContext-OutboundKafkaAdapterParser.xml";
	private static final Log LOG = LogFactory.getLog(OutboundRunner.class);
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	public static void main(final String args[]) {
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, OutboundRunner.class);
		ctx.start();

		//final MessageChannel channel = ctx.getBean("inputToKafka", MessageChannel.class);
		//LOG.info(channel.getClass());
		
		final OutboundSender outboundSender = ctx.getBean("outboundSender", OutboundSender.class);

		//sending 100,000 messages to Kafka server for topic test1
		for (int i = 0; i < 500; i++) {
			MessageType[] types = MessageType.values();
			MessageType randType = RandomPicker.pick(types);
			CommonMessage cmessage = null;
			switch(randType){
				case UserCreate:
					cmessage = MessageFactoryBuilder.toCommonMessage(
							PayloadModelBuilder.createUserCreateDTO(RandomData.intNumber(1000), RandomPicker.randString(letters, 8)));
					break;
				case UserOnline:
					cmessage = MessageFactoryBuilder.toCommonMessage(
							PayloadModelBuilder.createUserOnlineDTO(RandomData.intNumber(1000), RandomPicker.randString(letters, 8), RandomPicker.randString(letters, 4)));
					break;
				case UserOffline:
					cmessage = MessageFactoryBuilder.toCommonMessage(
							PayloadModelBuilder.createUserOfflineDTO(RandomData.intNumber(1000), RandomPicker.randString(letters, 8), RandomData.flag()));
					break;
				default:
					
					break;
			}
			outboundSender.send(cmessage,"CommonMessageQueue",String.valueOf(i));
					/*MessageBuilder.withPayload(cmessage)
							.setHeader("messageKey", String.valueOf(i))
							.setHeader("topic", "CommonMessageQueue").build());*/

			LOG.info("message sent " + i);
		}

		/*//sending 5,000 messages to kafka server for topic test2
		for (int i = 0; i < 50; i++) {
			channel.send(
				MessageBuilder.withPayload("hello Fom ob adapter test22 -  " + i)
					.setHeader("messageKey", String.valueOf(i))
					.setHeader("topic", "test22").build());

			LOG.info("message sent " + i);
		}

		//Send some messages to multiple topics matching regex.
		for (int i = 0; i < 10; i++) {
			channel.send(
				MessageBuilder.withPayload("hello Fom ob adapter regextopic1 -  " + i)
					.setHeader("messageKey", String.valueOf(i))
					.setHeader("topic", "regextopic1").build());

			LOG.info("message sent " + i);
		}
		for (int i = 0; i < 10; i++) {
			channel.send(
				MessageBuilder.withPayload("hello Fom ob adapter regextopic2 -  " + i)
					.setHeader("messageKey", String.valueOf(i))
					.setHeader("topic", "regextopic2").build());

			LOG.info("message sent " + i);
		}*/
	}
}
