package com.bhu.vas.rpc.service.device;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.charging.vto.OpsBatchImportVTO;
import com.bhu.vas.api.rpc.charging.vto.SharedealDefaultVTO;
import com.bhu.vas.api.vto.device.DeviceSharedealVTO;
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
	public RpcResponseDTO<TailPage<BatchImportVTO>> doStatRowPages(int uid,int status, boolean upact, int lastrowid, int start, int ps) {
		logger.info(String.format("doStatRowPages uid:%s status:%s upact:%s  lastrowid:%s start:%s ps:%s",uid, status,upact,lastrowid,start,ps));
		return chargingUnitFacadeService.doStatRowPages(uid,status,upact,lastrowid,start,ps);
	}
	@Override
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,
			int countrycode, String bmobileno,int distributor_uid,
			String sellor, String partner,
			boolean canbeturnoff,boolean enterpriselevel, 
			boolean customized,
			String sharedeal_owner_percent,String sharedeal_manufacturer_percent,String sharedeal_distributor_percent,
			String range_cash_mobile,String range_cash_pc,String access_internet_time,
			String channel_lv1, String channel_lv2,
			String remark) {
		logger.info(String.format("doInputDeviceRecord uid:%s countrycode:%s bmobileno:%s sellor:%s partner:%s canbeturnoff:%s enterpriselevel:%s customized:%s sharedeal_owner_percent:%s sharedeal_manufacturer_percent:%s sharedeal_distributor_percent:%s range_cash_mobile:%s range_cash_pc:%s access_internet_time:%s channel_lv1:%s channel_lv2:%s remark:%s",
				uid,countrycode, bmobileno,sellor,partner,canbeturnoff,enterpriselevel,customized,sharedeal_owner_percent,sharedeal_manufacturer_percent, sharedeal_distributor_percent, range_cash_mobile, range_cash_pc, access_internet_time, channel_lv1, channel_lv2, remark));
		return chargingUnitFacadeService.doInputDeviceRecord(uid,countrycode, bmobileno,distributor_uid,
				sellor,partner,
				canbeturnoff,enterpriselevel,
				customized,
				sharedeal_owner_percent, sharedeal_manufacturer_percent,sharedeal_distributor_percent,
				range_cash_mobile, range_cash_pc, access_internet_time, channel_lv1, channel_lv2, remark);

	}

	
	@Override
	public RpcResponseDTO<OpsBatchImportVTO> doOpsInputDeviceRecord(int uid, String opsid,
			int countrycode,String mobileno,int distributor_uid, String distributor_type,
			String sellor,String partner,
            boolean canbeturnoff,
            String sharedeal_owner_percent,String sharedeal_manufacturer_percent,String sharedeal_distributor_percent,
			String channel_lv1, String channel_lv2,
			String sns,
            String remark){
		logger.info(String.format("doInputDeviceRecord uid:%s opsid:%s countrycode:%s bmobileno:%s sellor:%s partner:%s canbeturnoff:%s sharedeal_owner_percent:%s sharedeal_manufacturer_percent:%s sharedeal_distributor_percent:%s channel_lv1:%s channel_lv2:%s sns:%s remark:%s",
				uid, opsid, countrycode, mobileno,sellor,partner,canbeturnoff,sharedeal_owner_percent,sharedeal_manufacturer_percent, sharedeal_distributor_percent, channel_lv1, channel_lv2, sns, remark));
		return chargingUnitFacadeService.doOpsInputDeviceRecord(uid, opsid, countrycode, mobileno,distributor_uid, distributor_type,
				sellor,partner,
				canbeturnoff,
				sharedeal_owner_percent, sharedeal_manufacturer_percent,sharedeal_distributor_percent,
				channel_lv1, channel_lv2, sns, remark);
	}


	@Override
	public RpcResponseDTO<Boolean> doBatchSharedealModify(int uid,
			String message, Boolean canbeturnoff,Boolean enterpriselevel, 
			boolean customized,
			String owner_percent,String manufacturer_percent,String distributor_percent,
			String range_cash_mobile, String range_cash_pc,
			String access_internet_time,
			String free_access_internet_time,
			boolean needCheckBinding) {
		logger.info(String.format("doBatchSharedealModify uid:%s message:%s canbeturnoff:%s enterpriselevel:%s customized:%s owner_percent:%s manufacturer_percent:%s distributor_percent:%s range_cash_mobile:%s range_cash_pc:%s access_internet_time:%s free_access_internet_time:%s needCheckBinding",
				uid,message, canbeturnoff,enterpriselevel,customized,owner_percent,manufacturer_percent,distributor_percent,range_cash_mobile, range_cash_pc,access_internet_time, free_access_internet_time, needCheckBinding));
		return chargingUnitFacadeService.doBatchSharedealModify(uid,message, canbeturnoff,enterpriselevel,customized,owner_percent,manufacturer_percent,distributor_percent,range_cash_mobile, range_cash_pc,access_internet_time, free_access_internet_time, needCheckBinding);
	}

	@Override
	public RpcResponseDTO<SharedealDefaultVTO> doFetchDefaultSharedeal(int uid) {
		logger.info(String.format("doFetchDefaultSharedeal uid:%s",uid));
		return chargingUnitFacadeService.doFetchDefaultSharedeal(uid);
	}

	@Override
	public RpcResponseDTO<DeviceSharedealVTO> sharedealDetail(int uid,String mac) {
		logger.info(String.format("sharedealDetail uid:%s mac[%s]",uid,mac));
		return chargingUnitFacadeService.sharedealDetail(uid, mac);
	}

}
