package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.OnlineEnum;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType.SnkTurnStateEnum;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.api.rpc.tag.model.TagGroupRelation;
import com.bhu.vas.api.rpc.user.dto.UserDeviceInfoDTO;
import com.bhu.vas.api.rpc.user.dto.UserIncomeDTO;
import com.bhu.vas.api.rpc.user.dto.UserManageDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserTransInfoDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.tag.service.TagGroupService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.validate.UserTypeValidateService;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * userManage RPC组件的业务service
 * @author Jason
 *
 */
@Service
public class UserManageFacadeService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	
	@Resource
	private TagGroupService tagGroupService;
	
	@Resource
	private TagGroupRelationService tagGroupRelationService;
	
	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	/**
	 * 查询用户交易信息
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserIncomeDTO>> queryUserIncomeDetail(int uid,String transtype,String transmode,int pageno,int pagesize){
		try{
			User user  = userService.getById(uid);
			if(user == null ){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
			}
			//UserTypeValidateService.validConsoleUser(user);
			//查询用户钱包信息
			UserIncomeDTO userIncomeDTO = new UserIncomeDTO();
			UserWalletDetailVTO userWallet = userWalletFacadeService.walletDetail(uid);
			if(userWallet == null){
				userIncomeDTO.setWithdraw("0.00");
				userIncomeDTO.setVcurrency("0");
				userIncomeDTO.setWalletMoney("");
			}else{
				userIncomeDTO.setWalletMoney(String.valueOf(userWallet.getCash()));
				userIncomeDTO.setVcurrency(String.valueOf(userWallet.getVcurrency_total()));
				if(userWallet.getPayments() != null && userWallet.getPayments().size()>0){
					userIncomeDTO.setWithdraw(userWallet.getPayments().get(0).getNick());
				}else{
					userIncomeDTO.setWithdraw("");
				}
			}
			
			//TODO 获取历史总收益 订单数 打赏成功数信息
			
			//根据uid查询用户交易信息
			UWalletTransMode tmode = null;
			if(StringUtils.isNotEmpty(transmode)){
				tmode = UWalletTransMode.fromKey(transmode);
			}
			
			UWalletTransType ttype = null;
			if(StringUtils.isNotEmpty(transtype)){
				ttype = UWalletTransType.fromKey(transtype);
			}
			List<UserIncomeDTO> vtos = new ArrayList<UserIncomeDTO>();
			TailPage<UserWalletLog> walletPages = userWalletFacadeService.pageUserWalletlogs(uid, tmode, ttype, pageno, pagesize);
			List<UserTransInfoDTO> userTransList = new ArrayList<UserTransInfoDTO>();
			UserTransInfoDTO userTransInfoDTO = null;
			if(walletPages != null){
				userTransInfoDTO = new UserTransInfoDTO();
				for (UserWalletLog log:walletPages.getItems()) {
					if(log == null){
						continue;
					}
					userTransInfoDTO.setUid(uid);
					userTransInfoDTO.setTransType(log.getTranstype());
					userTransInfoDTO.setTransNo(log.getOrderid());
					userTransInfoDTO.setTransTime(log.getUpdated_at().toString());
					
					//根据订单Id获取mac和终端mac信息
					Order orders = orderService.getById(log.getOrderid());
					if(orders == null){
						userTransInfoDTO.setDeciveMac("");
						userTransInfoDTO.setTerminalMac("");
					}else{
						userTransInfoDTO.setDeciveMac(orders.getMac());
						userTransInfoDTO.setTerminalMac(orders.getUmac());
					}
					userTransList.add(userTransInfoDTO);
				}
				
			}
			userIncomeDTO.setUserTransInfoList(userTransList);
			vtos.add(userIncomeDTO);
			TailPage<UserIncomeDTO> pages = new CommonPage<UserIncomeDTO>(walletPages.getPageNumber(), pagesize, walletPages.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(pages);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/**
	 * 查询用户设备信息
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserManageDeviceDTO>> queryUserDeviceInfo(int uid,int pageno,int pagesize){
		try{
			User user  = userService.getById(uid);
			if(user == null ){
				throw new BusinessI18nCodeException(ResponseErrorCode.USER_DATA_NOT_EXIST);
			}
			
			UserManageDeviceDTO userManageDeviceDTO = new UserManageDeviceDTO();
			//根据用户Id查询设备数量
			long deviceNum = 0;
			deviceNum = wifiDeviceDataSearchService.searchCountByCommon(uid, "", "", "", OnlineEnum.Offline.getType(), "");
			userManageDeviceDTO.setDeviceNum(String.valueOf(deviceNum));
			//根据用户Id查询在线设备数量
			long onLinedeviceNum = 0;
			onLinedeviceNum = wifiDeviceDataSearchService.searchCountByCommon(uid, "", "", "", OnlineEnum.Online.getType(), "");
			userManageDeviceDTO.setDeviceOnlineNum(String.valueOf(onLinedeviceNum));
			//根据用户Id查询开启打赏设备数量
			long rewardDeviceNum = 0;
			rewardDeviceNum = wifiDeviceDataSearchService.searchCountByCommon(uid, "", "", "", "", SnkTurnStateEnum.On.getType());
			userManageDeviceDTO.setRewardDeviceNum(String.valueOf(rewardDeviceNum));
			//根据用户Id查询获取设备详细信息 
			List<UserDeviceInfoDTO> deviceList = new ArrayList<UserDeviceInfoDTO>();
			Page<WifiDeviceDocument> devicePages = wifiDeviceDataSearchService.searchPageByCommon(uid, "", "", "", "", "", pageno, pagesize);
			List<UserManageDeviceDTO> vtos = new ArrayList<UserManageDeviceDTO>();
			int total = 0;
			if(devicePages != null){
				total = (int)devicePages.getTotalElements();
				if(total == 0){
					vtos = Collections.emptyList();
				}else{
					List<WifiDeviceDocument> wifiDeviceDocumentList = devicePages.getContent();
					if(wifiDeviceDocumentList.isEmpty()){
						vtos = Collections.emptyList();
					}else{
						UserDeviceInfoDTO userDeviceInfoDTO = null;
						for (WifiDeviceDocument wifiDeviceDocument:wifiDeviceDocumentList) {
							if(wifiDeviceDocument == null){
								continue;
							}
							userDeviceInfoDTO = new UserDeviceInfoDTO();
							userDeviceInfoDTO.setDeviceType(wifiDeviceDocument.getD_type());
							userDeviceInfoDTO.setDeviceMac(wifiDeviceDocument.getD_mac());
							userDeviceInfoDTO.setDeviceSN(wifiDeviceDocument.getD_sn());
							userDeviceInfoDTO.setAccNetType(wifiDeviceDocument.getD_snk_type());
							//用户分成比例 TODO
							//userDeviceInfoDTO.setUserGainsharing(userGainsharing);
							//收益  TODO
							//userDeviceInfoDTO.setIncome(income);
							//状态
							userDeviceInfoDTO.setDeviceStatus(wifiDeviceDocument.getD_online());
							//所属分组
							//根据设备mac查询设备分组信息
							TagGroupRelation tagGroupRelation = tagGroupRelationService.getById(wifiDeviceDocument.getD_mac());
							if(tagGroupRelation != null){
								//根据分组Id查询分组详细
								TagGroup tagGroup = tagGroupService.getById(tagGroupRelation.getGid());
								if(tagGroup != null){
									userDeviceInfoDTO.setSubordinateGroup(tagGroup.getName());
								}else{
									userDeviceInfoDTO.setSubordinateGroup("");
								}
							}else{
								userDeviceInfoDTO.setSubordinateGroup("");
							}
							//userDeviceInfoDTO.setSubordinateGroup(subordinateGroup);
							deviceList.add(userDeviceInfoDTO);
						}
						userManageDeviceDTO.setUserDeviceInfoList(deviceList);
						vtos.add(userManageDeviceDTO);
					}
				}
				
			}
			TailPage<UserManageDeviceDTO> pages = new CommonPage<UserManageDeviceDTO>(pageno, pagesize,total, vtos);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(pages);
		}catch(BusinessI18nCodeException bex){
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(bex.getErrorCode());
			//return new RpcResponseDTO<TaskResDTO>(bex.getErrorCode(),null);
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			//return new RpcResponseDTO<TaskResDTO>(ResponseErrorCode.COMMON_BUSINESS_ERROR,null);
		}
	}
}
