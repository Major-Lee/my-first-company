package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
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
			String remark) {
		try{
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(
					chargingFacadeService.doBatchImportCreate(uid, countrycode, mobileno,
							sellor,partner,
							sharedeal_owner_percent,
							canbeturnoff, 
							enterpriselevel,
							remark));
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
			TailPage<BatchImportVTO> vtos = chargingFacadeService.pagesBatchImport(uid, status,pageNo,pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vtos);
		}catch(BusinessI18nCodeException bex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode(),bex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
