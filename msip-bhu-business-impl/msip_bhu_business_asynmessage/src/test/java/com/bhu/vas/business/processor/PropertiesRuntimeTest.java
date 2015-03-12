package com.bhu.vas.business.processor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesRuntimeTest {
	public static void main(String[] argv){
		Properties properties = new Properties();
		boolean porperties_loaded = false;
		InputStream in = null;
	    try {
	    	in = PropertiesRuntimeTest.class.getResourceAsStream("/deploy/lazyloadconf/dynamic.activemq.properties");
			properties.load(in);
			porperties_loaded = true;
		} catch (Exception e) {
			try{
				in = PropertiesRuntimeTest.class.getResourceAsStream("/lazyloadconf/dynamic.activemq.properties");
				properties.load(in);
				porperties_loaded = true;
			}catch(Exception ex){
				e.printStackTrace(System.out);
			}
		}finally{
			if(in != null){
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
			}
		}
	    if(porperties_loaded){
	    	String file = PropertiesRuntimeTest.class.getResource("/deploy/lazyloadconf/dynamic.activemq.properties").getFile();
	    	OutputStream outputStream;
			try {
				outputStream = new FileOutputStream(file);
				System.out.println(file);
		    	String consumer_key = "mq.activemq.server.consumer.queues";
		    	properties.setProperty(consumer_key, properties.getProperty(consumer_key).concat(",").concat("cm003_1"));  
		    	String producer_key = "mq.activemq.server.producer.queues";
	            properties.setProperty(producer_key, properties.getProperty(producer_key).concat(",").concat("cm003_1")); 
	            properties.store(outputStream, "author: liwh@bhunetworks.com"); 
	            outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  
	    	
	    }
	}
}
