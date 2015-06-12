package com.bhu.vas.business.spark.streaming.wifistasniffer.action;

import java.io.IOException;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.spark.streaming.wifistasniffer.rddto.WifistasnifferRddto;

/**
 * 打印最后聚合结果
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class TerminalScanPrint implements Function2<JavaRDD<WifistasnifferRddto>, Time, Void> {
	private final Logger logger = LoggerFactory.getLogger("wifistasniffer");
	
	private UserService userService;
	
	public TerminalScanPrint(ApplicationContext ctx){
		userService = (UserService)ctx.getBean("userService");
	}
	
	@Override
    public Void call(JavaRDD<WifistasnifferRddto> rdd, Time time) throws IOException {
		List<WifistasnifferRddto> dtos = rdd.collect();
		for(WifistasnifferRddto dto : dtos){
			logger.info("设备 " + dto.getMac());
		}
		logger.info("User  " + userService.getById(7).getNick());
      return null;
    }
}
