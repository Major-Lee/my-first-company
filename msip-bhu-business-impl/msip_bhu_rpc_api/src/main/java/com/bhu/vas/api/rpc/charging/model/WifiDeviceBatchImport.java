package com.bhu.vas.api.rpc.charging.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

/**
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceBatchImport extends BaseStringModel implements IRedisSequenceGenable{
	//ID 格式为 yyyyMMdd-longsequence
	
	//取消导入
	public static final int STATUS_IMPORTING_CANCEL = -1;
	//导入上传中
	public static final int STATUS_IMPORTING_FILE = 0;
	//导入上传成功
	public static final int STATUS_IMPORTED_FILE = 1;
	//确定导入批次
	public static final int STATUS_CONFIRMED_DOING = 2;
	//导入内容正在预处理中
	public static final int STATUS_CONTENT_PRE_CHECK = 3;
	//导入内容预处理成功
	public static final int STATUS_CONTENT_PRE_CHECK_DONE = 4;
	//导入内容正在处理中
	public static final int STATUS_CONTENT_IMPORTING = 5;
	//导入内容处理成功
	public static final int STATUS_CONTENT_IMPORTED = 6;
	//文件存储路径
	private String filepath;
	// 0-文件正在导入 1-文件导入成功 2-确认导入成功
	private int status;
	//导入用户
	private int importor;
	private String mobileno;
	private int distributor = -1;
	private int distributor_l2 = -1;
	private String sellor;
	private String partner;
	private boolean canbeturnoff;
	private boolean enterpriselevel;
	private boolean customized = false;
	private String owner_percent;
	private String manufacturer_percent;
	private String distributor_percent;
	private String distributor_l2_percent;
	private String range_cash_mobile;
	private String range_cash_pc;
	private String access_internet_time;
	private String opsid; //运营商系统的批次id
	//private int total;
	private int succeed;
	private int failed;
	private String channel_lv1;
	private String channel_lv2;
	private String distributor_type;
	private String remark;
    /**
     * 创建日期
     */
    private Date created_at;
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getSucceed() {
		return succeed;
	}
	public void setSucceed(int succeed) {
		this.succeed = succeed;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	public int getImportor() {
		return importor;
	}
	public void setImportor(int importor) {
		this.importor = importor;
	}
	
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getSellor() {
		return sellor;
	}
	public void setSellor(String sellor) {
		this.sellor = sellor;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	
	public String getOwner_percent() {
		return owner_percent;
	}
	public void setOwner_percent(String owner_percent) {
		this.owner_percent = owner_percent;
	}
	public boolean isEnterpriselevel() {
		return enterpriselevel;
	}
	public void setEnterpriselevel(boolean enterpriselevel) {
		this.enterpriselevel = enterpriselevel;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	private static final String FormatSequenceTemplete = "%08d";
	@Override
	public void setSequenceKey(Long key) {
		StringBuilder sb = new StringBuilder();
		sb.append(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern7))
			.append(StringHelper.MINUS_CHAR_GAP)
			.append(String.format(FormatSequenceTemplete, key));
		this.setId(sb.toString());
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public BatchImportVTO toBatchImportVTO(String importor_nick,String importor_mobileno,String distributor_nick, String distributor_l2_nick){
		BatchImportVTO vto = new BatchImportVTO();
		vto.setId(this.getId());
		vto.setImportor(this.getImportor());
		vto.setImportor_nick(importor_nick);
		vto.setImportor_mobileno(importor_mobileno);
		vto.setMobileno(this.getMobileno());
		vto.setDistributor(this.getDistributor());
		vto.setDistributor_nick(StringUtils.isNotEmpty(distributor_nick)?distributor_nick:StringHelper.EMPTY_STRING_GAP);
		vto.setDistributor_l2(this.getDistributor_l2());
		vto.setDistributor_nick(StringUtils.isNotEmpty(distributor_l2_nick)?distributor_l2_nick:StringHelper.EMPTY_STRING_GAP);
		vto.setSellor(this.getSellor());
		vto.setPartner(this.getPartner());
		vto.setOwner_percent(this.getOwner_percent());
		vto.setManufacturer_percent(this.getManufacturer_percent());
		vto.setDistributor_percent(this.getDistributor_percent());
		vto.setDistributor_l2_percent(this.distributor_l2_percent);
		vto.setCustomized(this.isCustomized());
		//vto.setManufacturer_percent(manufacturer_percent);
		vto.setCanbeturnoff(this.isCanbeturnoff());
		vto.setEnterpriselevel(this.isEnterpriselevel());
		vto.setAit(this.getAccess_internet_time());
		vto.setRcm(this.getRange_cash_mobile());
		vto.setRcp(this.getRange_cash_pc());
		vto.setRemark(this.getRemark());
		vto.setStatus(this.getStatus());
		vto.setSucceed(this.getSucceed());
		vto.setFailed(this.getFailed());
		vto.setOpsid(this.getOpsid());
		vto.setDistributor_type(this.getDistributor_type());
		vto.setCreated_at(DateTimeHelper.getDateTime(this.getCreated_at(), DateTimeHelper.FormatPattern0));
		vto.setUpdated_at(DateTimeHelper.getDateTime(this.getCreated_at(), DateTimeHelper.FormatPattern0));
		vto.setChannel_lv1(this.getChannel_lv1());
		vto.setChannel_lv2(this.getChannel_lv2());
		return vto;
	}
	
	public boolean isCanbeturnoff() {
		return canbeturnoff;
	}
	public void setCanbeturnoff(boolean canbeturnoff) {
		this.canbeturnoff = canbeturnoff;
	}
	public String getRange_cash_mobile() {
		return range_cash_mobile;
	}
	public void setRange_cash_mobile(String range_cash_mobile) {
		this.range_cash_mobile = range_cash_mobile;
	}
	public String getRange_cash_pc() {
		return range_cash_pc;
	}
	public void setRange_cash_pc(String range_cash_pc) {
		this.range_cash_pc = range_cash_pc;
	}
	public String getAccess_internet_time() {
		return access_internet_time;
	}
	public void setAccess_internet_time(String access_internet_time) {
		this.access_internet_time = access_internet_time;
	}
	public boolean isCustomized() {
		return customized;
	}
	public void setCustomized(boolean customized) {
		this.customized = customized;
	}
	public int getDistributor() {
		return distributor;
	}
	public void setDistributor(int distributor) {
		this.distributor = distributor;
	}
	public String getManufacturer_percent() {
		return manufacturer_percent;
	}
	public void setManufacturer_percent(String manufacturer_percent) {
		this.manufacturer_percent = manufacturer_percent;
	}
	public String getDistributor_percent() {
		return distributor_percent;
	}
	public void setDistributor_percent(String distributor_percent) {
		this.distributor_percent = distributor_percent;
	}
	public String getChannel_lv1() {
		return channel_lv1;
	}
	public void setChannel_lv1(String channel_lv1) {
		this.channel_lv1 = channel_lv1;
	}
	public String getChannel_lv2() {
		return channel_lv2;
	}
	public void setChannel_lv2(String channel_lv2) {
		this.channel_lv2 = channel_lv2;
	}
	public String getOpsid() {
		return opsid;
	}
	public void setOpsid(String opsid) {
		this.opsid = opsid;
	}
	public String getDistributor_type() {
		return distributor_type;
	}
	public void setDistributor_type(String distributor_type) {
		this.distributor_type = distributor_type;
	}
	public int getDistributor_l2() {
		return distributor_l2;
	}
	public void setDistributor_l2(int distributor_l2) {
		this.distributor_l2 = distributor_l2;
	}
	public String getDistributor_l2_percent() {
		return distributor_l2_percent;
	}
	public void setDistributor_l2_percent(String distributor_l2_percent) {
		this.distributor_l2_percent = distributor_l2_percent;
	}
	
}
