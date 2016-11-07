package com.bhu.vas.api.rpc.advertise.model;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bhu.vas.api.dto.advertise.AdvertiseVTO;
import com.bhu.vas.api.rpc.commdity.helper.StructuredIdHelper;
import com.bhu.vas.api.rpc.sequence.helper.IRedisSequenceGenable;
import com.smartwork.msip.cores.orm.model.BaseStringModel;

@SuppressWarnings("serial")
public class Advertise extends BaseStringModel implements IRedisSequenceGenable{
	private int uid;
	private String title;
	private int type;
	private String orderId;
	private String description;
	private String image;
	private String url;
	//域名
	private String domain;
	//省
	private String province;
	//市
	private String city;
	//区
	private String district;
	private int cash;
	private Date start;
	private Date end;
	//广告持续时间
	private int duration;
	//广告覆盖的设备数
	private long count;
	//有效设备数
	private long ableDevicesNum;
	//审核驳回原因
	private String reject_reason;
	//0:待付款 1:待审核 2:待发布 3:发布中 4:发布完成 5:审核驳回 6:订单取消
	private int state;
	//审核人id
	private int verify_uid;
	private Date created_at;
	private Date updated_at;
	
	public Advertise() {
		super();
	}

	public Advertise(String id) {
		super();
		this.id = id;
	}
	
	public long getAbleDevicesNum() {
		return ableDevicesNum;
	}
	public void setAbleDevicesNum(long ableDevicesNum) {
		this.ableDevicesNum = ableDevicesNum;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getVerify_uid() {
		return verify_uid;
	}
	public void setVerify_uid(int verify_uid) {
		this.verify_uid = verify_uid;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getReject_reason() {
		return reject_reason;
	}
	public void setReject_reason(String reject_reason) {
		this.reject_reason = reject_reason;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCash() {
		return cash;
	}
	public void setCash(int cash) {
		this.cash = cash;
	}
	
	@Override
	public void preInsert() {
		if (this.created_at == null)
			this.created_at = new Date();
		super.preInsert();
	}
	
	@Override
	public void preUpdate() {
		super.preUpdate();
	}
	
	public AdvertiseVTO toVTO(){
		
		AdvertiseVTO singleAdvertise=new AdvertiseVTO();
		//金额处理
		int cash=this.cash;
		double sd=cash/(10000*1.0);
		DecimalFormat formater = new DecimalFormat();
		formater.setMaximumFractionDigits(2);
		formater.setGroupingSize(0);
		formater.setRoundingMode(RoundingMode.FLOOR);
		singleAdvertise.setCash(formater.format(sd));
		singleAdvertise.setCity(this.city);
		singleAdvertise.setCount(this.count);
		singleAdvertise.setDescription(this.description);
		singleAdvertise.setDistrict(this.district);
		singleAdvertise.setEnd(this.end);
		singleAdvertise.setId(this.id);
		singleAdvertise.setProvince(this.province);
		singleAdvertise.setStart(this.start);
		singleAdvertise.setTitle(this.title);
		singleAdvertise.setType(this.type);
		singleAdvertise.setState(this.state);
		singleAdvertise.setImage(this.image);
		singleAdvertise.setUrl(this.url);
		singleAdvertise.setDomain(this.domain);
		singleAdvertise.setReject_reason(this.reject_reason);
		singleAdvertise.setCreate_at(this.created_at);
		return singleAdvertise;
	}
	@Override
	public void setSequenceKey(Long autoId) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		int fir=Integer.valueOf(df.format(new Date()));
		this.setId(StructuredIdHelper.generateStructuredIdStringAD(fir, this.type+"", autoId));
	}
	public static void main(String[] args) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		int fir=Integer.valueOf(df.format(new Date()));
		String randoms=StructuredIdHelper.generateStructuredIdStringAD(fir, "1", 1L);
		System.out.println(randoms);
	}
}
