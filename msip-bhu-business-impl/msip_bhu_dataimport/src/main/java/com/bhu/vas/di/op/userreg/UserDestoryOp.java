package com.bhu.vas.di.op.userreg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * 销毁用户数据
 * 1：删除用户的设备device绑定信息数据
 * 2：删除用户的设备handset关联信息
 * 3：用户相关所有设备解绑
 * 4：TBD
 * 7：删除用户token数据
 * 8：删除用户redis数据（token，mobileno, nick）
 * 9：删除用户扩展数据
 * 10：删除用户本体数据
 * @author Edmond Lee 
 * 待实现
 */
public class UserDestoryOp {
	//被销毁用户的uids
	public static List<Integer> destory_uids = new ArrayList<Integer>();
	
	public static int success_count = 0;//处理成功的用户数量
	public static List<String> destory_users_step = new ArrayList<String>();
	
	public static void init() {
		System.out.println("destory user start init");
//		Integer[] destory_uids_array = new Integer[] { 200047, 200048, 200051,
//				200077, 200078, 200081, 200089, 200090, 200091, 200092, 200093,
//				200094, 200095, 200096, 200098, 200099, 200100, 200101, 200103,
//				200111, 200112, 200113, 200117, 200121, 200122, 200123, 200124,
//				200126, 200127, 200129, 200130, 200131, 200133, 200134, 200135,
//				200136, 200141, 200142, 200152, 200171, 200177, 200179, 200180,
//				200302, 200303, 200361, 200380, 200452, 200501, 200560, 200577,
//				200593 };
		
		//Integer[] destory_uids_array = new Integer[] {200800};
		int i = 200660;
		for(;i<200725;i++){
			destory_uids.add(i);
		}
		
		//destory_uids = ArrayHelper.toList(destory_uids_array);
		System.out.println("destory user total : " + destory_uids.size() + " show : " + destory_uids);
		System.out.println("destory user end init");
		
		if(destory_uids != null && !destory_uids.isEmpty()){
			doDestory();
		}
	}
	
	public static void main(String[] argv) throws ElasticsearchException, IOException{
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:/com/whisper/di/business/dataimport/dataImportCtx.xml");

/*		userService = (UserService)ctx.getBean("userService");
		userIndexService = (UserIndexService)ctx.getBean("userIndexService");
		userFrdRelationService = (UserFrdRelationService)ctx.getBean("userFrdRelationService");
		userDeviceStateService = (UserDeviceStateService)ctx.getBean("userDeviceStateService");
		userDeviceService = (UserDeviceService)ctx.getBean("userDeviceService");
		userAvatarStateService = (UserAvatarStateService)ctx.getBean("userAvatarStateService");
		userExtensionService = (UserExtensionService)ctx.getBean("userExtensionService");
		userLoginStateService = (UserLoginStateService)ctx.getBean("userLoginStateService");
		userLoginIpStateService = (UserLoginIpStateService)ctx.getBean("userLoginIpStateService");
		userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		userAddressbookStateService = (UserAddressbookStateService)ctx.getBean("userAddressbookStateService");*/
		
		long t0 = System.currentTimeMillis();
		System.out.println("开始处理用户的销毁");
		init();
		System.out.println("用户数据销毁执行完毕，总耗时"+((System.currentTimeMillis()-t0)/1000)+"s 共处理用户的数量:" +  success_count);
		
		//printStepStrings();
		
		System.exit(1);
	}
	
	/**
	 * 处理用户的销毁步骤
	 * @param user
	 */
	public static void doDestory(){
		for(Integer uid : destory_uids){
			/*int[] user_step_success_static = new int[10];
			try{
				if(destoryUserFrdData(uid)) user_step_success_static[0] = 1;
				if(destoryUserDeviceData(uid)) user_step_success_static[1] = 1;
				if(destoryUserAvatarStateData(uid)) user_step_success_static[2] = 1;
				if(destoryUserExtensionData(uid)) user_step_success_static[3] = 1;
				if(destoryUserLoginStateData(uid)) user_step_success_static[4] = 1;
				if(destoryUserIndexData(uid)) user_step_success_static[5] = 1;
				if(destoryUserTokenData(uid)) user_step_success_static[6] = 1;
				if(destoryUserRedisData(uid)) user_step_success_static[7] = 1;
				if(destoryUserAddressbookData(uid)) user_step_success_static[8] = 1;
				if(destoryUserEntityData(uid)) user_step_success_static[9] = 1;
				success_count++;
			}catch(Exception ex){
				ex.printStackTrace();
			}
			generateUserStepString(uid, user_step_success_static);*/
		}
	}
	
}
