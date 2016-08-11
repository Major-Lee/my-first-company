package com.bhu.vas.di.op;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;



public class BuilderIdentityAuthOp {
	
	private static OrderService orderService;
	private static UserIdentityAuthService userIdentityAuthService;
	
	public static void main(String[] args) {
		initialize();
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("type", 10);
		List<Order> orders  = orderService.findModelByModelCriteria(mc);
		
		List<UserIdentityAuth> ids = new ArrayList<UserIdentityAuth>();
		int i = 0;
		for(Order order : orders){
			UserIdentityAuth auth = new UserIdentityAuth();
			auth.setCreated_at(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
			auth.setHdmac(order.getUmac());
			auth.setId(order.getContext());
			ids.add(auth);
			System.out.println("now: " + i +"count :" + orders.size());
			i++;
		}
		System.out.println("insertAll start...");
		userIdentityAuthService.insertAll(ids);
		System.out.println("insertAall end...");
		
	}
	
	
	public static void initialize(){
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		orderService = (OrderService)ctx.getBean("orderService");
		userIdentityAuthService = (UserIdentityAuthService)ctx.getBean("userIdentityAuthService");
	}
	
	
}
