package com.bhu.vas.business.spark.streaming.wifistasniffer.action;

import java.io.Serializable;

import org.apache.spark.api.java.function.VoidFunction;
import org.springframework.stereotype.Component;

import scala.Tuple2;

import com.bhu.vas.business.spark.streaming.log.SparkTaskLog;
import com.bhu.vas.business.spark.streaming.wifistasniffer.rddto.WifistasnifferItemRddto;

/**
 * 打印最后聚合结果
 * @author tangzichao
 *
 */
@Component
@SuppressWarnings("serial")
public class WifiStasnifferForeachAction implements VoidFunction<Tuple2<String, Iterable<WifistasnifferItemRddto>>>, Serializable {
	
	@Override
	public void call(Tuple2<String, Iterable<WifistasnifferItemRddto>> tuple2_rddto)
			throws Exception {
		SparkTaskLog.wifistasniffer().info("设备 " + tuple2_rddto._1());
	}
}
