package com.bhu.vas.rpc.service.device;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserIncomeDTO;
import com.bhu.vas.api.rpc.user.dto.UserManageDeviceDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserManageRpcService;
import com.bhu.vas.rpc.facade.UserManageFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("userManageRpcService")
public class UserManageRpcService implements IUserManageRpcService{
	private final Logger logger = LoggerFactory.getLogger(UserManageRpcService.class);
	@Resource
	private UserManageFacadeService userManagetFacadeService;
	@Override
	public RpcResponseDTO<TailPage<UserIncomeDTO>> queryUserIncomeDetail(
			int uid, String transtype, String transmode, int pageno,
			int pagesize) {
		logger.info(String.format("queryUserIncomeDetail with uid[%s] transtype[%s] pageno[%s] pagesize[%s] ",uid,transtype,pageno,pagesize));
		return userManagetFacadeService.queryUserIncomeDetail(uid, transtype, transmode, pageno, pagesize);
	}

	@Override
	public RpcResponseDTO<TailPage<UserManageDeviceDTO>> queryUserDeviceInfo(
			int uid, int pageno, int pagesize) {
		logger.info(String.format("queryUserDeviceInfo with uid[%s] pageno[%s] pagesize[%s]",uid,pageno,pagesize));
		return userManagetFacadeService.queryUserDeviceInfo(uid,pageno,pagesize);
	}

	

}
