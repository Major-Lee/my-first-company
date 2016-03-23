package com.bhu.vas.business.commdity;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.helper.BusinessEnumType.CommdityCategory;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityStatus;
import com.bhu.vas.api.rpc.commdity.helper.CommdityHelper;
import com.bhu.vas.api.rpc.commdity.model.Commdity;
import com.bhu.vas.business.ds.commdity.service.CommdityService;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;

/**
 * 主键索引
 * 其他字段不索引的前提下
 * @author Edmond
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommdityServiceTest extends BaseTest{
	long unit_test_start = 0;
	long unit_test_end = 0;
	long batch_insert_cost_time = 0;
	long batch_total_cost_time = 0;
	
	
	int batch_create_size = 10;
	
	@Resource
	private CommdityService commdityService;
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
		commdityService.deleteByCommonCriteria(mc);
    	unit_test_start = System.currentTimeMillis();
		//int count = 10;
		for(int i=0;i<batch_create_size;i++){
			Commdity commdity = new Commdity();
			commdity.setCategory(CommdityCategory.InternetLimit.getCategory());
			commdity.setPrice("[0.25,10.25]");
			commdity.setStatus(CommdityStatus.OnSale.getKey());
			commdity.setTags("tags"+i);
			commdity.setStock_quantity(CommdityHelper.STOCK_QUANTITY_INFINITE);
			commdity.setApp_deliver_detail(String.valueOf(i));
			commdityService.insert(commdity);
			System.out.println(commdity.getId());
		}
		batch_insert_cost_time = System.currentTimeMillis()-unit_test_start;
		//userService.insertMulti(users);
	}	
    
    //@Test
    public void testGetById(){
    	ModelCriteria mc = new ModelCriteria();
    	mc.setSize(5);
    	List<Integer> commdityids = commdityService.findIdsByModelCriteria(mc);
    	if(commdityids != null && !commdityids.isEmpty()){
    		for(Integer commdityid : commdityids){
    			System.out.println(commdityid);
    			Commdity commdity = commdityService.getById(commdityid);
    			System.out.println(commdity.getId() + " - " + commdity.getStock_quantity());
    		}
    	}
    }
    
    @Test
    public void testPorcByCommdityTest(){
    	int ret = commdityService.porcByCommdityTest(4);
    	System.out.println("testPorcByCommdityTest = " + ret);
    }
}
