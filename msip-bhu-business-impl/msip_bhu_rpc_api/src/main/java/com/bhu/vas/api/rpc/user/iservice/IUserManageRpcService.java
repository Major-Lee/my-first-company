package com.bhu.vas.api.rpc.user.iservice;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserIncomeDTO;
import com.bhu.vas.api.rpc.user.dto.UserManageDeviceDTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;

public interface IUserManageRpcService {
	/**
	 * 根据uid查询用户提现充值记录列表
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserIncomeDTO>> queryUserIncomeDetail(int uid,String transtype,String transmode,int pageno,int pagesize);
	
	/**
	 * 查询用户设备信息
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserManageDeviceDTO>> queryUserDeviceInfo(int uid,int pageno,int pagesize);
}
