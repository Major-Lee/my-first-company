package com.wecite.toplines.business.user;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bhu.vas.api.user.model.User;
import com.bhu.vas.business.user.service.UserService;
import com.smartwork.msip.cores.helper.AssertHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.SimpleCriteria;
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
public class UserServiceTest extends BaseTest{

	long unit_test_start = 0;
	long unit_test_end = 0;
	long batch_insert_cost_time = 0;
	long batch_total_cost_time = 0;
	
	
	int batch_create_size = 100;
	@Resource
	UserService userService;
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	private static Set<String> key_gen = new HashSet<String>();
	@Before//Class
    public void setUp() throws Exception {
		/*unit_test_start = System.currentTimeMillis();
		testBatchCreateUser();*/
    }

    @After//Class
    public void tearDown() throws Exception {
    	/*unit_test_end = System.currentTimeMillis();
    	emptyUser();
    	System.out.println(String.format("Unit test batch insert cost:[%s]s", batch_insert_cost_time/1000));
    	System.out.println(String.format("Unit test cost:[%s]s", (unit_test_end-unit_test_start)/1000));*/
    }
    
    @Test
	public void testBatchCreateUser(){
    	System.out.println(Integer.MAX_VALUE);
    	CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		userService.deleteByCommonCriteria(mc);
    	unit_test_start = System.currentTimeMillis();
		//int count = 10;
		for(int i=0;i<batch_create_size;i++){
			User user = new User();
			user.setId(doGenKey());
			user.setScore(RandomData.intNumber(1000));
			user.setLastlogin_coordination(" 1234  ");
			user.setReg_coordination(" 5678  ");
			user.setRegdevice("O");
			user.setRegip("192.168.1.6");
			user.setUsertype(RandomData.intNumber(1000));
			userService.insert(user);	
		}
		batch_insert_cost_time = System.currentTimeMillis()-unit_test_start;
		//userService.insertMulti(users);
	}	
    
    @Test
    public void testUdidExistAndCached(){
    	String randon_key = RandomPicker.pick(key_gen);
    	//System.out.println(randon_key);
    	User random_user = userService.getById(randon_key);
    	AssertHelper.notNull(random_user);
    	userService.getById(randon_key);
    	userService.getById(randon_key);
    }

    @Test
    public void countAllRecords(){
		int all = userService.countAllRecords();
		AssertHelper.isTrue(all == batch_create_size);
	}
    
    @Test
	public void testByModelCriteriaEqualCondition(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("regip", "192.168.1.6");
		List<User> result = userService.findModelByModelCriteria(mc);
		AssertHelper.isTrue(!result.isEmpty());
		AssertHelper.isTrue(result.size() == batch_create_size);
	}
    
    @Test
   	public void testByModelCriteriaLikeCondition(){
   		ModelCriteria mc = new ModelCriteria();
   		mc.createCriteria().andColumnLike("regip", "192.168%");
   		List<User> result = userService.findModelByModelCriteria(mc);
   		AssertHelper.isTrue(!result.isEmpty());
   		AssertHelper.isTrue(result.size() == batch_create_size);
   	}
    @Test
	public void testIterAlltables(){
    	int total  = 0;
		ModelCriteria mc = new ModelCriteria();
		System.out.println(String.format("UserIterOp开始，startid[%s]", "未指定"));
		mc.createCriteria().andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause(" id asc ");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<String, User> it = new KeyBasedEntityBatchIterator<String,User>(String.class,User.class, userService.getEntityDao(), mc);
		while(it.hasNext()){
			List<User> users = it.next();
			total += users.size();
		}
		AssertHelper.isTrue(total == batch_create_size);
	}
    
    @Test
  	public void testFindByIds(){
    	List<User> result = userService.findByIds(new ArrayList<String>(key_gen));
    	AssertHelper.isTrue(!result.isEmpty());
   		AssertHelper.isTrue(result.size() == batch_create_size);
  	}
    
    @Test
  	public void testFindByIdsWhenNotExist(){
    	key_gen.add("how are u1");
    	key_gen.add("how are u2");
    	key_gen.add("how are u3");
    	
    	List<User> result = userService.findByIds(new ArrayList<String>(key_gen), true, true);
    	AssertHelper.isTrue(!result.isEmpty());
   		AssertHelper.isTrue(result.size() == batch_create_size+3);
   		
   		key_gen.remove("how are u1");
    	key_gen.remove("how are u2");
    	key_gen.remove("how are u3");
  	}
    
    @Test
    public void testFindUniqueIdByProperty(){
    	String randon_key = RandomPicker.pick(key_gen);
		String userid = userService.findUniqueIdByProperty("id", randon_key);
		AssertHelper.isTrue(randon_key.equals(userid));
	}
    
    @Test
    public void testModelCriteriaPageQuery(){
    	
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("regip", "192.168.1.6");
		mc.setOrderByClause("id");
		mc.setPageNumber(1);
		mc.setPageSize(10);
		
		Page<User> users = this.userService.findModelPageByModelCriteria(mc);
		System.out.println(users.getItems().size());
		System.out.println(users.isFirstPage());
		System.out.println(users.isLastPage());
		System.out.println(users.getPageNumber());
		
		
		TailPage<User> tailusers = this.userService.findModelTailPageByModelCriteria(mc);//findModelBysql(null, null, 1, 10);
		System.out.println(tailusers.getTotalItemsCount());
		System.out.println(tailusers.getItems().size());
		System.out.println(tailusers.isFirstPage());
		System.out.println(tailusers.isLastPage());
		System.out.println("pn:"+tailusers.getPageNumber());
	}
    
    
    @Test
    public void testModelCriteriaPageQuery4Offset(){
    	
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("regip", "192.168.1.6");
		mc.setOrderByClause("id");
		mc.setStart(1);//.setPageNumber(1);
		mc.setSize(25);
		
		
		List<User> users = this.userService.findModelByModelCriteria(mc);
		System.out.println("!!!!!!!!!!!!!!"+users.size());
		AssertHelper.isTrue(users.size() == 25);
		
		/*Page<User> users = this.userService.findModelPageByModelCriteria(mc);
		System.out.println(users.getItems().size());
		System.out.println(users.isFirstPage());
		System.out.println(users.isLastPage());
		System.out.println(users.getPageNumber());
		
		
		TailPage<User> tailusers = this.userService.findModelTailPageByModelCriteria(mc);//findModelBysql(null, null, 1, 10);
		System.out.println(tailusers.getTotalItemsCount());
		System.out.println(tailusers.getItems().size());
		System.out.println(tailusers.isFirstPage());
		System.out.println(tailusers.isLastPage());
		System.out.println("pn:"+tailusers.getPageNumber());*/
	}
    
    @Test
    public void testModelCriteriaWithComplexPageQuery(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("regip", "192.168.1.6");
		mc.or(mc.createCriteria().andColumnEqualTo("id", "怀玉"));
		mc.or(mc.createCriteria().andColumnEqualTo("id", "测试"));
		mc.setOrderByClause("id");
		mc.setPageNumber(1);
		mc.setPageSize(10);
		Page<User> users = this.userService.findModelPageByModelCriteria(mc);
		System.out.println(users.getItems().size());
		System.out.println(users.isFirstPage());
		System.out.println(users.isLastPage());
		System.out.println(users.getPageNumber());
		//this.userService.deleteAll(users.getItems());
	}
    
    @Test
    public void testUpdateUser(){
    	String randon_key = RandomPicker.pick(key_gen);
    	User random_user = userService.getById(randon_key);
    	AssertHelper.notNull(random_user);
    	int old_point = random_user.getScore();
    	random_user.setScore(old_point+200);
    	userService.update(random_user);
    	
    	random_user = userService.getById(randon_key);
    	AssertHelper.isTrue(old_point+200 == random_user.getScore());
    }
    
    @Test
    public void testRemoveUser(){
    	List<String> findAllIds = userService.findAllIds();
    	System.out.println("~~~~~~~~~~~~"+findAllIds.size());
    	String randon_key = RandomPicker.pick(key_gen);
    	System.out.println("~~~~~~~~~~~~"+randon_key);
    	User random_user = userService.getById(randon_key);
    	AssertHelper.notNull(random_user);
    	userService.deleteById(randon_key);
    	
    	random_user = userService.getById(randon_key);
    	
    	AssertHelper.isNull(random_user);
    	
    	findAllIds = userService.findAllIds();
    	System.out.println("~~~~~~~~~~~~"+findAllIds.size());
    	AssertHelper.notEmpty(findAllIds);
    	AssertHelper.isTrue(findAllIds.size() == batch_create_size-1);
    }
    
    @Test
    public void testRemoveMultiUsers(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("regip", "192.168.1.6");
		mc.or(mc.createCriteria().andColumnEqualTo("id", "怀玉"));
		mc.or(mc.createCriteria().andColumnEqualTo("id", "测试"));
		mc.setOrderByClause("id");
		mc.setPageNumber(1);
		mc.setPageSize(10);
		Page<User> users = this.userService.findModelPageByModelCriteria(mc);
		System.out.println(users.getItems().size());
		System.out.println(users.isFirstPage());
		System.out.println(users.isLastPage());
		System.out.println(users.getPageNumber());
		this.userService.deleteAll(users.getItems());
		
		List<String> findAllIds = userService.findAllIds();
    	AssertHelper.notEmpty(findAllIds);
    	AssertHelper.isTrue(findAllIds.size() == batch_create_size-1-users.getItems().size());
	}
    
    @Test
	public void emptyUser(){
		unit_test_end = System.currentTimeMillis();
    	System.out.println(String.format("Unit test batch insert cost:[%s]s", batch_insert_cost_time/1000));
    	System.out.println(String.format("Unit test cost:[%s]s", (unit_test_end-unit_test_start)/1000));
		
		/*CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		userService.deleteByCommonCriteria(mc);*/
	}
    
	private static String doGenKey(){
		String genKey = null;
		boolean exist = true;
		do{
			genKey = RandomPicker.randString(letters,10);
			exist = key_gen.add(genKey);
		}while(!exist);
		return genKey;
	}
	
	
	//@Test
	/*public void testDeleteMultiElement(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnBetween("id", 100, 5678).andColumnIsNotNull("nickname").andColumnEqualTo("email", "root33@hotmail.com");
		mc.or(mc.createCriteria().andColumnEqualTo("nickname", "怀玉"));
		mc.or(mc.createCriteria().andColumnEqualTo("nickname", "测试"));
		mc.setOrderByClause("nickname");
		mc.setPageNumber(1);
		mc.setPageSize(10);
		Page<User> users = this.userService.findModelPageByModelCriteria(mc);
		System.out.println(users.getItems().size());
		System.out.println(users.isFirstPage());
		System.out.println(users.isLastPage());
		System.out.println(users.getPageNumber());
		this.userService.deleteAll(users.getItems());
	}*/
	
	
	//@Test
	public void testEmptyModelCriteriaPageQuery(){
		ModelCriteria mc = new ModelCriteria();
		mc.setPageNumber(1);
		mc.setPageSize(10);
		
		Page<User> users = this.userService.findModelPageByModelCriteria(mc);
		System.out.println(users.getItems().size());
		System.out.println(users.isFirstPage());
		System.out.println(users.isLastPage());
		System.out.println(users.getPageNumber());
		
		
		TailPage<User> tailusers = this.userService.findModelTailPageByModelCriteria(mc);//findModelBysql(null, null, 1, 10);
		System.out.println(tailusers.getTotalItemsCount());
		System.out.println(tailusers.getItems().size());
		System.out.println(tailusers.isFirstPage());
		System.out.println(tailusers.isLastPage());
		System.out.println(tailusers.getPageNumber());
	}	
	//
	/*public void testPageJson(){
		TailPage<User> page =(TailPage<User>) this.userService.findTail(null, null, 1, 15);
		//Page<Map> pagemap = JsonHelper.filterPageData(page, false, "id","username","truename","email");
		//String jsonStr = JsonHelper.getPageData(pagemap, true);
		String jsonStr = JsonHelper.getPageJsonData(page,DateConvertType.TOLONG,false, "id","username","truename","email");
		System.out.println(jsonStr);
	}*/
	//@Test
	/*public void testFindUniqueModel(){
		String username = "木藤下雨";
		User user  = this.userService.findUserByNickname(username);
		//System.out.println(user.getSalt());
		System.out.println(user.getNickname());
	}*/
	
	//@Test
	/*public void testFindUniqueModelByNickname(){
		//String username = "edmond";
		User user  = this.userService.findUserByNickname("木藤下雨");//施凯文Kay");//findUniqueByProperty("username", username);
		//System.out.println(user.getSalt());
		System.out.println(user.getPassword());
	}*/
	//@Test

}
