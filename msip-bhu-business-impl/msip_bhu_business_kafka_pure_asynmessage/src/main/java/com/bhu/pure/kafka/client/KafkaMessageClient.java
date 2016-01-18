package com.bhu.pure.kafka.client;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMessageClient implements IKafkaMessageClient{
	private static final Logger logger = LoggerFactory.getLogger(KafkaMessageClient.class);
	
	@Override
	public Properties loadProperties(){
		InputStream in = null;
		Properties properties = new Properties();
	    try {
	    	in = KafkaMessageClient.class.getResourceAsStream("/deploy/conf/kafka.properties");
			properties.load(in);
		} catch (Exception e) {
			try{
				in = KafkaMessageClient.class.getResourceAsStream("/conf/kafka.properties");
				properties.load(in);
			}catch(Exception ex){
				logger.error("init loading /deploy/conf/kafka.properties or /conf/kafka.properties failed!", e);
				e.printStackTrace();
			}
		}
	    return properties;
	}
}
