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
	private int pc_order_created_count;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int pc_order_finish_count;
	@OUT(jdbcType = JdbcType.VARCHAR)
	private String pc_order_finish_amount;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int mb_order_created_count;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int mb_order_finish_count;
	@OUT(jdbcType = JdbcType.VARCHAR)
	private String mb_order_finish_amount;
	
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
	
	public int getPc_order_created_count() {
		return pc_order_created_count;
	}

	public void setPc_order_created_count(int pc_order_created_count) {
		this.pc_order_created_count = pc_order_created_count;
	}

	public int getPc_order_finish_count() {
		return pc_order_finish_count;
	}

	public void setPc_order_finish_count(int pc_order_finish_count) {
		this.pc_order_finish_count = pc_order_finish_count;
	}

	public String getPc_order_finish_amount() {
		return pc_order_finish_amount;
	}

	public void setPc_order_finish_amount(String pc_order_finish_amount) {
		this.pc_order_finish_amount = pc_order_finish_amount;
	}

	public int getMb_order_created_count() {
		return mb_order_created_count;
	}

	public void setMb_order_created_count(int mb_order_created_count) {
		this.mb_order_created_count = mb_order_created_count;
	}

	public int getMb_order_finish_count() {
		return mb_order_finish_count;
	}

	public void setMb_order_finish_count(int mb_order_finish_count) {
		this.mb_order_finish_count = mb_order_finish_count;
	}

	public String getMb_order_finish_amount() {
		return mb_order_finish_amount;
	}

	public void setMb_order_finish_amount(String mb_order_finish_amount) {
		this.mb_order_finish_amount = mb_order_finish_amount;
	}

	public OrderStatisticsVTO toVTO(){
		OrderStatisticsVTO vto = new OrderStatisticsVTO();
		vto.setPc_occ(this.getPc_order_created_count());
		vto.setPc_ofc(this.getPc_order_finish_count());
		vto.setPc_ofa(this.getPc_order_finish_amount());
		vto.setMb_occ(this.getMb_order_created_count());
		vto.setMb_ofc(this.getMb_order_finish_count());
		vto.setMb_ofa(this.getMb_order_finish_amount());
		return vto;
	}
}
