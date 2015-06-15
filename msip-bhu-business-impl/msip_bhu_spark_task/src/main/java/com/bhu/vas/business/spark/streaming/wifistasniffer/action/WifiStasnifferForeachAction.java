package com.bhu.vas.business.spark.streaming.wifistasniffer.action;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.function.VoidFunction;
import org.springframework.stereotype.Component;

import scala.Tuple2;

import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;
import com.bhu.vas.business.spark.streaming.log.SparkTaskLog;
import com.bhu.vas.business.spark.streaming.wifistasniffer.action.handle.WifiStasnifferActionHandler;

/**
 * 聚合后的数据落地处理
 * mac : list<item>
 * @author tangzichao
 *
 */
@Component
@SuppressWarnings("serial")
public class WifiStasnifferForeachAction implements VoidFunction<Tuple2<String, Iterable<WifistasnifferItemRddto>>>, Serializable {
	
	@Resource
	private WifiStasnifferActionHandler wifiStasnifferActionHandler;
	
	@Override
	public void call(Tuple2<String, Iterable<WifistasnifferItemRddto>> tuple2_rddto)
			throws Exception {
		wifiStasnifferActionHandle(tuple2_rddto._1(), tuple2_rddto._2());
	}
	
	public void wifiStasnifferActionHandle(String d_mac, Iterable<WifistasnifferItemRddto> item_rddtos){
		SparkTaskLog.wifistasniffer().info(String.format("设备 [%s] 探测数据进行落地处理", d_mac));
		if(StringUtils.isEmpty(d_mac) || item_rddtos == null) return;
		
		wifiStasnifferActionHandler.wifistasnifferSparkHandle(d_mac, item_rddtos.iterator());
	}
}
