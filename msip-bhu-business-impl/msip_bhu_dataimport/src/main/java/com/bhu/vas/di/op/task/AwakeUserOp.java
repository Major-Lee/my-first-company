package com.bhu.vas.di.op.task;

import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWalletService;
import com.bhu.vas.di.op.userreg.UserUniqueRegisterEnvOp;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

public class AwakeUserOp {
	//（必虎昵称，没有昵称的为：必虎用户）
	//金额为0的不发
	public static String AwakeSMS_Template = "亲爱的%s,你可能还不知道有%s元正静静地躺在钱包里等着你提现呢！详情点击%s";
	public static String DefaultUserName = "必虎用户";
	public static String DefaultLink = "http://aaa/s2234.shtml";
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserUniqueRegisterEnvOp.class);
		ctx.start();
		UserService userService = (UserService)ctx.getBean("userService");
		UserWalletService userWalletService = (UserWalletService)ctx.getBean("userWalletService");
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIsNotNull("mobileno").andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc.setOrderByClause("id desc");
    	mc.setPageNumber(1);
    	mc.setPageSize(1000);
    	EntityIterator<Integer, User> itit = new KeyBasedEntityBatchIterator<Integer, User>(Integer.class, User.class, userService.getEntityDao(), mc);
		while(itit.hasNext()){
			List<User> list = itit.next();
			for(User user:list){
				UserWallet wallet = userWalletService.getById(user.getId());
				if(wallet != null && wallet.getCash() > 0 ){
					String smsUserName = StringUtils.isEmpty(user.getNick())?DefaultUserName:user.getNick();
					String smsCash =String.valueOf(ArithHelper.round(wallet.getCash(), 2));
					String.format(AwakeSMS_Template, smsUserName,smsCash,DefaultLink);
				}
			}
		}
		System.out.println("数据增量导入，共耗时"+((System.currentTimeMillis()-t0)/1000)+"s");
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
	
}
