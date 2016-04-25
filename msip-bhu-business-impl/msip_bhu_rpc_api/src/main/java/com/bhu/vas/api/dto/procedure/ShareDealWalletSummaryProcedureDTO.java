package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;
import com.smartwork.msip.cores.orm.logic.procedure.OUT;

@SuppressWarnings("serial")
public class ShareDealWalletSummaryProcedureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.INTEGER)
	private int userid;
	
	@OUT(jdbcType = JdbcType.DOUBLE)
	private String cur_cash;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int cur_total;
	
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getCur_cash() {
		return cur_cash;
	}

	public void setCur_cash(String cur_cash) {
		this.cur_cash = cur_cash;
	}

	public int getCur_total() {
		return cur_total;
	}

	public void setCur_total(int cur_total) {
		this.cur_total = cur_total;
	}

	@Override
	public String getName() {
		return "msip_bhu_core.user_sharedeal_sum";
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
		ShareDealWalletSummaryProcedureDTO proDto = new ShareDealWalletSummaryProcedureDTO();
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
