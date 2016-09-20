package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.bhu.vas.api.rpc.user.dto.ShareDealDailyGroupSummaryProcedureVTO;
import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;
import com.smartwork.msip.cores.orm.logic.procedure.OUT;

@SuppressWarnings("serial")
public class ShareDealDailyGroupSummaryProcedureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.INTEGER)
	private int userid;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String gpath;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String cdate;
	
	@OUT(jdbcType = JdbcType.DOUBLE)
	private double total_cash;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int total_nums;
	
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public double getTotal_cash() {
		return total_cash;
	}

	public void setTotal_cash(double total_cash) {
		this.total_cash = total_cash;
	}

	public int getTotal_nums() {
		return total_nums;
	}

	public void setTotal_nums(int total_nums) {
		this.total_nums = total_nums;
	}

	public String getCdate() {
		return cdate;
	}

	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

	public String getGpath() {
		return gpath;
	}

	public void setGpath(String gpath) {
		this.gpath = gpath;
	}

	@Override
	public String getName() {
		return "msip_bhu_core.daily_group_sharedeal_sum";
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
		ShareDealDailyGroupSummaryProcedureDTO proDto = new ShareDealDailyGroupSummaryProcedureDTO();
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
	
	public ShareDealDailyGroupSummaryProcedureVTO toVTO(){
		ShareDealDailyGroupSummaryProcedureVTO vto = new ShareDealDailyGroupSummaryProcedureVTO();
		vto.setUserid(this.getUserid());
		vto.setCdate(this.getCdate());
		vto.setGpath(this.getGpath());
		vto.setTotal_cash(this.getTotal_cash());
		vto.setTotal_nums(this.getTotal_nums());
		return vto;
	}

	public boolean isMaster() {
		return false;
	}
	
}
