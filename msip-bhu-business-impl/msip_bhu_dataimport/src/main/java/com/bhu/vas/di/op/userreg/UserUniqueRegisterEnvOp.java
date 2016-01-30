package com.bhu.vas.di.op.userreg;

import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 用户unique 
 * 		手机号重写入redis
 * 		昵称重写入redis
 * @author Edmond
 *
 */
public class UserUniqueRegisterEnvOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserUniqueRegisterEnvOp.class);
		ctx.start();
		//ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserService userService = (UserService)ctx.getBean("userService");

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc.setOrderByClause("id desc");
    	mc.setPageNumber(1);
    	mc.setPageSize(1000);
    	EntityIterator<Integer, User> itit = new KeyBasedEntityBatchIterator<Integer, User>(Integer.class, User.class, userService.getEntityDao(), mc);
		while(itit.hasNext()){
			List<User> list = itit.next();
			for(User user:list){
				System.out.println(String.format("user[%s] cc[%s] mobileno[%s] nick[%s]", user.getId(), user.getCountrycode(), user.getMobileno(),user.getNick()));
				UniqueFacadeService.uniqueMobilenoRegister(user.getId(), user.getCountrycode(), user.getMobileno());
				if(StringUtils.isNotEmpty(user.getNick()))
					UniqueFacadeService.uniqueNickRegister(user.getId(), user.getNick());
			}
		}
		System.out.println("数据增量导入，共耗时"+((System.currentTimeMillis()-t0)/1000)+"s");
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
