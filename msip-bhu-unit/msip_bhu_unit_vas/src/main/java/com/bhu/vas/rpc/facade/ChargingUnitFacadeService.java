package com.bhu.vas.rpc.facade;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.model.WifiDeviceSharedealConfigs;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.charging.vto.OpsBatchImportVTO;
import com.bhu.vas.api.rpc.charging.vto.SharedealDefaultVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.vto.device.DeviceSharedealVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.validate.UserTypeValidateService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * task RPC组件的业务service
 * @author Edmond Lee
 *
 */
@Service
public class ChargingUnitFacadeService {
	//private final Logger logger = LoggerFactory.getLogger(ChargingUnitFacadeService.class);
	//@Resource
	//private DeliverMessageService deliverMessageService;
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,int countrycode,
			String mobileno,int distributor_uid, 
			String sellor,String partner,
			boolean canbeturnoff,
			boolean enterpriselevel,
			boolean customized,
			String sharedeal_owner_percent,String sharedeal_manufacturer_percent,String sharedeal_distributor_percent, 
			String range_cash_mobile,String range_cash_pc,String access_internet_time,
			String channel_lv1, String channel_lv2,
			String remark) {
		try{
			User operUser = chargingFacadeService.getUserService().getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			
			BatchImportVTO ret = 
					chargingFacadeService.doBatchImportCreate(uid, countrycode, mobileno,distributor_uid,
							sellor,partner,
							canbeturnoff, 
							enterpriselevel,
							customized,
							sharedeal_owner_percent,sharedeal_manufacturer_percent,sharedeal_distributor_percent,
							range_cash_mobile, range_cash_pc, access_internet_time,
							channel_lv1, channel_lv2,
							remark);
			asyncDeliverMessageService.sendBatchImportPreCheckMessage(uid, ret.getId());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(ret);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<OpsBatchImportVTO> doOpsInputDeviceRecord(int uid, String opsid, int countrycode,
			String mobileno,int distributor_uid, String distributor_type,
			String sellor,String partner,
			boolean canbeturnoff,
			String sharedeal_owner_percent,String sharedeal_manufacturer_percent,String sharedeal_distributor_percent, 
			String channel_lv1, String channel_lv2,
			String sns,
			String remark) {
		try{
			User operUser = chargingFacadeService.getUserService().getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			
			BatchImportVTO ret = 
					chargingFacadeService.doOpsBatchImportCreate(uid, opsid, countrycode, mobileno,distributor_uid, distributor_type,
							sellor,partner,
							canbeturnoff, 
							sharedeal_owner_percent,sharedeal_manufacturer_percent,sharedeal_distributor_percent,
							channel_lv1, channel_lv2,
							remark);
			
			System.out.println("path:"+ret.toAbsoluteFileInputPath());
			File targetFile = new File(ret.toAbsoluteFileInputPath());
			targetFile.getParentFile().mkdirs();
			FileUtils.writeStringToFile(targetFile, sns);

			asyncDeliverMessageService.sendBatchImportConfirmActionMessage(uid, ret.getId());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(OpsBatchImportVTO.fromBatchImportVTO(ret));
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> doBatchSharedealModify(int uid,
			String message, Boolean canbeturnoff,Boolean enterpriselevel,
			boolean customized,
			String owner_percent,String manufacturer_percent,String distributor_percent,
			String range_cash_mobile, String range_cash_pc,
			String access_internet_time,
//			String channel_lv1, String channel_lv2,
			boolean needCheckBinding) {
		try{
			//User operUser = chargingFacadeService.getUserService().getById(uid);
			//UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			asyncDeliverMessageService.sendBatchSharedealModifyActionMessage(uid, message, canbeturnoff,enterpriselevel,customized, 
					owner_percent,manufacturer_percent,distributor_percent, range_cash_mobile, range_cash_pc, access_internet_time,
					needCheckBinding);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<DeviceSharedealVTO> sharedealDetail(int uid,String mac){
		try{
			/*WifiDevice wifiDevice = chargingFacadeService.getWifiDeviceService().getById(mac);
			if(wifiDevice == null){
				throw new BusinessI18nCodeException(ResponseErrorCode.DEVICE_DATA_NOT_EXIST,new String[]{"mac"});
			}*/
			UserValidateServiceHelper.validateUser(uid, chargingFacadeService.getUserService());
			UserValidateServiceHelper.validateUserDevice(uid, mac, chargingFacadeService.getUserWifiDeviceFacadeService());
			//分成详情
			WifiDeviceSharedealConfigs configs = chargingFacadeService.userfulWifiDeviceSharedealConfigsJust4View(mac);
			DeviceSharedealVTO dsv = new DeviceSharedealVTO();
			dsv.setMac(configs.getId());
			dsv.setBatchno(configs.getBatchno());
			dsv.setOwner_percent(configs.getOwner_percent());
			dsv.setManufacturer_percent(configs.getManufacturer_percent());
			dsv.setDistributor_percent(configs.getDistributor_percent());
			dsv.setRcm(configs.getRange_cash_mobile());
			dsv.setRcp(configs.getRange_cash_pc());
			dsv.setAitm(configs.getAit_mobile());
			dsv.setAitp(configs.getAit_pc());
			dsv.setCanbeturnoff(configs.isCanbe_turnoff());
			dsv.setRuntime_applydefault(configs.isRuntime_applydefault());
			dsv.setCustomized(configs.isCustomized());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(dsv);
		}catch(BusinessI18nCodeException i18nex){
			i18nex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	
	public RpcResponseDTO<BatchImportVTO> doCancelDeviceRecord(int uid,String batchno) {
		try{
			User operUser = chargingFacadeService.getUserService().getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			BatchImportVTO importVTO = chargingFacadeService.doCancelDeviceRecord(uid, batchno);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(importVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<BatchImportVTO> doConfirmDeviceRecord(int uid,String batchno) {
		try{
			User operUser = chargingFacadeService.getUserService().getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			BatchImportVTO importVTO = chargingFacadeService.doConfirmDeviceRecord(uid, batchno);
			//TODO:async message
			asyncDeliverMessageService.sendBatchImportConfirmActionMessage(uid, batchno);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(importVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<TailPage<BatchImportVTO>> doPages(int uid,
			int status, int pageNo, int pageSize) {
		try{
			User operUser = chargingFacadeService.getUserService().getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			TailPage<BatchImportVTO> vtos = chargingFacadeService.pagesBatchImport(uid, status,pageNo,pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<TailPage<BatchImportVTO>> doStatRowPages(int uid,int status, boolean upact, int lastrowid, int start, int ps) {
		try{
			User operUser = chargingFacadeService.getUserService().getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			
			//TailPage<BatchImportVTO> vtos = chargingFacadeService.pagesBatchImport(uid, status,pageNo,pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(null);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public RpcResponseDTO<SharedealDefaultVTO> doFetchDefaultSharedeal(int uid){
		try{
			//User operUser = chargingFacadeService.getUserService().getById(uid);
			//UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(chargingFacadeService.defaultDeviceSharedealConfigs());
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
