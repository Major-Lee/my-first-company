package com.bhu.vas.business.spark.streaming.wifistasniffer.parser;

import java.io.IOException;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.bhu.vas.business.ds.user.service.UserService;

/**
 * 打印最后聚合结果
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class TerminalScanPrint implements Function2<JavaRDD<String>, Time, Void> {
	private final Logger logger = LoggerFactory.getLogger("wifistasniffer");
	
	private UserService userService;
	
	public TerminalScanPrint(ApplicationContext ctx){
		userService = (UserService)ctx.getBean("userService");
	}
	
	@Override
    public Void call(JavaRDD<String> rdd, Time time) throws IOException {
//    	Map<String, Iterable<TerminalScanStreamingDTO>> group_map_dtos = rdd.collectAsMap();
//    	Set<String> keys = group_map_dtos.keySet();
//    	int i = 0;
//    	for(String key : keys){
//    		print(i, key, group_map_dtos.get(key));
//    		i++;
//    	}
    	//String counts = "Counts at time " + time + " " + rdd.collect();
    	//System.out.println(counts);
		List<String> strs = rdd.collect();
		for(String str : strs){
			logger.info("终端 " + str);
		}
		logger.info("User  " + userService.getById(7).getNick());
      return null;
    }
    
//	public void print(int index, String mac, Iterable<TerminalScanStreamingDTO> dtos){
//		logger.info(index + "设备mac* : " + mac);
//		Iterator<TerminalScanStreamingDTO> dto_iterator = dtos.iterator();
//		while(dto_iterator.hasNext()){
//			TerminalScanStreamingDTO dto = dto_iterator.next();
//			//System.out.println("终端 " + dto.getHd_mac() + " 登录时间 " + dto.getTs());
//			logger.info("终端 " + dto.getHd_mac() + " 登录时间 " + dto.getTs());
//		}
//		User user = userService.getById(7);
//		logger.info("Test user " + user.getNick());
//	}
}
