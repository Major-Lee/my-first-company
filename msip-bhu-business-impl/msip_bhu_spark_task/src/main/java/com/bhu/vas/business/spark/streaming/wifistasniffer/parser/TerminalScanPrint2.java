package com.bhu.vas.business.spark.streaming.wifistasniffer.parser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.business.spark.streaming.wifistasniffer.TerminalScanStreamingDTO;

/**
 * 打印最后聚合结果
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class TerminalScanPrint2 implements Function2<JavaPairRDD<String, Iterable<TerminalScanStreamingDTO>>, Time, Void> {
	private final Logger logger = LoggerFactory.getLogger(TerminalScanPrint2.class);
	
	@Override
    public Void call(JavaPairRDD<String, Iterable<TerminalScanStreamingDTO>> rdd, Time time) throws IOException {
    	Map<String, Iterable<TerminalScanStreamingDTO>> group_map_dtos = rdd.collectAsMap();
    	Set<String> keys = group_map_dtos.keySet();
    	int i = 0;
    	for(String key : keys){
    		print(i, key, group_map_dtos.get(key));
    		i++;
    	}
    	//String counts = "Counts at time " + time + " " + rdd.collect();
    	//System.out.println(counts);
      return null;
    }
    
	public void print(int index, String mac, Iterable<TerminalScanStreamingDTO> dtos){
		//System.out.println(index + "设备mac* : " + mac);
		logger.info(index + "设备mac* : " + mac);
		Iterator<TerminalScanStreamingDTO> dto_iterator = dtos.iterator();
		while(dto_iterator.hasNext()){
			TerminalScanStreamingDTO dto = dto_iterator.next();
			//System.out.println("终端 " + dto.getHd_mac() + " 登录时间 " + dto.getTs());
			logger.info("终端 " + dto.getHd_mac() + " 登录时间 " + dto.getTs());
		}
	}
}
