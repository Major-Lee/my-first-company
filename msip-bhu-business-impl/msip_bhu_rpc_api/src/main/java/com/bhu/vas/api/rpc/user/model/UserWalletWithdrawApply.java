package com.bhu.vas.api.rpc.user.model;

import java.util.Date;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderExtSegmentPayMode;
import com.bhu.vas.api.rpc.commdity.helper.StructuredIdHelper;
import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.bhu.vas.api.rpc.user.dto.ApplyCost;
import com.bhu.vas.api.rpc.user.dto.WithdrawRemoteResponseDTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.smartwork.msip.cores.orm.model.extjson.ListJsonExtStringModel;

/**
 * 用户提现申请
 * 用户提取申发起之后金额会从钱包零钱中扣除，如果提现金额审核不通过或者提现失败会重新返还金额到钱包
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
//BaseStringModel
public class UserWalletWithdrawApply extends ListJsonExtStringModel<WithdrawRemoteResponseDTO> implements IRedisSequenceGenable {
	private int uid;
	private int appid;
	//提现入账账户类别
	private String payment_type;
	//申请提现的现金
	private double cash;
	//提现操作状态BusinessEnumType.UWithdrawStatus
	private String withdraw_oper;
	//最后一次的审核员
	private int last_reckoner;
	private String remoteip;
	
	private Date created_at;
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public String getWithdraw_oper() {
		return withdraw_oper;
	}
	public void setWithdraw_oper(String withdraw_oper) {
		this.withdraw_oper = withdraw_oper;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getLast_reckoner() {
		return last_reckoner;
	}

	public void setLast_reckoner(int last_reckoner) {
		this.last_reckoner = last_reckoner;
	}

	public String getRemoteip() {
		return remoteip;
	}

	public void setRemoteip(String remoteip) {
		this.remoteip = remoteip;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}


	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	@Override
	public void setSequenceKey(Long autoid) {
		this.id = StructuredIdHelper.generateStructuredIdString(appid, 
				StructuredIdHelper.buildStructuredExtSegmentString(OrderExtSegmentPayMode.Expend.getKey()),
				autoid);
	}
	
	public String toString(){
		return String.format("WalletWithdrawApply id[%s] appid[%s] payment_type[%s] uid[%s] cash[%s] withdraw_oper[%s] last_reckoner[%s]", id,appid,payment_type,uid,cash,withdraw_oper,last_reckoner);
	}
	
	public UserWithdrawApplyVTO toUserWithdrawApplyVTO(String mobileno,String nick,ApplyCost applyCost){
		UserWithdrawApplyVTO vto = new UserWithdrawApplyVTO();
		vto.setApplyid(id);
		vto.setUid(uid);
		vto.setAppid(appid);
		vto.setPayment_type(payment_type);
		vto.setNick(nick);
		vto.setMobileno(mobileno);
		vto.setCash(applyCost.getCash());
		vto.setTaxcost(applyCost.getTaxcost());
		vto.setTranscost(applyCost.getTranscost());
		vto.setWithdraw_oper(withdraw_oper);
		vto.setWithdraw_oper_desc(BusinessEnumType.UWithdrawStatus.fromKey(withdraw_oper).getName());
		//vto.calculate(withdraw_tax_percent, withdraw_trancost_percent);
		return vto;
	}

	public void addResponseDTO(WithdrawRemoteResponseDTO resDTO){
		this.putInnerModel(resDTO);
	}
	
	@Override
	public Class<WithdrawRemoteResponseDTO> getJsonParserModel() {
		return WithdrawRemoteResponseDTO.class;
	}

	@Override
	public int limitSize() {
		return 20;
	}
}
