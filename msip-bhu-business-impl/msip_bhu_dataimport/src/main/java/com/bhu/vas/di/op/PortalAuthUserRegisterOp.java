package com.bhu.vas.di.op;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserIdentityAuth;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
public class PortalAuthUserRegisterOp {
	
	public static void main(String[] argv) {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserIdentityAuthService userIdentityAuthService = (UserIdentityAuthService)ctx.getBean("userIdentityAuthService");
		UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" uid is null ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc.setOrderByClause("id desc");
		mc.setPageNumber(1);
		mc.setPageSize(200);
    	EntityIterator<String, UserIdentityAuth> itit = new KeyBasedEntityBatchIterator<String, UserIdentityAuth>(String.class, UserIdentityAuth.class, userIdentityAuthService.getEntityDao(), mc);
		while(itit.hasNext()){
			List<UserIdentityAuth> list = itit.next();
			for(UserIdentityAuth auth:list){
				System.out.println(String.format("handle hdmac[%s], mobileno[%s]", auth.getId(), auth.getMobileno()));
				Integer uid = UniqueFacadeService.fetchUidByMobileno(auth.getCountrycode(), auth.getMobileno());
				if(uid != null){
					auth.setUid(uid);
				} else {
					System.out.println(String.format("creating new user for mobileno[%s]", auth.getMobileno()));
					User	user = new User();
					user.setCountrycode(auth.getCountrycode());
					user.setMobileno(auth.getMobileno());
					user.setRegip("127.0.0.1");
	//				user.setLastlogindevice_uuid();
					//标记用户注册时使用的设备，缺省为DeviceEnum.Android
	//				user.setRegdevice(device);
					//标记用户最后登录设备，缺省为DeviceEnum.PC
	//				user.setLastlogindevice(device);
					user = userService.insert(user);
					UniqueFacadeService.uniqueMobilenoRegister(user.getId(), user.getCountrycode(), user.getMobileno());
					// token validate code
					UserTokenDTO uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
					{//write header to response header
						//BusinessWebHelper.setCustomizeHeader(response, uToken);
						IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
					}
					auth.setUid(user.getId());
					System.out.println(String.format("mobileno[%s], new uid[%s]", auth.getMobileno(), user.getId()));
				}
				userIdentityAuthService.update(auth);
			}
		}

		System.exit(0);
	}
}
