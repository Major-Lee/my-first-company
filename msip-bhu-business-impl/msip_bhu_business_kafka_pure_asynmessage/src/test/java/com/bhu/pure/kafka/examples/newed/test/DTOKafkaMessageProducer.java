package com.bhu.pure.kafka.examples.newed.test;

import com.bhu.pure.kafka.client.producer.KafkaMessageProducer;

public class DTOKafkaMessageProducer extends KafkaMessageProducer<String, TestDTO>{


/*	@Override
	public Properties getClientProperties() {
		Properties props = new Properties();
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	    props.put(ProducerConfig.CLIENT_ID_CONFIG, this.getClass().getSimpleName());
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TestDTOSerializer.class.getName());
	    //Custom Partitioner
	    props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, HashPartitioner.class.getName());
		return props;
	}*/

}
