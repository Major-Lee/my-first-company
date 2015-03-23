package com.bhu.jorion;

public class JOrionConfig {
    public final static int PRE_ALLOC_BUFF_SIZE = 2048;
	public final static int JORION_LISTEN_PORT = 19498;
	public final static String MQ_URL = "failover:(tcp://" + "192.168.66.7" + ":" + 61616 +")";
	public final static String MANAGEMENT_MQ_NAME = "mng_queue";
	public static final int URSIDS_READ_BUFFER_SIZE = 1024*32;
	
	
	public final static String MSG_URSIDS_ONLINE 	= "00000001";
	public final static String MSG_URSIDS_OFFLINE 	= "00000002";
	public final static String MSG_DEV_OFFLINE		= "00000003";
	public final static String MSG_DEV_NOTEXIST		= "00000004";
	public final static String MSG_DEV_MSG_FORWARD 	= "00000005";
	public final static String MSG_DEV_XML_APPLY	= "00001001";
	
	
}
