package com.bhu.vas.business.spark.streaming.wifistasniffer.transform;

import java.io.Serializable;

import org.apache.spark.api.java.function.PairFunction;
import org.springframework.stereotype.Component;

import scala.Tuple2;

import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;

/**
 * 把itemdto序列转换成KV形式 key为设备mac value为itemdto
 * @author tangzichao
 *
 */
@Component
@SuppressWarnings("serial")
public class WifiStasnifferToPairTransform implements PairFunction<WifistasnifferItemRddto, String, WifistasnifferItemRddto>,Serializable {
	@Override
	public Tuple2<String, WifistasnifferItemRddto> call(WifistasnifferItemRddto dto) throws Exception {
	      if(dto != null){
	    	  return new Tuple2<String, WifistasnifferItemRddto>(dto.getD_mac(), dto);
	      }
	      return null;
	}
}
