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
	public RpcResponseDTO<BatchImportVTO> doCancelDeviceRecord(int uid,String batchno) {
		logger.info(String.format("doCancelDeviceRecord uid:%s batchno:%s",uid, batchno));
		return chargingUnitFacadeService.doCancelDeviceRecord(uid, batchno);
	}
    
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
			boolean canbeturnoff,boolean enterpriselevel, 
			boolean customized,
			String sharedeal_owner_percent,
			String range_cash_mobile,String range_cash_pc,String access_internet_time,
			String remark) {
		logger.info(String.format("doInputDeviceRecord uid:%s countrycode:%s bmobileno:%s sellor:%s partner:%s canbeturnoff:%s enterpriselevel:%s customized:%s sharedeal_owner_percent:%s range_cash_mobile:%s range_cash_pc:%s access_internet_time:%s remark:%s",
				uid,countrycode, bmobileno,sellor,partner,canbeturnoff,enterpriselevel,customized,sharedeal_owner_percent, range_cash_mobile, range_cash_pc, access_internet_time, remark));
		return chargingUnitFacadeService.doInputDeviceRecord(uid,countrycode, bmobileno,sellor,partner,
				canbeturnoff,enterpriselevel,
				customized,
				sharedeal_owner_percent, range_cash_mobile, range_cash_pc, access_internet_time, remark);

	}

	@Override
	public RpcResponseDTO<Boolean> doBatchSharedealModify(int uid,
			String message, Boolean canbeturnoff,Boolean enterpriselevel, 
			boolean customized,
			String owner_percent,
			String range_cash_mobile, String range_cash_pc,
			String access_internet_time) {
		logger.info(String.format("doBatchSharedealModify uid:%s message:%s canbeturnoff:%s enterpriselevel:%s customized:%s owner_percent:%s range_cash_mobile:%s range_cash_pc:%s",uid,message, canbeturnoff,enterpriselevel,customized,owner_percent,range_cash_pc, range_cash_pc,access_internet_time));
		return chargingUnitFacadeService.doBatchSharedealModify(uid,message, canbeturnoff,enterpriselevel,customized,owner_percent,range_cash_mobile, range_cash_pc,access_internet_time);
	}





}
