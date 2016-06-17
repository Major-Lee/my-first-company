package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.charging.vto.SharedealDefaultVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
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
			String remark) {
		try{
			User operUser = chargingFacadeService.getUserService().getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(
					chargingFacadeService.doBatchImportCreate(uid, countrycode, mobileno,distributor_uid,
							sellor,partner,
							canbeturnoff, 
							enterpriselevel,
							customized,
							sharedeal_owner_percent,sharedeal_manufacturer_percent,sharedeal_distributor_percent,
							range_cash_mobile, range_cash_pc, access_internet_time,
							remark));
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
			String access_internet_time) {
		try{
			//User operUser = chargingFacadeService.getUserService().getById(uid);
			//UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			asyncDeliverMessageService.sendBatchSharedealModifyActionMessage(uid, message, canbeturnoff,enterpriselevel,customized, 
					owner_percent,manufacturer_percent,distributor_percent, range_cash_mobile, range_cash_pc, access_internet_time);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
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
