package com.bhu.vas.api.dto.procedure;

import java.util.Date;

import org.apache.ibatis.type.JdbcType;

import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;

@SuppressWarnings("serial")
public class ShareDealWalletProcedureDTO extends AbstractProcedureDTO {

	@IN(jdbcType = JdbcType.VARCHAR)
	private String mac;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String umac;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String orderid;
	@IN(jdbcType = JdbcType.INTEGER)
	private int ownerid;
	// @IN(jdbcType = JdbcType.INTEGER)
	// private int agentid;
	@IN(jdbcType = JdbcType.INTEGER)
	private int manufacturerid;
	@IN(jdbcType = JdbcType.INTEGER)
	private int distributorid;
	@IN(jdbcType = JdbcType.INTEGER)
	private int distributorl2id;
	@IN(jdbcType = JdbcType.DOUBLE)
	private double ownercash;
	// @IN(jdbcType = JdbcType.DOUBLE)
	// private double agentcash;
	@IN(jdbcType = JdbcType.DOUBLE)
	private double manufacturercash;
	@IN(jdbcType = JdbcType.DOUBLE)
	private double distributorcash;
	@IN(jdbcType = JdbcType.DOUBLE)
	private double distributorl2cash;

	@IN(jdbcType = JdbcType.VARCHAR)
	private String transmode;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String transmode_desc;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String transtype;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String transtype_desc;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String description;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String owner_memo;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String manufacturer_memo;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String distributor_memo;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String distributor_l2_memo;
	@IN(jdbcType = JdbcType.DATE)
	private Date pay_time;	//订单时间
	@IN(jdbcType = JdbcType.INTEGER)
	private long detail_id=-1;	//广告分成时为广告分成明细id

	
	public long getDetail_id() {
		return detail_id;
	}

	public void setDetail_id(long detail_id) {
		this.detail_id = detail_id;
	}

	public Date getPay_time() {
		return pay_time;
	}

	public void setPay_time(Date order_time) {
		this.pay_time = order_time;
	}

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

	public int getManufacturerid() {
		return manufacturerid;
	}

	public void setManufacturerid(int manufacturerid) {
		this.manufacturerid = manufacturerid;
	}

	/**
	 * public int getAgentid() { return agentid; } public void setAgentid(int
	 * agentid) { this.agentid = agentid; }
	 */
	public double getOwnercash() {
		return ownercash;
	}

	public void setOwnercash(double ownercash) {
		this.ownercash = ownercash;
	}

	/**
	 * public double getAgentcash() { return agentcash; } public void
	 * setAgentcash(double agentcash) { this.agentcash = agentcash; }
	 */
	public double getManufacturercash() {
		return manufacturercash;
	}

	public void setManufacturercash(double manufacturercash) {
		this.manufacturercash = manufacturercash;
	}

	public String getTransmode() {
		return transmode;
	}

	public void setTransmode(String transmode) {
		this.transmode = transmode;
	}

	public String getTransmode_desc() {
		return transmode_desc;
	}

	public void setTransmode_desc(String transmode_desc) {
		this.transmode_desc = transmode_desc;
	}

	public String getTranstype() {
		return transtype;
	}

	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}

	public String getTranstype_desc() {
		return transtype_desc;
	}

	public void setTranstype_desc(String transtype_desc) {
		this.transtype_desc = transtype_desc;
	}

	public String getOwner_memo() {
		return owner_memo;
	}

	public void setOwner_memo(String owner_memo) {
		this.owner_memo = owner_memo;
	}

	public String getManufacturer_memo() {
		return manufacturer_memo;
	}

	public void setManufacturer_memo(String manufacturer_memo) {
		this.manufacturer_memo = manufacturer_memo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDistributorid() {
		return distributorid;
	}

	public void setDistributorid(int distributorid) {
		this.distributorid = distributorid;
	}

	public double getDistributorcash() {
		return distributorcash;
	}

	public void setDistributorcash(double distributorcash) {
		this.distributorcash = distributorcash;
	}

	public String getDistributor_memo() {
		return distributor_memo;
	}

	public void setDistributor_memo(String distributor_memo) {
		this.distributor_memo = distributor_memo;
	}

	public int getDistributorl2id() {
		return distributorl2id;
	}

	public void setDistributorl2id(int distributorl2id) {
		this.distributorl2id = distributorl2id;
	}

	public double getDistributorl2cash() {
		return distributorl2cash;
	}

	public void setDistributorl2cash(double distributorl2cash) {
		this.distributorl2cash = distributorl2cash;
	}

	public String getDistributor_l2_memo() {
		return distributor_l2_memo;
	}

	public void setDistributor_l2_memo(String distributor_l2_memo) {
		this.distributor_l2_memo = distributor_l2_memo;
	}

	@Override
	public String getName() {
		return "msip_bhu_core.sharedeal";
	}

	public static ShareDealWalletProcedureDTO buildWith(SharedealInfo sharedeal){
		ShareDealWalletProcedureDTO dto = new ShareDealWalletProcedureDTO();
		dto.setMac(sharedeal.getMac());
		dto.setUmac(sharedeal.getUmac());
		dto.setOrderid(sharedeal.getOrderid());
		dto.setOwnerid(sharedeal.getOwner());
		dto.setManufacturerid(sharedeal.getManufacturer());
		dto.setDistributorid(sharedeal.getDistributor());
		dto.setDistributorl2id(sharedeal.getDistributor_l2());
		dto.setOwnercash(sharedeal.getOwner_cash());
		dto.setManufacturercash(sharedeal.getManufacturer_cash());
		dto.setDistributorcash(sharedeal.getDistributor_cash());
		dto.setDistributorl2cash(sharedeal.getDistributor_l2_cash());
		return dto;
	}

	public static void main(String[] argv) {
		ShareDealWalletProcedureDTO proDto = new ShareDealWalletProcedureDTO();
		// proDto.setName("msip_bhu_core.sharedeal");
		System.out.println(proDto.getProc_call());
		/*
		 * proDto.setName("subjectIncrement"); proDto.setIncr_down(10);
		 * proDto.setIncr_up(101); proDto.setId(786001);
		 * 
		 * 
		 * proDto.setOut1("1234"); proDto.setOut2("435");
		 * System.out.println(proDto.getProc_call()); int result =
		 * subjectMClickService.executeProcedure(proDto);
		 * System.out.println(result); System.out.println(proDto.getOut1());
		 * System.out.println(proDto.getOut2());
		 */
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getUmac() {
		return umac;
	}

	public void setUmac(String umac) {
		this.umac = umac;
	}

}
