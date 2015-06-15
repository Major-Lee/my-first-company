package com.bhu.vas.business.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.business.asyn.kafka.producer.KafkaMessageQueueProducer;
import com.smartwork.async.messagequeue.builder.MessageFactoryBuilder;
import com.smartwork.async.messagequeue.builder.PayloadModelBuilder;
import com.smartwork.async.messagequeue.kafka.model.CommonMessage;
import com.smartwork.async.messagequeue.kafka.outbound.OutboundRunner;
import com.smartwork.async.messagequeue.kafka.outbound.OutboundSender;
import com.smartwork.async.messagequeue.type.MessageType;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;


public class KafkaProducerTest {
	//private static final String CONFIG = "applicationContext-OutboundKafkaAdapterParser.xml";
	private static final Log LOG = LogFactory.getLog(OutboundRunner.class);
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	public static void main(final String args[]) {
		
		String[] CONFIG = {
				"/com/bhu/vas/business/processor/testCtx.xml",
				"/spring/applicationContextCore-resource.xml",
				"applicationContext-OutboundKafkaAdapterParser.xml"
		};
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
	}
	/*public static void main(String[] argv){
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/springkafka/testProducerCtx.xml", KafkaProducerTest.class);
		ctx.start();

		KafkaMessageQueueProducer producer = ctx.getBean("producer", KafkaMessageQueueProducer.class);
		for(int i=0;i<100;i++){
			producer.send(String.valueOf(i));
		}
	}*/
	
}
