package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;

/**
 * 钱包出入账存储过程定义
 * @author fengshibo
 *
 */
@SuppressWarnings("serial")
public class ConsumptiveWalletInOrOutProcedureDTO extends AbstractProcedureDTO{
	@IN(jdbcType = JdbcType.INTEGER)
	private int puserid;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String porderid;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String ptransmode;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String ptransmode_desc;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String ptranstype;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String ptranstype_desc;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String prmoney;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String pcash;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String pdescription;
	@IN(jdbcType = JdbcType.VARCHAR)
	private String pmemo;
	
	public int getPuserid() {
		return puserid;
	}

	public void setPuserid(int puserid) {
		this.puserid = puserid;
	}

	public String getPorderid() {
		return porderid;
	}

	public void setPorderid(String porderid) {
		this.porderid = porderid;
	}

	public String getPtransmode() {
		return ptransmode;
	}

	public void setPtransmode(String ptransmode) {
		this.ptransmode = ptransmode;
	}

	public String getPtransmode_desc() {
		return ptransmode_desc;
	}

	public void setPtransmode_desc(String ptransmode_desc) {
		this.ptransmode_desc = ptransmode_desc;
	}

	public String getPtranstype() {
		return ptranstype;
	}

	public void setPtranstype(String ptranstype) {
		this.ptranstype = ptranstype;
	}

	public String getPtranstype_desc() {
		return ptranstype_desc;
	}

	public void setPtranstype_desc(String ptranstype_desc) {
		this.ptranstype_desc = ptranstype_desc;
	}

	public String getPrmoney() {
		return prmoney;
	}

	public void setPrmoney(String prmoney) {
		this.prmoney = prmoney;
	}

	public String getPcash() {
		return pcash;
	}

	public void setPcash(String pcash) {
		this.pcash = pcash;
	}


	public String getPdescription() {
		return pdescription;
	}

	public void setPdescription(String pdescription) {
		this.pdescription = pdescription;
	}

	public String getPmemo() {
		return pmemo;
	}

	public void setPmemo(String pmemo) {
		this.pmemo = pmemo;
	}

	@Override
	public String getName() {
		return "msip_bhu_core.consumptive_wallet_inputoroutput";
	}
	
	public static ConsumptiveWalletInOrOutProcedureDTO build(int userid,String orderid,
			BusinessEnumType.UConsumptiveWalletTransMode transMode,
			BusinessEnumType.UConsumptiveWalletTransType transType,
			double rmoney,double cash,String description,String memo){
		long cashLong = ArithHelper.doubleCurrencyToLong(cash, 
				BusinessRuntimeConfiguration.WalletDataBaseDegree);
		long rmoneyLong = ArithHelper.doubleCurrencyToLong(rmoney, 
				BusinessRuntimeConfiguration.WalletDataBaseDegree);
		ConsumptiveWalletInOrOutProcedureDTO dto = new ConsumptiveWalletInOrOutProcedureDTO();
		dto.setPuserid(userid);
		dto.setPorderid(orderid);
		dto.setPtransmode(transMode.getKey());
		dto.setPtransmode_desc(transMode.getName());		
		dto.setPtranstype(transType.getKey());
		dto.setPtranstype_desc(transType.getName());
		dto.setPrmoney("0");
		dto.setPcash("0");
		dto.setPdescription(description);
		switch(transMode){
			case CashPayment://消费
				dto.setPcash(String.valueOf(cashLong));
				break;
			case RealMoneyPayment://充值
				dto.setPrmoney(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(rmoneyLong)));
				dto.setPcash(String.valueOf(cashLong));
				break;
			default:
				break;
		}
		dto.setPmemo(memo);
		return dto;
	}
}
