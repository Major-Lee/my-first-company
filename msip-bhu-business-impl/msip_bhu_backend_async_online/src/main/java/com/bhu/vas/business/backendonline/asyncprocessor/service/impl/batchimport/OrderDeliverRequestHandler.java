package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchimport;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.commdity.TechServiceDataDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderProcessStatus;
import com.bhu.vas.api.helper.BusinessEnumType.OrderStatus;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.business.asyn.spring.model.async.OrderDeliverRequestDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.buservice.BackendBusinessService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.PageHelper;

@Service
public class OrderDeliverRequestHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(OrderDeliverRequestHandler.class);

	@Resource
	private OrderFacadeService orderFacadeService;
	@Resource
	private OrderService orderService;
	
	@Resource
	private BackendBusinessService backendBusinessService;
	
	@Resource
	private ChargingFacadeService chargingFacadeService;
	
	

	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		try{
			OrderDeliverRequestDTO dto = JsonHelper.getDTO(message, OrderDeliverRequestDTO.class);
			Order order = orderService.getById(dto.getOrderid());
			if(order == null){
				logger.error(String.format("order is null: %s", dto.getOrderid()));
				return;
			}
			if(BusinessEnumType.CommdityCategory.SoftServiceLimit.getCategory() != order.getType()){
				logger.error(String.format("order type error: %s", dto.getOrderid()));
				return;
			}

			Boolean noapp = null;
			Boolean cbtf = null;

			TechServiceDataDTO context_dto = JsonHelper.getDTO(order.getContext(), TechServiceDataDTO.class);
			if(context_dto == null || context_dto.getMacs() == null || context_dto.getMacs().isEmpty()){
				logger.error(String.format("order context empty: %s", dto.getOrderid()));
				return;
			}

			if(BusinessRuntimeConfiguration.Soft_Service_Noapp_Commdity_ID == order.getCommdityid())
				noapp = Boolean.TRUE;
			if(BusinessRuntimeConfiguration.Soft_Service_CanbeTurnoff_Commdity_ID == order.getCommdityid())
				cbtf = Boolean.TRUE;
												
			
			List<String> all_dmacs = context_dto.getMacs();
			int total = all_dmacs.size();
			int totalPages = PageHelper.getTotalPages(total, 100);
			
			for(int pageno= 1;pageno<=totalPages;pageno++){
				List<String> pages = PageHelper.pageList(all_dmacs, pageno, 100);
				logger.info(String.format("pageno:%s pagesize:%s pages:%s", pageno,100,pages));
				
				for(String dmac:pages){
					chargingFacadeService.doWifiDeviceSharedealConfigsUpdate(null, null, null,null, null,
							dmac, 
							cbtf, noapp,
							null,
							true,
							null, null, null, null,
		        			false);
				}
				
				try {
					backendBusinessService.blukIndexs(pages);
					Thread.sleep(500);
					orderFacadeService.orderStatusChanged(order, OrderStatus.DeliverCompleted.getKey(), OrderProcessStatus.DeliverCompleted.getKey());
				} catch (InterruptedException e) {
					e.printStackTrace(System.out);
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			e.printStackTrace(System.out);
		}finally{
		}
	}
}
