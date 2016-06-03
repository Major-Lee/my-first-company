package com.bhu.vas.business.commdity;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.dto.commdity.internal.portal.RequestDeliverNotifyDTO;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.vto.statistics.OrderStatisticsVTO;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;

/**
 * 主键索引
 * 其他字段不索引的前提下
 * @author Edmond
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderServiceTest extends BaseTest{
	long unit_test_start = 0;
	long unit_test_end = 0;
	long batch_insert_cost_time = 0;
	long batch_total_cost_time = 0;
	
	
	int batch_create_size = 10;
	
	@Resource
	private OrderService orderService;
	@Resource
	private CommdityService commdityService;
	@Resource
	private OrderFacadeService orderFacadeService;
	
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	//private static Set<Integer> key_gen = new HashSet<Integer>();
	
	@BeforeClass
    public static void setUp() throws Exception {
		System.out.println("111111111");
		Thread.sleep(1000);
		/*unit_test_start = System.currentTimeMillis();
		testBatchCreateUser();*/
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	System.out.println("0000000");
    	Thread.sleep(1000);
    	/*unit_test_end = System.currentTimeMillis();
    	emptyUser();
    	System.out.println(String.format("Unit test batch insert cost:[%s]s", batch_insert_cost_time/1000));
    	System.out.println(String.format("Unit test cost:[%s]s", (unit_test_end-unit_test_start)/1000));*/
    }

    //@Test
	public void test001BatchCreateOrder(){
    	//System.out.println(Integer.MAX_VALUE);
    	CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		orderService.deleteByCommonCriteria(mc);
    	unit_test_start = System.currentTimeMillis();
		//int count = 10;
		for(int i=0;i<batch_create_size;i++){
			Order order = new Order();
			order.setCommdityid(RandomData.intNumber(100,9999));
			order.setAppid(RandomData.intNumber(100,9999));
			
			/*	
			 * 附带业务扩展占位的id生成		
			String orderId = OrderHelper.generateOrderId(order.getAppid(), String.format("%08d", 
					RandomData.intNumber(99999999)));
			order.setId(orderId);*/
			
			order.setMac("28:e0:2c:bc:2a:16");
			order.setUmac("28:e0:2c:bc:2a:17");
			order.setUid(7);
			//order.setPay_orderid("pay_orderid"+i);
			order.setAmount(String.valueOf(ArithHelper.round(RandomData.floatNumber(10), 2)));
			order.setContext("context"+i);
			order.setStatus(1);
			order.setProcess_status(2);
			orderService.insert(order);
			System.out.println(order.getId());
		}
		batch_insert_cost_time = System.currentTimeMillis()-unit_test_start;
		//userService.insertMulti(users);
	}	
    
    //@Test
    public void testGetById(){
    	ModelCriteria mc = new ModelCriteria();
    	mc.setSize(5);
    	List<String> orderids = orderService.findIdsByModelCriteria(mc);
    	if(orderids != null && !orderids.isEmpty()){
    		for(String orderid : orderids){
    			System.out.println(orderid);
    			Order order = orderService.getById(orderid);
    			Commdity commdity = commdityService.getById(order.getCommdityid());
    			RequestDeliverNotifyDTO dto = RequestDeliverNotifyDTO.from(order, commdity, null);
    			System.out.println(JsonHelper.getJSONString(dto));
    			System.out.println(order.getId() + " - " + order.getAmount());
    		}
    	}
    }
    
    //@Test
    public void testGetByProcedure(){
    	OrderStatisticsVTO vto = orderFacadeService.rewardOrderStatisticsWithProcedure("2016-03-11 17:15:50", "2016-06-11 17:15:50");
    	System.out.println(vto.getPc_occ());
    	System.out.println(vto.getPc_ofa());
    	System.out.println(vto.getPc_ofc());
    	System.out.println(vto.getMb_occ());
    	System.out.println(vto.getMb_ofa());
    	System.out.println(vto.getMb_ofc());
    }
    
    @Test
    public void testFindByIds(){
    	List<String> ids = new ArrayList<String>();
    	ids.add("10012016031200000000000000000129");
    	ids.add("-1");
    	List<Order> orders = orderService.findByIds(ids, true, true);
    	for(Order order : orders){
    		System.out.println(order);
    	}
    }
}
