package com.bhu.vas.business.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.user.model.User;
import com.bhu.vas.business.sequence.service.SequenceService;
import com.bhu.vas.business.user.dao.UserDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class UserService extends AbstractCoreService<String,User, UserDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	private SequenceService sequenceService;
	
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
