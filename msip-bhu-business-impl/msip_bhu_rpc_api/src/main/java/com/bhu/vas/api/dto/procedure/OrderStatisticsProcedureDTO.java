package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.bhu.vas.api.vto.statistics.OrderStatisticsVTO;
import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;
import com.smartwork.msip.cores.orm.logic.procedure.OUT;

@SuppressWarnings("serial")
public class OrderStatisticsProcedureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.VARCHAR)
	private String start_date;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String end_date;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int order_created_count;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int order_finish_count;
	@OUT(jdbcType = JdbcType.VARCHAR)
	private String order_finish_amount;
	
	@Override
	public String getName() {
		return "msip_bhu_commdity.order_statistics";
	}
	
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public int getOrder_created_count() {
		return order_created_count;
	}
	public void setOrder_created_count(int order_created_count) {
		this.order_created_count = order_created_count;
	}
	public int getOrder_finish_count() {
		return order_finish_count;
	}
	public void setOrder_finish_count(int order_finish_count) {
		this.order_finish_count = order_finish_count;
	}
	public String getOrder_finish_amount() {
		return order_finish_amount;
	}
	public void setOrder_finish_amount(String order_finish_amount) {
		this.order_finish_amount = order_finish_amount;
	}
	
	public OrderStatisticsVTO toVTO(){
		OrderStatisticsVTO vto = new OrderStatisticsVTO();
		vto.setOcc(this.getOrder_created_count());
		vto.setOfc(this.getOrder_finish_count());
		vto.setOfa(this.getOrder_finish_amount());
		return vto;
	}
}
