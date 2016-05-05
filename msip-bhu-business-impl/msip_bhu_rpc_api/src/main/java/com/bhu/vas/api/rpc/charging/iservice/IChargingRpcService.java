package com.bhu.vas.api.rpc.charging.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IChargingRpcService {
	public RpcResponseDTO<Boolean> doBatchSharedealModify(int uid, String message, 
			boolean canbeturnoff,double owner_percent,
			String range_cash_mobile,String range_cash_pc,String access_internet_time);
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,
			int countrycode,String bmobileno,
			String sellor,
            String partner,
			double sharedeal_owner_percent,
			boolean canbeturnoff,
			boolean enterpriselevel,
            String remark);
	public RpcResponseDTO<BatchImportVTO> doConfirmDeviceRecord(int uid,String batchno);
	public RpcResponseDTO<TailPage<BatchImportVTO>> doPages(int uid,int status,int pageNo,int pageSize);
}
