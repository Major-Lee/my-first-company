package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;

@SuppressWarnings("serial")
public class ShareDealWalletProcedureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.VARCHAR)
	private String orderid;
	@IN(jdbcType = JdbcType.INTEGER)
	private int ownerid;
	@IN(jdbcType = JdbcType.INTEGER)
	private int agentid;
	@IN(jdbcType = JdbcType.INTEGER)
	private int manufacturerid;
	@IN(jdbcType = JdbcType.DOUBLE)
	private double ownercash;
	@IN(jdbcType = JdbcType.DOUBLE)
	private double agentcash;
	@IN(jdbcType = JdbcType.DOUBLE)
	private double manufacturercash;
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public int getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(int ownerid) {
		this.ownerid = ownerid;
	}
	public int getAgentid() {
		return agentid;
	}
	public void setAgentid(int agentid) {
		this.agentid = agentid;
	}
	public int getManufacturerid() {
		return manufacturerid;
	}
	public void setManufacturerid(int manufacturerid) {
		this.manufacturerid = manufacturerid;
	}
	public double getOwnercash() {
		return ownercash;
	}
	public void setOwnercash(double ownercash) {
		this.ownercash = ownercash;
	}
	public double getAgentcash() {
		return agentcash;
	}
	public void setAgentcash(double agentcash) {
		this.agentcash = agentcash;
	}
	public double getManufacturercash() {
		return manufacturercash;
	}
	public void setManufacturercash(double manufacturercash) {
		this.manufacturercash = manufacturercash;
	}
	
	public static void main(String[] argv){
		ShareDealWalletProcedureDTO proDto = new ShareDealWalletProcedureDTO();
		proDto.setName("msip_bhu_core.sharedeal");
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
