package com.bhu.vas.business.user;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.model.UserOAuthState;
import com.bhu.vas.api.rpc.user.model.pk.UserOAuthStatePK;
import com.bhu.vas.business.ds.user.facade.UserOAuthFacadeService;
import com.smartwork.msip.cores.helper.AssertHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
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
public class UserOAuthServiceTest extends BaseTest{

	long unit_test_start = 0;
	long unit_test_end = 0;
	long batch_insert_cost_time = 0;
	long batch_total_cost_time = 0;
	int batch_create_size = 100;
	@Resource
	private UserOAuthFacadeService userOAuthFacadeService;
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
    
    @Test
	public void test001BatchCreateUserOAuth(){
    	System.out.println(Integer.MAX_VALUE);
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		userOAuthFacadeService.getUserOAuthStateService().deleteByModelCriteria(mc);//.deleteByCommonCriteria(mc);
    	unit_test_start = System.currentTimeMillis();
		//int count = 10;
		for(int i=1;i<=batch_create_size;i++){
			userOAuthFacadeService.createOrUpdateIdentifies(
					i,
					RandomPicker.pick(BusinessEnumType.OAuthType.values()).getType(),
					String.format("1861%s2%s", RandomData.intNumber(100,999),RandomData.intNumber(100,999)),
					RandomPicker.randString(letters, 5),
					RandomPicker.randString(letters, 10));
		}
		batch_insert_cost_time = System.currentTimeMillis()-unit_test_start;
		//userService.insertMulti(users);
	}	
    
    @Test
    public void test002OAuthExistAndCached(){
    	UserOAuthStatePK pk = new UserOAuthStatePK(1,BusinessEnumType.OAuthType.Weichat.getType());
    	UserOAuthState random_user = userOAuthFacadeService.getUserOAuthStateService().getById(pk);
    	AssertHelper.notNull(random_user);
    	
    	UserOAuthStateDTO dto = random_user.getInnerModel();
    	userOAuthFacadeService.getUserOAuthStateService().getById(pk);
    	userOAuthFacadeService.getUserOAuthStateService().getById(pk);
    }

    /*@Test
    public void test003CountAllRecords(){
		int all = userService.countAllRecords();
		AssertHelper.isTrue(all == batch_create_size);
	}*/
   
    @Test
	public void test004ByModelCriteriaEqualCondition(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("identify", "weichat");
		List<UserOAuthState> result = userOAuthFacadeService.getUserOAuthStateService().findModelByModelCriteria(mc);
		AssertHelper.isTrue(!result.isEmpty());
		AssertHelper.isTrue(result.size() == batch_create_size);
	}
    
    @Test
   	public void test005ByModelCriteriaLikeCondition(){
   		ModelCriteria mc = new ModelCriteria();
   		mc.createCriteria().andColumnLike("identify", "weichat%");
   		List<UserOAuthState> result = userOAuthFacadeService.getUserOAuthStateService().findModelByModelCriteria(mc);
   		AssertHelper.isTrue(!result.isEmpty());
   		
   		System.out.println(result.size());
   	}
     
    @Test
	public void test006IterAlltables(){
    	int total  = 0;
		ModelCriteria mc = new ModelCriteria();
		System.out.println(String.format("UserIterOp开始，startid[%s]", "未指定"));
		mc.createCriteria().andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause(" uid asc ");
    	mc.setPageNumber(1);
    	mc.setPageSize(20);
		EntityIterator<UserOAuthStatePK, UserOAuthState> it = new KeyBasedEntityBatchIterator<UserOAuthStatePK,UserOAuthState>(UserOAuthStatePK.class,UserOAuthState.class, userOAuthFacadeService.getUserOAuthStateService().getEntityDao(), mc);
		while(it.hasNext()){
			List<UserOAuthState> users = it.next();
			for(UserOAuthState state:users){
				System.out.println(state.getId().toString());
			}
			total += users.size();
		}
		AssertHelper.isTrue(total == batch_create_size);
	}
    /*
    @Test
  	public void test007FindByIds(){
    	List<User> result = userService.findByIds(new ArrayList<Integer>(key_gen));
    	AssertHelper.isTrue(!result.isEmpty());
   		AssertHelper.isTrue(result.size() == batch_create_size);
  	}
    
    @Test
  	public void testFindByIdsWhenNotExist(){
    	key_gen.add(10000);
    	key_gen.add(10001);
    	key_gen.add(10002);
    	
    	List<User> result = userService.findByIds(new ArrayList<Integer>(key_gen), true, true);
    	AssertHelper.isTrue(!result.isEmpty());
   		AssertHelper.isTrue(result.size() == batch_create_size+3);
   		
   		key_gen.remove(10000);
    	key_gen.remove(10001);
    	key_gen.remove(10002);
  	}
    
    @Test
    public void testFindUniqueIdByProperty(){
    	Integer randon_key = RandomPicker.pick(key_gen);
    	Integer userid = userService.findUniqueIdByProperty("id", randon_key);
		AssertHelper.isTrue(randon_key.equals(userid));
	}
    
    @Test
    public void test008ModelCriteriaPageQuery(){
    	
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
    public void test009ModelCriteriaPageQuery4Offset(){
    	
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("regip", "192.168.1.6");
		mc.setOrderByClause("id");
		mc.setStart(1);//.setPageNumber(1);
		mc.setSize(25);
		
		
		List<User> users = this.userService.findModelByModelCriteria(mc);
		System.out.println("!!!!!!!!!!!!!!"+users.size());
		AssertHelper.isTrue(users.size() == 25);
		
	}
    
    @Test
    public void test010ModelCriteriaWithComplexPageQuery(){
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
    public void test011UpdateUser(){
    	Integer randon_key = RandomPicker.pick(key_gen);
    	User random_user = userService.getById(randon_key);
    	AssertHelper.notNull(random_user);
    	String oldnick = random_user.getNick();
    	//int old_point = random_user.getScore();
    	//random_user.setScore(old_point+200);
    	random_user.setNick(random_user.getNick()+" g");
    	userService.update(random_user);
    	
    	random_user = userService.getById(randon_key);
    	AssertHelper.isTrue(random_user.getNick().equals(oldnick+" g"));
    }
    
    @Test
    public void test012RemoveUser(){
    	List<Integer> findAllIds = userService.findAllIds();
    	System.out.println("~~~~~~~~~~~~"+findAllIds.size());
    	Integer randon_key = RandomPicker.pick(key_gen);
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
    public void test013RemoveMultiUsers(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("regip", "192.168.1.6");
		mc.or(mc.createCriteria().andColumnEqualTo("id", "怀玉"));
		mc.or(mc.createCriteria().andColumnEqualTo("id", "测试"));
		mc.setOrderByClause("id");
		mc.setPageNumber(1);
		mc.setPageSize(10);
		Page<User> users = this.userService.findModelPageByModelCriteria(mc);
		System.out.println("123123123``````````");
		System.out.println(users.getItems().size());
		System.out.println(users.isFirstPage());
		System.out.println(users.isLastPage());
		System.out.println(users.getPageNumber());
		this.userService.deleteAll(users.getItems());
		
		List<Integer> findAllIds = userService.findAllIds();
    	AssertHelper.notEmpty(findAllIds);
    	AssertHelper.isTrue(findAllIds.size() == batch_create_size-1-users.getItems().size());
	}
    
    //@Test
	public void test014EmptyUser(){
		unit_test_end = System.currentTimeMillis();
    	System.out.println(String.format("Unit test batch insert cost:[%s]s", batch_insert_cost_time/1000));
    	System.out.println(String.format("Unit test cost:[%s]s", (unit_test_end-unit_test_start)/1000));
		
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		userService.deleteByCommonCriteria(mc);
	}
    
    private static void doGenKey(Integer uid){
    	key_gen.add(uid);
		String genKey = null;
		boolean exist = true;
		do{
			genKey = RandomPicker.randString(letters,10);
			exist = key_gen.add(genKey);
		}while(!exist);
		return genKey;
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
	public void testDeleteMultiElement(){
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
	}
	
	
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
	}	*/

}
