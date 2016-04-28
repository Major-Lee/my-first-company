package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.rpc.facade.ChargingUnitFacadeService;

/**
 * 
 * @author Edmond
 *
 */
@Service("vapRpcService")
public class ChargingRpcService  implements IChargingRpcService{
    private final Logger logger = LoggerFactory.getLogger(ChargingRpcService.class);

    @Resource
    private ChargingUnitFacadeService chargingUnitFacadeService;

	@Override
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,
			String mobileno, String filepath_suffix, String remark) {
		logger.info(String.format("doInputDeviceRecord uid:%s mobileno:%s filepath_suffix:%s remark:%s",uid, mobileno, filepath_suffix, remark));
		return chargingUnitFacadeService.doInputDeviceRecord(uid, mobileno, filepath_suffix, remark);
	}

	@Override
	public RpcResponseDTO<BatchImportVTO> doConfirmDeviceRecord(int uid,String import_id) {
		logger.info(String.format("doConfirmDeviceRecord uid:%s import_id:%s",uid, import_id));
		return chargingUnitFacadeService.doConfirmDeviceRecord(uid, import_id);
	}

}
