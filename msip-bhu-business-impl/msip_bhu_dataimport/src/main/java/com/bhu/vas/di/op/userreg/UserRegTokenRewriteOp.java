package com.bhu.vas.di.op.userreg;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.business.bucache.redis.serviceimpl.token.IegalTokenHashService;
import com.bhu.vas.business.ds.user.service.UserTokenService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 用户access token 重写入redis
 * @author Edmond
 *
 */
public class UserRegTokenRewriteOp {
	
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:/com/whisper/di/business/dataimport/dataImportCtx.xml");
		//UserService userService = (UserService)ctx.getBean("userService");
		UserTokenService userTokenService = (UserTokenService)ctx.getBean("userTokenService");
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc.setOrderByClause("id desc");
    	mc.setPageNumber(1);
    	mc.setPageSize(1000);
		EntityIterator<Integer, UserToken> itit = new KeyBasedEntityBatchIterator<Integer, UserToken>(Integer.class, UserToken.class, userTokenService.getEntityDao(), mc);
    	while(itit.hasNext()){
    		List<UserToken> tokens = itit.next();
    		for(UserToken uToken:tokens){
    			IegalTokenHashService.getInstance().userTokenRegister(uToken.getId().intValue(), uToken.getAccess_token());
    			System.out.println(String.format("uid[%s] token[%s]", uToken.getId(),uToken.getAccess_token()));
    		}
    	}
	}

}
