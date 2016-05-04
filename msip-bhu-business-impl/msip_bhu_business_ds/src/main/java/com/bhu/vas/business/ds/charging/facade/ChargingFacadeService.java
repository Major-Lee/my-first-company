package com.bhu.vas.business.ds.charging.facade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType.OrderUmacType;
import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.bhu.vas.api.rpc.charging.dto.WithdrawCostInfo;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchImport;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.charging.service.UserWithdrawCostConfigsService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceBatchImportService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class ChargingFacadeService {
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private WifiDeviceBatchImportService wifiDeviceBatchImportService;

	@Resource
	private UserWithdrawCostConfigsService userWithdrawCostConfigsService;

    @Resource
    private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;

	/*public UserService getUserService() {
		return userService;
	}*/

    public BatchImportVTO doBatchImportCreate(int uid,
    		int countrycode,String mobileno, 
    		String sellor,String partner,
    		double sharedeal_owner_percent,
    		boolean canbeturnoff,
    		boolean enterpriselevel,
    		String remark){
    	User user = UserValidateServiceHelper.validateUser(uid,this.userService);
    	if(StringUtils.isNotEmpty(mobileno)){
    		boolean exist = UniqueFacadeService.checkMobilenoExist(countrycode,mobileno);
        	if(!exist){
        		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"mobileno",mobileno});
        	}
    	}
    	WifiDeviceBatchImport batch_import = new WifiDeviceBatchImport();
    	batch_import.setImportor(uid);
    	batch_import.setMobileno(mobileno);
    	batch_import.setSellor(sellor);
    	batch_import.setPartner(partner);
    	batch_import.setOwner_percent(ArithHelper.round(sharedeal_owner_percent, 2));
    	batch_import.setCanbeturnoff(canbeturnoff);
    	batch_import.setEnterpriselevel(enterpriselevel);
    	//batch_import.setFilepath(filepath_suffix);
    	batch_import.setRemark(remark);
    	batch_import.setStatus(WifiDeviceBatchImport.STATUS_IMPORTED_FILE);
    	wifiDeviceBatchImportService.insert(batch_import);
    	return batch_import.toBatchImportVTO(user.getNick(),user.getMobileno());
    }
    
    public BatchImportVTO doConfirmDeviceRecord(int uid,String import_id) {
    	User user = UserValidateServiceHelper.validateUser(uid,this.userService);
    	WifiDeviceBatchImport batch_import = wifiDeviceBatchImportService.getById(import_id);
    	if(batch_import == null){
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"BatchImport",import_id});
    	}
    	batch_import.setStatus(WifiDeviceBatchImport.STATUS_CONFIRMED_DOING);
    	wifiDeviceBatchImportService.update(batch_import);
    	return batch_import.toBatchImportVTO(user.getNick(),user.getMobileno());
    }
    public TailPage<BatchImportVTO> pagesBatchImport(int uid,int status, int pageNo, int pageSize){
    	TailPage<BatchImportVTO> result_pages = null;
    	List<BatchImportVTO> vtos_result = new ArrayList<>();
    	ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		if(status != 0){
			createCriteria.andColumnEqualTo("status", status);
		}
		createCriteria.andSimpleCaulse(" 1=1 ");
    	mc.setPageNumber(pageNo);
    	mc.setPageSize(pageSize);
    	mc.setOrderByClause(" updated_at desc ");
		TailPage<WifiDeviceBatchImport> pages = wifiDeviceBatchImportService.findModelTailPageByModelCriteria(mc);
		if(!pages.isEmpty()){
			List<Integer> uids = new ArrayList<>();
			for(WifiDeviceBatchImport batchimport:pages.getItems()){
				uids.add(batchimport.getImportor());
			}
			List<User> users = userService.findByIds(uids, true, true);
			int index = 0;
			for(WifiDeviceBatchImport batchimport:pages.getItems()){
				User user = users.get(index);
				vtos_result.add(batchimport.toBatchImportVTO(user.getNick(), user.getMobileno()));
				index++;
			}
		}
		result_pages = new CommonPage<BatchImportVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), vtos_result);
		return result_pages;
    }
    
    
    
	public void wifiDeviceBindedNotify(String dmac,int uid){
		WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(dmac);
		if(configs == null){
			configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
			configs.doRuntimeInit(dmac,uid);
			wifiDeviceSharedealConfigsService.insert(configs);
		}else{
			configs.setOwner(uid);
			wifiDeviceSharedealConfigsService.update(configs);
		}
	}
	
	public void wifiDeviceUnBindedNotify(String dmac){
		WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(dmac);
		if(configs == null){
			configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
			configs.doRuntimeInit(dmac,WifiDeviceSharedealConfigs.None_Owner);
			wifiDeviceSharedealConfigsService.insert(configs);
		}else{
			configs.setOwner(WifiDeviceSharedealConfigs.None_Owner);
			wifiDeviceSharedealConfigsService.update(configs);
		}
	}
	
	public void wifiDeviceResetNotify(String dmac){
		wifiDeviceUnBindedNotify(dmac);
	}
	
	/**
	 * 获取当前对此用户生效的钱包配置
	 * 目前直接取Default_ConfigsID对应的数据
	 * @return
	 */
	public WifiDeviceSharedealConfigs userfulWifiDeviceSharedealConfigs(String dmac){
		WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(dmac);
		if(configs == null){
			configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
			Integer bindUid = userDeviceService.fetchBindUid(dmac);
			if(bindUid == null)
				configs.doRuntimeInit(dmac,WifiDeviceSharedealConfigs.None_Owner);
			else
				configs.doRuntimeInit(dmac,bindUid.intValue());
			configs = wifiDeviceSharedealConfigsService.insert(configs);
		}
		return configs;
	}
	
	/*public boolean canBeTurnoff(String dmac){
		WifiDeviceSharedealConfigs configs = userfulWifiDeviceSharedealConfigs(dmac);
		if(configs == null){
			return true;
		}else{
			return configs.isCanbe_turnoff();
		}
	}*/
	public boolean canBeTurnoff(String dmac){
		WifiDeviceSharedealConfigs config = wifiDeviceSharedealConfigsService.getById(dmac);
		if(config == null){
			return true;
		}else{
			return config.isCanbe_turnoff();
		}
	}
	
	/**
	 * 如果有一个设备不能被关闭 返回false
	 * 不存在配置的设备 缺省定义为可以关闭
	 * @param dmacs
	 * @return
	 */
	public boolean canAllBeTurnoff(List<String> dmacs){
		if(dmacs == null || dmacs.isEmpty()) return true;
		boolean ret = true;
		List<WifiDeviceSharedealConfigs> configs = wifiDeviceSharedealConfigsService.findByIds(dmacs,true,true);
		for(WifiDeviceSharedealConfigs config:configs){
			if(config == null) continue;
			if(!config.isCanbe_turnoff()){
				ret = false;
				break;
			}
		}
		return ret;
	}
	
	public String fetchAmountRange(String dmac,Integer umactype){
		try{
			WifiDeviceSharedealConfigs configs = userfulWifiDeviceSharedealConfigs(dmac);
			String amountRange = null;
			if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue()){
				amountRange = configs.getRange_cash_pc();
			}else{
				amountRange = configs.getRange_cash_mobile();
			}
			return amountRange;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return WifiDeviceSharedealConfigs.Default_Range_Cash_Mobile;
		}
		
	}
	
	public String fetchAccessInternetTime(String dmac,Integer umactype){
		try{
			WifiDeviceSharedealConfigs configs = userfulWifiDeviceSharedealConfigs(dmac);
			String ait = null;
			if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue()){
				ait = configs.getAit_pc();
			}else{
				ait = configs.getAit_mobile();
			}
			return ait;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return WifiDeviceSharedealConfigs.Default_AIT;
		}
	}
	
	/**
	 * 计算收益分成
	 * @param uid
	 * @param cash
	 * @return
	 */
	public SharedealInfo calculateSharedeal(String dmac,String orderid,double cash){
		WifiDeviceSharedealConfigs configs = this.userfulWifiDeviceSharedealConfigs(dmac);
		SharedealInfo result =  SharedealInfo.calculate(dmac, orderid, cash, configs.getOwner_percent(), configs.getManufacturer_percent());
		if(configs.getOwner()>0){
			result.setOwner(configs.getOwner());
			result.setBelong(true);
		}else{
			result.setOwner(WifiDeviceSharedealConfigs.Default_Owner);
			result.setBelong(false);
		}
		result.setManufacturer(configs.getManufacturer());
		return result;
	}
	
	
	public WithdrawCostInfo calculateWithdrawCost(int uid,String applyid,double cash){
		return userWithdrawCostConfigsService.calculateWithdrawCost(uid,applyid,cash);
	}
	
	public WifiDeviceService getWifiDeviceService() {
		return wifiDeviceService;
	}

	public WifiDeviceBatchImportService getWifiDeviceBatchImportService() {
		return wifiDeviceBatchImportService;
	}

	public UserWithdrawCostConfigsService getUserWithdrawCostConfigsService() {
		return userWithdrawCostConfigsService;
	}

	public WifiDeviceSharedealConfigsService getWifiDeviceSharedealConfigsService() {
		return wifiDeviceSharedealConfigsService;
	}
}
