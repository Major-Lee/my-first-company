/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年5月26日 下午5:02:13
 */
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
public class PaymentRecordInfoDTO extends AbstractProcedureDTO{

	@OUT(jdbcType = JdbcType.INTEGER)
	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public String getName() {
		return "payment.history_order_user";
	}
	
	public int count(){
		return this.getCount();
	}
}

