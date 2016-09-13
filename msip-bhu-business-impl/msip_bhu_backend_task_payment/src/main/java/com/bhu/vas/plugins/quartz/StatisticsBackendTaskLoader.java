package com.bhu.vas.plugins.quartz;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.payment.dto.PaymentErrorCountDTO;
import com.bhu.vas.business.BusinessHelper;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.qqmail.SendMailHelper;

/**
 * 此任务暂定5分钟执行一次 根据配置的同时运行的任务数量决定是否需要重新把新的任务加入到任务池中
 * 
 * @author xiaowei
 * 
 */
public class StatisticsBackendTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(StatisticsBackendTaskLoader.class);

	@Resource
	private PaymentReckoningService paymentReckoningService;

	boolean flag = false;
	int sendTime = 0;
	public void execute() {
		logger.info("StatisticsBackendTaskLoader start...");
		
		StringBuffer sendMsg= new StringBuffer();
		sendMsg = sendMsg.append("截止:"+BusinessHelper.getDate()+"今天,");
		//"，1159单成功支付，24单超过1分钟，3单超过2分钟，2单超过3分钟的";
		
		PaymentErrorCountDTO errorCount = paymentReckoningService.paymentRecordInfo();
		int heeOneMin = errorCount.getHee_more_one_min_count();
		int heeThreeMin = errorCount.getHee_more_three_min_count();
		if(heeOneMin >= 50 || heeThreeMin >= 10){
			sendMsg.append("汇元宝微信支付"+heeOneMin+"单回调超过1分钟， "+heeThreeMin+"单回调超过3分钟;");
			flag = true;
		}
		
		int midasOneMin = errorCount.getMidas_more_one_min_count();
		int midasThreeMin = errorCount.getMidas_more_three_min_count();
		
		if(midasOneMin >= 120 || midasThreeMin >= 20){
			sendMsg.append("米大师微信支付"+midasOneMin+"单回调超过1分钟， "+midasThreeMin+"单回调超过3分钟;");
			flag = true;
		}
		
		int nowOneMin = errorCount.getNow_more_one_min_count();
		int nowThreeMin = errorCount.getNow_more_three_min_count();
		
		if(nowOneMin >= 100 || nowThreeMin >= 10){
			sendMsg.append("聚合微信支付(现在支付)"+nowOneMin+"单回调超过1分钟， "+nowThreeMin+"单回调超过3分钟;");
			flag = true;
		}
		if(flag){
			String msg = "";
			if(sendTime < 3){
				msg = SendMailHelper.doSendMail(sendMsg+"");
			}
			if(msg.equals("success")){
				sendTime++;
				flag = false;
			}
		}
		
		logger.info("StatisticsBackendTaskLoader end...");
	}
	
	
}
