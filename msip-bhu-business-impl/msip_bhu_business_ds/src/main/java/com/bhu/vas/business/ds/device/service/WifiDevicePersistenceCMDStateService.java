package com.bhu.vas.business.ds.device.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.dto.ret.param.ParamVasModuleDTO;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.helper.PersistenceAction;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDTO;
import com.bhu.vas.api.rpc.devices.dto.PersistenceCMDDetailDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevicePersistenceCMDState;
import com.bhu.vas.business.ds.device.dao.WifiDevicePersistenceCMDStateDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
@Transactional("coreTransactionManager")
public class WifiDevicePersistenceCMDStateService extends AbstractCoreService<String,WifiDevicePersistenceCMDState,WifiDevicePersistenceCMDStateDao>{
	
	@Resource
	@Override
	public void setEntityDao(WifiDevicePersistenceCMDStateDao wifiDevicePersistenceCMDStateDao) {
		super.setEntityDao(wifiDevicePersistenceCMDStateDao);
	}

	public void filterPersistenceCMD(String mac, OperationCMD opt, OperationDS subopt, String extparams){
		
		List<PersistenceAction> actions = WifiDeviceHelper.needPersistenceAction(opt, subopt);
		if(actions != null && !actions.isEmpty()){
			WifiDevicePersistenceCMDState persist = this.getOrCreateById(mac);
			for(PersistenceAction action:actions){
				
				if(PersistenceAction.Oper_Update.equals(action.getOperation())){
					persist.addOrUpdatePersistence(action.getKey(),new PersistenceCMDDTO(opt.getNo(),subopt!=null?subopt.getNo():OperationDS.Empty_OperationDS,extparams));
				}else if(PersistenceAction.Oper_Remove.equals(action.getOperation())){
					persist.removePersistence(action.getKey());
					//persist.removePersistence(new PersistenceCMDDTO(opt.getNo(),subopt!=null?subopt.getNo():OperationDS.Empty_OperationDS,extparams).toKey());
				}else{
					;
				}
				
			}
			this.update(persist);
		}
	}
	
	public List<PersistenceCMDDetailDTO> fetchDevicePersistenceDetailCMD(String mac){
		try{
			WifiDevicePersistenceCMDState cmdState = this.getById(mac);
			if(cmdState == null || cmdState.getExtension().isEmpty()) return Collections.emptyList();
			
			List<PersistenceCMDDetailDTO> result = new ArrayList<>();
			Set<Entry<String, PersistenceCMDDTO>> entrySet = cmdState.getExtension().entrySet();
			for(Entry<String, PersistenceCMDDTO> entry : entrySet){
				PersistenceCMDDTO dto = entry.getValue();
				result.add(PersistenceCMDDetailDTO.from(dto));
			}
			return result;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public String fetchDeviceVapModuleStyle(String mac){
		try{
			WifiDevicePersistenceCMDState cmdState = this.getById(mac);
			if(cmdState == null || cmdState.getExtension().isEmpty()) return null;
			
			String key = WifiDeviceHelper.builderPersistenceKey(OperationCMD.ModifyDeviceSetting.getNo(),OperationDS.DS_Http_VapModuleCMD_Start.getNo());
			
			PersistenceCMDDTO innerModel = cmdState.getInnerModel(key);
			if(innerModel == null) return null;
			ParamVasModuleDTO param_dto = JsonHelper.getDTO(innerModel.getExtparams(), ParamVasModuleDTO.class);
			if(param_dto == null || StringUtils.isEmpty(param_dto.getStyle()))
				return null;
			return param_dto.getStyle();
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
}
