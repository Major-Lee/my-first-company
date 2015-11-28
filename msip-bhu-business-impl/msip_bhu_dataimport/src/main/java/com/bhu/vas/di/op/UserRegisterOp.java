package com.bhu.vas.di.op;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
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
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 100027 13811561274 test 张盼
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 100025 18911372223 test 郭旭锋
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90000 18911370000 agent1234 代理商1
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90001 18911370001 agent1234 代理商2
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90002 18911370002 agent1234 代理商3
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90003 18911370003 agent1234 代理商4
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90010 18911370010 agent1234 仓储管理员1
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90011 18911370011 agent1234 仓储管理员2
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90012 18911370012 agent1234 仓储管理员3
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90020 18911370020 agent1234 财务1
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90021 18911370021 agent1234 财务2
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90022 18911370022 agent1234 财务3
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90023 18911370023 agent1234 财务4
 * 
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90030 18911370030 agent1234 销售1
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90031 18911370031 agent1234 销售2
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90032 18911370032 agent1234 销售3
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90033 18911370033 agent1234 销售4
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90034 18911370034 agent1234 销售5
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90035 18911370035 agent1234 销售6
 * ./startupbuilder_bhu_userregister.sh RemoveAndADD 90036 18911370036 agent1234 销售7
 * 
 */
public class UserRegisterOp {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		if(argv.length <1) return;
		String oper = argv[0];// ADD REMOVE
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		UserFacadeService userFacadeService = (UserFacadeService)ctx.getBean("userFacadeService");
		if(argv.length < 5) return;
		int id = Integer.parseInt(argv[1]);
		String acc = argv[2];
		String pwd = argv[3];
		String nick = argv[4];
		
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
			UniqueFacadeService.uniqueMobilenoRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			// token validate code
			UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			System.out.println(String.format("userReg[id:%s mobileno:%s nick:%s] successfully!", user.getId(),user.getMobileno(),user.getNick()));
		} else if ("Remove".equals(oper)) {
			userFacadeService.clearUsersMarkByUid(id);
			userFacadeService.clearUsersMarkByMobileno(86, acc);
		}else if("RemoveAndADD".equals(oper)){
			{//empty user mark from system
				userFacadeService.clearUsersMarkByUid(id);
				userFacadeService.clearUsersMarkByMobileno(86, acc);
				/*userService.deleteById(id);
				UniqueFacadeService.removeByMobileno(86, acc);
				List<String> bindedMacs = new ArrayList<String>();
				List<UserDevice> bindedDevices = userDeviceService.fetchBindDevicesWithLimit(id, 100);
				if(bindedDevices!= null && !bindedDevices.isEmpty()){
					for(UserDevice device:bindedDevices){
						bindedMacs.add(device.getMac());
					}
				}
				if(!bindedMacs.isEmpty()){
					WifiDeviceMobilePresentStringService.getInstance().destoryMobilePresent(bindedMacs);
				}
				userDeviceService.clearBindedDevices(id);
				userMobileDeviceService.deleteById(id);
				userMobileDeviceStateService.deleteById(id);*/
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
			UniqueFacadeService.uniqueMobilenoRegister(user.getId(), user.getCountrycode(), user.getMobileno());
			// token validate code
			UserToken uToken = userTokenService.generateUserAccessToken(user.getId().intValue(), true, true);
			{//write header to response header
				//BusinessWebHelper.setCustomizeHeader(response, uToken);
				IegalTokenHashService.getInstance().userTokenRegister(user.getId().intValue(), uToken.getAccess_token());
			}
			System.out.println(String.format("userReg[id:%s mobileno:%s nick:%s] successfully!", user.getId(),user.getMobileno(),user.getNick()));
		}
	}
}
