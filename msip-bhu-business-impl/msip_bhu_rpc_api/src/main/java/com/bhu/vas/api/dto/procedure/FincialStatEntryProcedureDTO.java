package com.bhu.vas.api.dto.procedure;

import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;

/**
 * 财务对账数据生成存储过程定义
 * @author Yetao
 *
 */
@SuppressWarnings("serial")
public class FincialStatEntryProcedureDTO extends AbstractProcedureDTO{
	@Override
	public String getName() {
		return "msip_bhu_core.fincial_stat_entry";
	}

	public static void main(String[] argv){
		FincialStatEntryProcedureDTO proDto = new FincialStatEntryProcedureDTO();
		System.out.println(proDto.getProc_call());
	}
	
	public static FincialStatEntryProcedureDTO build(){
		FincialStatEntryProcedureDTO dto = new FincialStatEntryProcedureDTO();
		return dto;
	}
}
