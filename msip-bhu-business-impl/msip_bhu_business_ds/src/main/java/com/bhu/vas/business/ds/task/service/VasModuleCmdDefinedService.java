package com.bhu.vas.business.ds.task.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.task.model.VasModuleCmdDefined;
import com.bhu.vas.api.rpc.task.model.pk.VasModuleCmdPK;
import com.bhu.vas.business.ds.task.dao.VasModuleCmdDefinedDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class VasModuleCmdDefinedService extends AbstractCoreService<VasModuleCmdPK,VasModuleCmdDefined, VasModuleCmdDefinedDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(VasModuleCmdDefinedDao vasModuleCmdDefinedDao) {
		super.setEntityDao(vasModuleCmdDefinedDao);
	}
	
	public String fetchCommonStopTemplate(){
		VasModuleCmdDefined cmdDefined = this.getById(VasModuleCmdDefined.stopCmdPk);
		if(cmdDefined == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VAP_MODULE_CMD_NOT_DEFINED,
					new String[]{VasModuleCmdDefined.stopCmdPk.getDref(),VasModuleCmdDefined.stopCmdPk.getStyle()});
		}else{
			return cmdDefined.getTemplate();
		}
	}

}
