package com.bhu.vas.api.dto.procedure;

import org.apache.ibatis.type.JdbcType;

import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.logic.procedure.AbstractProcedureDTO;
import com.smartwork.msip.cores.orm.logic.procedure.IN;

/**
 * 钱包出入账存储过程定义
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WalletInOrOutProcedureDTO extends AbstractProcedureDTO{
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
	private String pvcurrency;
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

	public String getPvcurrency() {
		return pvcurrency;
	}

	public void setPvcurrency(String pvcurrency) {
		this.pvcurrency = pvcurrency;
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
		return "msip_bhu_core.wallet_inputoroutput";
	}
	
/*	public static WalletInOrOutProcedureDTO buildWith(SharedealInfo sharedeal){
		WalletInOrOutProcedureDTO dto = new WalletInOrOutProcedureDTO();
		dto.setMac(sharedeal.getMac());
		dto.setOrderid(sharedeal.getOrderid());
		dto.setOwnerid(sharedeal.getOwner());
		dto.setManufacturerid(sharedeal.getManufacturer());
		dto.setOwnercash(sharedeal.getOwner_cash());
		dto.setManufacturercash(sharedeal.getManufacturer_cash());
		return dto;
		//dto.set
	}*/
	
	public static void main(String[] argv){
		WalletInOrOutProcedureDTO proDto = new WalletInOrOutProcedureDTO();
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
	
	public static WalletInOrOutProcedureDTO build(int userid,String orderid,
			BusinessEnumType.UWalletTransMode transMode,
			BusinessEnumType.UWalletTransType transType,
			double rmoney,double cash,long vcurrency,String description,String memo){
		WalletInOrOutProcedureDTO dto = new WalletInOrOutProcedureDTO();
		dto.setPuserid(userid);
		dto.setPorderid(orderid);
		dto.setPtransmode(transMode.getKey());
		dto.setPtransmode_desc(transMode.getName());		
		dto.setPtranstype(transType.getKey());
		dto.setPtranstype_desc(transType.getName());
		dto.setPrmoney("0.00");
		dto.setPcash("0.00");
		dto.setPvcurrency("0");
		dto.setPdescription(description);
		switch(transMode){
			case DrawPresent://抽奖馈赠 --》充值零钱、充值虎钻
				dto.setPrmoney(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(rmoney)));
				if(UWalletTransType.Recharge2C.equals(transType)){
					dto.setPcash(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(cash)));
				}
				if(UWalletTransType.Recharge2V.equals(transType)){
					dto.setPvcurrency(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(vcurrency)));
				}
				break;
			case RealMoneyPayment://现金支付 --》 充值零钱、充值虎钻
				dto.setPrmoney(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(rmoney)));
				if(UWalletTransType.Recharge2C.equals(transType)){
					dto.setPcash(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(cash)));
				}
				if(UWalletTransType.Recharge2V.equals(transType)){
					dto.setPvcurrency(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(vcurrency)));
				}
				break;
			case CashPayment://充值虎钻、零钱购买道具、提现
				dto.setPcash(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(cash)));
				if(UWalletTransType.Recharge2V.equals(transType)){
					dto.setPvcurrency(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(vcurrency)));
				}
				if(UWalletTransType.PurchaseGoodsUsedC.equals(transType)){
					;//日志里不体现具体道具
				}
				if(UWalletTransType.PurchaseInternetServiceUsedVForSMS.equals(transType) || UWalletTransType.PurchaseInternetServiceUsedVForWechat.equals(transType)){
					;//日志里不体现具体什么上网服务
				}
				if(UWalletTransType.Cash2Realmoney.equals(transType)){
					dto.setPrmoney(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(rmoney)));
				}
				break;
			case VCurrencyPayment://购买道具
				dto.setPvcurrency(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(vcurrency)));
				break;
			case SharedealPayment://红包打赏结算
				dto.setPrmoney(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(rmoney)));
				dto.setPcash(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(cash)));
				break;
			case CashRollbackPayment://
				dto.setPrmoney(StringHelper.MINUS_STRING_GAP.concat(String.valueOf(rmoney)));
				dto.setPcash(StringHelper.PLUS_STRING_GAP.concat(String.valueOf(cash)));
				break;
			default:
				break;
		}
		dto.setPmemo(memo);
		return dto;
	}
}
