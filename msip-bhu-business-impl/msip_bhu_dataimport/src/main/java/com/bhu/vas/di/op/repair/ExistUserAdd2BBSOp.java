package com.bhu.vas.di.op.repair;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.helper.ExchangeBBSHelper;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class ExistUserAdd2BBSOp {
	public static void main(String[] argv) {
		ApplicationContext actx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		
		UserService userService = (UserService)actx.getBean("userService");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria()/*.andColumnEqualTo("online", 1)*/.andSimpleCaulse(" 1=1 ");//.andColumnIsNotNull("lat").andColumnIsNotNull("lon");//.andColumnEqualTo("online", 1);
		mc.setPageNumber(1);
		mc.setPageSize(200);
		EntityIterator<Integer, User> it = new KeyBasedEntityBatchIterator<Integer,User>(Integer.class
				,User.class, userService.getEntityDao(), mc);
		while(it.hasNext()){
			List<User> users = it.next();
			for(User user:users){
				int addret = ExchangeBBSHelper.userAdd2BBS(user.getMobileno());
				if(addret == 1){
					System.out.println(String.format("userRegister2BBS successful mobileno[%s] pwd[%s]", user.getMobileno(),ExchangeBBSHelper.bbsPwdGen(user.getMobileno())));
				}else{
					System.out.println("userRegister2BBS error:"+addret);
				}
			}
		}
		
	}

}
