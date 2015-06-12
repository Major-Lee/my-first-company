package com.bhu.vas.business.spark.streaming.wifistasniffer.conf;

import java.io.Serializable;

import com.bhu.vas.business.spark.streaming.log.SparkTaskLog;
/**
 * Spark Wifistasniffer 任务运行时应用配置数据
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifistasnifferRuntimeConf implements Serializable{
	//应用运行频率 秒
	public static int duration = 10;
	public static String kafka_topic_name = "wifistasniffer";
	public static String kafka_consumer_group = "wifistasniffer_group";
	public static int kafka_consumer_threads = 4;
	public static String kafka_zookeeper = "192.168.66.7:2181";
	/**
	 * 根据命令行参数加载
	 * @param args
	 */
	public static void load(String[] args){
		try{
			if(args != null && args.length == 5){
				duration = Integer.parseInt(args[0]);
				kafka_topic_name = args[1];
				kafka_consumer_group = args[2];
				kafka_consumer_threads = Integer.parseInt(args[3]);
				kafka_zookeeper = args[4];
			}
		}catch(Exception ex){
			ex.printStackTrace();
			SparkTaskLog.wifistasniffer().error("WifistasnifferRuntimeConf load failed", ex);
		}
	}
}
