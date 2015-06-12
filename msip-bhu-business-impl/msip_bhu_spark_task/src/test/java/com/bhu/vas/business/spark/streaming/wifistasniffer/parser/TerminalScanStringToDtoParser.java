package com.bhu.vas.business.spark.streaming.wifistasniffer.parser;

import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Tuple2;

/**
 * 把json string 转成 dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class TerminalScanStringToDtoParser implements Function<Tuple2<String, String>, String> {
	private final Logger logger = LoggerFactory.getLogger("wifistasniffer");
	
	@Override
	public String call(Tuple2<String, String> tuple2) throws Exception {
		String json = tuple2._2();
		logger.info(tuple2._1() + "-" + json);
//		if(json != null && !"".equals(json)){
//			return JsonHelper.getDTO(json, TerminalScanStreamingDTO.class);
//		}
		return json;
	}

}
