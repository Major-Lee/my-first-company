package com.bhu.vas.plugins.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;



import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.PageHelper;

/**
 * 限时上网服务的订单支付成功 发货通知redis失败的后续操作处理
 * 重新进行发货通知redis 如果发送成功 更新订单的状态
 * (暂时不判断订单的商品是否是限时上网服务分类)
 * @author tangzichao
 * 
 */
public class InternetLimitOrderDeliverFailedRetryLoader {
    private static Logger logger = LoggerFactory.getLogger(InternetLimitOrderDeliverFailedRetryLoader.class);
    
	@Resource
	private OrderService orderService;
	
	@Resource
	private CommdityService commdityService;
	
	@Resource
	private OrderFacadeService orderFacadeService;
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	@Resource
	private UserService userService;
    
    public void execute(){
		logger.info("OrderDeliverFailedRetryLoader starting...");
		int pageSize = 100;
		int failed_count = 0;
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("status", BusinessEnumType.OrderStatus.PaySuccessed.getKey());
		mc.setOrderByClause(" updated_at ");
    	mc.setPageNumber(1);
    	mc.setPageSize(pageSize);
    	int count = orderService.countByModelCriteria(mc);
    	int pagecount = PageHelper.getTotalPages(count, pageSize);
    	//由于遍历过程中会更新条件字段的数据 所以采用只获取第一页的数据 最多获取pagecount次
    	int current_page = 0;
    	while(pagecount > current_page){
    		List<Order> orders = orderService.findModelByModelCriteria(mc);
    		for(Order order : orders){
    			User bindUser = null;
    			if(order.getUid() != null){
    				bindUser = userService.getById(order.getUid());
    			}
    			String accessInternetTime = chargingFacadeService.fetchAccessInternetTime(order.getMac(), order.getUmactype());
				boolean deliver_notify_ret = orderFacadeService.orderDeliverNotify(order, bindUser,accessInternetTime);
				if(deliver_notify_ret){
					//如果通知发货成功 更新订单状态为发货完成
					orderFacadeService.orderStatusChanged(order, OrderStatus.DeliverCompleted.getKey(),  
							OrderProcessStatus.DeliverCompleted.getKey());
				}
				failed_count++;
    		}
    		current_page++;
    	}
		logger.info("OrderDeliverFailedRetryLoader done, total:"+failed_count);
    }
    
    
}
