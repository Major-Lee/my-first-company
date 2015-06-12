package com.bhu.vas.business.spark.streaming.wifistasniffer.action;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Time;
import org.springframework.stereotype.Component;

import com.bhu.vas.business.spark.streaming.wifistasniffer.rddto.WifistasnifferItemRddto;

/**
 * 触发foreachRDD action 进行数据落地
 * @author tangzichao
 *
 */
@Component
@SuppressWarnings("serial")
public class WifiStasnifferForeachRDDAction implements Function2<JavaPairRDD<String, Iterable<WifistasnifferItemRddto>>, Time, Void>, Serializable {
	
//	@Resource
//	private UserService userService;
	
	@Resource
	private WifiStasnifferForeachAction wifiStasnifferForeachAction;
	
	@Override
    public Void call(JavaPairRDD<String, Iterable<WifistasnifferItemRddto>> rdd, Time time) throws IOException {
		//logger.info("User  " + userService.getById(7).getNick());
		rdd.foreach(wifiStasnifferForeachAction);
		return null;
    }
}
