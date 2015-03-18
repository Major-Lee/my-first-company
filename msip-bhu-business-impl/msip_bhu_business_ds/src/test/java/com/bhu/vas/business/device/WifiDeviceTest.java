package com.bhu.vas.business.device;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.Page;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;

public class WifiDeviceTest extends BaseTest{

	@Resource
	WifiDeviceService wifiDeviceService;

	@Test
	public void testInsert(){
		WifiDevice entity = new WifiDevice();
		entity.setId("1");
		entity.setOrig_swver("2015-03-11-18:27 Revision: 6855");
		wifiDeviceService.insert(entity);
	}
	
	//@Test
	public void buildAllData(){
		System.out.println(RandomData.intNumber(1, 9));
	}
	
	//@Test
	public void testBatchCreate(){
		String mac = "34:36:3b:d0:4b:ac";
		int count = 1000;
		List<WifiDevice> entitys = new ArrayList<WifiDevice>();
		for(int i=0;i<count;i++){
			WifiDevice entity = new WifiDevice();
			//,RandomPicker.randString(truenames,3),RandomPicker.randString(letters, 4),RandomPicker.randString(letters, 4)+"@"+RandomPicker.randString(letters, 2)+".net"
			//user.setEmail(RandomPicker.randString(letters, 4)+"@"+RandomPicker.randString(letters, 2)+".net");
			//user.setNickname(RandomPicker.randString(letters, 10));
			//user.setTruename(RandomPicker.randString(truenames,4));
			entity.setId(mac.concat(String.valueOf(i)));
			entitys.add(entity);
			try{
				wifiDeviceService.insert(entity);	
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		//wifiDeviceService.insertMulti(entitys);
	}	
	
	//@Test
	public void countAllRecords(){
		int all = wifiDeviceService.countByCommonCriteria(new CommonCriteria());
		System.out.println(all);
	}
	
	//@Test
	public void testIterAlltables(){
		ModelCriteria mc = new ModelCriteria();
		/*if(startuid != null){
			System.out.println(String.format("UserMigrateOp开始，startid[%s]", startuid));
			mc.createCriteria().andColumnGreaterThanOrEqualTo("id", Integer.parseInt(startuid));
		}else{*/
		System.out.println(String.format("MigrateOp开始，startid[%s]", "未指定"));
		mc.createCriteria().andSimpleCaulse(" 1=1 ");
		//}
		mc.setOrderByClause(" id asc ");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class,WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
		while(it.hasNext()){
			List<WifiDevice> entitys = it.next();
			for(WifiDevice entity:entitys){
				System.out.println(entity.getId());
			}
		}
	}
	
	//@Test
	public void testGetByIds(){
		List<String> wifiIds = new ArrayList<String>();
		wifiIds.add("34:36:3b:d0:4b:ac1");
		wifiIds.add("34:36:3b:d0:4b:ac2");
		List<WifiDevice> entitys = wifiDeviceService.findByIds(wifiIds);
		for(WifiDevice entity : entitys){
			System.out.println(entity.getId());
		}
		//readWriteUserCacheableService.getById(id)
	}
	
	//@Test
	/*public void testFindUniqueIdByProperty(){
		Integer userid = userService.findUniqueIdByProperty("username", "edmond11");
		System.out.print(userid);
	}*/
	
	//@Test
	public void testFindUniqueModelByProperty(){
		//User user = userService.findUserByEmail("sbsmdddddd@sv.net");//("username", "edmond11");
		//System.out.print(user);
	}
	
	//@Test
	public void testCacheDeleteById(){
		WifiDevice entity = this.wifiDeviceService.getById("34:36:3b:d0:4b:ac");
		Assert.assertNotNull(entity);
		this.wifiDeviceService.deleteById("34:36:3b:d0:4b:ac");
		entity = this.wifiDeviceService.getById("34:36:3b:d0:4b:ac");
		Assert.assertNull(entity);
	}
	
	//@Test
	/*public void testDeleteMulti(){
		List<Integer> userids = new ArrayList<Integer>();
		userids.add(2655);
		userids.add(2656);
		userids.add(2657);
		this.userService.deleteMulti(userids);
	}*/
	//@Test
	public void testGet(){
		this.wifiDeviceService.getById("34:36:3b:d0:4b:ac");
	}
	
	//@Test
	/*public void testIterateUser(){
		this.userService.iterateUser();
	}*/
	
	//@Test
	public void testModelCriteriaQuery(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		mc.setOrderByClause("id");
		mc.setPageNumber(1);
		mc.setPageSize(10);
		List<String> ids = this.wifiDeviceService.findIdsByModelCriteria(mc);
		System.out.println(ids.size());
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
	
	
	public void testModelCriteriaPageQuery(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		mc.setOrderByClause("id");
		mc.setPageNumber(1);
		mc.setPageSize(10);
		
		Page<WifiDevice> entitys = this.wifiDeviceService.findModelPageByModelCriteria(mc);
		System.out.println(entitys.getItems().size());
		System.out.println(entitys.isFirstPage());
		System.out.println(entitys.isLastPage());
		System.out.println(entitys.getPageNumber());
		
		
		TailPage<WifiDevice> tailentitys = this.wifiDeviceService.findModelTailPageByModelCriteria(mc);//findModelBysql(null, null, 1, 10);
		System.out.println(tailentitys.getTotalItemsCount());
		System.out.println(tailentitys.getItems().size());
		System.out.println(tailentitys.isFirstPage());
		System.out.println(tailentitys.isLastPage());
		System.out.println("pn:"+tailentitys.getPageNumber());
		for(WifiDevice entity:tailentitys){
			System.out.println(entity.getId());
			//System.out.println(user.getRoleIds());
		}
	}
	
	//@Test
	public void testEmptyModelCriteriaPageQuery(){
		ModelCriteria mc = new ModelCriteria();
		mc.setPageNumber(1);
		mc.setPageSize(10);
		
		Page<WifiDevice> entitys = this.wifiDeviceService.findModelPageByModelCriteria(mc);
		System.out.println(entitys.getItems().size());
		System.out.println(entitys.isFirstPage());
		System.out.println(entitys.isLastPage());
		System.out.println(entitys.getPageNumber());
		
		
		TailPage<WifiDevice> tailentitys = this.wifiDeviceService.findModelTailPageByModelCriteria(mc);//findModelBysql(null, null, 1, 10);
		System.out.println(tailentitys.getTotalItemsCount());
		System.out.println(tailentitys.getItems().size());
		System.out.println(tailentitys.isFirstPage());
		System.out.println(tailentitys.isLastPage());
		System.out.println("pn:"+tailentitys.getPageNumber());
		for(WifiDevice entity:tailentitys){
			System.out.println(entity.getId());
			//System.out.println(user.getRoleIds());
		}
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
