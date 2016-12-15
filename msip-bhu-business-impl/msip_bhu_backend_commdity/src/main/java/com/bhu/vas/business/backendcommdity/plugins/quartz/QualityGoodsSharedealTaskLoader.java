package com.bhu.vas.business.backendcommdity.plugins.quartz;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.backendcommdity.asyncprocessor.service.AsyncOrderPaymentNotifyService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 此任务暂定每小时执行一次，再良品订单完成15天之后开始进行分润
 * 
 * @author yetao
 * 
 */
public class QualityGoodsSharedealTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(QualityGoodsSharedealTaskLoader.class);
	
	@Resource
	private OrderService orderService;
	@Resource
	private AsyncOrderPaymentNotifyService asyncOrderPaymentNotifyService;
	
	public void execute() {
		logger.info("QualityGoodsSharedealTaskLoader start...");
		try{
			int count = 0;
			do{
				count = doQualidyGoodsSharedealOnePage();
			}while(count > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		logger.info("QualityGoodsSharedealTaskLoader end...");
	}
	
	private void sharedealOne(Order order){
		logger.info(String.format("sharedeal order id[%s]", order.getId()));
		asyncOrderPaymentNotifyService.qualityGoodsSharedealHandle(order);
	}
	
	//找出一页待处理的订单
	public int doQualidyGoodsSharedealOnePage(){
		Date tm = new Date(System.currentTimeMillis() - BusinessRuntimeConfiguration.QualityGoodsSharedealWaitSeconds * 1000);
		String[] typeList = {"8", "1"};
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("type",  Arrays.asList(typeList)).andColumnLessThan("created_at", tm).
			andColumnEqualTo("status", BusinessEnumType.OrderStatus.DeliverCompleted.getKey()).
			andColumnEqualTo("process_status", BusinessEnumType.OrderProcessStatus.DeliverCompleted.getKey());
		mc.setLimit(200);
		
		List<Order> lists = orderService.findModelByModelCriteria(mc);
		if(!lists.isEmpty()){
			logger.info("going to sharedeal:" + lists.size());
			for(Order order : lists){
				try{
					sharedealOne(order);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			logger.info("sharedeal one page end");
			return lists.size();
		}
		return 0;
	}
}
