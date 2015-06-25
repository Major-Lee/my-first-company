package com.bhu.vas.business.spark.streaming.wifistasniffer.transform;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.function.Function;
import org.springframework.stereotype.Component;

import scala.Tuple2;

import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferItemRddto;
import com.bhu.vas.api.dto.wifistasniffer.WifistasnifferRddto;
import com.bhu.vas.business.spark.streaming.log.SparkTaskLog;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 把设备周边探测的json格式数据转成dto
 * @author tangzichao
 *
 */
@Component
@SuppressWarnings("serial")
public class WifiStasnifferToRddtoTransform implements Function<Tuple2<String, String>, WifistasnifferRddto>, Serializable {
	
	@Override
	public WifistasnifferRddto call(Tuple2<String, String> tuple2) throws Exception {
		String wifiStasniffer_string = tuple2._2();
		SparkTaskLog.wifistasniffer().info(String.format("WifiStasnifferToRddtoTransform call content %s", wifiStasniffer_string));
		return wifiStasnifferStringToRddto(wifiStasniffer_string);
	}
	
	/**
	 * 转换string为WifistasnifferRddto
	 * 并且填充item中的设备mac
	 * @param wifiStasniffer_string
	 * @return
	 */
	public WifistasnifferRddto wifiStasnifferStringToRddto(String wifiStasniffer_string){
		try{
			if(!StringUtils.isEmpty(wifiStasniffer_string)){
				WifistasnifferRddto dto = JsonHelper.getDTO(wifiStasniffer_string, WifistasnifferRddto.class);
				if(dto != null){
					//获取设备mac 并填充到item中
					String d_mac = dto.getMac();
					if(!StringUtils.isEmpty(d_mac)){
						List<WifistasnifferItemRddto> items = dto.getItems();
						if(items != null && !items.isEmpty()){
							for(WifistasnifferItemRddto item_dto : items){
								item_dto.setD_mac(d_mac);
								item_dto.setSnifftime(item_dto.getSnifftime() * 1000l);//上报的是秒 转换成毫秒
							}
							return dto;
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			SparkTaskLog.wifistasniffer().error("WifiStasnifferToRddtoTransform parser json error", ex);
		}
		return null;
	}
}
