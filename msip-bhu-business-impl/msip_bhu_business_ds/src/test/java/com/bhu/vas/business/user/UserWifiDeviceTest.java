package com.bhu.vas.business.user;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserWifiDeviceTest extends BaseTest{
	@Resource
	private UserWifiDeviceService userWifiDeviceService;
	
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
    
    @Test
    public void test001Insert(){
    	UserWifiDevice entity = new UserWifiDevice();
    	entity.setId("84:82:f4:32:2e:8c");
    	entity.setUid(100153);
    	entity.setDevice_name("devicename");
    	entity.setDevice_name_modifyed(false);
    	entity.setCreated_at(new Date());
    	userWifiDeviceService.insert(entity);
    }
    
    @Test
    public void test002GetById(){
    	UserWifiDevice entity = userWifiDeviceService.getById("84:82:f4:32:2e:8c");
    	if(entity != null){
    		System.out.println(entity.getId());
    	}
    }
}
