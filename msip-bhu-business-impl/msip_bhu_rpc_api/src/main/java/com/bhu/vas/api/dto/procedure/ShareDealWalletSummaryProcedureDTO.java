package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.bhu.vas.api.rpc.user.dto.ShareDealWalletSummaryProcedureVTO;
import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;
import com.smartwork.msip.cores.orm.logic.procedure.OUT;

@SuppressWarnings("serial")
public class ShareDealWalletSummaryProcedureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.INTEGER)
	private int userid;
	@OUT(jdbcType = JdbcType.VARCHAR)
	private String today_date;
	@OUT(jdbcType = JdbcType.DOUBLE)
	private double today_cash;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int today_nums;
	
	@OUT(jdbcType = JdbcType.VARCHAR)
	private String yesterday_date;
	@OUT(jdbcType = JdbcType.DOUBLE)
	private double yesterday_cash;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int yesterday_nums;
	
	@OUT(jdbcType = JdbcType.DOUBLE)
	private double total_cash;
	@OUT(jdbcType = JdbcType.INTEGER)
	private int total_nums;
	
	@OUT(jdbcType = JdbcType.INTEGER)
	private int ods;//online devices
	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public double getToday_cash() {
		return today_cash;
	}

	public void setToday_cash(double today_cash) {
		this.today_cash = today_cash;
	}

	public int getToday_nums() {
		return today_nums;
	}

	public void setToday_nums(int today_nums) {
		this.today_nums = today_nums;
	}

	public double getYesterday_cash() {
		return yesterday_cash;
	}

	public void setYesterday_cash(double yesterday_cash) {
		this.yesterday_cash = yesterday_cash;
	}

	public int getYesterday_nums() {
		return yesterday_nums;
	}

	public void setYesterday_nums(int yesterday_nums) {
		this.yesterday_nums = yesterday_nums;
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

	public int getOds() {
		return ods;
	}

	public void setOds(int ods) {
		this.ods = ods;
	}

	public String getToday_date() {
		return today_date;
	}

	public void setToday_date(String today_date) {
		this.today_date = today_date;
	}

	public String getYesterday_date() {
		return yesterday_date;
	}

	public void setYesterday_date(String yesterday_date) {
		this.yesterday_date = yesterday_date;
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
	
	public ShareDealWalletSummaryProcedureVTO toVTO(){
		ShareDealWalletSummaryProcedureVTO vto = new ShareDealWalletSummaryProcedureVTO();
		vto.setUserid(this.getUserid());
		vto.setOds(this.getOds());
		vto.setToday_date(this.getToday_date());
		vto.setToday_cash(this.getToday_cash());
		vto.setToday_nums(this.getToday_nums());
		vto.setYesterday_date(this.getYesterday_date());
		vto.setYesterday_cash(this.getYesterday_cash());
		vto.setYesterday_nums(this.getYesterday_nums());
		vto.setTotal_cash(this.getTotal_cash());
		vto.setTotal_nums(this.getTotal_nums());
		return vto;
	}
}
