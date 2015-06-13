package com.bhu.vas.business.spark.streaming.wifistasniffer.transform;

import java.io.Serializable;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.springframework.stereotype.Component;

import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;
import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferRddto;
import com.google.common.collect.Lists;

/**
 * 把数据dto转换成itemdto序列
 * @author tangzichao
 *
 */
@Component
@SuppressWarnings("serial")
public class WifiStasnifferToRddtoIterableTransform implements FlatMapFunction<WifistasnifferRddto, WifistasnifferItemRddto>, Serializable {
	
    @Override
    public Iterable<WifistasnifferItemRddto> call(WifistasnifferRddto rddto) {
	  	  if(rddto != null){
	  		  List<WifistasnifferItemRddto> items = rddto.getItems();
	  		  if(items != null && !items.isEmpty()){
	  			  return Lists.newArrayList(items);
	  		  }
	  	  }
	  	  return Lists.newArrayList();
    }
}
