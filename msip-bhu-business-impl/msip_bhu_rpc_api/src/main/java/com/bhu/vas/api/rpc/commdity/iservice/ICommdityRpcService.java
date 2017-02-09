package com.bhu.vas.api.rpc.commdity.iservice;

import com.bhu.vas.api.dto.commdity.CommdityAmountDTO;
import com.bhu.vas.api.dto.commdity.CommdityDTO;
import com.bhu.vas.api.dto.commdity.CommdityPhysicalDTO;
import com.bhu.vas.api.dto.commdity.CommditySaasAmountDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface ICommdityRpcService {
	public RpcResponseDTO<TailPage<CommdityDTO>> commdityPages(Integer status, Integer category, int pageNo, int pageSize);
	
	public RpcResponseDTO<CommdityAmountDTO> rewardIntervalAMount(Integer commdityid, String mac, String umac, Integer umactype);

	public RpcResponseDTO<CommdityPhysicalDTO> physical_get_address(String umac_lower);

	public RpcResponseDTO<CommdityPhysicalDTO> physical_set_address(String umac_lower, String uname, String acc,
			String address, boolean needInvoice, String invoiceDetail);

	public RpcResponseDTO<CommditySaasAmountDTO> saasAmount(String uid);
}
