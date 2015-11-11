package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.user.dao.UserDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

import java.util.List;

//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class UserService extends AbstractCoreService<Integer,User, UserDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	//@Resource
	//private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(UserDao userDao) {
		super.setEntityDao(userDao);
	}
	
	/*@Override
	public User insert(User entity) {
		if(entity.getId() == null)
			sequenceService.onCreateSequenceKey(entity, false);
		return super.insert(entity);
	}*/
	
	private static final String Total_Users_Cached_Key = "Total_Users_Cached_Key";
	/**
	 * 统计所有用户记录
	 * 不可在生产环境调用 目前只用于dashboard
	 * @return
	 */
	public int countAllRecords(){
		Object obj = this.getEntityCache().get(Total_Users_Cached_Key);
		if(obj == null){
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse("1=1");
			int count = countByModelCriteria(mc);
			/*Integer[] array = ShardStratgyHelper.generateSimulatePrimeArray(User.class.getSimpleName());
			for(Integer sa:array){
				ModelCriteria mc = new ModelCriteria();
				mc.createCriteria().andSimpleCaulse(sa+"="+sa);
				mc.setBusinessValue4Spliter(sa);
				int count = countByModelCriteria(mc);
				System.out.println("~~~~~~~:"+count);
				total +=count;
			}*/
			if(count >0){
				this.getEntityCache().put(Total_Users_Cached_Key, new Integer(count));
			}
			return count;
		}else{
			return Integer.class.cast(obj).intValue();
		}
	}

	/**
	 * 检查是否有相同的公司名称 true:有 false:无
	 * @param org
	 * @return
	 */
	public boolean isExistsOrg(String org) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("org", org);
		List<Integer> ids = this.findIdsByCommonCriteria(mc);
		return !(ids == null || ids.isEmpty());

	}


//	public void deleteAndCount(){
//		//System.out.println(this.countByCommonCriteria(new CommonCriteria()));
////    	int ret = this.deleteById(100023);
//		User user = new User();
//		//user.setId(i+5);
//		user.setCountrycode(86);
//		user.setMobileno(String.format("1861%s2%s", RandomData.intNumber(100,999),RandomData.intNumber(100,999)));
//		user.setBirthday(String.format("%s-%s-%s", RandomData.intNumber(1969,2010),String.format("%02d", RandomData.intNumber(1,12)),String.format("%02d", RandomData.intNumber(1,28))));
//		user.setNick("dasddsadsadsa");
//		user.setSex(RandomData.flag()?"男":"女");
//		//user.setId(doGenKey());
//		user.setRegdevice(DeviceEnum.IPhone.getSname());
//		user.setRegip("192.168.1.6");
//		user.setLastlogindevice(DeviceEnum.IPhone.getSname());
//		user.setLastlogindevice_uuid(UUID.randomUUID().toString());
//		this.insert(user);
//    	//super.getEntityDao().getSqlSessionSlaverTemplate().clearCache();
//    	System.out.println(this.countByCommonCriteria(new CommonCriteria()));
//    	throw new RuntimeException();
//	}
	/*public List<User> findRandUsers(int pagesize){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIsNotNull("avatar").andColumnEqualTo("newbie", 0);
		//this.getEntityCache().p
		int count = countAllRecords();//this.countByModelCriteria(mc);
		if(count == 0) return Collections.emptyList();
		//int totalpage = PageHelper.getTotalPages(count, pagesize);
		//int randpage = 1;
		//if(totalpage <= 1) randpage=1;
		//else randpage = RandomData.intNumber(1, totalpage);
		mc.setOrderByClause(" email desc ");
		mc.setPageNumber(1);
		mc.setPageSize(pagesize);
		Integer[] array = ShardStratgyHelper.generateSimulatePrimeArray(User.class.getSimpleName());
		mc.setBusinessValue4Spliter(RandomPicker.pick(array));
		return this.findModelByModelCriteria(mc);
		//TailPage<User> page =this.findModelTailPageByModelCriteria(mc);//findTail(null, null, pageno, pagesize);
		//return page;
	}*/
}
