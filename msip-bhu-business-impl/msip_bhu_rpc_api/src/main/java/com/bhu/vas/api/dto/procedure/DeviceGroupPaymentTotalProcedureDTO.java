package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;

@SuppressWarnings("serial")
public class DeviceGroupPaymentTotalProcedureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.INTEGER)
	private int userid;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String gid;
	
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	@Override
	public String getName() {
		return "msip_bhu_core.total_group_sharedeal_sum";
	}
	
	/*public static ShareDealWalletSummaryProcedureDTO buildWith(SharedealInfo sharedeal){
		ShareDealWalletSummaryProcedureDTO dto = new ShareDealWalletSummaryProcedureDTO();
		dto.setOrderid(sharedeal.getOrderid());
		dto.setOwnerid(sharedeal.getOwner());
		dto.setManufacturerid(sharedeal.getManufacturer());
		dto.setOwnercash(sharedeal.getOwner_cash());
		dto.setManufacturercash(sharedeal.getManufacturer_cash());
		return dto;
		//dto.set
	}*/
	
	public static void main(String[] argv){
		DeviceGroupPaymentTotalProcedureDTO proDto = new DeviceGroupPaymentTotalProcedureDTO();
		//proDto.setName("msip_bhu_core.sharedeal");
		System.out.println(proDto.getProc_call());
			/*proDto.setName("subjectIncrement");
			proDto.setIncr_down(10);
			proDto.setIncr_up(101);
			proDto.setId(786001);
			
			
			proDto.setOut1("1234");
			proDto.setOut2("435");
			System.out.println(proDto.getProc_call());
			int result = subjectMClickService.executeProcedure(proDto);
			System.out.println(result);
			System.out.println(proDto.getOut1());
			System.out.println(proDto.getOut2());*/
	}

}
