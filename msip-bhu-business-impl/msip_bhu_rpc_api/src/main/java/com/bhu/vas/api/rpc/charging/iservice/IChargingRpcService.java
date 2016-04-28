package com.bhu.vas.api.rpc.charging.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;

public interface IChargingRpcService {
	
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,String mobileno,String filepath_suffix,String remark);
	public RpcResponseDTO<BatchImportVTO> doConfirmDeviceRecord(int uid,String import_id);
}
