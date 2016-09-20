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
public class PaymentErrorCountDTO extends AbstractProcedureDTO {

	@OUT(jdbcType = JdbcType.INTEGER)
	private int midas_more_one_min_count;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int midas_more_three_min_count;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int now_more_one_min_count;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int now_more_three_min_count;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int hee_more_one_min_count;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int hee_more_three_min_count;
	
	public int getMidas_more_one_min_count() {
		return midas_more_one_min_count;
	}

	public void setMidas_more_one_min_count(int midas_more_one_min_count) {
		this.midas_more_one_min_count = midas_more_one_min_count;
	}

	public int getMidas_more_three_min_count() {
		return midas_more_three_min_count;
	}

	public void setMidas_more_three_min_count(int midas_more_three_min_count) {
		this.midas_more_three_min_count = midas_more_three_min_count;
	}

	public int getNow_more_one_min_count() {
		return now_more_one_min_count;
	}

	public void setNow_more_one_min_count(int now_more_one_min_count) {
		this.now_more_one_min_count = now_more_one_min_count;
	}

	public int getNow_more_three_min_count() {
		return now_more_three_min_count;
	}

	public void setNow_more_three_min_count(int now_more_three_min_count) {
		this.now_more_three_min_count = now_more_three_min_count;
	}

	public int getHee_more_one_min_count() {
		return hee_more_one_min_count;
	}

	public void setHee_more_one_min_count(int hee_more_one_min_count) {
		this.hee_more_one_min_count = hee_more_one_min_count;
	}

	public int getHee_more_three_min_count() {
		return hee_more_three_min_count;
	}

	public void setHee_more_three_min_count(int hee_more_three_min_count) {
		this.hee_more_three_min_count = hee_more_three_min_count;
	}

	@Override
	public String getName() {
		return "statistic_pay_channel_by_time";
	}

}