package com.bhu.vas.business.user;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.AssertHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.Page;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

/**
 * 主键索引
 * 其他字段不索引的前提下
 * @author Edmond
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserService1Test extends BaseTest{

	long unit_test_start = 0;
	long unit_test_end = 0;
	long batch_insert_cost_time = 0;
	long batch_total_cost_time = 0;
	
	
	int batch_create_size = 10;
	@Resource
	UserService userService;
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	private static Set<Integer> key_gen = new HashSet<Integer>();
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
	public void test001BatchCreateUser(){
    	System.out.println(Integer.MAX_VALUE);
    	CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		userService.deleteByCommonCriteria(mc);
    	unit_test_start = System.currentTimeMillis();
		//int count = 10;
		for(int i=0;i<batch_create_size;i++){
			User user = new User();
			//user.setId(i+5);
			user.setCountrycode(86);
			user.setMobileno(String.format("1861%s2%s", RandomData.intNumber(100,999),RandomData.intNumber(100,999)));
			user.setBirthday(String.format("%s-%s-%s", RandomData.intNumber(1969,2010),String.format("%02d", RandomData.intNumber(1,12)),String.format("%02d", RandomData.intNumber(1,28))));
			user.setNick(RandomPicker.randString(letters, 10));
			user.setSex(RandomData.flag()?"男":"女");
			//user.setId(doGenKey());
			user.setRegdevice(DeviceEnum.IPhone.getSname());
			user.setRegip("192.168.1.6");
			user.setLastlogindevice(DeviceEnum.IPhone.getSname());
			user.setLastlogindevice_uuid(UUID.randomUUID().toString());
			userService.insert(user);	
			
			System.out.println("userid:"+user.getId());
		}
		batch_insert_cost_time = System.currentTimeMillis()-unit_test_start;
		//userService.insertMulti(users);
	}	

}
