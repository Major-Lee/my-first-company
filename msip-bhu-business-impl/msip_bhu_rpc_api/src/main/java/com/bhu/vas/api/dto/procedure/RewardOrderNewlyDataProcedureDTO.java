package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.bhu.vas.api.dto.commdity.OrderRewardNewlyDataVTO;
import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;
import com.smartwork.msip.cores.orm.logic.procedure.OUT;

@SuppressWarnings("serial")
public class RewardOrderNewlyDataProcedureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.INTEGER)
	private Integer uid;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String start_date;

	@OUT(jdbcType = JdbcType.VARCHAR)
	private String newly_count;
	@OUT(jdbcType = JdbcType.VARCHAR)
	private String newly_amount_count;
	
	@Override
	public String getName() {
		return "msip_bhu_commdity.reward_order_newly_data";
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getStart_date() {
		return start_date;
	}
	
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getNewly_count() {
		return newly_count;
	}

	public void setNewly_count(String newly_count) {
		this.newly_count = newly_count;
	}

	public String getNewly_amount_count() {
		return newly_amount_count;
	}

	public void setNewly_amount_count(String newly_amount_count) {
		this.newly_amount_count = newly_amount_count;
	}

	public OrderRewardNewlyDataVTO toVTO(){
		OrderRewardNewlyDataVTO vto = new OrderRewardNewlyDataVTO();
		vto.setNewly_count(newly_count);
		vto.setNewly_amount_count(newly_amount_count);
		return vto;
	}
	
	@Override
	public boolean isMaster(){
		return false;
	}
}
