package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
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
			double sharedeal_manufacturer_percent, 
			boolean canbeturnoff,
			String remark) {
		try{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(chargingFacadeService.doBatchImportCreate(uid, countrycode, mobileno,sharedeal_manufacturer_percent,canbeturnoff, remark));
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<BatchImportVTO> doConfirmDeviceRecord(int uid,String batchno) {
		try{
			BatchImportVTO importVTO = chargingFacadeService.doConfirmDeviceRecord(uid, batchno);
			//TODO:async message
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(importVTO);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}