package com.bhu.vas.di.op;

import java.io.IOException;
import java.text.ParseException;

import org.elasticsearch.ElasticsearchException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.smartwork.msip.es.exception.ESException;
/**
 * @author Edmond Lee
 * ./startupbuilder_bhu_userregister.sh ADD 1 13901076750 bhunetworks xudd
 * ./startupbuilder_bhu_userregister.sh ADD 1 13911716572 bhunetworks 杨涛
 */
public class UserRegisterOp {
	
	public static void main(String[] argv) throws ElasticsearchException, ESException, IOException, ParseException{
		if(argv.length <1) return;
		String oper = argv[0];// ADD REMOVE
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		
		if("ADD".equals(oper)){
			if(argv.length < 5) return;
			int id = Integer.parseInt(argv[1]);
			String acc = argv[2];
			String pwd = argv[3];
			String nick = argv[4];
			Integer uid = UniqueFacadeService.fetchUidByMobileno(86,acc);
			if(uid != null){
				User byId = userService.getById(uid);
				if(byId != null){
					System.out.println(String.format("acc[%s] 已经被注册，请换个号码！", acc));
					return;
				}else{
					UniqueFacadeService.removeByMobileno(86, acc);
					System.out.println(String.format("acc[%s] 记录被移除！", acc));
					return;
				}
			}
			User user = new User();
			user.setId(id);
			user.setCountrycode(86);
			user.setMobileno(acc);
			user.setPlainpwd(pwd);
			user.setRegip("127.0.0.1");
			user.setNick(nick);
			//标记用户注册时使用的设备，缺省为DeviceEnum.Android
			user.setRegdevice(DeviceEnum.PC.getSname());
			//标记用户最后登录设备，缺省为DeviceEnum.PC
			user.setLastlogindevice(DeviceEnum.PC.getSname());
			user = userService.insert(user);
			//user.setId(id);
			System.out.println("uid:"+user.getId());
			UniqueFacadeService.uniqueRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			// token validate code
			UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			System.out.println(String.format("userReg[id:%s mobileno:%s nick:%s] successfully!", user.getId(),user.getMobileno(),user.getNick()));
		}else{
			System.out.println(String.format("unimplements"));
		}
		
	}
}
