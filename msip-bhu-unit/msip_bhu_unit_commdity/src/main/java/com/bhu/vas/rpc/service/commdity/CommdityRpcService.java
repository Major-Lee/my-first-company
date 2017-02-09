package com.bhu.vas.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.CommdityAmountDTO;
import com.bhu.vas.api.dto.commdity.CommdityDTO;
import com.bhu.vas.api.dto.commdity.CommdityPhysicalDTO;
import com.bhu.vas.api.dto.commdity.CommditySaasAmountDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.ICommdityRpcService;
import com.bhu.vas.rpc.facade.CommdityUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("commdityRpcService")
public class CommdityRpcService implements ICommdityRpcService{
	private final Logger logger = LoggerFactory.getLogger(CommdityRpcService.class);
	
	@Resource
	private CommdityUnitFacadeService commdityUnitFacadeService;

	@Override
	public RpcResponseDTO<TailPage<CommdityDTO>> commdityPages(Integer status, Integer category, int pageNo, int pageSize) {
		logger.info(String.format("commdityPages with status[%s] category[%s] pageNo[%s] pageSize[%s]", status, category, pageNo, pageSize));
		return commdityUnitFacadeService.commdityPages(status, category, pageNo, pageSize);
	}
	
	public RpcResponseDTO<CommdityAmountDTO> rewardIntervalAMount(Integer commdityid, String mac, String umac, 
			Integer umactype){
		logger.info(String.format("rewardIntervalAMount with commdityid[%s] mac[%s] umac[%s] umactype[%s]", commdityid, 
				 mac, umac, umactype));
		return commdityUnitFacadeService.rewardIntervalAMount(commdityid, mac, umac, umactype);
	}

	@Override
	public RpcResponseDTO<CommdityPhysicalDTO> physical_get_address(String umac) {
		logger.info(String.format("physical_get_address with umac[%s]", umac));
		return commdityUnitFacadeService.physical_get_address(umac);
	}

	@Override
	public RpcResponseDTO<CommdityPhysicalDTO> physical_set_address(String umac, String uname, String acc,
			String address, boolean needInvoice, String invoiceDetail) {
		logger.info(String.format("physical_set_address with umac[%s] uname[%s] acc[%s] address[%s] needInvoice[%s] invoiceDetail[%s]", 
				umac, uname, acc, address, needInvoice, invoiceDetail));
		return commdityUnitFacadeService.physical_set_address(umac, uname, acc, address, needInvoice, invoiceDetail);
	}

	@Override
	public RpcResponseDTO<CommditySaasAmountDTO> saasAmount(String uid) {
		logger.info(String.format("saasAmount uid[%s]", uid));
		return commdityUnitFacadeService.saasAmount(uid);
	}

}
