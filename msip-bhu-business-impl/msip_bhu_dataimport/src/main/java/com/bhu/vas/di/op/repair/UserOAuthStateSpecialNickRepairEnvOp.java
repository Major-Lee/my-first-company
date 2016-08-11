package com.bhu.vas.di.op.repair;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.model.UserOAuthState;
import com.bhu.vas.api.rpc.user.model.pk.UserOAuthStatePK;
import com.bhu.vas.business.ds.user.service.UserOAuthStateService;
import com.smartwork.msip.cores.helper.encrypt.Base64Helper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 更新共享oauth的extension_content中的nick为base64编码
 * @author Edmond
 *
 */
public class UserOAuthStateSpecialNickRepairEnvOp {
	public static void main(String[] argv){
		long t0 = System.currentTimeMillis();
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, UserOAuthStateSpecialNickRepairEnvOp.class);
		ctx.start();
		UserOAuthStateService userOAuthStateService = (UserOAuthStateService)ctx.getBean("userOAuthStateService");
		ModelCriteria mc_wdsc = new ModelCriteria();
		mc_wdsc.createCriteria().andSimpleCaulse(" 1=1 ");//.andColumnEqualTo("singer", 1);//.andColumnBetween("updated_at", d2, d1);
		mc_wdsc.setOrderByClause("uid desc");
		mc_wdsc.setPageNumber(1);
		mc_wdsc.setPageSize(200);
		AtomicInteger incr = new AtomicInteger(0);
		//EntityIterator<Integer, User> itit_wdsc = new KeyBasedEntityBatchIterator<Integer, User>(Integer.class, User.class, userService.getEntityDao(), mc_wdsc);
		//EntityIterator<String, WifiDeviceSharedealConfigs> itit_wdsc = new KeyBasedEntityBatchIterator<String, WifiDeviceSharedealConfigs>(String.class, WifiDeviceSharedealConfigs.class, chargingFacadeService.getWifiDeviceSharedealConfigsService().getEntityDao(), mc_wdsc);
    	//EntityIterator<Integer, UserOAuthState> itit_wdsc = new KeyBasedEntityBatchIterator<Integer, UserOAuthState>(Integer.class, UserOAuthState.class, userOAuthStateService.getEntityDao(), mc_wdsc);
		EntityIterator<UserOAuthStatePK, UserOAuthState> itit_wdsc = new KeyBasedEntityBatchIterator<UserOAuthStatePK, UserOAuthState>(UserOAuthStatePK.class, UserOAuthState.class, userOAuthStateService.getEntityDao(), mc_wdsc);
		while(itit_wdsc.hasNext()){
			List<UserOAuthState> list = itit_wdsc.next();
			for(UserOAuthState wdsc:list){
				UserOAuthStateDTO dto = wdsc.getInnerModel();
				if(dto != null && StringUtils.isNotEmpty(dto.getNick())){
					String oldnick = dto.getNick();
					dto.setNick(new String(Base64Helper.encode(dto.getNick().getBytes())));
					System.out.println(String.format("[%s] [%s]", oldnick,dto.getNick()));
					wdsc.replaceInnerModel(dto);
					userOAuthStateService.update(wdsc);
				}
			}
		}
		ctx.stop();
		ctx.close();
		System.exit(1);
	}
}
