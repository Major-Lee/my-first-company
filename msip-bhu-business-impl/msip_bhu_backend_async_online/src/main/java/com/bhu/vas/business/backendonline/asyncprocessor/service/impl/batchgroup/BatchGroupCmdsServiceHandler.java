package com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchgroup;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.dto.sharednetwork.DeviceStatusExchangeDTO;
import com.bhu.vas.business.asyn.spring.model.async.group.OperGroupDTO;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class BatchGroupCmdsServiceHandler implements IMsgHandlerService {
	private final Logger logger = LoggerFactory.getLogger(BatchGroupCmdsServiceHandler.class);

	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;

	@Resource
	private TaskFacadeService taskFacadeService;

    @Resource
    private IDaemonRpcService daemonRpcService;
    
	@Override
	public void process(String message) {
		logger.info(String.format("process message[%s]", message));
		final OperGroupDTO operGroupDto = JsonHelper.getDTO(message, OperGroupDTO.class);
		
		final List<DownCmds> downCmdsList = new ArrayList<DownCmds>();
		
		wifiDeviceDataSearchService.iteratorAll(operGroupDto.getMessage(),
				new IteratorNotify<Page<WifiDeviceDocument>>() {
					@Override
					public void notifyComming(Page<WifiDeviceDocument> pages) {
						for (WifiDeviceDocument doc : pages) {
							
							String payload = autoGenerateCmds(operGroupDto.getUid(),doc.getD_mac(),
									operGroupDto.getOpt(),operGroupDto.getSubopt(),operGroupDto.getExtparams(),
									DeviceStatusExchangeDTO.build(doc.getD_workmodel(), doc.getD_origswver()));
							downCmdsList.add(DownCmds.builderDownCmds(doc.getD_mac(),payload));
						}
					    daemonRpcService.wifiMultiDevicesCmdsDown(downCmdsList.toArray(new DownCmds[0]));
					}
		});
		
	}
	/**
	 * 生成指令
	 * @param uid
	 * @param mac
	 * @param opt
	 * @param subopt
	 * @param extparams
	 * @param d_status_dto
	 * @return
	 */
	public String autoGenerateCmds(int uid, String mac, String opt, String subopt, String extparams,
			DeviceStatusExchangeDTO d_status_dto) {

		OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(opt);
		OperationDS ods_cmd = OperationDS.getOperationDSFromNo(subopt);
		String payload = taskFacadeService.apiCmdGenerate(uid, mac, opt_cmd, ods_cmd, extparams, 0, d_status_dto, null);

		return payload;
	}

}
