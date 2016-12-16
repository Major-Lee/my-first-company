package com.bhu.vas.business.backendcommdity.plugins.quartz;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.backendcommdity.asyncprocessor.service.AsyncOrderPaymentNotifyService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
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
			doQualidyGoodsSharedeal();
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
	public int doQualidyGoodsSharedeal(){
		Date tm = new Date(System.currentTimeMillis() - BusinessRuntimeConfiguration.QualityGoodsSharedealWaitSeconds * 1000);
		String[] typeList = {"8", "1"};
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("type",  Arrays.asList(typeList)).andColumnLessThan("created_at", tm).
			andColumnEqualTo("status", BusinessEnumType.OrderStatus.DeliverCompleted.getKey()).
			andColumnEqualTo("process_status", BusinessEnumType.OrderProcessStatus.DeliverCompleted.getKey());
		mc.setPageNumber(1);
		mc.setPageSize(200);
		
		logger.info("going to sharedeal:");
		//因为订单状态的改变，会造成下次获取第二页的时候，实际取的是第一次查询时候的第三页。不过下次再分润的时候就可以继续分润，所以暂时忽略
    	EntityIterator<String, Order> it = new KeyBasedEntityBatchIterator<String, Order>(String.class, Order.class, orderService.getEntityDao(), mc);
		while(it.hasNext()){
			List<Order> list = it.next();
			for(Order order:list){
				try{
					sharedealOne(order);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		logger.info("sharedeal one page end");
		return 0;
	}
}
