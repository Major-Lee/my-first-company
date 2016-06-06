package com.bhu.vas.di.op.repair;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderUmacType;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 数据修复
 * 根据用户绑定设备表进行设备push消息redis数据重建
 * @author Edmond
 *
 */
public class OrderSharedealRepairOp {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		
		ApplicationContext actx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		long t0 = System.currentTimeMillis();
		
		UserWalletFacadeService userWalletFacadeService = (UserWalletFacadeService)actx.getBean("userWalletFacadeService");
		OrderService orderService = (OrderService)actx.getBean("orderService");
		//select * from t_commdity_orders where status = 10 and process_status = 10 and created_at > '2016-06-06 00:00:00'
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("status", 10).andColumnEqualTo("process_status", 10).andColumnGreaterThan("created_at", "2016-06-06 00:00:00").andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc.setOrderByClause("uid");
		mc.setPageNumber(1);
    	mc.setPageSize(200);
		EntityIterator<String, Order> it = new KeyBasedEntityBatchIterator<String,Order>(String.class,Order.class, orderService.getEntityDao(), mc);
		while(it.hasNext()){
			List<Order> orders = it.next();
			for(Order order:orders){
				double amount = Double.parseDouble(order.getAmount());
				//userWalletFacadeService.sharedealCashToUserWallet(order.getUid(), amount, orderid);
				OrderUmacType uMacType = OrderUmacType.fromKey(order.getUmactype());
				if(uMacType == null){
					uMacType = OrderUmacType.Terminal;
				}
				if(StringUtils.isEmpty(order.getPayment_type())){
					order.setPayment_type(BusinessEnumType.unknownPaymentType);
				}
				final String order_payment_type = order.getPayment_type();
				final Integer order_umac_type = order.getUmactype();
				final String mac = order.getMac();
				final String umac = order.getUmac();
				OrderPaymentType orderPaymentType = OrderPaymentType.fromKey(order.getPayment_type());
				String description = String.format(BusinessEnumType.templateRedpacketPaymentDesc, uMacType.getDesc(), 
						orderPaymentType != null ? orderPaymentType.getDesc() : StringHelper.EMPTY_STRING_GAP);
				
				int ret  = userWalletFacadeService.sharedealCashToUserWalletWithProcedure(order.getMac(), amount, order.getId(), description, null);//sharedealCashToUserWalletWithProcedure
				
				System.out.println(order.getId()+"----"+ret);
			}
			System.out.println(orders.size());
		}
		
		System.out.println("数据修复完成，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s");
	}
}
