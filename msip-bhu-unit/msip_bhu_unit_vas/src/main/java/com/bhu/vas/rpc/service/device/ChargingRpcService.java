package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.rpc.facade.ChargingUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * 
 * @author Edmond
 *
 */
@Service("chargingRpcService")
public class ChargingRpcService  implements IChargingRpcService{
    private final Logger logger = LoggerFactory.getLogger(ChargingRpcService.class);

    @Resource
    private ChargingUnitFacadeService chargingUnitFacadeService;

	/*@Override
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,int countrycode,
			String mobileno,double sharedeal_manufacturer_percent,boolean canbeturnoff,String remark) {
		logger.info(String.format("doInputDeviceRecord uid:%s countrycode:%s mobileno:%s canbeturnoff:%s remark:%s",uid,countrycode, mobileno,canbeturnoff, remark));
		return chargingUnitFacadeService.doInputDeviceRecord(uid,countrycode, mobileno,sharedeal_manufacturer_percent,canbeturnoff, remark);
	}*/

	@Override
	public RpcResponseDTO<BatchImportVTO> doConfirmDeviceRecord(int uid,String batchno) {
		logger.info(String.format("doConfirmDeviceRecord uid:%s batchno:%s",uid, batchno));
		return chargingUnitFacadeService.doConfirmDeviceRecord(uid, batchno);
	}

	@Override
	public RpcResponseDTO<TailPage<BatchImportVTO>> doPages(int uid,
			int status, int pageNo, int pageSize) {
		logger.info(String.format("doPages uid:%s status:%s pageNo:%s pageSize:%s",uid, status,pageNo,pageSize));
		return chargingUnitFacadeService.doPages(uid,status,pageNo,pageSize);
	}

	@Override
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,
			int countrycode, String bmobileno, String sellor, String partner,
			double sharedeal_owner_percent, boolean canbeturnoff,
			boolean enterpriselevel, String remark) {
		logger.info(String.format("doInputDeviceRecord uid:%s countrycode:%s bmobileno:%s sellor:%s partner:%s sharedeal_owner_percent:%s canbeturnoff:%s enterpriselevel:%s remark:%s",uid,countrycode, bmobileno,sellor,partner,sharedeal_owner_percent,canbeturnoff,enterpriselevel, remark));
		return chargingUnitFacadeService.doInputDeviceRecord(uid,countrycode, bmobileno,sellor,partner,sharedeal_owner_percent,canbeturnoff,enterpriselevel, remark);

	}

}
