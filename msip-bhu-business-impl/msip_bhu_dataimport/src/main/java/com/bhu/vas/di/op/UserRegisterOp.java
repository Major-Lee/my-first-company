package com.bhu.vas.di.op;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.smartwork.msip.business.token.UserTokenDTO;
import com.smartwork.msip.jdo.ResponseErrorCode;
/**
 * @author Edmond Lee
 * ./startupbuilder_bhu_userregister.sh ADD 1 13901076750 bhunetworks 徐冬冬
 * ./startupbuilder_bhu_userregister.sh ADD 2 13911716572 bhunetworks 杨涛
 * ./startupbuilder_bhu_userregister.sh ADD 3 18612272825 bhunetworks 李文华
 * ./startupbuilder_bhu_userregister.sh ADD 4 18001161616 bhunetworks 姚永新
 * ./startupbuilder_bhu_userregister.sh ADD 5 18601267579 bhunetworks 罗征
 * ./startupbuilder_bhu_userregister.sh ADD 6 18601002857 bhunetworks 熊出没
 * ./startupbuilder_bhu_userregister.sh ADD 7 13810048517 bhunetworks 唐子超
 * ./startupbuilder_bhu_userregister.sh ADD 8 13691091975 test 高凯
 * 
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 100027 13811561274 test 张盼
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 100025 18911372223 test 郭旭锋
 * 
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90010 13910222600 222600 黄运强

 * 
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90020 18911370020 agent1234 财务1
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90021 13910139032 139032 佟艳
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90022 13501261329 sjr1022 孙继茹
 * 
 * 
 销售
（1）崔建平  cuijp@bhuwifi.com 13720492962
（2）高云凌  gaoyl@bhuwifi.com 18601640418
（3）魏晓川  weixc@bhuwifi.com 18911666698
（4）孙荣辉  sunrh@bhuwifi.com 18802932533
   ./startupbuilder_bhu_userregister.sh RemoveAndADD 90029 18911370030 370030 默认销售
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90030 13720492962 492962 崔建平
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90031 18601640418 640418 高云凌
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90032 18911666698 666698 魏晓川
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90033 18802932533 932533 孙荣辉
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90034 18911370034 agent1234 销售5
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90035 18911370035 agent1234 销售6
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90036 18911370036 agent1234 销售7
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90050 18911370000 370000 默认分销商
 * 
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 9 13611361274 361274 小武
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 10 18655580588 580588 宋顺利
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 11 18611765239 BHU2016 然然
 * 
 * 
 */
public class UserRegisterOp {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		if(argv.length < 5) return;
		String oper = argv[0];// ADD REMOVE
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		UserFacadeService userFacadeService = (UserFacadeService)ctx.getBean("userFacadeService");

		
		int id = Integer.parseInt(argv[1]);
		String acc = argv[2];
		String pwd = argv[3];
		String nick = argv[4];
		String ut = UserType.Normal.getSname();
		//int utype = UserType.Normal.getIndex();
		//UserType userType = UserType.getBySName(ut);
		if(argv.length > 5){
			ut = argv[5];
		}
		UserType userType = UserType.getBySName(ut);
		if("ADD".equals(oper)){
			/*if(argv.length < 5) return;
			int id = Integer.parseInt(argv[1]);
			String acc = argv[2];
			String pwd = argv[3];
			String nick = argv[4];*/
			Integer uid = UniqueFacadeService.fetchUidByMobileno(86,acc);
			if(uid != null){
				User byId = userService.getById(uid);
				if(byId != null){
					System.out.println(String.format("acc[%s] 已经被注册，请换个号码！", acc));
					System.exit(0);
					return;
				}else{
					UniqueFacadeService.removeByMobileno(86, acc);
					System.out.println(String.format("acc[%s] 记录被移除！", acc));
					System.exit(0);
					return;
				}
			}
			User user = new User();
			user.setId(id);
			user.setCountrycode(86);
			user.setMobileno(acc);
			user.setPlainpwd(pwd);
			user.setRegip("127.0.0.1");
			if(StringUtils.isNotEmpty(nick)){
				//判定nick是否已经存在
				if(UniqueFacadeService.checkNickExist(nick)){
					System.out.println(String.format("选用的nick [%s]已经被别人使用了，请换个nick！", nick));
					System.exit(0);
					return;
				}else{
					user.setNick(nick);
				}
			}
			//标记用户注册时使用的设备，缺省为DeviceEnum.Android
			user.setRegdevice(DeviceEnum.PC.getSname());
			//标记用户最后登录设备，缺省为DeviceEnum.PC
			user.setLastlogindevice(DeviceEnum.PC.getSname());
			user.setUtype(userType.getIndex());
			user = userService.insert(user);
			//user.setId(id);
			System.out.println("uid:"+user.getId());
			UniqueFacadeService.uniqueMobilenoRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			if(StringUtils.isNotEmpty(nick)){
				UniqueFacadeService.uniqueNickRegister(user.getId(), nick);
			}
			// token validate code
			UserTokenDTO uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
			}
			System.out.println(String.format("userReg[id:%s mobileno:%s nick:%s] successfully!", user.getId(),user.getMobileno(),user.getNick()));
		} else if ("Remove".equals(oper)) {
			userFacadeService.clearUsersMarkByUid(id);
			userFacadeService.clearUsersMarkByMobileno(86, acc);
		}else if("RemoveAndADD".equals(oper)){
			if(StringUtils.isNotEmpty(nick)){
				//判定nick是否已经存在
				if(UniqueFacadeService.checkNickExist(nick)){
					System.out.println(String.format("选用的nick [%s]已经被别人使用了，请换个nick！", nick));
					System.exit(0);
					return;
				}
			}
			{//empty user mark from system
				userFacadeService.clearUsersMarkByUid(id);
				userFacadeService.clearUsersMarkByMobileno(86, acc);
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
			user.setUtype(userType.getIndex());
			user = userService.insert(user);
			//user.setId(id);
			System.out.println("uid:"+user.getId());
			UniqueFacadeService.uniqueMobilenoRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			if(StringUtils.isNotEmpty(nick)){
				UniqueFacadeService.uniqueNickRegister(user.getId(), nick);
			}
			// token validate code
			UserTokenDTO uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAtoken());
			}
			System.out.println(String.format("RemoveAndADD[id:%s mobileno:%s nick:%s] successfully!", user.getId(),user.getMobileno(),user.getNick()));
		}
		System.exit(0);
	}
}
