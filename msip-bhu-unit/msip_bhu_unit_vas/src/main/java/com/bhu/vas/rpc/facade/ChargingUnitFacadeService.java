package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
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
	@Resource
	private DeliverMessageService deliverMessageService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,int countrycode,
			String mobileno, 
			String sellor,String partner,
			double sharedeal_owner_percent, 
			boolean canbeturnoff,
			boolean enterpriselevel,
			String range_cash_mobile,String range_cash_pc,String access_internet_time,
			String remark) {
		try{
			User operUser = chargingFacadeService.getUserService().getById(uid);
			UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(
					chargingFacadeService.doBatchImportCreate(uid, countrycode, mobileno,
							sellor,partner,
							sharedeal_owner_percent,
							canbeturnoff, 
							enterpriselevel,
							range_cash_mobile, range_cash_pc, access_internet_time,
							remark));
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
			deliverMessageService.sendBatchImportConfirmActionMessage(uid, batchno);
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
	
	public RpcResponseDTO<Boolean> doBatchSharedealModify(int uid,
			String message, boolean canbeturnoff,boolean enterpriselevel, double owner_percent,
			String range_cash_mobile, String range_cash_pc,
			String access_internet_time) {
		try{
			//User operUser = chargingFacadeService.getUserService().getById(uid);
			//UserTypeValidateService.validUserType(operUser, UserType.SelfCmdUser.getSname());
			deliverMessageService.sendBatchSharedealModifyActionMessage(uid, message, canbeturnoff,enterpriselevel, owner_percent, range_cash_mobile, range_cash_pc, access_internet_time);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
