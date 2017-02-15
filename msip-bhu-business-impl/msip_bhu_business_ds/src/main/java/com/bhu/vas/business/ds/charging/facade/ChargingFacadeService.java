package com.bhu.vas.business.ds.charging.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType.OrderUmacType;
import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.bhu.vas.api.rpc.charging.dto.WithdrawCostInfo;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceBatchImport;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.charging.vto.SharedealDefaultVTO;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.ParamSharedNetworkDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.facade.UniqueFacadeService;
import com.bhu.vas.business.ds.charging.service.UserWithdrawCostConfigsService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceBatchDetailService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceBatchImportService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.StringHelper;
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
	
/*	@Resource
	private UserDeviceService userDeviceService;*/
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	@Resource
	private WifiDeviceBatchImportService wifiDeviceBatchImportService;

	@Resource
	private WifiDeviceBatchDetailService wifiDeviceBatchDetailService;
	
	@Resource
	private UserWithdrawCostConfigsService userWithdrawCostConfigsService;

    @Resource
    private WifiDeviceSharedealConfigsService wifiDeviceSharedealConfigsService;

    @Resource
    private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;
    
	/*public UserService getUserService() {
		return userService;
	}*/

    public BatchImportVTO doBatchImportCreate(int uid,
    		int countrycode,String mobileno, int distributor_uid,
    		String sellor,String partner,
    		Boolean canbeturnoff,
    		Boolean noapp,
    		boolean enterpriselevel,
    		boolean customized,
    		String sharedeal_owner_percent,String sharedeal_manufacturer_percent,String sharedeal_distributor_percent, 
    		String range_cash_mobile,String range_cash_pc,String access_internet_time,
			String channel_lv1, String channel_lv2,
    		String remark){
    	User user = UserValidateServiceHelper.validateUser(uid,this.userService);
    	
    	if(StringUtils.isNotEmpty(mobileno)){
    		boolean exist = UniqueFacadeService.checkMobilenoExist(countrycode,mobileno);
        	if(!exist){
        		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"mobileno",mobileno});
        	}
    	}
    	User distributor_user = null;
    	if(distributor_uid >0){
    		distributor_user = UserValidateServiceHelper.validateUser(distributor_uid,this.userService);
    	}
    	
    	WifiDeviceBatchImport batch_import = new WifiDeviceBatchImport();
    	batch_import.setImportor(uid);
    	batch_import.setMobileno(mobileno);
    	batch_import.setDistributor(distributor_uid);
    	batch_import.setSellor(sellor);
    	batch_import.setPartner(partner);
    	//ArithHelper.round(sharedeal_owner_percent, 2)
    	
    	batch_import.setCanbeturnoff(canbeturnoff);
    	batch_import.setNoapp(noapp);
    	batch_import.setEnterpriselevel(enterpriselevel);
    	batch_import.setCustomized(customized);
    	batch_import.setChannel_lv1(channel_lv1);
    	batch_import.setChannel_lv2(channel_lv2);
    	//填充数据
    	WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
    	if(!customized){
    		batch_import.setOwner_percent(String.valueOf(configs.getOwner_percent()));
    		batch_import.setManufacturer_percent(String.valueOf(configs.getManufacturer_percent()));
    		batch_import.setDistributor_percent(String.valueOf(configs.getDistributor_percent()));
//        	batch_import.setRange_cash_mobile(configs.getRange_cash_mobile());
//        	batch_import.setRange_cash_pc(configs.getRange_cash_pc());
//        	batch_import.setAccess_internet_time(configs.getAit_pc());
    	}else{
    		if(StringUtils.isEmpty(sharedeal_owner_percent) || StringHelper.MINUS_STRING_GAP.equals(sharedeal_owner_percent)){
    			batch_import.setOwner_percent(String.valueOf(configs.getOwner_percent()));
    		}else{
    			batch_import.setOwner_percent(sharedeal_owner_percent);
    		}
    		if(StringUtils.isEmpty(sharedeal_manufacturer_percent) || StringHelper.MINUS_STRING_GAP.equals(sharedeal_manufacturer_percent)){
    			batch_import.setManufacturer_percent(String.valueOf(configs.getManufacturer_percent()));
    		}else{
    			batch_import.setManufacturer_percent(sharedeal_manufacturer_percent);
    		}
    		if(StringUtils.isEmpty(sharedeal_distributor_percent) || StringHelper.MINUS_STRING_GAP.equals(sharedeal_distributor_percent)){
    			batch_import.setDistributor_percent(String.valueOf(configs.getDistributor_percent()));
    		}else{
    			batch_import.setDistributor_percent(sharedeal_distributor_percent);
    		}
			batch_import.setDistributor_l2_percent("0.00");
    		
    		
//    		if(StringUtils.isEmpty(range_cash_mobile) || StringHelper.MINUS_STRING_GAP.equals(range_cash_mobile)){
//    			batch_import.setRange_cash_mobile(String.valueOf(configs.getRange_cash_mobile()));
//    		}else{
//    			batch_import.setRange_cash_mobile(range_cash_mobile);
//    		}
//    		if(StringUtils.isEmpty(range_cash_pc) || StringHelper.MINUS_STRING_GAP.equals(range_cash_pc)){
//    			batch_import.setRange_cash_pc(String.valueOf(configs.getRange_cash_pc()));
//    		}else{
//    			batch_import.setRange_cash_pc(range_cash_pc);
//    		}
//    		if(StringUtils.isEmpty(access_internet_time) || StringHelper.MINUS_STRING_GAP.equals(access_internet_time)){
//    			batch_import.setAccess_internet_time(String.valueOf(configs.getAit_pc()));
//    		}else{
//    			batch_import.setAccess_internet_time(access_internet_time);
//    		}
    	}
    	//batch_import.setFilepath(filepath_suffix);
    	batch_import.setRemark(remark);
    	batch_import.setStatus(WifiDeviceBatchImport.STATUS_IMPORTED_FILE);
    	wifiDeviceBatchImportService.insert(batch_import);
    	return batch_import.toBatchImportVTO(user.getNick(),user.getMobileno(),distributor_user!=null?distributor_user.getNick():StringHelper.EMPTY_STRING_GAP, StringHelper.EMPTY_STRING_GAP);
    }
  
    public BatchImportVTO doOpsBatchImportCreate(int uid, String opsid,
    		int countrycode,String mobileno, int distributor_uid, int distributor_l2_uid, String distributor_type,
    		String sellor,String partner,
    		Boolean canbeturnoff, Boolean noapp,
    		String sharedeal_owner_percent,String sharedeal_manufacturer_percent,String sharedeal_distributor_percent, String sharedeal_distributor_l2_percent,
			String channel_lv1, String channel_lv2,
    		String remark){
    	User user = UserValidateServiceHelper.validateUser(uid,this.userService);
    	
    	if(StringUtils.isNotEmpty(mobileno)){
    		boolean exist = UniqueFacadeService.checkMobilenoExist(countrycode,mobileno);
        	if(!exist){
        		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"mobileno",mobileno});
        	}
    	}
    	User distributor_user = null;
    	User distributor_l2_user = null;
    	if(distributor_uid >0){
    		distributor_user = UserValidateServiceHelper.validateUser(distributor_uid,this.userService);
    	}
    	if(distributor_l2_uid >0){
    		distributor_l2_user = UserValidateServiceHelper.validateUser(distributor_l2_uid,this.userService);
    	}
    	
    	WifiDeviceBatchImport batch_import = new WifiDeviceBatchImport();
    	batch_import.setImportor(uid);
    	batch_import.setMobileno(mobileno);
    	batch_import.setDistributor(distributor_uid);
    	batch_import.setDistributor_l2(distributor_l2_uid);
    	batch_import.setSellor(sellor);
    	batch_import.setPartner(partner);
    	batch_import.setOpsid(opsid);
    	batch_import.setCustomized(true);
    	batch_import.setCanbeturnoff(canbeturnoff);
    	batch_import.setNoapp(noapp);
    	batch_import.setChannel_lv1(channel_lv1);
    	batch_import.setChannel_lv2(channel_lv2);
    	batch_import.setDistributor_type(distributor_type);
    	//填充数据
    	WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
		if(StringUtils.isEmpty(sharedeal_owner_percent) || StringHelper.MINUS_STRING_GAP.equals(sharedeal_owner_percent)){
			batch_import.setOwner_percent(String.valueOf(configs.getOwner_percent()));
		}else{
			batch_import.setOwner_percent(sharedeal_owner_percent);
		}
		if(StringUtils.isEmpty(sharedeal_manufacturer_percent) || StringHelper.MINUS_STRING_GAP.equals(sharedeal_manufacturer_percent)){
			batch_import.setManufacturer_percent(String.valueOf(configs.getManufacturer_percent()));
		}else{
			batch_import.setManufacturer_percent(sharedeal_manufacturer_percent);
		}
		if(StringUtils.isEmpty(sharedeal_distributor_percent) || StringHelper.MINUS_STRING_GAP.equals(sharedeal_distributor_percent)){
			batch_import.setDistributor_percent(String.valueOf(configs.getDistributor_percent()));
		}else{
			batch_import.setDistributor_percent(sharedeal_distributor_percent);
		}
		if(StringUtils.isEmpty(sharedeal_distributor_l2_percent) || StringHelper.MINUS_STRING_GAP.equals(sharedeal_distributor_l2_percent)){
			batch_import.setDistributor_l2_percent(String.valueOf(configs.getDistributor_l2_percent()));
		}else{
			batch_import.setDistributor_l2_percent(sharedeal_distributor_l2_percent);
		}
		
    	//batch_import.setFilepath(filepath_suffix);
    	batch_import.setRemark(remark);
    	batch_import.setStatus(WifiDeviceBatchImport.STATUS_IMPORTED_FILE);
    	wifiDeviceBatchImportService.insert(batch_import);
    	return batch_import.toBatchImportVTO(user.getNick(),user.getMobileno(),distributor_user!=null?distributor_user.getNick():StringHelper.EMPTY_STRING_GAP,
    			distributor_l2_user!=null?distributor_l2_user.getNick():StringHelper.EMPTY_STRING_GAP);
    }
    
    public BatchImportVTO doCancelDeviceRecord(int uid,String import_id) {
    	User user = UserValidateServiceHelper.validateUser(uid,this.userService);
    	WifiDeviceBatchImport batch_import = wifiDeviceBatchImportService.getById(import_id);
    	if(batch_import == null){
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"BatchImport",import_id});
    	}
    	batch_import.setImportor(uid);
    	batch_import.setStatus(WifiDeviceBatchImport.STATUS_IMPORTING_CANCEL);
    	wifiDeviceBatchImportService.update(batch_import);
    	
    	User distributor_user = null;
    	if(batch_import.getDistributor() >0){
    		distributor_user = userService.getById(batch_import.getDistributor());
    	}
    	return batch_import.toBatchImportVTO(user.getNick(),user.getMobileno(),distributor_user!=null?distributor_user.getNick():StringHelper.EMPTY_STRING_GAP, StringHelper.EMPTY_STRING_GAP);
    }
    
    public BatchImportVTO doConfirmDeviceRecord(int uid,String import_id) {
    	User user = UserValidateServiceHelper.validateUser(uid,this.userService);
    	WifiDeviceBatchImport batch_import = wifiDeviceBatchImportService.getById(import_id);
    	if(batch_import == null){
    		throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"BatchImport",import_id});
    	}
    	batch_import.setStatus(WifiDeviceBatchImport.STATUS_CONFIRMED_DOING);
    	wifiDeviceBatchImportService.update(batch_import);
    	
    	User distributor_user = null;
    	if(batch_import.getDistributor() >0){
    		distributor_user = userService.getById(batch_import.getDistributor());
    	}
    	return batch_import.toBatchImportVTO(user.getNick(),user.getMobileno(),distributor_user!=null?distributor_user.getNick():StringHelper.EMPTY_STRING_GAP, StringHelper.EMPTY_STRING_GAP);
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
				User distributor_user = null;
				User distributor_l2_user = null;
		    	if(batchimport.getDistributor() >0){
		    		distributor_user = userService.getById(batchimport.getDistributor());
		    	}
		    	if(batchimport.getDistributor_l2() >0){
		    		distributor_l2_user = userService.getById(batchimport.getDistributor_l2());
		    	}
				vtos_result.add(batchimport.toBatchImportVTO(user.getNick(), user.getMobileno(),distributor_user!=null?distributor_user.getNick():StringHelper.EMPTY_STRING_GAP,
						distributor_l2_user!=null?distributor_l2_user.getNick():StringHelper.EMPTY_STRING_GAP));
				index++;
			}
		}
		result_pages = new CommonPage<BatchImportVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), vtos_result);
		return result_pages;
    }
    
    /**
     * 第一次访问时 upact = true lastrowid = Integer.MaxValue ps = 20
     * TODO：是否需要返回count？
     * @param uid
     * @param status
     * @param upact
     * @param lastrowid
     * @param ps
     * @return
     */
    public List<BatchImportVTO> pagesStartRowBatchImport(int uid,int status, boolean upact, int lastrowid, int ps){
    	List<BatchImportVTO> vtos_result = new ArrayList<>();
    	ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		if(status != 0){
			createCriteria.andColumnEqualTo("status", status);
		}
		boolean needReverse = false;
		if(upact){//上滑
			createCriteria.andColumnLessThan("id", lastrowid);
			mc.setOrderByClause(" id desc ");
		}else{//下拉 down
			createCriteria.andColumnGreaterThan("id", lastrowid);
			mc.setOrderByClause(" id asc ");
			needReverse = true;
			//取完数据后reverse
		}
		createCriteria.andSimpleCaulse(" 1=1 ");
		mc.setStartRow(0);
		mc.setSize(ps);
		List<WifiDeviceBatchImport> pages = wifiDeviceBatchImportService.findModelByModelCriteria(mc);
		if(!pages.isEmpty()){
			if(needReverse) Collections.reverse(pages);
			List<Integer> uids = new ArrayList<>();
			for(WifiDeviceBatchImport batchimport:pages){
				uids.add(batchimport.getImportor());
			}
			List<User> users = userService.findByIds(uids, true, true);
			int index = 0;
			for(WifiDeviceBatchImport batchimport:pages){
				User user = users.get(index);
				User distributor_user = null;
		    	if(batchimport.getDistributor() >0){
		    		distributor_user = userService.getById(batchimport.getDistributor());
		    	}
				User distributor_l2_user = null;
		    	if(batchimport.getDistributor_l2() >0){
		    		distributor_l2_user = userService.getById(batchimport.getDistributor_l2());
		    	}
				vtos_result.add(batchimport.toBatchImportVTO(user.getNick(), user.getMobileno(),distributor_user!=null?distributor_user.getNick():StringHelper.EMPTY_STRING_GAP,
						distributor_l2_user!=null?distributor_l2_user.getNick():StringHelper.EMPTY_STRING_GAP));
				index++;
			}
		}
		return vtos_result;
		//result_pages = new CommonPage<BatchImportVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), vtos_result);
		//return result_pages;
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
			//Integer bindUid = userDeviceService.fetchBindUid(dmac);
			Integer bindUid = userWifiDeviceFacadeService.findUidById(dmac);
			if(bindUid == null)
				configs.doRuntimeInit(dmac,WifiDeviceSharedealConfigs.None_Owner);
			else
				configs.doRuntimeInit(dmac,bindUid.intValue());
			configs = wifiDeviceSharedealConfigsService.insert(configs);
		}
		return configs;
	}
	
	/**
	 * 只用于显示，不进行数据创建
	 * @param dmac
	 * @return
	 */
	public WifiDeviceSharedealConfigs userfulWifiDeviceSharedealConfigsJust4View(String dmac){
		WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(dmac);
		if(configs == null){
			configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
			configs.setId(dmac);
		}
		return configs;
	}
	
	
	public SharedealDefaultVTO defaultDeviceSharedealConfigs(){
		WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
		SharedealDefaultVTO vto = new SharedealDefaultVTO();
		vto.setId(configs.getId());
		vto.setOwner_percent(configs.getOwner_percent());
		vto.setManufacturer_percent(configs.getManufacturer_percent());
		vto.setDistributor_percent(configs.getDistributor_percent());
//		vto.setRcm(configs.getRange_cash_mobile());
//		vto.setRcp(configs.getRange_cash_pc());
//		vto.setAit(configs.getAit_mobile());
		return vto;
	}
	
	/*
	public void doWifiDeviceSharedealConfigsUpdate(Integer owner,String dmac,
			String range_cash_mobile,String range_cash_pc, String access_internet_time){
		this.doWifiDeviceSharedealConfigsUpdate(null, owner, null, null, dmac,
				null, null, true, null, null, null, 
				range_cash_mobile, range_cash_pc, access_internet_time, 
				false);
	}
	*/
	
	/**
	 * 
	 * @param batchno
	 * @param owner null <=0 >0 三种情况 null值代表忽略替换值内容,在insert的情况下需要去查询设备的绑定用户
	 * @param distributor null <=0 >0 三种情况 null值代表忽略替换值内容
	 * @param dmac
	 * @param owner_percent
	 * @param range_cash_mobile
	 * @param range_cash_pc
	 * @param access_internet_time
	 * @param canbeturnoff null 忽略此属性
	 * @param enterpriselevel null 忽略此属性
	 * @param runtime_applydefault
	 */
	public void doWifiDeviceSharedealConfigsUpdate(String batchno,Integer owner,Integer distributor, Integer distributor_l2, String distributor_type, String dmac,
			Boolean canbeturnoff, Boolean noapp,
			Boolean enterpriselevel,
			boolean customized,
			String owner_percent,String manufacturer_percent,String distributor_percent,String distributor_l2_percent,
//			String range_cash_mobile,String range_cash_pc, String access_internet_time,
			boolean runtime_applydefault){
		boolean insert = false;
		WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(dmac);
		if(configs == null){
			insert = true;
			//先用缺省值进行填充
			configs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
			configs.setId(dmac);
			configs.setCreated_at(null);
			configs.setUpdated_at(null);
		}else{
			if(!customized){//填充分成比例、打赏金额和上网时长的缺省值 
				WifiDeviceSharedealConfigs defaultConfigs = wifiDeviceSharedealConfigsService.getById(WifiDeviceSharedealConfigs.Default_ConfigsWifiID);
//				configs.setAit_mobile(defaultConfigs.getAit_mobile());
//				configs.setAit_pc(defaultConfigs.getAit_pc());
//				configs.setRange_cash_mobile(defaultConfigs.getRange_cash_mobile());
//				configs.setRange_cash_pc(defaultConfigs.getRange_cash_pc());
				configs.setOwner_percent(defaultConfigs.getOwner_percent());
				configs.setManufacturer_percent(defaultConfigs.getManufacturer_percent());
				configs.setDistributor_percent(defaultConfigs.getDistributor_percent());
				configs.setManufacturer_percent(defaultConfigs.getManufacturer_percent());
				configs.setDistributor_l2_percent(defaultConfigs.getDistributor_l2_percent());
			}
		}
		if(StringUtils.isNotEmpty(batchno)){
			configs.setBatchno(batchno);
		}
		if(owner != null){
			if(owner <=0){
				configs.setOwner(WifiDeviceSharedealConfigs.None_Owner);
			}else{
				configs.setOwner(owner);
			}
		}else{
			if(insert){//获取设备owner
//				Integer bindUid = userDeviceService.fetchBindUid(dmac);
				Integer bindUid = userWifiDeviceFacadeService.findUidById(dmac);
				if(bindUid == null || bindUid.intValue()<=0){
					configs.setOwner(WifiDeviceSharedealConfigs.None_Owner);
				}else{
					configs.setOwner(bindUid.intValue());
				}
			}
		}
		if(distributor != null){
			if(distributor <=0){
				configs.setDistributor(WifiDeviceSharedealConfigs.None_Distributor);
			}else{
				configs.setDistributor(distributor);
			}
		}/*else{
			configs.setDistributor(WifiDeviceSharedealConfigs.None_Distributor);
		}*/

		if(distributor_l2 != null){
			if(distributor_l2 <=0){
				configs.setDistributor_l2(WifiDeviceSharedealConfigs.None_Distributor);
			}else{
				configs.setDistributor_l2(distributor_l2);
			}
		}

		if(distributor_type != null)
			configs.setDistributor_type(distributor_type);
		
		if(customized){
			if(StringUtils.isEmpty(owner_percent) || StringUtils.isEmpty(manufacturer_percent)){
				;//以前是啥值就是什么值
			}else{
				try{
					double custom_owner_percent = Double.valueOf(owner_percent);
					double custom_manufacturer_percent = Double.valueOf(manufacturer_percent);
					double custom_distributor_l2_percent = Double.valueOf(distributor_l2_percent);
					double custom_distributor_percent = ArithHelper.round(ArithHelper.sub(1, ArithHelper.add(custom_owner_percent,custom_manufacturer_percent, custom_distributor_l2_percent)), 2);
					configs.setOwner_percent(custom_owner_percent);
					configs.setManufacturer_percent(custom_manufacturer_percent);
					configs.setDistributor_percent(custom_distributor_percent);
					configs.setDistributor_l2_percent(custom_distributor_l2_percent);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
			
			
//			if(StringUtils.isNotEmpty(range_cash_mobile)){
//				configs.setRange_cash_mobile(range_cash_mobile);
//			}
//			
//			if(StringUtils.isNotEmpty(range_cash_pc)){
//				configs.setRange_cash_pc(range_cash_pc);
//			}
//			
//			if(StringUtils.isNotEmpty(access_internet_time)){
//				configs.setAit_mobile(access_internet_time);
//				configs.setAit_pc(access_internet_time);
//			}
		}
		if(canbeturnoff != null){
			configs.setCanbe_turnoff(canbeturnoff);
		}
		if(noapp != null){
			configs.setNoapp(noapp);
		}
		if(enterpriselevel != null){
			configs.setEnterpriselevel(enterpriselevel.booleanValue());
		}
		configs.setRuntime_applydefault(runtime_applydefault);
		configs.setCustomized(customized);
		if(insert){
			wifiDeviceSharedealConfigsService.insert(configs);
		}else{
			wifiDeviceSharedealConfigsService.update(configs);
		}
		
		/*WifiDeviceSharedealConfigs userfulWifiDeviceSharedealConfigs = this.userfulWifiDeviceSharedealConfigs(dmac);
    	userfulWifiDeviceSharedealConfigs.setBatchno(batchno);
    	userfulWifiDeviceSharedealConfigs.setOwner_percent(importVto.getOwner_percent());
    	userfulWifiDeviceSharedealConfigs.setManufacturer_percent(ArithHelper.round(ArithHelper.sub(1, importVto.getOwner_percent()), 2));
    	userfulWifiDeviceSharedealConfigs.setCanbe_turnoff(importVto.isCanbeturnoff());
    	userfulWifiDeviceSharedealConfigs.setRuntime_applydefault(false);
    	chargingFacadeService.getWifiDeviceSharedealConfigsService().update(userfulWifiDeviceSharedealConfigs);*/
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
	
	public ParamSharedNetworkDTO getParamSharedNetwork(String dmac){
		try{
			WifiDeviceSharedNetwork configs = wifiDeviceSharedNetworkService.getById(dmac);
			return configs.getInnerModel().getPsn();
		}catch(Exception e){
			return null;
		}
	}
	
	public String fetchAmountRange(ParamSharedNetworkDTO psn, Integer umactype){
		try{
			if(psn == null){
				if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue())
				return ParamSharedNetworkDTO.Default_City_Range_Cash_PC;
			else
				return ParamSharedNetworkDTO.Default_City_Range_Cash_Mobile;
			} else {
				String amountRange = null;
				if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue()){
					amountRange = psn.getRange_cash_pc();
				}else{
					amountRange = psn.getRange_cash_mobile();
				}
				return amountRange;
			}
		}catch(Exception ex){
			return ParamSharedNetworkDTO.Default_Channel_Range_Cash_Mobile;
		}
		
	}

	public String fetchAccessInternetTime(ParamSharedNetworkDTO psn, Integer umactype){
		try{
			String ait = null;
			if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue()){
				ait = psn.getAit_pc();
			}else{
				ait = psn.getAit_mobile();
			}
			return ait;
		}catch(Exception ex){
			return ParamSharedNetworkDTO.Default_AIT;
		}
	}
	//免费上网商品上网时间获取
	public String fetchFreeAccessInternetTime(ParamSharedNetworkDTO psn, Integer umactype){
		try{
			String ait = null;
			if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue()){
				ait = psn.getFree_ait_pc();
			}else{
				ait = psn.getFree_ait_mobile();
			}
			return ait;
		}catch(Exception ex){
//			ex.printStackTrace(System.out);
			return ParamSharedNetworkDTO.Default_AIT;
		}
	}
	//是否开启了免费上网方式
	public boolean fetchDeviceIsOpenFreeMode(ParamSharedNetworkDTO psn){
		try{
			return psn.getIsfree() == 1 ? true:false;
		}catch(Exception ex){
//			ex.printStackTrace(System.out);
			return false;
		}
	}
	
	//是否不需要下载app就可以上
	public boolean fetchDeviceIsNoappdl(String dmac){
		try{
			WifiDeviceSharedealConfigs configs = wifiDeviceSharedealConfigsService.getById(dmac);
			return configs.isNoapp();
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return false;
		}
	}

	
	/**
	 * 计算收益分成
	 * @param uid
	 * @param cash
	 * @return
	 */
	public SharedealInfo calculateSharedeal(String dmac, String umac, String orderid,double cash) {
		WifiDeviceSharedealConfigs configs = this.userfulWifiDeviceSharedealConfigs(dmac);
		SharedealInfo result =  SharedealInfo.calculate(dmac, umac, orderid, cash,  
				                                        configs.getOwner_percent(), 
				                                        configs.getManufacturer_percent(),
				                                        configs.getDistributor_percent(),
				                                        configs.getDistributor_l2_percent()
				                                        );
		
		if (configs.getManufacturer() > 0) {
			result.setManufacturer(configs.getManufacturer());
		} else {
			result.setManufacturer(WifiDeviceSharedealConfigs.Default_Manufacturer);
		}

		// 不存在的 Distributor 分成进入Default_Owner
		if (configs.getDistributor() > 0) {
			result.setDistributor(configs.getDistributor());
		} else {
			result.setDistributor(WifiDeviceSharedealConfigs.Default_Owner);
		}
		
		// 不存在的 Distributor 分成进入Default_Owner
		if (configs.getDistributor_l2() > 0) {
			result.setDistributor_l2(configs.getDistributor_l2());
		} else {
			result.setDistributor_l2(WifiDeviceSharedealConfigs.Default_Owner);
		}
		
		if (configs.getOwner() > 0) {
			result.setOwner(configs.getOwner());
			result.setBelong(true);
		} else {
			//owner不存在，把owner的分润合并到最近的运营商
			if(configs.getDistributor_l2() > 0){
				result.setDistributor_l2_cash(result.getDistributor_l2_cash() + result.getOwner_cash());
				result.setOwner_cash(0);
			} else if(configs.getDistributor() > 0){
				result.setDistributor_cash(result.getDistributor_cash() + result.getOwner_cash());
				result.setOwner_cash(0);
			}
			result.setOwner(WifiDeviceSharedealConfigs.Default_Owner);
			result.setBelong(false);
		}
		// result.setDistributor(configs.getDistributor());
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

	public WifiDeviceBatchDetailService getWifiDeviceBatchDetailService() {
		return wifiDeviceBatchDetailService;
	}

	public UserService getUserService() {
		return userService;
	}

	public UserWifiDeviceFacadeService getUserWifiDeviceFacadeService() {
		return userWifiDeviceFacadeService;
	}
	
	public WifiDeviceSharedNetworkService getWifiDeviceSharedNetworkService() {
		return wifiDeviceSharedNetworkService;
	}

	public void setWifiDeviceSharedNetworkService(WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService) {
		this.wifiDeviceSharedNetworkService = wifiDeviceSharedNetworkService;
	}

	public String fetchWifiDeviceSharedNetworkSSID(ParamSharedNetworkDTO psn){
		try{
			String ssid = psn.getSsid();
			if(StringUtils.isEmpty(ssid)){
				ssid = VapEnumType.SharedNetworkType.SafeSecure.getDefaultSsid();
			}
			return ssid;
		}catch(Exception ex){
			return VapEnumType.SharedNetworkType.SafeSecure.getDefaultSsid();
		}
	}
	
	public int fetchWifiDeviceSharedNetworkUsersRxRate(ParamSharedNetworkDTO psn){
		try{
			return psn.getUsers_rx_rate()/InternetSpeedsUnit;
		}catch(Exception ex){
			return WifiDeviceHelper.SharedNetworkWifi_Default_Users_rx_rate/InternetSpeedsUnit;
		}
	}
	
	public int fetchWifiDeviceSharedNetworkUsersTxRate(ParamSharedNetworkDTO psn){
		try{
			return psn.getUsers_tx_rate()/InternetSpeedsUnit;
		}catch(Exception ex){
			return WifiDeviceHelper.SharedNetworkWifi_Default_Users_tx_rate/InternetSpeedsUnit;
		}
	}
	public static final int InternetSpeedsUnit = 128;
	
	public String fetchAccessInternetCardAmountRange(ParamSharedNetworkDTO psn, Integer commdityid,Integer umactype){
		String amount = null;
		switch (commdityid) {
		case BusinessRuntimeConfiguration.Reward_Month_Internet_Commdity_ID:
			if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue()){
				amount = (psn == null || StringUtils.isEmpty(psn.getCash_pc_month()))?ParamSharedNetworkDTO.Default_Cash_Pc_For_Month:psn.getCash_pc_month();
			}else{
				amount = (psn == null || StringUtils.isEmpty(psn.getCash_mobile_month()))?ParamSharedNetworkDTO.Default_Cash_Mobile_For_Month:psn.getCash_mobile_month();
			}
			break;
		case BusinessRuntimeConfiguration.Reward_Week_Internet_Commdity_ID:
			if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue()){
				amount = (psn == null || StringUtils.isEmpty(psn.getCash_pc_week()))?ParamSharedNetworkDTO.Default_Cash_Pc_For_Week:psn.getCash_pc_week();
			}else{
				amount = (psn == null || StringUtils.isEmpty(psn.getCash_mobile_week()))?ParamSharedNetworkDTO.Default_Cash_Mobile_For_Week:psn.getCash_mobile_week();
			}
			break;
		case BusinessRuntimeConfiguration.Reward_Day_Internet_Commdity_ID:
			if(OrderUmacType.Pc.getKey().intValue() == umactype.intValue()){
				amount = (psn == null || StringUtils.isEmpty(psn.getCash_pc_day()))?ParamSharedNetworkDTO.Default_Cash_Pc_For_Day:psn.getCash_pc_day();
			}else{
				amount = (psn == null || StringUtils.isEmpty(psn.getCash_mobile_day()))?ParamSharedNetworkDTO.Default_Cash_Mobile_For_Day:psn.getCash_mobile_day();
			}
			break;

		default:
			break;
		}
		return amount;
	}
}
