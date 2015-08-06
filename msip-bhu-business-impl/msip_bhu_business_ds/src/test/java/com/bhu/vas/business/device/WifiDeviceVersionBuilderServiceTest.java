package com.bhu.vas.business.device;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionBuilder;
import com.bhu.vas.api.rpc.user.dto.UpgradeDTO;
import com.bhu.vas.business.ds.device.facade.DeviceUpgradeFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceVersionBuilderService;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.localunit.BaseTest;

/**
 * 主键索引
 * 其他字段不索引的前提下
 * @author Edmond
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WifiDeviceVersionBuilderServiceTest extends BaseTest{

	long unit_test_start = 0;
	long unit_test_end = 0;
	long batch_insert_cost_time = 0;
	long batch_total_cost_time = 0;
	
	
	int batch_create_size = 100;
	@Resource
	WifiDeviceVersionBuilderService wifiDeviceVersionBuilderService;
	
	@Resource
	DeviceUpgradeFacadeService deviceUpgradeFacadeService;
	
	@Resource
	WifiDeviceService wifiDeviceService;
	
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	private static Set<Integer> key_gen = new HashSet<Integer>();
	@BeforeClass
    public static void setUp() throws Exception {
		
		//System.out.println("111111111");
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
    public void test001BatchInit(){
    	WifiDeviceVersionBuilder builder_gray = new WifiDeviceVersionBuilder();
		builder_gray.setId(WifiDeviceVersionBuilder.VersionBuilder_Normal);
		builder_gray.setD_firmware_name("AP106P06V1.2.15Build8064");
		builder_gray.setD_versions("V1.2.15");
		builder_gray.setD_builderno("8064");
		builder_gray.setFirmware_upgrade_url("http://7xk1fm.dl1.z0.glb.clouddn.com/device/build/AP106P06V1.2.15Build8064");
		builder_gray.setForce_device_update(true);
		builder_gray.setForce_ios_app_update(false);
		builder_gray.setMin_ios_version("1.0.8.3");
		builder_gray.setForce_adr_app_update(false);
		builder_gray.setMin_adr_version("1.3.8.3");
		wifiDeviceVersionBuilderService.insert(builder_gray);
		
		WifiDeviceVersionBuilder builder_normal = new WifiDeviceVersionBuilder();
		builder_normal.setId(WifiDeviceVersionBuilder.VersionBuilder_FirstGray);
		builder_normal.setD_firmware_name("AP106P06V1.2.15Build8084");
		builder_normal.setD_versions("V1.2.15");
		builder_normal.setD_builderno("8084");
		builder_normal.setFirmware_upgrade_url("http://7xk1fm.dl1.z0.glb.clouddn.com/device/build/AP106P06V1.2.15Build8084");
		builder_normal.setForce_device_update(true);
		builder_normal.setForce_ios_app_update(false);
		builder_normal.setMin_ios_version("1.0.8.3");
		builder_normal.setForce_adr_app_update(false);
		builder_normal.setMin_adr_version("1.3.8.3");
		wifiDeviceVersionBuilderService.insert(builder_normal);
    }
    
    //public void test
    
    @Test
    public void test005DeviceVersionCheck(){
    	String mac1 = "84:82:f4:19:01:0c";
    	WifiDevice wifiDevice1 = wifiDeviceService.getById(mac1);
    	//UpgradeDTO checkDeviceUpgrade = deviceUpgradeFacadeService.checkDeviceUpgrade(mac, wifiDevice);
    	//System.out.println(mac+"   "+checkDeviceUpgrade);
    	UpgradeDTO checkDeviceUpgrade = deviceUpgradeFacadeService.checkDeviceUpgrade(mac1, wifiDevice1,"R","1.3.8.4");
    	
    	String mac2 = "62:68:75:ff:10:80";
    	WifiDevice wifiDevice2 = wifiDeviceService.getById(mac2);
    	checkDeviceUpgrade = deviceUpgradeFacadeService.checkDeviceUpgrade(mac2, wifiDevice2,"O","1.0.8.2");
    	/*boolean ret1 = wifiDeviceVersionBuilderService.deviceVersionUpdateCheck(true,"AP106P06V1.2.15Build8038");
    	if(ret1 ){
			System.out.println("AP106P06V1.2.15Build8038 device need force Updated" );
		}else{
			System.out.println("AP106P06V1.2.15Build8038 device no need force Updated" );
		}
    	boolean ret2 = wifiDeviceVersionBuilderService.deviceVersionUpdateCheck(false,"AP106P06V1.2.15Build8038");
    	if(ret2 ){
			System.out.println("AP106P06V1.2.15Build8038 device need force Updated" );
		}else{
			System.out.println("AP106P06V1.2.15Build8038 device no need force Updated" );
		}*/
    }
    
    //@Test
    public void test101EmptyData(){
		CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		wifiDeviceVersionBuilderService.deleteByCommonCriteria(mc);
    }
/*    //@Test
	public void test001BatchCreateUser(){
    	System.out.println(Integer.MAX_VALUE);
    	CommonCriteria mc = new CommonCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		userService.deleteByCommonCriteria(mc);
    	unit_test_start = System.currentTimeMillis();
		//int count = 10;
		for(int i=0;i<batch_create_size;i++){
			User user = new User();
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
			doGenKey(user.getId());
		}
		batch_insert_cost_time = System.currentTimeMillis()-unit_test_start;
		//userService.insertMulti(users);
	}	
    
    @Test
    public void test002UdidExistAndCached(){
    	Integer randon_key = RandomPicker.pick(key_gen);
    	//System.out.println(randon_key);
    	User random_user = userService.getById(randon_key);
    	AssertHelper.notNull(random_user);
    	userService.getById(randon_key);
    	userService.getById(randon_key);
    }

    @Test
    public void test003CountAllRecords(){
		int all = userService.countAllRecords();
		AssertHelper.isTrue(all == batch_create_size);
	}
    
    @Test
	public void test004ByModelCriteriaEqualCondition(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("regip", "192.168.1.6");
		List<User> result = userService.findModelByModelCriteria(mc);
		AssertHelper.isTrue(!result.isEmpty());
		AssertHelper.isTrue(result.size() == batch_create_size);
	}
    
    @Test
   	public void test005ByModelCriteriaLikeCondition(){
   		ModelCriteria mc = new ModelCriteria();
   		mc.createCriteria().andColumnLike("regip", "192.168%");
   		List<User> result = userService.findModelByModelCriteria(mc);
   		AssertHelper.isTrue(!result.isEmpty());
   		AssertHelper.isTrue(result.size() == batch_create_size);
   	}
    @Test
	public void test006IterAlltables(){
    	int total  = 0;
		ModelCriteria mc = new ModelCriteria();
		System.out.println(String.format("UserIterOp开始，startid[%s]", "未指定"));
		mc.createCriteria().andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause(" id asc ");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<Integer, User> it = new KeyBasedEntityBatchIterator<Integer,User>(Integer.class,User.class, userService.getEntityDao(), mc);
		while(it.hasNext()){
			List<User> users = it.next();
			total += users.size();
		}
		AssertHelper.isTrue(total == batch_create_size);
	}
    
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
	}*/	

}
