package com.bhu.vas.di.op.userreg;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.CommonCriteria;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 用户access token 清除
 * 1、clear redis certain user token
 * 2、clear db    certain user token
 * @author Edmond
 *
 */
public class UserRegTokenInvalidOp {
	
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		if(argv == null || argv.length == 0 || argv.length >1) {
			System.out.println("need argv [ALL or userids]");
			return;
		}
		if(argv[0].trim().equals("")){
			System.out.println("argv[0] empty!");
			return;
		}
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:/com/whisper/di/business/dataimport/dataImportCtx.xml");
		//UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		
		String param = argv[0].trim();
		if(param.equalsIgnoreCase("ALL")){
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
			mc.setOrderByClause("id desc");
	    	mc.setPageNumber(1);
	    	mc.setPageSize(1000);
			EntityIterator<Integer, UserToken> itit = new KeyBasedEntityBatchIterator<Integer, UserToken>(Integer.class, UserToken.class, userTokenService.getEntityDao(), mc);
	    	while(itit.hasNext()){
	    		List<UserToken> tokens = itit.next();
	    		for(UserToken uToken:tokens){
	    			IegalTokenHashService.getInstance().userTokenRemove(uToken.getId());//.userTokenRegister(uToken.getId().intValue(), uToken.getAccess_token());
	    			System.out.println(String.format("uid[%s] token[%s] redis clear!", uToken.getId(),uToken.getAccess_token()));
	    		}
	    	}
	    	CommonCriteria remove_mc = new CommonCriteria();
	    	remove_mc.createCriteria().andSimpleCaulse(" 1=1");
	    	userTokenService.deleteByCommonCriteria(remove_mc);
	    	System.out.println("userToken table clear!");
		}else{
			String[] uids = param.split(StringHelper.COMMA_STRING_GAP);
			for(String uid:uids){
				Integer userid = new Integer(uid);
				IegalTokenHashService.getInstance().userTokenRemove(userid);
				userTokenService.deleteById(userid);
				System.out.println(String.format("uid[%s] redis and db clear!",uid));
			}
		}
		
	}

}
