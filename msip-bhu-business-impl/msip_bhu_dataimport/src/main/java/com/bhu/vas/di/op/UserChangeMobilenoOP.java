package com.bhu.vas.di.op;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;




public class UserChangeMobilenoOP {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		if(argv.length != 2){
			System.out.println("param: oldmobile, newmobile");
			return;
		}

		String oldmobileno = argv[0];
		String newmobileno = argv[1];
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		UserFacadeService userFacadeService = (UserFacadeService)ctx.getBean("userFacadeService");
		
		User user = userFacadeService.getUserByMobileno(newmobileno);
		if(user != null){
			System.out.println(String.format("new mobileno[%s] has already been registered", newmobileno));
			System.exit(0);
			return;
		}
		user =  userFacadeService.getUserByMobileno(oldmobileno);
		if(user == null){
			System.out.println(String.format("no user can be found for mobileno[%s]", oldmobileno));
			System.exit(0);
			return;
		}

		user.setMobileno(newmobileno);
		userService.update(user);
		UniqueFacadeService.uniqueMobilenoChanged(user.getId(), user.getCountrycode(), newmobileno, oldmobileno);
		
		System.exit(0);
	}
}
