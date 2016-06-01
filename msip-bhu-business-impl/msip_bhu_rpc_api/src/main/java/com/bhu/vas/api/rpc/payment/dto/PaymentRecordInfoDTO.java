package com.bhu.vas.api.rpc.payment.dto;

import org.apache.ibatis.type.JdbcType;

import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.OUT;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年5月26日 下午5:02:13
 */
@SuppressWarnings("serial")
public class PaymentRecordInfoDTO extends AbstractProcedureDTO {

	@OUT(jdbcType = JdbcType.INTEGER)
	private int history_order_count;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int history_order_user;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int pay_again_user_count;

	public int getHistory_order_count() {
		return history_order_count;
	}

	public void setHistory_order_count(int history_order_count) {
		this.history_order_count = history_order_count;
	}

	public int getHistory_order_user() {
		return history_order_user;
	}

	public void setHistory_order_user(int history_order_user) {
		this.history_order_user = history_order_user;
	}

	public int getPay_again_user_count() {
		return pay_again_user_count;
	}

	public void setPay_again_user_count(int pay_again_user_count) {
		this.pay_again_user_count = pay_again_user_count;
	}

	@Override
	public String getName() {
		return "payment.history_order_user";
	}

}