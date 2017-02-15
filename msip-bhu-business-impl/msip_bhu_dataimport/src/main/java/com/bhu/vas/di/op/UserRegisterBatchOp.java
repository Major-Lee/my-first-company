package com.bhu.vas.di.op;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.user.facade.UserFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.smartwork.msip.business.token.UserTokenDTO;
/**
 * @author Yetao
 * 命令格式参考UserRegisterOp说明.
 */
public class UserRegisterBatchOp {
	
	public static void main(String[] argv) {//throws ElasticsearchException, ESException, IOException, ParseException{
		if(argv.length < 1) return;
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		UserFacadeService userFacadeService = (UserFacadeService)ctx.getBean("userFacadeService");

		File file = new File(argv[0]);
        if (file.isFile() && file.exists()) { //判断文件是否存在
        	try{
		        InputStreamReader read = new InputStreamReader(new FileInputStream(file));//考虑到编码格式
		        BufferedReader bufferedReader = new BufferedReader(read);
		        String line = null;
		        while ((line = bufferedReader.readLine()) != null) {
		        	if(StringUtils.isEmpty(line))
		        		continue;
		        	String[] pm = line.split(" ");
		        	String oper = pm[0];// ADD REMOVE
		        	int id = Integer.parseInt(pm[1]);
		    		String acc = pm[2];
		    		String pwd = pm[3];
		    		String nick = pm[4];
		    		String ut = UserType.Normal.getSname();
		    		//int utype = UserType.Normal.getIndex();
		    		//UserType userType = UserType.getBySName(ut);
		    		if(pm.length > 5){
		    			ut = pm[5];
		    		}
		    		UserType userType = UserType.getBySName(ut);
		    		if("ADD".equals(oper)){
		    			Integer uid = UniqueFacadeService.fetchUidByMobileno(86,acc);
		    			if(uid != null){
		    				User byId = userService.getById(uid);
		    				if(byId != null){
		    					System.out.println(String.format("acc[%s] 已经被注册，请换个号码！", acc));
		    					continue;
		    				}else{
		    					UniqueFacadeService.removeByMobileno(86, acc);
		    					System.out.println(String.format("acc[%s] 记录被移除！", acc));
		    					continue;
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
								continue;
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
		    					continue;
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
		        }
        	}catch(Exception e){
        		e.printStackTrace();
        	}
	    }
		System.exit(0);
	}
}
