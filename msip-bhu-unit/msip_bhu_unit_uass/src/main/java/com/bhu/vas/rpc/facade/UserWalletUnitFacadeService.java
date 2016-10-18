package com.bhu.vas.rpc.facade;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.CommdityApplication;
import com.bhu.vas.api.helper.BusinessEnumType.OAuthType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.helper.BusinessEnumType.UWithdrawStatus;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.dto.WithdrawCostInfo;
import com.bhu.vas.api.rpc.charging.model.UserIncomeMonthRank;
import com.bhu.vas.api.rpc.charging.model.UserIncomeRank;
import com.bhu.vas.api.rpc.commdity.model.Order;
import com.bhu.vas.api.rpc.statistics.model.FincialStatistics;
import com.bhu.vas.api.rpc.unifyStatistics.vto.UcloudMacStatisticsVTO;
import com.bhu.vas.api.rpc.user.dto.ShareDealWalletSummaryProcedureVTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserPublishAccount;
import com.bhu.vas.api.rpc.user.model.UserSharedealDistributorView;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.rpc.user.vto.UserOAuthStateVTO;
import com.bhu.vas.api.vto.statistics.FincialStatisticsVTO;
import com.bhu.vas.api.vto.statistics.RankSingle;
import com.bhu.vas.api.vto.statistics.RankingCardInfoVTO;
import com.bhu.vas.api.vto.statistics.RankingListVTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogFFVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.business.bucache.local.serviceimpl.wallet.BusinessWalletCacheService;
import com.bhu.vas.business.ds.charging.service.DeviceGroupPaymentStatisticsService;
import com.bhu.vas.business.ds.commdity.service.OrderService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeMonthRankService;
import com.bhu.vas.business.ds.statistics.service.UserIncomeRankService;
import com.bhu.vas.business.ds.user.facade.UserOAuthFacadeService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserCaptchaCodeService;
import com.bhu.vas.business.ds.user.service.UserPublishAccountService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserSharedealDistributorViewService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class UserWalletUnitFacadeService {
	@Resource
	private UserWalletFacadeService userWalletFacadeService;

	@Resource
	private UserCaptchaCodeService userCaptchaCodeService;

	@Resource
	private BusinessWalletCacheService businessWalletCacheService;

	@Resource
	private DeviceGroupPaymentStatisticsService deviceGroupPaymentStatisticsService;

	@Resource
	private UserService userService;

	@Resource
	private UserPublishAccountService userPublishAccountService;

	@Resource
	private UserOAuthFacadeService userOAuthFacadeService;

	@Resource
	private UserIncomeRankService userIncomeRankService;

	@Resource
	private UserIncomeMonthRankService userIncomeMonthRankService;

	@Resource
	private OrderService orderService;

	@Resource
	private UserSharedealDistributorViewService userSharedealDistributorViewService;
	private final Logger logger = LoggerFactory.getLogger(UserWalletUnitFacadeService.class);
	public RpcResponseDTO<TailPage<UserWalletLogVTO>> pageUserWalletlogs(
			int uid, String transmode, String transtype, int pageNo,
			int pageSize) {
		try {
			UWalletTransMode tmode = null;
			if (StringUtils.isNotEmpty(transmode)) {
				tmode = UWalletTransMode.fromKey(transmode);
			}

			UWalletTransType ttype = null;
			if (StringUtils.isNotEmpty(transtype)) {
				ttype = UWalletTransType.fromKey(transtype);
			}
			TailPage<UserWalletLog> pages = userWalletFacadeService
					.pageUserWalletlogs(uid, tmode, ttype, pageNo, pageSize);
			TailPage<UserWalletLogVTO> result_pages = null;
			List<UserWalletLogVTO> vtos = new ArrayList<>();
			if (!pages.isEmpty()) {
				List<Integer> uids = new ArrayList<>();
				for (UserWalletLog log : pages.getItems()) {
					uids.add(log.getUid());
				}
				List<User> users = userWalletFacadeService.getUserService()
						.findByIds(uids, true, true);
				int index = 0;
				for (UserWalletLog log : pages.getItems()) {
					User user = users.get(index);
					// String payment_type = StringUtils.EMPTY;
					// String description = StringUtils.EMPTY;
					/*
					 * ThirdpartiesPaymentType paymentType = null;
					 * if(BusinessEnumType
					 * .UWalletTransMode.CashPayment.getKey().
					 * equals(log.getTransmode()) &&
					 * BusinessEnumType.UWalletTransType
					 * .Cash2Realmoney.getKey().equals(log.getTranstype()) &&
					 * StringUtils.isNotEmpty(log.getOrderid())){
					 * UserWalletWithdrawApply apply =
					 * userWalletFacadeService.getUserWalletWithdrawApplyService
					 * ().getById(log.getOrderid()); paymentType =
					 * ThirdpartiesPaymentType.fromType(apply!= null
					 * ?apply.getPayment_type():StringUtils.EMPTY); String
					 * payment_type = apply!= null
					 * ?apply.getPayment_type():StringUtils.EMPTY;
					 * if(StringUtils.isNotEmpty(payment_type)){
					 * if(ThirdpartiesPaymentType.Alipay.equals(payment_type)){
					 * description =
					 * ThirdpartiesPaymentType.Alipay.getDescription(); }else
					 * if(ThirdpartiesPaymentType.Weichat.equals(payment_type))
					 * { description =
					 * ThirdpartiesPaymentType.Weichat.getDescription(); } } }
					 */
					vtos.add(log.toUserWalletLogVTO(
							user != null ? user.getMobileno()
									: StringUtils.EMPTY,
							user != null ? user.getNick() : StringUtils.EMPTY));
					index++;
				}
			}
			result_pages = new CommonPage<UserWalletLogVTO>(
					pages.getPageNumber(), pages.getPageSize(),
					pages.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(result_pages);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<TailPage<UserWalletLogFFVTO>> pageUserWalletlogsByFeifan(
			int uid, String transmode, String transtype, Date start_date,
			Date end_date, int pageNo, int pageSize) {
		try {
			UWalletTransMode tmode = null;
			if (StringUtils.isNotEmpty(transmode)) {
				tmode = UWalletTransMode.fromKey(transmode);
			}

			UWalletTransType ttype = null;
			if (StringUtils.isNotEmpty(transtype)) {
				ttype = UWalletTransType.fromKey(transtype);
			}

			TailPage<UserWalletLogFFVTO> result_pages = null;
			List<UserWalletLogFFVTO> vtos = new ArrayList<UserWalletLogFFVTO>();

			ModelCriteria mc = new ModelCriteria();
			Criteria createCriteria = mc.createCriteria();
			createCriteria.andColumnEqualTo("uid", uid);

			if (transmode != null)
				createCriteria.andColumnEqualTo("transmode", tmode.getKey());

			if (transtype != null)
				createCriteria.andColumnEqualTo("transtype", ttype.getKey());

			if (start_date != null)
				createCriteria.andColumnGreaterThanOrEqualTo("updated_at",
						start_date);

			if (end_date != null)
				createCriteria.andColumnLessThanOrEqualTo("updated_at",
						end_date);

			mc.setPageNumber(pageNo);
			mc.setPageSize(pageSize);
			mc.setOrderByClause(" updated_at desc ");

			int count = userSharedealDistributorViewService
					.countByModelCriteria(mc);
			if (count > 0) {
				List<UserSharedealDistributorView> list = userSharedealDistributorViewService
						.findModelByCommonCriteria(mc);
				if (list != null && !list.isEmpty()) {
					List<String> orderids = new ArrayList<String>();
					for (UserSharedealDistributorView view : list) {
						orderids.add(view.getOrderid());
					}

					List<Order> orders = orderService.findByIds(orderids, true,
							true);
					int index = 0;
					for (UserSharedealDistributorView view : list) {
						Order order = orders.get(index);
						vtos.add(view.toUserWalletLogFFVTO(
								order != null ? order.getAmount()
										: StringUtils.EMPTY,
								order != null ? order.getMac()
										: StringUtils.EMPTY));
						index++;
					}
				}
			}

			result_pages = new CommonPage<UserWalletLogFFVTO>(pageNo, pageSize,
					count, vtos);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(result_pages);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 需要判定用户是否是财务用户 财务审核、采用支付用户
	 * 
	 * @param uid
	 * @param tuid
	 * @param withdraw_status
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> pageWithdrawApplies(
			int reckoner, int tuid, String withdraw_status,
			String payment_type, String startTime, String endTime, int pageNo,
			int pageSize) {
		try {
			User validateUser = UserValidateServiceHelper.validateUser(
					reckoner, userWalletFacadeService.getUserService());
			if (validateUser.getUtype() != UserType.PaymentFinance.getIndex()
					&& validateUser.getUtype() != UserType.VerifyFinance
							.getIndex()) {
				throw new BusinessI18nCodeException(
						ResponseErrorCode.USER_TYPE_NOTMATCHED,
						new String[] { UserType.PaymentFinance.getSname()
								.concat(StringHelper.MINUS_STRING_GAP)
								.concat(UserType.VerifyFinance.getSname()) });
			}
			UWithdrawStatus status = null;
			if (StringUtils.isNotEmpty(withdraw_status)) {
				status = UWithdrawStatus.fromKey(withdraw_status);
			}
			TailPage<UserWalletWithdrawApply> pages = userWalletFacadeService
					.pageWithdrawApplies(tuid, status, payment_type, startTime,
							endTime, pageNo, pageSize);
			TailPage<UserWithdrawApplyVTO> result_pages = null;
			List<UserWithdrawApplyVTO> vtos = new ArrayList<>();
			if (!pages.isEmpty()) {
				List<Integer> uids = new ArrayList<>();
				for (UserWalletWithdrawApply apply : pages.getItems()) {
					uids.add(apply.getUid());
				}
				List<User> users = userWalletFacadeService.getUserService()
						.findByIds(uids, true, true);
				int index = 0;
				for (UserWalletWithdrawApply apply : pages.getItems()) {
					User user = users.get(index);
					WithdrawCostInfo calculateApplyCost = userWalletFacadeService
							.getChargingFacadeService().calculateWithdrawCost(
									apply.getUid(), apply.getId(),
									apply.getCash());
					// ApplyCost calculateApplyCost =
					// userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(apply.getUid(),
					// apply.getCash());
					// UserWalletConfigs walletConfigs =
					// userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(user.getId());
					// add by dongrui 2016-06-20 Start
					// 查询操作人和审核人信息
					User verifyUser = null;
					if (apply.getVerify_uid() != 0) {
						verifyUser = new User();
						verifyUser = userService.getById(apply.getVerify_uid());
					}
					User operateUser = null;
					if (apply.getOperate_uid() != 0) {
						operateUser = new User();
						operateUser = userService.getById(apply
								.getOperate_uid());
					}
					// add by dongrui 2016-06-20 E N D
					vtos.add(apply.toUserWithdrawApplyVTO(
							user != null ? user.getMobileno()
									: StringUtils.EMPTY,
							user != null ? user.getNick() : StringUtils.EMPTY,
							verifyUser != null ? verifyUser.getNick()
									: StringUtils.EMPTY,
							operateUser != null ? operateUser.getNick()
									: StringUtils.EMPTY, calculateApplyCost));
					index++;
				}
			}
			result_pages = new CommonPage<UserWithdrawApplyVTO>(
					pages.getPageNumber(), pages.getPageSize(),
					pages.getTotalItemsCount(), vtos);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(result_pages);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 返回指定的applyid的提现状态
	 * 
	 * @param reckoner
	 * @param applyid
	 * @return
	 */
	public RpcResponseDTO<String> withdrawApplyStatus(int reckoner,
			String applyid) {
		try {
			User validateUser = UserValidateServiceHelper.validateUser(
					reckoner, userWalletFacadeService.getUserService());
			if (validateUser.getUtype() != UserType.PaymentFinance.getIndex()) {
				throw new BusinessI18nCodeException(
						ResponseErrorCode.USER_TYPE_NOTMATCHED,
						new String[] { UserType.PaymentFinance.getSname() });
			}
			UserWalletWithdrawApply apply = userWalletFacadeService
					.getUserWalletWithdrawApplyService().getById(applyid);
			if (apply == null) {
				throw new BusinessI18nCodeException(
						ResponseErrorCode.COMMON_DATA_NOTEXIST,
						new String[] { applyid });
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(apply
					.getWithdraw_oper());
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 提现审核
	 * 
	 * @param reckoner
	 * @param applyid
	 * @param passed
	 * @return
	 */
	public RpcResponseDTO<Boolean> verifyApplies(int reckoner, String applyid,
			boolean passed) {
		try {
			// modify by dongrui 2016-06-20 start
			// 批量操作提现审核
			String currApplyId = StringUtils.EMPTY;
			String[] array = applyid.split(",");
			UserWithdrawApplyVTO withdrawApplyVTO = null;
			for (int i = 0; i < array.length; i++) {
				currApplyId = array[i];
				User validateUser = UserValidateServiceHelper.validateUser(
						reckoner, userWalletFacadeService.getUserService());
				if (validateUser.getUtype() != UserType.VerifyFinance
						.getIndex()) {
					throw new BusinessI18nCodeException(
							ResponseErrorCode.USER_TYPE_NOTMATCHED,
							new String[] { UserType.VerifyFinance.getSname() });
				}
				// modify by dongrui 2016-06-17 start
				UserWalletWithdrawApply withdrawApply = userWalletFacadeService
						.verifyApplies(reckoner, currApplyId, passed);
				User user = UserValidateServiceHelper.validateUser(
						withdrawApply.getUid(),
						userWalletFacadeService.getUserService());
				// UserWalletConfigs walletConfigs =
				// userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
				WithdrawCostInfo calculateApplyCost = userWalletFacadeService
						.getChargingFacadeService().calculateWithdrawCost(
								withdrawApply.getUid(), withdrawApply.getId(),
								withdrawApply.getCash());
				// ApplyCost calculateApplyCost =
				// userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(withdrawApply.getUid(),
				// withdrawApply.getCash());
				withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(
						user.getMobileno(), user.getNick(), user.getNick(), "",
						calculateApplyCost);
			}
			// modify by dongrui 2016-06-20 E N D
			if (withdrawApplyVTO != null) {
				return RpcResponseDTOBuilder
						.builderSuccessRpcResponse(Boolean.TRUE);
			} else {
				return RpcResponseDTOBuilder
						.builderSuccessRpcResponse(Boolean.FALSE);
			}
			// modify by dongrui 2016-06-17 E N D

			// UserWalletWithdrawApply withdrawApply =
			// userWalletFacadeService.doWithdrawVerify(reckoner, applyid,
			// passed);
			/*
			 * if(passed){//需要写入uPay数据队列 BusinessEnumType.UWithdrawStatus
			 * current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
			 * withdrawApply
			 * .addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(),
			 * current.getName()));
			 * withdrawApply.setWithdraw_oper(current.getKey()); User user =
			 * UserValidateServiceHelper
			 * .validateUser(withdrawApply.getUid(),userWalletFacadeService
			 * .getUserService()); UserWalletConfigs walletConfigs =
			 * userWalletFacadeService
			 * .getUserWalletConfigsService().userfulWalletConfigs
			 * (withdrawApply.getUid()); UserWithdrawApplyVTO withdrawApplyVTO =
			 * withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(),
			 * user.getNick(), walletConfigs.getWithdraw_tax_percent(),
			 * walletConfigs.getWithdraw_trancost_percent());
			 * ThirdpartiesPaymentDTO paymentDTO =
			 * userWalletFacadeService.fetchThirdpartiesPayment
			 * (withdrawApply.getUid(),
			 * ThirdpartiesPaymentType.fromType(withdrawApply
			 * .getPayment_type())); RequestWithdrawNotifyDTO withdrawNotify =
			 * RequestWithdrawNotifyDTO.from(withdrawApplyVTO,paymentDTO,
			 * System.currentTimeMillis()); String jsonNotify =
			 * JsonHelper.getJSONString(withdrawNotify);
			 * System.out.println("to Redis prepare:"+jsonNotify); {
			 * //保证写入redis后，提现申请设置成为转账中...状态 //BusinessEnumType.UWithdrawStatus
			 * current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
			 * CommdityInternalNotifyListService
			 * .getInstance().rpushWithdrawAppliesRequestNotify(jsonNotify);
			 * //withdrawApply
			 * .addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(),
			 * current.getName()));
			 * //withdrawApply.setWithdraw_oper(current.getKey());
			 * userWalletFacadeService
			 * .getUserWalletWithdrawApplyService().update(withdrawApply); }
			 * System.out.println("to Redis ok:"+jsonNotify); }
			 */

		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<RequestWithdrawNotifyDTO> doStartPaymentWithdrawApply(
			int reckoner, String applyid, String note) {
		try {
			User validateUser = UserValidateServiceHelper.validateUser(
					reckoner, userWalletFacadeService.getUserService());
			if (validateUser.getUtype() != UserType.PaymentFinance.getIndex()) {
				throw new BusinessI18nCodeException(
						ResponseErrorCode.USER_TYPE_NOTMATCHED,
						new String[] { UserType.PaymentFinance.getSname() });
			}
			UserWalletWithdrawApply withdrawApply = userWalletFacadeService
					.doStartPaymentWithdrawApply(reckoner, applyid, note);
			User user = UserValidateServiceHelper.validateUser(
					withdrawApply.getUid(),
					userWalletFacadeService.getUserService());
			WithdrawCostInfo calculateApplyCost = userWalletFacadeService
					.getChargingFacadeService().calculateWithdrawCost(
							withdrawApply.getUid(), withdrawApply.getId(),
							withdrawApply.getCash());
			// ApplyCost calculateApplyCost =
			// userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(withdrawApply.getUid(),
			// withdrawApply.getCash());
			// UserWalletConfigs walletConfigs =
			// userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
			UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply
					.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(),
							"", "", calculateApplyCost);

			// modify by dongrui 2016-06-17 start
			UserOAuthStateVTO paymentVTO = null;
			if (withdrawApply.getPayment_type().equals("public")) {
				paymentVTO = new UserOAuthStateVTO();
				// 根据uid查询对公账号信息
				UserPublishAccount userPublicAccount = userPublishAccountService
						.getById(withdrawApply.getUid());
				if (userPublicAccount == null) {
					throw new BusinessI18nCodeException(
							ResponseErrorCode.USER_WALLET_WITHDRAW_PUBLISHACCOUNT_NOTEXIST);
				}
				paymentVTO.setAuid(String.valueOf(userPublicAccount.getId()));
				paymentVTO.setAvatar("");
				paymentVTO.setIdentify("public");
				paymentVTO.setNick(userPublicAccount.getCompanyName());
				paymentVTO.setOpenid(userPublicAccount
						.getPublish_account_number());
			} else {
				// paymentDTO = new UserOAuthStateDTO();
				paymentVTO = userWalletFacadeService
						.getUserOAuthFacadeService().fetchRegisterIndetify(
								withdrawApply.getUid(),
								OAuthType.fromType(withdrawApply
										.getPayment_type()), true);
			}
			if (paymentVTO == null) {
				throw new BusinessI18nCodeException(
						ResponseErrorCode.USER_WALLET_PAYMENT_WASEMPTY);
			}

			/*
			 * UserOAuthStateDTO paymentDTO =
			 * userWalletFacadeService.getUserOAuthFacadeService
			 * ().fetchRegisterIndetify
			 * (withdrawApply.getUid(),OAuthType.fromType
			 * (withdrawApply.getPayment_type()),true); //ThirdpartiesPaymentDTO
			 * paymentDTO =
			 * userWalletFacadeService.fetchThirdpartiesPayment(withdrawApply
			 * .getUid(),
			 * ThirdpartiesPaymentType.fromType(withdrawApply.getPayment_type
			 * ())); if(paymentDTO == null){ throw new
			 * BusinessI18nCodeException(
			 * ResponseErrorCode.USER_WALLET_PAYMENT_WASEMPTY); }
			 */
			// modify by dongrui 2016-06-17 E N D

			RequestWithdrawNotifyDTO withdrawNotify = RequestWithdrawNotifyDTO
					.from(withdrawApplyVTO, paymentVTO,
							System.currentTimeMillis());
			String jsonNotify = JsonHelper.getJSONString(withdrawNotify);
			System.out.println("prepare JsonData:" + jsonNotify);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(withdrawNotify);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<UserWithdrawApplyVTO> doWithdrawNotifyFromLocal(
			int reckoner, String applyid, boolean successed) {
		try {
			User validateUser = UserValidateServiceHelper.validateUser(
					reckoner, userWalletFacadeService.getUserService());
			if (validateUser.getUtype() != UserType.PaymentFinance.getIndex()) {
				throw new BusinessI18nCodeException(
						ResponseErrorCode.USER_TYPE_NOTMATCHED,
						new String[] { UserType.PaymentFinance.getSname() });
			}
			UserWalletWithdrawApply withdrawApply = userWalletFacadeService
					.doWithdrawNotifyFromLocal(applyid, successed);
			User user = UserValidateServiceHelper.validateUser(
					withdrawApply.getUid(),
					userWalletFacadeService.getUserService());
			// UserWalletConfigs walletConfigs =
			// userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
			WithdrawCostInfo calculateApplyCost = userWalletFacadeService
					.getChargingFacadeService().calculateWithdrawCost(
							withdrawApply.getUid(), withdrawApply.getId(),
							withdrawApply.getCash());
			// ApplyCost calculateApplyCost =
			// userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(withdrawApply.getUid(),
			// withdrawApply.getCash());
			UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply
					.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(),
							"", "", calculateApplyCost);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(withdrawApplyVTO);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<UserWithdrawApplyVTO> withdrawOper(int appid,
			String payment_type, int uid, String pwd, double cash,
			String remoteip) {
		try {
			// 验证appid
			if (!CommdityApplication.supported(appid)) {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.VALIDATE_APPID_INVALID,
						new String[] { String.valueOf(appid) });
			}
			if (!OAuthType.paymentSupported(payment_type)) {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,
						new String[] { String.valueOf(payment_type) });
			}
			User user = UserValidateServiceHelper.validateUser(uid,
					userWalletFacadeService.getUserService());

			// add by Jason 2016-06-07 start
			// 根据uid查询当前用户是否存在对公账号
			UserPublishAccount userPublishAccount = userPublishAccountService
					.getById(uid);
			if (StringUtils.equals(payment_type, "weixin")) {
				if (userPublishAccount != null) {
					// 返回错误码 提示当前用户已绑定对公行号 在app端进行对公账号提现
					return RpcResponseDTOBuilder
							.builderErrorRpcResponse(ResponseErrorCode.USER_WALLET_WITHDRAW_PUBLISHACCOUNT_EXIST);
				}
			} else if (StringUtils.equals(payment_type, "public")) {
				if (userPublishAccount == null) {
					return RpcResponseDTOBuilder
							.builderErrorRpcResponse(ResponseErrorCode.USER_WALLET_WITHDRAW_PUBLISHACCOUNT_NOTEXIST);
				}
			}
			// add by Jason 2016-06-07 E N D

			UserWalletWithdrawApply apply = userWalletFacadeService
					.doWithdrawApply(appid, OAuthType.fromType(payment_type),
							uid, pwd, cash, remoteip);
			// UserWalletConfigs walletConfigs =
			// userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(uid);
			WithdrawCostInfo calculateApplyCost = userWalletFacadeService
					.getChargingFacadeService().calculateWithdrawCost(
							apply.getUid(), apply.getId(), apply.getCash());
			// ApplyCost calculateApplyCost =
			// userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(uid,
			// cash);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(apply
					.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(),
							"", "", calculateApplyCost));
		} catch (BusinessI18nCodeException bex) {
			bex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<UserWalletDetailVTO> walletDetail(int uid) {
		try {
			// UserWallet userWallet = userWalletFacadeService.userWallet(uid);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(userWalletFacadeService
							.walletDetail(uid));
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/*
	 * public RpcResponseDTO<List<ThirdpartiesPaymentDTO>>
	 * fetchUserThirdpartiesPayments(int uid) { try{
	 * List<ThirdpartiesPaymentDTO> payments =
	 * userWalletFacadeService.fetchAllThirdpartiesPayment(uid); return
	 * RpcResponseDTOBuilder.builderSuccessRpcResponse(payments);
	 * }catch(BusinessI18nCodeException bex){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse
	 * (bex.getErrorCode(),bex.getPayload()); }catch(Exception ex){
	 * ex.printStackTrace(System.out); return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse
	 * (ResponseErrorCode.COMMON_BUSINESS_ERROR); } }
	 * 
	 * public RpcResponseDTO<Boolean> removeUserThirdpartiesPayment(int
	 * uid,String payment_type) { try{
	 * if(!ThirdpartiesPaymentType.supported(payment_type)){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * AUTH_COMMON_DATA_PARAM_NOTSUPPORTED); }
	 * userWalletFacadeService.removeThirdpartiesPayment(uid,
	 * ThirdpartiesPaymentType.fromType(payment_type)); return
	 * RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	 * }catch(BusinessI18nCodeException bex){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse
	 * (bex.getErrorCode(),bex.getPayload()); }catch(Exception ex){
	 * ex.printStackTrace(System.out); return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse
	 * (ResponseErrorCode.COMMON_BUSINESS_ERROR); } }
	 * 
	 * public RpcResponseDTO<List<ThirdpartiesPaymentDTO>>
	 * createUserThirdpartiesPayment(int uid, String payment_type, String id,
	 * String name,String avatar) { try{
	 * if(!ThirdpartiesPaymentType.supported(payment_type)){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new
	 * String[]{"payment_type:".concat(payment_type)}); }
	 * ThirdpartiesPaymentType paymenttype =
	 * ThirdpartiesPaymentType.fromType(payment_type); if(paymenttype == null){
	 * return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.
	 * AUTH_COMMON_DATA_PARAM_NOTSUPPORTED,new
	 * String[]{"payment_type:".concat(payment_type)}); }
	 * List<ThirdpartiesPaymentDTO> payments =
	 * userWalletFacadeService.addThirdpartiesPayment(uid, paymenttype,
	 * ThirdpartiesPaymentDTO.build(paymenttype, id, name,avatar)); return
	 * RpcResponseDTOBuilder.builderSuccessRpcResponse(payments);
	 * }catch(BusinessI18nCodeException bex){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse
	 * (bex.getErrorCode(),bex.getPayload()); }catch(Exception ex){
	 * ex.printStackTrace(System.out); return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse
	 * (ResponseErrorCode.COMMON_BUSINESS_ERROR); } }
	 */

	public RpcResponseDTO<Boolean> withdrawPwdSet(int uid, String captcha,
			String pwd) {
		try {
			// TODO验证用户是否存在，验证手机号是否存在，此手机号的验证验证码
			User user = UserValidateServiceHelper.validateUser(uid,
					userWalletFacadeService.getUserService());
			if (StringUtils.isEmpty(user.getMobileno())) {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.USER_MOBILE_ATTRIBUTE_NOTEXIST,
						new String[] { "uid:".concat(String.valueOf(uid)) });
			}
			if (!RuntimeConfiguration.SecretInnerTest) {
				String accWithCountryCode = PhoneHelper.format(
						user.getCountrycode(), user.getMobileno());
				if (!BusinessRuntimeConfiguration
						.isSystemNoneedCaptchaValidAcc(accWithCountryCode)) {
					ResponseErrorCode errorCode = userCaptchaCodeService
							.validCaptchaCode(accWithCountryCode, captcha);
					if (errorCode != null) {
						return RpcResponseDTOBuilder
								.builderErrorRpcResponse(errorCode);
					}
				}
			}
			userWalletFacadeService.doSetWithdrawPwd(uid, pwd);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(Boolean.TRUE);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> directDrawPresent(int uid,
			String thirdparties_orderid, double cash, String desc) {
		try {
			int ret = userWalletFacadeService.cashToUserWallet(uid,
					thirdparties_orderid, UWalletTransMode.DrawPresent, 0.00d,
					cash, desc);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(ret == 0 ? Boolean.TRUE
							: Boolean.FALSE);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/*
	 * public RpcResponseDTO<Boolean> withdrawPwdUpd(int uid, String pwd, String
	 * npwd) { try{ userWalletFacadeService.doChangedWithdrawPwd(uid, pwd,
	 * npwd); return
	 * RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	 * }catch(BusinessI18nCodeException bex){ return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse
	 * (bex.getErrorCode(),bex.getPayload()); }catch(Exception ex){
	 * ex.printStackTrace(System.out); return
	 * RpcResponseDTOBuilder.builderErrorRpcResponse
	 * (ResponseErrorCode.COMMON_BUSINESS_ERROR); } }
	 */

	public RpcResponseDTO<ShareDealWalletSummaryProcedureVTO> walletLogStatistics(
			int uid) {
		try {
			ShareDealWalletSummaryProcedureVTO cacheByUser = businessWalletCacheService
					.getWalletLogStatisticsDSCacheByUser(uid);
			if (cacheByUser == null) {
				cacheByUser = userWalletFacadeService
						.sharedealSummaryWithProcedure(uid);
				businessWalletCacheService
						.storeWalletLogStatisticsDSCacheResult(uid, cacheByUser);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(cacheByUser);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<FincialStatisticsVTO> fincialStatistics(String time) {
		try {
			FincialStatistics fincial = userWalletFacadeService
					.getFincialStatisticsService().getById(time);
			FincialStatisticsVTO fincialStatisticsVTO = new FincialStatisticsVTO();
			if (fincial == null) {
				fincialStatisticsVTO.setId(time);
				return RpcResponseDTOBuilder
						.builderSuccessRpcResponse(fincialStatisticsVTO);
			} else {
				fincialStatisticsVTO.setId(fincial.getId());
				fincialStatisticsVTO
						.setaTotal((float) (Math.round(100 * (fincial.getCpa() + fincial
								.getCta()))) / 100);
				fincialStatisticsVTO.setCpa((float) (Math.round(100 * fincial
						.getCpa())) / 100);
				fincialStatisticsVTO.setCpm((float) (Math.round(100 * fincial
						.getCpm())) / 100);
				fincialStatisticsVTO
						.setCpTotal((float) (Math.round(100 * (fincial.getCpa()
								+ fincial.getCpm() + fincial.getCpw()))) / 100);
				fincialStatisticsVTO.setCpw((float) (Math.round(100 * fincial
						.getCpw())) / 100);
				fincialStatisticsVTO.setCta((float) (Math.round(100 * fincial
						.getCta())) / 100);
				fincialStatisticsVTO.setCtm((float) (Math.round(100 * fincial
						.getCtm())) / 100);
				fincialStatisticsVTO
						.setCtTotal((float) (Math.round(100 * (fincial.getCta()
								+ fincial.getCtm() + fincial.getCtw()))) / 100);
				fincialStatisticsVTO.setCtw((float) (Math.round(100 * fincial
						.getCtw())) / 100);
				fincialStatisticsVTO
						.setmTotal((float) (Math.round(100 * (fincial.getCpm() + fincial
								.getCtm()))) / 100);
				fincialStatisticsVTO
						.setwTotal((float) (Math.round(100 * (fincial.getCpw() + fincial
								.getCtw()))) / 100);
				fincialStatisticsVTO
						.setTotal((float) (Math
								.round(100 * (fincialStatisticsVTO.getCpTotal() + fincialStatisticsVTO
										.getCtTotal()))) / 100);
				fincialStatisticsVTO
						.setCpow((float) (Math.round(100 * (java.lang.Math
								.abs(fincialStatisticsVTO.getCpTotal()
										- fincialStatisticsVTO.getCpw())))) / 100);
				fincialStatisticsVTO
						.setCtow((float) (Math.round(100 * (java.lang.Math
								.abs(fincialStatisticsVTO.getCtTotal()
										- fincialStatisticsVTO.getCtw())))) / 100);
				List<FincialStatistics> fincialStatistics = userWalletFacadeService
						.getFincialStatisticsService().findAll();
				DateFormat df = new SimpleDateFormat("yyyy-MM");
				Date dt1 = df.parse(time);
				if (fincialStatistics != null && fincialStatistics.size() > 0) {
					int owTotal = 0;
					int wTotal = 0;
					for (FincialStatistics i : fincialStatistics) {
						Date dt2 = df.parse(i.getId());
						if (dt1.getTime() > dt2.getTime()) {
							if (i.getCtw() > i.getCpw()) {
								wTotal += i.getCtw() - i.getCpw();
							}
							if ((i.getCta() - i.getCpa()) > (i.getCtm() - i
									.getCpm())) {
								owTotal += i.getCta() - i.getCpa() + i.getCtm()
										- i.getCpm();
							}
						}
					}
					fincialStatisticsVTO.setrTotal((float) (Math
							.round(100 * (wTotal + owTotal))) / 100);
					fincialStatisticsVTO.setRwTotal((float) (Math
							.round(100 * (wTotal))) / 100);
					fincialStatisticsVTO.setRowTotal((float) (Math
							.round(100 * (owTotal))) / 100);
					fincialStatisticsVTO
							.sethTotal((float) (Math
									.round(100 * (java.lang.Math
											.abs(fincialStatisticsVTO
													.getrTotal()
													+ fincialStatisticsVTO
															.getCpTotal()
													- fincialStatisticsVTO
															.getCtTotal())))) / 100);
					fincialStatisticsVTO
							.setHwTotal((float) (Math.round(100 * (fincialStatisticsVTO
									.getRwTotal()
									+ fincialStatisticsVTO.getCpTotal() - fincialStatisticsVTO
									.getCtTotal()))) / 100);
					fincialStatisticsVTO
							.setHowTotal((float) (Math
									.round(100 * (java.lang.Math
											.abs(fincialStatisticsVTO
													.gethTotal()
													- fincialStatisticsVTO
															.getHwTotal())))) / 100);
				}
			}
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(fincialStatisticsVTO);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<RankingListVTO> rankingList(int uid, int type,
			String time, int pn, int ps) {
		try {
			RankingListVTO rankingListVTO = new RankingListVTO();
			List<RankSingle> rankList = new ArrayList<RankSingle>();
			Map<String, List<RankSingle>> rankDayMap = new LinkedHashMap<String, List<RankSingle>>();
			Map<String, List<RankSingle>> rankMonthMap = new LinkedHashMap<String, List<RankSingle>>();

			Map<String, Object> rankMap = new HashMap<String, Object>();
			rankMap.put("total", null);

			List<String> days = getLastDay(7);
			for (String i : days) {
				rankDayMap.put(i, null);
			}
			rankMonthMap.put(GetMonthTime(-6), null);
			rankMonthMap.put(GetMonthTime(-5), null);
			rankMonthMap.put(GetMonthTime(-4), null);
			rankMonthMap.put(GetMonthTime(-3), null);
			rankMonthMap.put(GetMonthTime(-2), null);
			rankMonthMap.put(GetMonthTime(-1), null);
			rankMonthMap.put(GetMonthTime(0), null);

			int totalPage = 1;

			String beforeIncome = "0";
			int beforeRankNum = 0;
			int n = 1;
			int m = 1;

			if (type == 1) {
				String currentDay = StringUtils.EMPTY;
				if (StringUtils.isBlank(time)) {
					currentDay = GetDateTime("yyyy-MM-dd", 0);
				} else {
					currentDay = time;
				}
				if (GetDateTime("yyyy-MM-dd", 0).equals(currentDay)) {
					ModelCriteria mc = new ModelCriteria();
					mc.createCriteria()
							.andColumnNotEqualTo("today_cash_sum", 0)
							.andColumnLike("last_update_cash_time",
									currentDay + "%");
					mc.setOrderByClause("today_cash_sum desc");
					List<UserWallet> userWallets = userWalletFacadeService
							.getUserWalletService().findModelByCommonCriteria(
									mc);
					rankingListVTO.setRankNum(9999999);
					rankingListVTO.setUserIncome("0");
					if (userWallets.size() % ps == 0) {
						totalPage = userWallets.size() / ps;
					} else {
						totalPage = userWallets.size() / ps + 1;
					}
					for (int i = 0; i < userWallets.size(); i++) {
						RankSingle rankSingle = new RankSingle();
						if (i == 0) {
							beforeRankNum = n;
							beforeIncome = String.valueOf(round(userWallets
									.get(i).getToday_cash_sum(), 2));
						} else {
							if (!StringUtils.equals(beforeIncome, String
									.valueOf(round(userWallets.get(i)
											.getToday_cash_sum(), 2)))) {
								beforeRankNum = m;
								beforeIncome = String.valueOf(round(userWallets
										.get(i).getToday_cash_sum(), 2));
								n = m;
							}
						}
						if (uid == userWallets.get(i).getId()) {
							rankingListVTO.setRankNum(beforeRankNum);
							rankingListVTO
									.setUserIncome(String.valueOf(round(
											userWallets.get(i)
													.getToday_cash_sum(), 2)));
						}
						User singleUser = userService.getById(userWallets
								.get(i).getId());
						rankSingle.setRankNum(beforeRankNum);
						rankSingle.setUserIncome(String.valueOf(round(
								userWallets.get(i).getToday_cash_sum(), 2)));
						if (singleUser != null) {
							rankSingle.setUserName(singleUser.getNick());
							rankSingle.setAvatar(singleUser.getAvatar());
							rankSingle.setMemo(singleUser.getMemo());
						}
						if (m > (pn - 1) * ps && m <= pn * ps) {
							rankList.add(rankSingle);
						}
						m++;
					}
					rankingListVTO.setChangeFlag(1);
				}else{
					System.out.println(time+"========"+currentDay);
					logger.info("info:time=======>"+time);
					logger.info("info:currentDay=======>"+currentDay);
					List<UserIncomeRank> userIncomeRanks=userIncomeRankService.findByLimit(currentDay+"%",(pn-1)*ps,pn*ps);
					
					logger.info("info:userIncomeRanks=======>"+userIncomeRanks.size());
					UserIncomeRank incomeRank=userIncomeRankService.getByUid(uid,currentDay+"%");
					if(incomeRank==null){
						rankingListVTO.setRankNum(9999999);
						rankingListVTO.setUserIncome("0");
						rankingListVTO.setChangeFlag(1);
					}else{
						System.out.println("1:"+incomeRank.getIncome());
						logger.info("info:incomeRank=======>"+incomeRank.getIncome());
						if(incomeRank.getRank()==incomeRank.getBeforeRank()){
							rankingListVTO.setChangeFlag(1);
						} else if (incomeRank.getRank() > incomeRank
								.getBeforeRank()) {
							rankingListVTO.setChangeFlag(2);
						} else {
							rankingListVTO.setChangeFlag(0);
						}
						rankingListVTO.setRankNum(incomeRank.getRank());
						rankingListVTO.setUserIncome(String.valueOf(round(
								Float.valueOf(incomeRank.getIncome()), 2)));
					}
					if(userIncomeRanks != null&&userIncomeRanks.size()>0){
						System.out.println("2:"+userIncomeRanks.size());
						if(userIncomeRanks.size()%ps==0){
							totalPage=userIncomeRanks.size()/ps;
						}else{
							totalPage=userIncomeRanks.size()/ps+1;
						}
						for (int i = 0; i < userIncomeRanks.size(); i++) {
							RankSingle rankSingle = new RankSingle();
							UserIncomeRank userIncomeRank = userIncomeRanks
									.get(i);
							User singleUser = userService.getById(Integer
									.valueOf(userIncomeRank.getUid()));
							rankSingle.setRankNum(userIncomeRank.getRank());
							rankSingle.setUserIncome(String.valueOf(round(
									Float.valueOf(userIncomeRank.getIncome()),
									2)));
							if (singleUser != null) {
								rankSingle.setUserName(singleUser.getNick());
								rankSingle.setAvatar(singleUser.getAvatar());
								rankSingle.setMemo(singleUser.getMemo());
							}
							if (userIncomeRank.getRank() == userIncomeRank
									.getBeforeRank()) {
								rankSingle.setChangeFlag(1);
							} else if (userIncomeRank.getRank() > userIncomeRank
									.getBeforeRank()) {
								rankSingle.setChangeFlag(2);
							} else {
								rankSingle.setChangeFlag(0);
							}
							rankList.add(rankSingle);
						}
					}
				}
				rankDayMap.put(currentDay, rankList);
			} else if (type == 2) {
				String currentMonth = StringUtils.EMPTY;
				if (StringUtils.isBlank(time)) {
					currentMonth = GetMonthTime(0);
				} else {
					currentMonth = time;
				}
				if (GetMonthTime(0).equals(currentMonth)) {
					ModelCriteria mc = new ModelCriteria();
					mc.createCriteria()
							.andColumnNotEqualTo("month_cash_sum", 0);
					mc.createCriteria().andColumnLike("last_update_cash_time",
							currentMonth + "%");
					mc.setOrderByClause("month_cash_sum desc");
					List<UserWallet> userWallets = userWalletFacadeService
							.getUserWalletService().findModelByCommonCriteria(
									mc);
					rankingListVTO.setRankNum(9999999);
					rankingListVTO.setUserIncome("0");
					if (userWallets.size() % ps == 0) {
						totalPage = userWallets.size() / ps;
					} else {
						totalPage = userWallets.size() / ps + 1;
					}
					for (int i = 0; i < userWallets.size(); i++) {
						RankSingle rankSingle = new RankSingle();
						if (i == 0) {
							beforeRankNum = n;
							beforeIncome = String.valueOf(round(userWallets
									.get(i).getMonth_cash_sum(), 2));
						} else {
							if (!StringUtils.equals(beforeIncome, String
									.valueOf(round(userWallets.get(i)
											.getMonth_cash_sum(), 2)))) {
								beforeRankNum = m;
								beforeIncome = String.valueOf(round(userWallets
										.get(i).getMonth_cash_sum(), 2));
								n = m;
							}
						}
						if (uid == userWallets.get(i).getId()) {
							rankingListVTO.setRankNum(beforeRankNum);
							rankingListVTO
									.setUserIncome(String.valueOf(round(
											userWallets.get(i)
													.getMonth_cash_sum(), 2)));
						}
						User singleUser = userService.getById(Integer
								.valueOf(userWallets.get(i).getId()));
						rankSingle.setRankNum(beforeRankNum);
						rankSingle.setUserIncome(String.valueOf(round(
								userWallets.get(i).getMonth_cash_sum(), 2)));
						if (singleUser != null) {
							rankSingle.setUserName(singleUser.getNick());
							rankSingle.setAvatar(singleUser.getAvatar());
							rankSingle.setMemo(singleUser.getMemo());
						}
						if (m > (pn - 1) * ps && m <= pn * ps) {
							rankList.add(rankSingle);
						}
						m++;
					}
					rankingListVTO.setChangeFlag(1);
				}else{
					List<UserIncomeMonthRank> userIncomeMonthRanks=userIncomeMonthRankService.findByLimit(currentMonth+"%",(pn-1)*ps,pn*ps);
					UserIncomeMonthRank incomeMonthRank=userIncomeMonthRankService.getByUid(uid,currentMonth+"%");
					if(incomeMonthRank==null){
						rankingListVTO.setRankNum(9999999);
						rankingListVTO.setUserIncome("0");
						rankingListVTO.setChangeFlag(1);
					} else {
						if (incomeMonthRank.getRank() == incomeMonthRank
								.getBeforeRank()) {
							rankingListVTO.setChangeFlag(1);
						} else if (incomeMonthRank.getRank() > incomeMonthRank
								.getBeforeRank()) {
							rankingListVTO.setChangeFlag(2);
						} else {
							rankingListVTO.setChangeFlag(0);
						}
						rankingListVTO.setRankNum(incomeMonthRank.getRank());
						rankingListVTO
								.setUserIncome(String.valueOf(round(Float
										.valueOf(incomeMonthRank.getIncome()),
										2)));
					}
					if (userIncomeMonthRanks != null
							&& userIncomeMonthRanks.size() > 0) {
						if (userIncomeMonthRanks.size() % ps == 0) {
							totalPage = userIncomeMonthRanks.size() / ps;
						} else {
							totalPage = userIncomeMonthRanks.size() / ps + 1;
						}
						for (int i = 0; i < userIncomeMonthRanks.size(); i++) {
							RankSingle rankSingle = new RankSingle();
							UserIncomeMonthRank userIncomeMonthRank = userIncomeMonthRanks
									.get(i);
							User singleUser = userService.getById(Integer
									.valueOf(userIncomeMonthRank.getUid()));
							rankSingle
									.setRankNum(userIncomeMonthRank.getRank());
							rankSingle.setUserIncome(String.valueOf(round(Float
									.valueOf(userIncomeMonthRank.getIncome()),
									2)));
							if (singleUser != null) {
								rankSingle.setUserName(singleUser.getNick());
								rankSingle.setAvatar(singleUser.getAvatar());
								rankSingle.setMemo(singleUser.getMemo());
							}
							if (userIncomeMonthRank.getRank() == userIncomeMonthRank
									.getBeforeRank()) {
								rankSingle.setChangeFlag(1);
							} else if (userIncomeMonthRank.getRank() > userIncomeMonthRank
									.getBeforeRank()) {
								rankSingle.setChangeFlag(2);
							} else {
								rankSingle.setChangeFlag(0);
							}
							rankList.add(rankSingle);
						}
					}
				}
				rankMonthMap.put(currentMonth, rankList);
			} else if (type == 3) {
				rankingListVTO.setRankNum(9999999);
				rankingListVTO.setUserIncome("0");
				rankingListVTO.setChangeFlag(1);
				ModelCriteria mc = new ModelCriteria();
				mc.createCriteria().andColumnNotEqualTo("total_cash_sum", 0);
				mc.setOrderByClause("total_cash_sum desc");
				List<UserWallet> userWallets = userWalletFacadeService
						.getUserWalletService().findModelByCommonCriteria(mc);
				if (userWallets != null && userWallets.size() > 0) {
					if (userWallets.size() % ps == 0) {
						totalPage = userWallets.size() / ps;
					} else {
						totalPage = userWallets.size() / ps + 1;
					}
					for (int i = 0; i < userWallets.size(); i++) {
						RankSingle rankSingle = new RankSingle();
						if (i == 0) {
							beforeRankNum = n;
							beforeIncome = String.valueOf(round(userWallets
									.get(i).getTotal_cash_sum(), 2));
						} else {
							if (!StringUtils.equals(beforeIncome, String
									.valueOf(round(userWallets.get(i)
											.getTotal_cash_sum(), 2)))) {
								beforeRankNum = m;
								beforeIncome = String.valueOf(round(userWallets
										.get(i).getTotal_cash_sum(), 2));
								n = m;
							}
						}
						if (uid == userWallets.get(i).getId()) {
							rankingListVTO.setRankNum(beforeRankNum);
							rankingListVTO
									.setUserIncome(String.valueOf(round(
											userWallets.get(i)
													.getTotal_cash_sum(), 2)));
						}
						User singleUser = userService.getById(Integer
								.valueOf(userWallets.get(i).getId()));
						if (singleUser != null) {
							rankSingle.setUserName(singleUser.getNick());
							rankSingle.setAvatar(singleUser.getAvatar());
							rankSingle.setMemo(singleUser.getMemo());
						}
						rankSingle.setRankNum(beforeRankNum);
						rankSingle.setUserIncome(String.valueOf(round(
								userWallets.get(i).getTotal_cash_sum(), 2)));
						if (m > (pn - 1) * ps && m <= pn * ps) {
							rankList.add(rankSingle);
						}
						m++;
					}
				}
				rankMap.put("total", rankList);
			} else {
				return RpcResponseDTOBuilder.builderErrorRpcResponse(
						ResponseErrorCode.COMMON_DATA_PARAM_ERROR,
						new String[] { "可选类别错误" });
			}
			rankMap.put("day", rankDayMap);
			rankMap.put("month", rankMonthMap);
			rankingListVTO.setRankingList(rankMap);
			User user = userService.getById(uid);
			if (user != null) {
				rankingListVTO.setMemo(user.getMemo());
				rankingListVTO.setAvatar(user.getAvatar());
			}
			rankingListVTO.setRankingList(rankMap);
			rankingListVTO.setPn(pn);
			rankingListVTO.setPs(ps);
			rankingListVTO.setTotalPage(totalPage);
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(rankingListVTO);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 丰富统计信息
	 */
	public RpcResponseDTO<UcloudMacStatisticsVTO> richStatistics(int uid) {
		UcloudMacStatisticsVTO ucloudMacStatisticsVTO = new UcloudMacStatisticsVTO();
		try {
			// 折线图信息
			// 天数的计算
			List<String> lineChartDateInfo = new ArrayList<String>();
			// 折线图Y轴（收益）
			List<Double> lineChartIncomeInfo = new ArrayList<Double>();
			// 折线图Y轴（用户数）
			List<Double> lineChartUserNumInfo = new ArrayList<Double>();
			// 获取当前日期
			String startTime = GetDateTime("yyyy-MM-dd", 0);
			for (int i = 0; i < 30; i++) {
				// 折线图X轴（日期）
				lineChartDateInfo.add(getNewDay(startTime, i - 30));
				double lineChartIncome = userWalletFacadeService
						.getUserIncomeService()
						.getEntityDao()
						.countTotalIncomeByDay(uid,
								getNewDay(startTime, i - 30));
				lineChartIncome = round(lineChartIncome, 2);
				// 折线图Y轴（收益）
				lineChartIncomeInfo.add(lineChartIncome);
				double lineChartUserNum = userWalletFacadeService
						.getUserIncomeService()
						.getEntityDao()
						.countTotalUserNumByDay(uid,
								getNewDay(startTime, i - 30));
				// 折线图Y轴（用户数）
				lineChartUserNumInfo.add(lineChartUserNum);
			}

			// 折线图X轴（日期）
			ucloudMacStatisticsVTO.setLineChartDateInfo(lineChartDateInfo);
			// 折线图Y轴（收益）
			ucloudMacStatisticsVTO.setLineChartIncomeInfo(lineChartIncomeInfo);
			// 折线图Y轴（用户数）
			ucloudMacStatisticsVTO
					.setLineChartUserNumInfo(lineChartUserNumInfo);

			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(ucloudMacStatisticsVTO);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 获取日期
	 * 
	 * @param simpleDateFormat
	 *            日期的格式
	 * @param days
	 *            取得那天日期 如：0：今天
	 */
	public static String GetDateTime(String simpleDateFormat, int days) {
		// 返回日期
		String retDate = StringUtils.EMPTY;
		// 当前时间
		Date dt = new Date();
		// 得到日历
		Calendar calendar = Calendar.getInstance();
		// 把当前时间赋给日历
		calendar.setTime(dt);
		// 设置为前一天
		calendar.add(Calendar.DAY_OF_MONTH, days);
		// 得到前一天的时间
		Date dBefore = calendar.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat(simpleDateFormat);
		retDate = sdf.format(dBefore);
		return retDate;
	}

	/**
	 * 获取月份
	 * 
	 * @param simpleDateFormat
	 *            日期的格式
	 * @param days
	 *            取得那天日期 如：0：今天
	 */
	public static String GetMonthTime(int months) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, months);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		String time = format.format(c.getTime());
		return time;
	}

	/**
	 * 字符串的日期格式的计算(根据一个日期和天数获取新的日期)
	 * 
	 * @throws ParseException
	 */
	public String getNewDay(String inputValue, int days) throws ParseException {
		// 返回日期
		String retDate = StringUtils.EMPTY;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(inputValue));

		// 新的日期
		calendar.add(Calendar.DAY_OF_MONTH, days);
		// 得到新的日期
		Date newDay = calendar.getTime();

		retDate = sdf.format(newDay);
		return retDate;
	}

	/**
	 * 时间升序排列
	 * 
	 * @param dateNum
	 * @return
	 */
	public static List<String> getLastDay(int dateNum) {
		List<String> list = new ArrayList<String>();
		// 获取当前日期
		for (int i = dateNum; i >= 0; i--) {
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, -i);
			date = calendar.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateNowStr = sdf.format(date);
			list.add(dateNowStr);
		}
		return list;
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public RpcResponseDTO<RankingCardInfoVTO> rankingCardInfo(Integer uid) {
		try {
			RankingCardInfoVTO rankingCardInfoVTO = new RankingCardInfoVTO();
			String currentDay = StringUtils.EMPTY;
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnNotEqualTo("today_cash_sum", 0);
			mc.createCriteria().andColumnLike("last_update_cash_time",
					currentDay + "%");
			mc.setOrderByClause("today_cash_sum desc");
			List<UserWallet> userWallets = userWalletFacadeService
					.getUserWalletService().findModelByCommonCriteria(mc);
			rankingCardInfoVTO.setRank(9999999);
			rankingCardInfoVTO.setIncome("0");
			for (int i = 0; i < userWallets.size(); i++) {
				if (uid == userWallets.get(i).getId()) {
					rankingCardInfoVTO.setRank(i + 1);
					rankingCardInfoVTO.setIncome(String.valueOf(round(
							userWallets.get(i).getToday_cash_sum(), 2)));
				}
			}
			rankingCardInfoVTO.setAge(0);
			User user = userService.getById(uid);
			if (user != null) {
				// 得到日历
				Calendar calendar = Calendar.getInstance();
				// 把当前时间赋给日历
				calendar.setTime(user.getCreated_at());
				// 设置为前一天
				calendar.add(Calendar.DAY_OF_MONTH, 0);
				// 得到前一天的时间
				Date dBefore = calendar.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String retDate = sdf.format(dBefore);
				rankingCardInfoVTO.setAge(daysBetween(retDate,
						GetDateTime("yyyy-MM-dd", 0)));
				rankingCardInfoVTO.setMemo(user.getMemo());
				rankingCardInfoVTO.setAvatar(user.getAvatar());
				rankingCardInfoVTO.setUserName(user.getNick());
			}
			return RpcResponseDTOBuilder
					.builderSuccessRpcResponse(rankingCardInfoVTO);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public static void main(String[] args) {

		System.out.println(GetDateTime("yyyy-MM-dd", 0));
	}

	/**
	 * 字符串的日期格式的计算
	 */
	public static int daysBetween(String smdate, String bdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(smdate));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		long time1 = cal.getTimeInMillis();
		try {
			cal.setTime(sdf.parse(bdate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 查看用户当前提现简单信息（申请时间，提现）
	 * 
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<UserWithdrawApplyVTO> fetchWithdrawSimpleDetail(
			int uid) {
		try {
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria()
					.andColumnEqualTo("uid", uid)
					.andColumnNotEqualTo(
							"withdraw_oper",
							BusinessEnumType.UWithdrawStatus.WithdrawSucceed
									.getKey())
					.andColumnNotEqualTo(
							"withdraw_oper",
							BusinessEnumType.UWithdrawStatus.WithdrawFailed
									.getKey());
			List<UserWalletWithdrawApply> applys = userWalletFacadeService.getUserWalletWithdrawApplyService().findModelByModelCriteria(mc);
			UserWithdrawApplyVTO vto = null;
			
			if(!applys.isEmpty()){
				User user = UserValidateServiceHelper.validateUser(
						uid,userWalletFacadeService.getUserService());
				UserWalletWithdrawApply apply = applys.get(0);
				vto = apply.toUserWithdrawApplySimpleVTO(user.getMobileno(), user.getNick());
			}else{
				return RpcResponseDTOBuilder
						.builderErrorRpcResponse(ResponseErrorCode.USER_WALLET_WITHDRAW_NOT_HAS_RECORD);
			}
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(vto);
		} catch (BusinessI18nCodeException bex) {
			return RpcResponseDTOBuilder.builderErrorRpcResponse(
					bex.getErrorCode(), bex.getPayload());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder
					.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
}
