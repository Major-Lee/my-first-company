package com.bhu.jorion;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JOrionConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(JOrionConfig.class);

	public final static String MSG_URSIDS_ONLINE 	= "00000001";
	public final static String MSG_URSIDS_OFFLINE 	= "00000002";
	public final static String MSG_DEV_OFFLINE		= "00000003";
	public final static String MSG_DEV_NOTEXIST		= "00000004";
	public final static String MSG_DEV_MSG_FORWARD 	= "00000005";
	public final static String MSG_DEV_XML_APPLY	= "00001001";

	
	public static Integer JMX_PORT = 8092;
	public static String JMX_USER = "admin";
	public static String JMX_PASS = "82730100";
	
	
	public static Integer PRE_ALLOC_BUFF_SIZE = 0;
	public static Integer JORION_LISTEN_PORT = 0;
	public static Integer URSIDS_READ_BUFFER_SIZE = 1024*16;
	public static Integer URSIDS_THREADS_NUMBER	= 64;
	public static String MQ_URL = "";
	public static String MQ_MNG_URL = "";
	public static String MQ_BUSINESS_HOST = "";
	public static String MQ_BUSINESS_PORT = "";
	public static String MANAGEMENT_MQ_NAME = "mng_queue";
	public static String ZOO_NAME				= "";
	public static String ZOO_URL				= "";
	public static String ZOO_START_APTH		= "/cmBalance";
	public static Long ZOO_SYNC_STEPS			= Long.valueOf(20);
	public static Integer ZOO_SESSION_TIMEOUT		= 500000;
	public static Float ZOO_BALANCE_RATE		= Float.valueOf("0.80");
	
	
	public static void loadConfig() throws IllegalArgumentException, IllegalAccessException{
		ResourceBundle bundle = ResourceBundle.getBundle("orionconfig");
		Field[] f = JOrionConfig.class.getDeclaredFields();
		for(int i = 0; i < f.length; i ++){
			String type = f[i].getGenericType().toString();
			String name = f[i].getName();
			String valueStr = null;
			try{
				valueStr = bundle.getString(name);
			}catch(Exception e){
			}
			Object value = null;
			if(valueStr == null || valueStr.trim().isEmpty())
				continue;
			if(type.equals("class java.lang.String")){
				value = valueStr;
			} else if(type.equals("class java.lang.Integer")){
				value = Integer.valueOf(valueStr);
			} else if(type.equals("class java.lang.Long")){
				value = Long.valueOf(valueStr);
			} else if(type.equals("class java.lang.Float")){
				value = Float.valueOf(valueStr);
			} else {
				LOGGER.info("property " + name + " will not be configured");
				continue;
			}
			f[i].set(name, value);
		}
	}
	
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException{
		System.out.println("before, load:" + JOrionConfig.ZOO_URL);

		JOrionConfig.loadConfig();
		
		System.out.println("after, load:" + JOrionConfig.ZOO_URL);
	}
}
