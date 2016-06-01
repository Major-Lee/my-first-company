package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;
import com.smartwork.msip.cores.orm.logic.procedure.OUT;

@SuppressWarnings("serial")
public class FincialStatisticsProdureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.VARCHAR)
	private String beginTime;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String endTime;
	@IN(jdbcType = JdbcType.INTEGER)
	private int objType;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String payType;
	
	@OUT(jdbcType = JdbcType.DOUBLE)
	private double total;
	
	@Override
	public String getName() {
		return "msip_bhu_core.fincial_statistics";
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getObjType() {
		return objType;
	}

	public void setObjType(int objType) {
		this.objType = objType;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
	
	public double toTotal(){
		return this.getTotal();
	}
	public static void main(String[] args) {
		FincialStatisticsProdureDTO fincialStatisticsProdureDTO=new FincialStatisticsProdureDTO();
		System.out.println(fincialStatisticsProdureDTO.getProc_call());
	}
	
}	
