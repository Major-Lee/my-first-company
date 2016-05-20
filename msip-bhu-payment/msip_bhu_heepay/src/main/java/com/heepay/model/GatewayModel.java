package com.heepay.model;

public class GatewayModel 
{
	private String version;
	private String agent_id;
	private String agent_bill_id;
	private String pay_type;
	private String pay_flag;
	private String pay_code;
	private String pay_amt;
	private String notify_url;
	private String return_url;
	private String user_ip;
	private String agent_bill_time;
	private String goods_name;
	private String goods_num;
	private String remark;
	private String goods_note;
	private String is_test;
	private String is_phone;
	private String is_frame;
	public String getversion()//
	{
		return version;
	}
	public void setversion(String version)
	{
		this.version = version;
	}
	
	public String getagent_id()//
	{
		return agent_id;
	}
	public void setagent_id(String agent_id)
	{
		this.agent_id = agent_id;
	}
	
	public String getagent_bill_id()//
	{
		return agent_bill_id;
	}
	public void setagent_bill_id(String agent_bill_id)
	{
		this.agent_bill_id = agent_bill_id;
	}

	public String getpay_type()//
	{
		return pay_type;
	}
	public void setpay_type(String pay_type)
	{
		this.pay_type = pay_type;
	}
	
	public String getpay_code()//
	{
		return pay_code;
	}
	public void setpay_code(String pay_code)
	{
		this.pay_code = pay_code;
	}
	
	public String getpay_amt()//
	{
		return pay_amt;
	}
	public void setpay_amt(String pay_amt)
	{
		this.pay_amt = pay_amt;
	}
	
	public String getnotify_url()//
	{
		return notify_url;
	}
	public void setnotify_url(String notify_url)
	{
		this.notify_url = notify_url;
	}
	
	public String getreturn_url()//
	{
		return return_url;
	}
	public void setreturn_url(String return_url)
	{
		this.return_url = return_url;
	}
	
	public String getuser_ip()//
	{
		return user_ip;
	}
	public void setuser_ip(String user_ip)
	{
		this.user_ip = user_ip;
	}
	
	public String getagent_bill_time()//
	{
		return agent_bill_time;
	}
	public void setagent_bill_time(String agent_bill_time)
	{
		this.agent_bill_time = agent_bill_time;
	}
	
	public String getgoods_name()//
	{
		return goods_name;
	}
	public void setgoods_name(String goods_name)
	{
		this.goods_name = goods_name;
	}

	public String getgoods_num()//
	{
		return goods_num;
	}
	public void setgoods_num(String goods_num)
	{
		this.goods_num = goods_num;
	}

	public String getremark()//
	{
		return remark;
	}
	public void setremark(String remark)
	{
		this.remark = remark;
	}

	public String getgoods_note()//
	{
		return goods_note;
	}
	public void setgoods_note(String goods_note)
	{
		this.goods_note = goods_note;
	}

	public String getpay_flag()//
	{
		return pay_flag;
	}
	public void setpay_flag(String pay_flag)
	{
		this.pay_flag = pay_flag;
	}

	public String getis_test()//
	{
		return is_test;
	}
	public void setis_test(String is_test)
	{
		this.is_test = is_test;
	}
	public String getis_phone()//
	{
		return is_phone;
	}
	public void setis_phone(String is_phone)
	{
		this.is_phone = is_phone;
	}

	public String getis_frame() {
		return is_frame;
	}
	public void setis_frame(String is_frame) {
		this.is_frame = is_frame;
	}
}
