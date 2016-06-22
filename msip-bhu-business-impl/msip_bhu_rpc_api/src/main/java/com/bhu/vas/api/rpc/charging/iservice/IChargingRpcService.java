package com.bhu.vas.api.rpc.charging.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.charging.vto.SharedealDefaultVTO;
import com.bhu.vas.api.vto.device.DeviceSharedealVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IChargingRpcService {
	
	public RpcResponseDTO<SharedealDefaultVTO> doFetchDefaultSharedeal(int uid);
	public RpcResponseDTO<DeviceSharedealVTO> sharedealDetail(int uid,String mac);
	public RpcResponseDTO<Boolean> doBatchSharedealModify(int uid, String message, 
			Boolean canbeturnoff,
			Boolean enterpriselevel,
			boolean customized,
			String owner_percent,String manufacturer_percent,String distributor_percent,
			String range_cash_mobile,String range_cash_pc,String access_internet_time,boolean needCheckBinding);
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,
			int countrycode,String bmobileno,int distributor_uid,
			String sellor,String partner,
            boolean canbeturnoff,boolean enterpriselevel,
            boolean customized,
			String sharedeal_owner_percent,String sharedeal_manufacturer_percent,String sharedeal_distributor_percent,
			String range_cash_mobile,String range_cash_pc,String access_internet_time,
            String remark);
	public RpcResponseDTO<BatchImportVTO> doCancelDeviceRecord(int uid,String batchno);
	public RpcResponseDTO<BatchImportVTO> doConfirmDeviceRecord(int uid,String batchno);
	public RpcResponseDTO<TailPage<BatchImportVTO>> doPages(int uid,int status,int pageNo,int pageSize);
}
