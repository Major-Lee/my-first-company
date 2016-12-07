package com.bhu.vas.di.op.advertise;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.UserMobilePositionRelationSortedSetService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class importUserMobilenoOp {
	private static OrderService orderService;
	private static UserIdentityAuthService userIdentityAuthService;
	private static WifiDeviceService wifiDeviceService;
	
	public static void initialize(){
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		orderService = (OrderService)ctx.getBean("orderService");
		userIdentityAuthService = (UserIdentityAuthService)ctx.getBean("userIdentityAuthService");
		wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
	}
	
	public static void main(String[] args) {
		initialize();
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("type", 10);
		List<Order> orders  = orderService.findModelByModelCriteria(mc);
		
		for(Order order : orders){
			String mac = order.getMac();
			String hdmac = order.getUmac();
			
			UserIdentityAuth auth = userIdentityAuthService.getById(hdmac);
			if(auth!=null){
				 WifiDevice wifiDevice = wifiDeviceService.getById(mac);
				String mobileno = auth.getMobileno().substring(auth.getMobileno().indexOf(StringHelper.WHITESPACE_STRING_GAP)).trim();
				UserMobilePositionRelationSortedSetService.getInstance().mobilenoRecord(wifiDevice.getProvince(), wifiDevice.getCity(), wifiDevice.getDistrict(), mobileno);
			}
		}
	}
}
