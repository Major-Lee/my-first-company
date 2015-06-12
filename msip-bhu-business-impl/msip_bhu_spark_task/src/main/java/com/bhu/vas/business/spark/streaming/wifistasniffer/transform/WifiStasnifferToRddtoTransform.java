package com.bhu.vas.business.spark.streaming.wifistasniffer.transform;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Tuple2;

import com.bhu.vas.business.spark.streaming.wifistasniffer.rddto.WifistasnifferRddto;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 把设备周边探测的json格式数据转成dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiStasnifferToRddtoTransform implements Function<Tuple2<String, String>, WifistasnifferRddto> {
	private final Logger logger = LoggerFactory.getLogger("wifistasniffer");
	
	@Override
	public WifistasnifferRddto call(Tuple2<String, String> tuple2) throws Exception {
		String wifiStasniffer_content = tuple2._2();
		logger.info(String.format("WifiStasnifferToDtoParser call content %s", wifiStasniffer_content));
		if(!StringUtils.isEmpty(wifiStasniffer_content)){
			return JsonHelper.getDTO(wifiStasniffer_content, WifistasnifferRddto.class);
		}
		return null;
	}

}
