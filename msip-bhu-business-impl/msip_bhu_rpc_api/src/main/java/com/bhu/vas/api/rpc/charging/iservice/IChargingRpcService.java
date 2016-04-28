package com.bhu.vas.api.rpc.charging.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IChargingRpcService {
	
	public RpcResponseDTO<BatchImportVTO> doInputDeviceRecord(int uid,int countrycode,String mobileno,double sharedeal_manufacturer_percent,boolean canbeturnoff,String remark);
	public RpcResponseDTO<BatchImportVTO> doConfirmDeviceRecord(int uid,String batchno);
	public RpcResponseDTO<TailPage<BatchImportVTO>> doPages(int uid,int status,int pageNo,int pageSize);
}
